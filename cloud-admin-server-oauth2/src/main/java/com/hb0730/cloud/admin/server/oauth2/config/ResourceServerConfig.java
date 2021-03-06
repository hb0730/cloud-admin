package com.hb0730.cloud.admin.server.oauth2.config;

import com.hb0730.cloud.admin.server.oauth2.handler.Oauth2AccessDeniedHandler;
import com.hb0730.cloud.admin.server.oauth2.handler.Oauth2ExceptionEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import static com.hb0730.cloud.admin.common.util.RequestMappingConstants.OAUTH2_SERVER_REQUEST;

/**
 * <p>
 *
 * </P>
 *
 * @author bing_huang
 * @since V1.0
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Autowired
    private Oauth2AccessDeniedHandler accessDeniedHandler;
    @Autowired
    private Oauth2ExceptionEntryPoint exceptionEntryPoint;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(OAUTH2_SERVER_REQUEST + "/user/login", "/actuator/**").permitAll()
                // 增加了授权访问配置
                .antMatchers("/**").authenticated()
                .and().httpBasic()
                .and().csrf().disable();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(exceptionEntryPoint).stateless(false);
        ;
    }
}
