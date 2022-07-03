package com.assignment.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.assignment.backend.security.jwt.JwtAuthEntryPoint;
import com.assignment.backend.security.jwt.JwtAuthTokenFilter;
import com.assignment.backend.security.jwt.JwtUtils;

@Configuration
@EnableWebSecurity()
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private UserDetailsService userDetailsService;
    private JwtAuthEntryPoint jwtAuthEntryPoint;
    private JwtUtils jwtUtils;

    @Autowired
    public WebSecurityConfig(JwtAuthEntryPoint jwtAuthEntryPoint, JwtUtils jwtUtils,
            UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public JwtAuthTokenFilter authenticationJwtAuthTokenFilter() {
        return new JwtAuthTokenFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.addFilterBefore(authenticationJwtAuthTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        httpSecurity.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(jwtAuthEntryPoint).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/v1/auth/**").permitAll()
                .antMatchers("/product/all").hasAnyAuthority("ADMIN")
                .antMatchers("/category", "/category/**").hasAnyAuthority("USER")
                .anyRequest().permitAll();

        return httpSecurity.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html",
                "/swagger-ui/**",
                "/api-docs/**", "/api-doc-ui");
    }

}
