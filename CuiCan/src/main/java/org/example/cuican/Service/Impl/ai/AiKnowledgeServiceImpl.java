package org.example.cuican.Service.Impl.ai;

import jakarta.annotation.Resource;
import jakarta.annotation.PostConstruct;
import org.example.cuican.Bean.ai.AiKnowledge;
import org.example.cuican.Bean.product.Product;
import org.example.cuican.Mapper.ai.AiKnowledgeMapper;
import org.example.cuican.Mapper.product.ProductMapper;
import org.example.cuican.Service.admin.SystemSettingService;
import org.example.cuican.Service.ai.AiKnowledgeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI知识库服务实现类
 * 实现知识库的增删改查、商品同步、文件导入等功能
 */
@Service
public class AiKnowledgeServiceImpl implements AiKnowledgeService {
    @Resource
    private AiKnowledgeMapper knowledgeMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private SystemSettingService settingService;

    /**
     * 项目启动后初始化基础知识库
     * 同步系统配置相关的基础问答知识到知识库
     */
    @PostConstruct
    public void initBasics() {
        try {
            syncBasics();
        } catch (Exception ignored) {
        }
    }

    /**
     * 获取所有知识库条目列表
     * @return 所有知识库条目列表
     */
    @Override
    public List<AiKnowledge> list() {
        return knowledgeMapper.selectAll();
    }

    /**
     * 搜索知识库条目
     * @param keyword 搜索关键词
     * @param limit 最大返回条数
     * @return 匹配的知识库条目列表
     */
    @Override
    public List<AiKnowledge> search(String keyword, int limit) {
        if (keyword == null || keyword.trim().isEmpty()) return List.of();
        return knowledgeMapper.search(keyword.trim(), limit);
    }

    /**
     * 分页搜索知识库条目
     * @param keyword 搜索关键词
     * @param page 当前页码
     * @param size 每页条数
     * @return 分页结果Map，包含list、total、page、size
     */
    @Override
    public Map<String, Object> listPage(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<AiKnowledge> list = knowledgeMapper.selectByKeyword(keyword, offset, size);
        int total = knowledgeMapper.countByKeyword(keyword);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("list", list);
        data.put("total", total);
        data.put("page", page);
        data.put("size", size);
        return data;
    }

    /**
     * 保存知识库条目
     * ID为空新增，ID存在更新，自动设置创建时间和更新时间
     * @param knowledge 知识库条目实体
     * @return 保存后的知识库条目
     */
    @Override
    public AiKnowledge save(AiKnowledge knowledge) {
        if (knowledge.getId() == null) {
            if (knowledge.getEnabled() == null) knowledge.setEnabled(1);
            if (knowledge.getCategory() == null || knowledge.getCategory().isEmpty()) knowledge.setCategory("商品");
            knowledge.setCreateTime(LocalDateTime.now());
            knowledge.setUpdateTime(LocalDateTime.now());
            knowledgeMapper.insert(knowledge);
        } else {
            knowledge.setUpdateTime(LocalDateTime.now());
            knowledgeMapper.update(knowledge);
        }
        return knowledge;
    }

    /**
     * 删除知识库条目
     * @param id 条目ID
     */
    @Override
    public void delete(Long id) {
        knowledgeMapper.delete(id);
    }

    /**
     * 同步系统配置基础信息到知识库
     * 将订单规则、站点信息等自动创建问答条目
     * @return 成功同步的条目数量
     */
    public int syncBasics() {
        String cancelMinutes = settingService.get("order.countdown_minutes", "15");
        String confirmDays = settingService.get("order.auto_confirm_days", "7");
        String threshold = settingService.get("order.free_shipping_threshold", "500");
        String phone = settingService.get("site.service_phone", "400-888-9999");
        String hours = settingService.get("site.service_hours", "工作日 9:00-18:00");

        int count = 0;
        if (saveImported("订单支付倒计时", "未支付订单需要在 " + cancelMinutes + " 分钟内完成支付，超时会自动取消订单。", "订单,支付,倒计时", "规则")) count++;
        if (saveImported("什么时候自动确认收货", "发货后 " + confirmDays + " 天自动确认收货。", "确认收货,自动", "规则")) count++;
        if (saveImported("包邮条件","满 " + threshold + " 元包邮。", "包邮,运费", "规则")) count++;
        if (saveImported("客服电话", "客服电话是 " + phone + "，工作时间是 " + hours + "。", "客服,电话,联系", "站点")) count++;
        if (saveImported("客服工作时间", "客服工作时间是 " + hours + "，非工作时间请留言，上班后会第一时间回复。", "客服,时间,工作", "站点")) count++;
        return count;
    }

    /**
     * 同步所有商品到知识库
     * 将所有商品信息转换为知识库条目，商品信息作为回答，商品名称作为问题
     * @return 成功同步的商品数量
     */
    @Override
    public int syncProducts() {
        List<Product> products = productMapper.selectAll();
        int count = 0;
        for (Product p : products) {
            String name = p.getName();
            String answer = (p.getDescription() == null ? "" : p.getDescription())
                    + " 价格：" + p.getPrice()
                    + " 元，库存：" + p.getStock()
                    + "，已售：" + p.getSales()
                    + "。";
            List<AiKnowledge> exist = knowledgeMapper.selectByProductId(p.getId().longValue());
            if (exist == null || exist.isEmpty()) {
                AiKnowledge k = new AiKnowledge();
                k.setCategory("商品");
                k.setQuestion(name);
                k.setAnswer(answer);
                k.setKeywords(name);
                k.setProductId(p.getId().longValue());
                k.setEnabled(1);
                k.setCreateTime(LocalDateTime.now());
                k.setUpdateTime(LocalDateTime.now());
                knowledgeMapper.insert(k);
                count++;
            } else {
                AiKnowledge k = exist.get(0);
                k.setAnswer(answer);
                k.setKeywords(name);
                k.setUpdateTime(LocalDateTime.now());
                knowledgeMapper.update(k);
                count++;
            }
        }
        return count;
    }

