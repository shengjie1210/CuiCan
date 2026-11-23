package org.example.cuican.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * 配置静态资源映射、跨域资源共享、注册JWT认证拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private JwtInterceptor jwtInterceptor;

    /**
     * 添加静态资源映射
     * 将服务器本地上传目录映射为可访问的URL路径，使得上传的图片可以直接通过URL访问
     * 
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 通用图片映射
        registry.addResourceHandler("/image/**")
                .addResourceLocations("file:upload/image/");

        // 用户头像映射
        registry.addResourceHandler("/avatar/**")
                .addResourceLocations("file:upload/avatar/");

        // 商品图片映射
        registry.addResourceHandler("/product/**")
                .addResourceLocations("file:upload/product/");
    }

    /**
     * 配置跨域资源共享(CORS)
     * 允许指定的前端域名跨域访问后端接口，支持携带Cookie凭证
     * 
     * @param registry 跨域配置注册表
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:1913", "http://localhost:1210", "http://localhost:1314")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 注册JWT认证拦截器
     * 将JwtInterceptor注册到Spring MVC，配置拦截所有请求，排除登录、注册和静态资源路径
     * 
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",
                        "/auth/register",
                        "/image/**"
                );
    }
}
