package org.example.cuican.Controller.captcha;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.Result;
import org.example.cuican.Bean.captcha.CaptchaResult;
import org.example.cuican.Service.captcha.CaptchaService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码控制器
 * 提供验证码生成接口，用于登录等场景的人机验证
 */
@RestController
@RequestMapping("/api/captcha")
@CrossOrigin(origins = "http://localhost:5173")
public class CaptchaController {

    @Resource
    private CaptchaService captchaService;

    /**
     * 生成数学运算验证码接口
     * 接口路径：/api/captcha/math
     * 请求方式：GET
     * @return 统一响应结果，返回包含验证码token和验证码图片Base64的验证码结果
     */
    @GetMapping("/math")
    public Result math() {
        return Result.success(captchaService.generate());
    }
}
