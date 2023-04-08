package com.luckcat.config.satoken;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
public class SaTokenConfigure implements WebMvcConfigurer {
    /*** 注册 Sa-Token 拦截器，打开注解式鉴权功能* 如果在高版本 SpringBoot (≥2.6.x) 下注册拦截器失效，则需要额外添加 @EnableWebMvc 注解才可以使用* @param registry*/
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册路由拦截器，自定义认证规则
        registry.addInterceptor(new SaInterceptor(handler -> {
                    // 登录认证 -- 拦截所有路由，并判断是否登录
                    SaRouter.match("/**",r -> StpUtil.checkLogin());
                    SaRouter.match("/photo/**", r -> StpUtil.checkRoleOr("admin", "user"));
                }).isAnnotation(true))
                //拦截所有接口
                .addPathPatterns("/**")
                // 不拦截的接口
                .excludePathPatterns(
                        "/user/loginUser",
                        "/user/registerUser",
                        "/user/sendPasswordMail/{email}",
                        "/user/captchaCheck/{email}/{captcha}",
                        "/user/updatePassword/{email}/{captcha}/{password}",
                        "/user/exist/*"
                        )
                .excludePathPatterns(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/webjars/**"
                );
    }

    // Sa-Token 整合 jwt (Simple 简单模式)
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }
}
