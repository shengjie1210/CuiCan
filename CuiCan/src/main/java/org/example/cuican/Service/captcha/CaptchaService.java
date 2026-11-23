package org.example.cuican.Service.captcha;

import org.example.cuican.Bean.captcha.CaptchaResult;

/**
 * 验证码服务接口
 * 提供验证码生成和验证码验证功能
 */
public interface CaptchaService {
    /**
     * 生成数学运算验证码
     * @return 验证码结果，包含token和图片Base64
     */
    CaptchaResult generate();

    /**
     * 验证用户输入的验证码是否正确
     * @param token 验证码token
     * @param code 用户输入的答案
     * @return true 验证正确，false 验证错误或已过期
     */
    boolean verify(String token, String code);
}
