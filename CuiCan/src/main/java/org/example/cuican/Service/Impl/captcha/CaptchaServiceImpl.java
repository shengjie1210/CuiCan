package org.example.cuican.Service.Impl.captcha;

import lombok.extern.slf4j.Slf4j;
import org.example.cuican.Bean.captcha.CaptchaResult;
import org.example.cuican.Service.captcha.CaptchaService;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

/**
 * 验证码服务实现类
 * 生成数学运算验证码，使用内存存储验证码答案，支持验证
 */
@Slf4j
@Service
public class CaptchaServiceImpl implements CaptchaService {

    private static final long EXPIRE_MS = 300_000; // 验证码过期时间 5分钟
    private static final int WIDTH = 180;  // 图片宽度
    private static final int HEIGHT = 56;  // 图片高度
    private static final int CODE_LEN = 4; // 验证码长度
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // 验证码字符集，去除易混淆字符

    private final SecureRandom random = new SecureRandom();
    private final ConcurrentHashMap<String, CaptchaData> store = new ConcurrentHashMap<>();

    /**
     * 生成验证码
     * 生成验证码图片Base64和验证token
     * @return 验证码结果，包含图片Base64和验证token
     */
    @Override
    public CaptchaResult generate() {
        cleanup();

        String code = generateCode();
        String token = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        store.put(token, new CaptchaData(code, System.currentTimeMillis() + EXPIRE_MS));

        CaptchaResult result = new CaptchaResult();
        result.setImage(generateImage(code));
        result.setToken(token);

        log.info("验证码生成: code={}, token={}", code, token);
        return result;
    }

    /**
     * 验证用户输入的验证码是否正确
     * 验证成功后移除验证码，一次性使用
     * @param token 验证码token
     * @param code 用户输入的答案
     * @return true 验证正确，false 验证错误或已过期
     */
    public boolean verify(String token, String code) {
        CaptchaData data = store.remove(token);
        if (data == null) {
            log.warn("验证码不存在或已使用: token={}", token);
            return false;
        }
        if (System.currentTimeMillis() > data.expireTime) {
            log.warn("验证码已过期: token={}", token);
            return false;
        }
        if (!data.code.equalsIgnoreCase(code.trim())) {
            log.warn("验证码错误: token={}, expected={}, got={}", token, data.code, code);
            return false;
        }
        log.info("验证码验证通过: token={}", token);
        return true;
    }

    /**
     * 生成随机验证码字符串
     * @return 随机验证码
     */
    private String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LEN);
        for (int i = 0; i < CODE_LEN; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * 生成验证码图片，返回Base64编码
     * 添加干扰线、噪点，每个字符随机旋转，增加识别难度
     * @param code 验证码字符串
     * @return Base64编码的PNG图片
     */
    private String generateImage(String code) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 渐变背景
        Color bg1 = new Color(245 + random.nextInt(10), 245 + random.nextInt(10), 250 + random.nextInt(5));
        Color bg2 = new Color(235 + random.nextInt(15), 240 + random.nextInt(10), 248 + random.nextInt(7));
        GradientPaint gp = new GradientPaint(0, 0, bg1, WIDTH, HEIGHT, bg2);
        g.setPaint(gp);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 干扰线 (3-5条)
        int lineCount = 3 + random.nextInt(3);
        for (int i = 0; i < lineCount; i++) {
            g.setColor(new Color(
                160 + random.nextInt(80),
                160 + random.nextInt(80),
                160 + random.nextInt(80),
                180
            ));
            g.setStroke(new BasicStroke(1.5f + random.nextFloat()));
            int x1 = random.nextInt(WIDTH / 3);
            int y1 = random.nextInt(HEIGHT);
            int x2 = WIDTH / 2 + random.nextInt(WIDTH / 2);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 噪点
        g.setColor(new Color(180, 180, 190, 120));
        for (int i = 0; i < 80; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            g.fillRect(x, y, 1 + random.nextInt(2), 1 + random.nextInt(2));
        }

        // 绘制文字：每个字符独立旋转 + 随机颜色
        int charWidth = (WIDTH - 20) / CODE_LEN;
        for (int i = 0; i < CODE_LEN; i++) {
            char ch = code.charAt(i);
            int fontSize = 28 + random.nextInt(10);
            Font font = new Font("SansSerif", Font.BOLD, fontSize);
            g.setFont(font);

            double angle = (random.nextDouble() - 0.5) * 0.6;
            int x = 12 + i * charWidth + random.nextInt(6);
            int y = HEIGHT - 12 - random.nextInt(8);

            Color textColor = new Color(
                30 + random.nextInt(100),
                30 + random.nextInt(100),
                30 + random.nextInt(100)
            );

            Graphics2D g2 = (Graphics2D) g.create();
            AffineTransform at = AffineTransform.getRotateInstance(angle, x, y);
            g2.transform(at);
            g2.setColor(textColor);
            g2.drawString(String.valueOf(ch), x, y);
            g2.dispose();
        }

        // 再画一层半透明的交叉干扰线（文字上层）
        g.setStroke(new BasicStroke(1.0f));
        for (int i = 0; i < 2; i++) {
            g.setColor(new Color(
                200 + random.nextInt(55),
                200 + random.nextInt(55),
                200 + random.nextInt(55),
                100
            ));
            g.drawLine(
                random.nextInt(WIDTH), random.nextInt(HEIGHT),
                random.nextInt(WIDTH), random.nextInt(HEIGHT)
            );
        }

        g.dispose();

        // 编码为 base64 PNG
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            String b64 = Base64.getEncoder().encodeToString(bytes);
            return "data:image/png;base64," + b64;
        } catch (Exception e) {
            log.error("验证码图片生成失败", e);
            return "";
        }
    }

    /**
     * 清理过期验证码，释放内存
     */
    private void cleanup() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, CaptchaData>> it = store.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().expireTime < now) {
                it.remove();
            }
        }
    }

    /**
     * 验证码数据内部类
     * 存储验证码答案和过期时间
     */
    private static class CaptchaData {
        final String code;
        final long expireTime;

        CaptchaData(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
    }
}
