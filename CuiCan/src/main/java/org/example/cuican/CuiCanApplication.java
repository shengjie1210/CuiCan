package org.example.cuican;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 璀璨微服务项目启动类
 * Spring Boot应用入口，配置定时任务和Mapper扫描
 */
@Slf4j
@SpringBootApplication
@EnableScheduling   // 启用 @Scheduled 定时任务（订单超时取消/自动发货/自动确认收货）
@MapperScan("org.example.cuican.Mapper")
public class CuiCanApplication {

    /**
     * Spring Boot应用入口方法
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(CuiCanApplication.class, args);
        log.info("璀璨微服务启动成功");
    }
}
