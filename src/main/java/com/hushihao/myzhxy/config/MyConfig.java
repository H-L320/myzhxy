package com.hushihao.myzhxy.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration //告诉spring boot这是一个配置类 == 配置文件
@MapperScan("com.hushihao.myzhxy.mapper")
public class MyConfig {
    /**
     * 分页插件
     */
    @Bean //给容器中添加组件，以方法名作为组件的id
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // paginationInterceptor.setLimit(你的最大单页限制数量，默认 500 条，小于 0 如 -1 不受限制);
        return paginationInterceptor;
    }
}
