package com.hb0730.cloud.admin.server.permission.menu.config;

import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * <p>
 * </P>
 *
 * @author bing_huang
 * @since V1.0
 */
@EnableTransactionManagement
@Configuration
@MapperScan("com.hb0730.cloud.admin.server.permission.menu.**.mapper")
public class MybatisPlusConfig {

    /**
     * 乐观锁插件
     *
     * @return
     */
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }

}