    /**
     * 从上传的文本文件导入知识库
     * 支持txt、md、csv格式，自动解析问答
     * @param fileName 文件名
     * @param content 文件内容
     * @return 成功导入的条目数量
     */
    @Override
    public int importFile(String fileName, String content) {
        if (fileName == null) return 0;
        if (fileName.toLowerCase().endsWith(".csv")) {
            return importCsv(content);
        }
        return importText(content);
    }

    /**
     * 从CSV格式导入知识库
     * CSV格式要求：第一列问题，第二列答案，第三列关键词（可选），第四列分类（可选）
     * @param content CSV文件内容
     * @return 成功导入的条目数量
     */
    private int importCsv(String content) {
        String[] lines = content.split("\\r?\\n");
        int count = 0;
        for (String line : lines) {
            String t = line.trim();
            if (t.isEmpty()) continue;
            List<String> cols = parseCsvLine(t);
            if (cols.isEmpty()) continue;
            String question = cols.get(0).trim();
            String answer = cols.size() > 1 ? cols.get(1).trim() : "";
            if (question.isEmpty() || answer.isEmpty()) continue;
            String keywords = cols.size() > 2 ? cols.get(2).trim() : "";
            String category = cols.size() > 3 && !cols.get(3).trim().isEmpty() ? cols.get(3).trim() : "自定义";
            if (saveImported(question, answer, keywords, category)) count++;
        }
        return count;
    }

    /**
     * 从文本格式导入知识库
     * 支持 Q:/A: 问答格式，普通段落自动打包成单条知识
     * @param content 文本文件内容
     * @return 成功导入的条目数量
     */
    private int importText(String content) {
        String[] lines = content.split("\\r?\\n");
        int count = 0;
        Pattern qPattern = Pattern.compile("^(?:问|问题|Q)\\s*[:：]\\s*(.+)$");
        Pattern aPattern = Pattern.compile("^(?:答|答案|A)\\s*[:：]\\s*(.+)$");
        List<String> pending = new ArrayList<>();
        String q = null;
        StringBuilder ans = new StringBuilder();
        for (String line : lines) {
            String t = line.trim();
            if (t.isEmpty()) {
                if (q != null) {
                    if (ans.length() > 0) {
                        if (saveImported(q, ans.toString().trim(), "", "自定义")) count++;
                    }
                    q = null;
                    ans.setLength(0);
                }
                if (flushParagraph(pending) > 0) count++;
                continue;
            }
            Matcher qm = qPattern.matcher(t);
            Matcher am = aPattern.matcher(t);
            if (qm.matches()) {
                if (q != null) {
                    if (ans.length() > 0) {
                        if (saveImported(q, ans.toString().trim(), "", "自定义")) count++;
                    }
                }
                if (flushParagraph(pending) > 0) count++;
                q = qm.group(1).trim();
                ans.setLength(0);
                continue;
            }
            if (am.matches() && q != null) {
                ans.append(am.group(1).trim()).append("\n");
                continue;
            }
            if (q != null) {
                ans.append(t).append("\n");
            } else {
                pending.add(t);
            }
        }
        if (q != null && ans.length() > 0) {
            if (saveImported(q, ans.toString().trim(), "", "自定义")) count++;
        }
        if (flushParagraph(pending) > 0) count++;
        return count;
    }

    /**
     * 把非问答格式的普通段落打包成一条知识
     * 问题取前30个字，内容是整个段落
     * @param pending 待打包的段落行列表
     * @return 打包成功的数量（0或1）
     */
    private int flushParagraph(List<String> pending) {
        int count = 0;
        if (pending.isEmpty()) return 0;
        String para = String.join(" ", pending).trim();
        pending.clear();
        if (para.isEmpty()) return 0;
        String question = para.length() > 30 ? para.substring(0, 30) : para;
        if (saveImported(question, para, "", "自定义")) count++;
        return count;
    }

    /**
     * 解析CSV一行，处理引号和转义
     * @param line CSV一行文本
     * @return 解析后的列列表
     */
    private List<String> parseCsvLine(String line) {
        List<String> cols = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuote = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuote) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');
                        i++;
                    } else {
                        inQuote = false;
                    }
                } else {
                    cur.append(c);
                }
            } else if (c == '"') {
                inQuote = true;
            } else if (c == ',') {
                cols.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        cols.add(cur.toString());
        return cols;
    }

    /**
     * 保存导入的知识库条目，按问题去重
     * 问题不存在则新增，存在则更新答案/关键词/分类，保留商品关联
     * @param question 问题
     * @param answer 答案
     * @param keywords 关键词
     * @param category 分类
     * @return true 保存成功，false 保存失败
     */
    private boolean saveImported(String question, String answer, String keywords, String category) {
        try {
            AiKnowledge exist = knowledgeMapper.selectByQuestion(question);
            if (exist == null) {
                AiKnowledge k = new AiKnowledge();
                k.setCategory(category);
                k.setQuestion(question);
                k.setAnswer(answer);
                k.setKeywords(keywords);
                k.setEnabled(1);
                k.setCreateTime(LocalDateTime.now());
                k.setUpdateTime(LocalDateTime.now());
                knowledgeMapper.insert(k);
            } else {
                exist.setCategory(category);
                exist.setAnswer(answer);
                exist.setKeywords(keywords);
                exist.setUpdateTime(LocalDateTime.now());
                knowledgeMapper.update(exist);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

