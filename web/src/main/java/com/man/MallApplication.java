package com.man;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication//(exclude = {DataSourceAutoConfiguration.class}) // 自动配置数据源 spring.datasource
//@MapperScan("com.man.mapper") 在数据库配置bean里已经声明了
public class MallApplication {

    public static void main(String[] args) {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(MallApplication.class, args);
    }
}
