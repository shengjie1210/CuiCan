package org.example.cuican.Service.Impl.user;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.cuican.Bean.user.CreateOrderDTO;
import org.example.cuican.Bean.user.Order;
import org.example.cuican.Bean.user.OrderItem;
import org.example.cuican.Bean.product.Product;
import org.example.cuican.Mapper.product.ProductMapper;
import org.example.cuican.Mapper.user.OrderItemMapper;
import org.example.cuican.Mapper.user.OrderMapper;
import org.example.cuican.Mapper.user.CartMapper;
import org.example.cuican.Service.admin.OrderLogService;
import org.example.cuican.Service.user.MemberLevelService;
import org.example.cuican.Service.user.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 订单业务服务实现类
 * 实现订单创建、取消、支付、确认收货，以及管理员发货、取消等功能
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Resource private OrderMapper orderMapper;
    @Resource private OrderItemMapper orderItemMapper;
    @Resource private ProductMapper productMapper;
    @Resource private OrderLogService orderLogService;
    @Resource private MemberLevelService memberLevelService;
    @Autowired
    private CartMapper cartMapper;

    /** 支持的支付方式白名单 */
    private static final Set<String> PAY_METHODS = Set.of("alipay", "wechat", "card", "balance");

    /**
     * 查询指定用户的所有订单列表
     * @param userId 用户ID
     * @return 用户的订单列表，按创建时间倒序排列
     */
    @Override
    public List<Order> list(Long userId) {
        return orderMapper.selectByUserId(userId);
    }

    /**
     * 查询订单详情，包含订单主信息和订单商品明细
     * @param orderId 订单ID
     * @return 结果Map，包含order(订单主信息)和items(订单商品明细列表)
     */
    @Override
    public Map<String, Object> detail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        Map<String, Object> map = new HashMap<>();
        map.put("order", order);
        map.put("items", items);
        return map;
    }

    /**
     * 根据订单总金额计算折扣价格
     * 折扣规则：满10000打88折，满5000打92折，满1000打95折，满500打97折
     * @param total 订单原始总金额
     * @return 折扣后的价格
     */
    private BigDecimal calcDiscount(BigDecimal total) {
        if (total.compareTo(new BigDecimal("10000")) >= 0) return total.multiply(new BigDecimal("0.88"));
        if (total.compareTo(new BigDecimal("5000")) >= 0) return total.multiply(new BigDecimal("0.92"));
        if (total.compareTo(new BigDecimal("1000")) >= 0) return total.multiply(new BigDecimal("0.95"));
        if (total.compareTo(new BigDecimal("500")) >= 0) return total.multiply(new BigDecimal("0.97"));
        return total;
    }

    /**
     * 创建订单，支持从购物车创建和直接购买商品两种方式
     * 扣减商品库存，清空已选中购物车，应用金额折扣，保存订单和订单明细
     * @param userId 创建订单的用户ID
     * @param dto 创建订单参数，包含购物车ID列表或单个商品信息、收货地址信息
     * @return 创建成功的订单ID
     */
    @Override
    @Transactional
    public Long createOrder(Long userId, CreateOrderDTO dto) {
        String orderNo = "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", (int)(Math.random() * 10000));
        Long maxId = orderMapper.selectMaxId();
        Long orderId = (maxId == null ? 3000L : maxId) + 1;

        BigDecimal originalAmount = BigDecimal.ZERO;

        if (dto.getCartIds() != null && !dto.getCartIds().isEmpty()) {
            List<org.example.cuican.Bean.user.CartVO> cartItems = cartMapper.selectByIds(dto.getCartIds());
            for (org.example.cuican.Bean.user.CartVO ci : cartItems) {
                productMapper.decrementStock(ci.getProductId().intValue(), ci.getQuantity());
                OrderItem oi = new OrderItem();
                oi.setOrderId(orderId);
                oi.setProductId(ci.getProductId());
                oi.setProductName(ci.getProductName());
                oi.setProductImage(ci.getProductImage());
                oi.setPrice(ci.getPrice());
                oi.setQuantity(ci.getQuantity());
                orderItemMapper.insert(oi);
                originalAmount = originalAmount.add(oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())));
            }
            cartMapper.deleteByIds(dto.getCartIds());
        } else if (dto.getProductId() != null) {
            Product product = productMapper.selectById(dto.getProductId().intValue());
            if (product == null) throw new RuntimeException("商品不存在");
            if (product.getStock() < dto.getQuantity()) throw new RuntimeException("库存不足");
            productMapper.decrementStock(dto.getProductId().intValue(), dto.getQuantity());
            OrderItem oi = new OrderItem();
            oi.setOrderId(orderId);
            oi.setProductId(dto.getProductId());
            oi.setProductName(product.getName());
            oi.setProductImage(product.getCoverImage());
            oi.setPrice(product.getPrice());
            oi.setQuantity(dto.getQuantity());
            orderItemMapper.insert(oi);
            originalAmount = originalAmount.add(oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())));
        } else {
            throw new RuntimeException("请选择要购买的商品");
        }

        BigDecimal totalAmount = calcDiscount(originalAmount);

        Order order = new Order();
        order.setId(orderId);
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setOriginalAmount(originalAmount);
        order.setTotalAmount(totalAmount);
        order.setStatus(0);
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setProvince(dto.getProvince());
        order.setCity(dto.getCity());
        order.setDetailAddress(dto.getDetailAddress());
        order.setNote(dto.getNote());
        order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);

        return orderId;
    }

    /**
     * 用户取消订单，仅允许取消待支付订单
     * 释放预扣的商品库存，删除订单明细和订单主记录
     * @param userId 当前用户ID，用于权限校验
     * @param orderId 要取消的订单ID
     */
    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) throw new RuntimeException("订单不存在");
        if (order.getStatus() != 0) throw new RuntimeException("当前状态不允许取消");
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem it : items) {
            productMapper.incrementStock(it.getProductId().intValue(), it.getQuantity());
        }
        orderItemMapper.deleteByOrderId(orderId);
        orderMapper.deleteById(orderId);
    }

    /**
     * 用户支付订单，仅允许支付待支付订单
     * 更新订单状态为已支付，记录支付方式和时间，刷新用户会员等级
     * @param userId 当前用户ID，用于权限校验
     * @param orderId 订单ID
     * @param method 支付方式，支持alipay/wechat/card/balance
     */
    @Override
    @Transactional
    public void payOrder(Long userId, Long orderId, String method) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) throw new RuntimeException("订单不存在");
        if (order.getStatus() != 0) throw new RuntimeException("当前状态不允许支付");
        String payMethod = (method == null || method.trim().isEmpty()) ? "alipay" : method.trim();
        if (!PAY_METHODS.contains(payMethod)) payMethod = "alipay";
        orderMapper.updatePaidInfo(orderId, 1, LocalDateTime.now(), payMethod);
        memberLevelService.refresh(userId);
    }

    /**
     * 用户确认收货，仅允许确认已发货订单
     * 更新订单状态为已完成，刷新用户会员等级
     * @param userId 当前用户ID，用于权限校验
     * @param orderId 订单ID
     */
    @Override
    @Transactional
    public void confirmOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) throw new RuntimeException("订单不存在");
        if (order.getStatus() != 2) throw new RuntimeException("当前状态不允许确认收货");
        orderMapper.updateConfirmInfo(orderId, 3, LocalDateTime.now());
        memberLevelService.refresh(userId);
    }

    /* ======= 管理员功能 ======= */

    /**
     * 管理员发货，仅允许对已支付订单发货
     * 更新订单状态为已发货，记录物流信息，操作日志
     * @param operatorId 操作管理员ID
     * @param operatorName 操作管理员名称
     * @param orderId 订单ID
     * @param shipMethod 快递公司
     * @param trackingNo 快递单号
     */
    @Override
    @Transactional
    public void shipByAdmin(Long operatorId, String operatorName, Long orderId, String shipMethod, String trackingNo) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        if (order.getStatus() != 1) throw new RuntimeException("仅已支付订单可发货");

        orderMapper.updateShipInfo(orderId, 2, LocalDateTime.now(), shipMethod, trackingNo);

        orderLogService.log(orderId, operatorId, operatorName, "ship",
                shipMethod + " / " + trackingNo);
    }

    /**
     * 管理员取消订单，订单未取消即可取消
     * 释放商品库存，删除订单明细和订单，刷新用户会员等级，记录操作日志
     * @param operatorId 操作管理员ID
     * @param operatorName 操作管理员名称
     * @param orderId 订单ID
     * @param reason 取消原因
     */
    @Override
    @Transactional
    public void cancelByAdmin(Long operatorId, String operatorName, Long orderId, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        if (order.getStatus() == -1) throw new RuntimeException("订单已取消");

        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem it : items) {
            productMapper.incrementStock(it.getProductId().intValue(), it.getQuantity());
        }
        orderItemMapper.deleteByOrderId(orderId);
        orderMapper.deleteById(orderId);
        memberLevelService.refresh(order.getUserId());

        orderLogService.log(orderId, operatorId, operatorName, "cancel", reason);
    }

    /**
     * 管理员确认收货，仅允许对已发货订单确认
     * 更新订单状态为已完成，刷新用户会员等级，记录操作日志
     * @param operatorId 操作管理员ID
     * @param operatorName 操作管理员名称
     * @param orderId 订单ID
     */
    @Override
    @Transactional
    public void confirmByAdmin(Long operatorId, String operatorName, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        if (order.getStatus() != 2) throw new RuntimeException("仅已发货订单可确认收货");

        orderMapper.updateConfirmInfo(orderId, 3, LocalDateTime.now());
        memberLevelService.refresh(order.getUserId());
        orderLogService.log(orderId, operatorId, operatorName, "confirm", null);
    }

    /**
     * 管理员分页查询订单列表，支持按状态筛选和关键词搜索
     * @param status 订单状态筛选，null表示查询全部
     * @param keyword 订单号关键词搜索，null或空表示不搜索
     * @param page 当前页码，最小为1
     * @param size 每页条数，范围1-100，默认10
     * @return 分页结果Map，包含list(订单列表)、total(总条数)、page(当前页)、size(每页条数)
     */
    @Override
    public Map<String, Object> adminList(Integer status, String keyword, int page, int size) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        if (size > 100) size = 100;
        int offset = (page - 1) * size;

        List<Order> list = orderMapper.selectAll(status, keyword, offset, size);
        int total = orderMapper.countAll(status, keyword);

        Map<String, Object> resp = new HashMap<>();
        resp.put("list", list);
        resp.put("total", total);
        resp.put("page", page);
        resp.put("size", size);
        return resp;
    }
}
