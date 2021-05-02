package com.example.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
// 启用全局方法权限控制, 保证@PreAuthority, @PostAuthority, @PreFilter, @PostFilter生效
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;  // 使用BCryptPasswordEncoder加密

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
        ;
    }

    @Override
    protected void configure(HttpSecurity security) throws Exception {
        security
                .authorizeRequests()
                .antMatchers("/index.jsp", "/to/login.html", "/bootstrap/**", "/css/**", "/images/**", "/jquery/**", "/js/**", "/layer/**", "/layui/**")
                        .permitAll()    // 可以无条件访问
                        .anyRequest()   // 任意请求
                        .authenticated()    //需要登录后访问
                        .and()
                        .formLogin()    // 开启表单登录功能
                        .loginPage("/to/login.html") // 登录的页面
                        .loginProcessingUrl("/security/do/login.html")   // 登录请求
                        .defaultSuccessUrl("/")  // 登录成功后跳转的页面
                        .usernameParameter("username") // 账号请求参数名
                        .passwordParameter("password")   // 密码请求参数名
                        .and()
                        .logout()
                        .logoutUrl("/security/do/logout.html")  // 退出登录请求
                        .logoutSuccessUrl("/")   // 退出成功前往的地址
                        .and()
                        .csrf()
                        .disable()
        ;
    }
}
