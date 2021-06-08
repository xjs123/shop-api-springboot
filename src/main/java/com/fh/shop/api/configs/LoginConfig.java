package com.fh.shop.api.configs;

import com.fh.shop.api.Interceptor.LoginIntercepTor;
import com.fh.shop.api.Interceptor.TokenIntercepTor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LoginConfig implements WebMvcConfigurer {


    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getinterceptor()).addPathPatterns("/api/**");
        registry.addInterceptor(getTokeninterceptor()).addPathPatterns("/api/**");
    }


    @Bean
    public  LoginIntercepTor getinterceptor(){
        return  new LoginIntercepTor();
    }

    @Bean
    public TokenIntercepTor getTokeninterceptor(){
        return  new TokenIntercepTor();
    }






}
