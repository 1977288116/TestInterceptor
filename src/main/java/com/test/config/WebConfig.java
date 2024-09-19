package com.test.config;

import com.test.interceptor.IpUrlLimitInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Bean
    IpUrlLimitInterceptor getIpUrlLimitInterceptor(){
        return  new IpUrlLimitInterceptor();
    };
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getIpUrlLimitInterceptor()).addPathPatterns("/**");
    }
}
