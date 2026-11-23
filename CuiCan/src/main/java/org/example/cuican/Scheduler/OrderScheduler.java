package org.example.cuican.Scheduler;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.cuican.Bean.user.Order;
import org.example.cuican.Bean.user.OrderItem;
import org.example.cuican.Mapper.product.ProductMapper;
import org.example.cuican.Mapper.user.OrderItemMapper;
import org.example.cuican.Mapper.user.OrderMapper;
import org.example.cuican.Service.admin.OrderLogService;
import org.example.cuican.Service.admin.SystemSettingService;
import org.example.cuican.Service.user.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单定时任务
 *  - autoCancel: 待付款超过 countdown_minutes 没付款自动取消 + 释放库存
 *  - autoShip: 已支付超过 auto_ship_minutes 自动设为已发货（演示用）
 *  - autoConfirm: 已发货超过 7 天自动确认收货
 */
@Slf4j
@Component
public class OrderScheduler {

    @Resource private OrderMapper orderMapper;
    @Resource private OrderItemMapper orderItemMapper;
    @Resource private ProductMapper productMapper;
    @Resource private OrderService orderService;
    @Resource private OrderLogService orderLogService;
    @Resource private SystemSettingService settingService;

    // 系统操作者 ID（不会真有一个"系统用户"），做个虚拟的 999L
    private static final Long SYSTEM_OPERATOR_ID = 999L;
    private static final String SYSTEM_OPERATOR_NAME = "system-scheduler";

    /** 每 60 秒扫一次过期未付款订单 */
    @Scheduled(fixedRate = 60_000)
    public void autoCancel() {
        int minutes = settingService.getInt("order.countdown_minutes", 15);
        // 一次性扫一波（生产环境应分页）
        List<Order> orders = orderMapper.selectAll(0, null, 0, 200);
        long now = System.currentTimeMillis() / 1000;
        long deadlineSec = minutes * 60L;
        for (Order o : orders) {
            if (o.getStatus() != 0 || o.getCreateTime() == null) continue;
            long createSec = o.getCreateTime().atZone(java.time.ZoneId.systemDefault()).toEpochSecond();
            if (now - createSec < deadlineSec) continue;
            try {
                orderService.cancelByAdmin(SYSTEM_OPERATOR_ID, SYSTEM_OPERATOR_NAME,
                        o.getId(), "auto-cancel: 待付款超时 " + minutes + " 分钟");
                log.info("auto-cancel order {}", o.getId());
            } catch (Exception e) {
                log.error("auto-cancel order {} failed: {}", o.getId(), e.getMessage());
            }
        }
    }

    /** 每 60 秒扫一次已支付订单，自动发货 */
    @Scheduled(fixedRate = 60_000)
    public void autoShip() {
        int minutes = settingService.getInt("order.auto_ship_minutes", 0);
        if (minutes <= 0) return; // 关闭自动发货
        String[] methods = settingService.get("order.ship_methods", "顺丰,圆通,中通,韵达,邮政").split(",");
        List<Order> orders = orderMapper.selectAll(1, null, 0, 200);
        long now = System.currentTimeMillis() / 1000;
        long deadlineSec = minutes * 60L;
        for (Order o : orders) {
            if (o.getPayTime() == null) continue;
            long paySec = o.getPayTime().atZone(java.time.ZoneId.systemDefault()).toEpochSecond();
            if (now - paySec < deadlineSec) continue;
            try {
                String shipMethod = methods[(int) (Math.random() * methods.length)];
                String tracking = "AUTO" + System.currentTimeMillis();
                orderService.shipByAdmin(SYSTEM_OPERATOR_ID, SYSTEM_OPERATOR_NAME,
                        o.getId(), shipMethod, tracking);
                log.info("auto-ship order {}", o.getId());
            } catch (Exception e) {
                log.error("auto-ship order {} failed: {}", o.getId(), e.getMessage());
            }
        }
    }

    /** 每 5 分钟扫一次发货满 7 天的订单，自动确认收货 */
    @Scheduled(fixedRate = 300_000)
    public void autoConfirm() {
        List<Order> orders = orderMapper.selectAll(2, null, 0, 200);
        long now = System.currentTimeMillis() / 1000;
        long sevenDays = 7 * 24 * 60 * 60L;
        for (Order o : orders) {
            if (o.getShipTime() == null) continue;
            long shipSec = o.getShipTime().atZone(java.time.ZoneId.systemDefault()).toEpochSecond();
            if (now - shipSec < sevenDays) continue;
            try {
                orderService.confirmByAdmin(SYSTEM_OPERATOR_ID, SYSTEM_OPERATOR_NAME, o.getId());
                log.info("auto-confirm order {}", o.getId());
            } catch (Exception e) {
                log.error("auto-confirm order {} failed: {}", o.getId(), e.getMessage());
            }
        }
    }
}