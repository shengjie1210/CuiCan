package org.example.cuican.Bean.captcha;

import lombok.Data;

/**
 * 图形验证码生成结果DTO
 * 封装生成的验证码图片Base64和验证Token，返回给前端
 */
@Data
public class CaptchaResult {
    /**
     * 验证码图片的Base64编码Data URL，可以直接作为img标签的src显示
     */
    private String image;
    
    /**
     * 验证码Token，用于后续验证时关联验证码答案，存储在服务端
     */
    private String token;
}
