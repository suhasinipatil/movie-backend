package com.example.moviebackend.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@EnableWebSecurity
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().disable();

        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/users/login").permitAll()
                .antMatchers(HttpMethod.POST, "/users/**").permitAll()
                .antMatchers(HttpMethod.GET, "/movies/**").permitAll()
                .antMatchers(HttpMethod.POST, "/movies/**").permitAll()
                .antMatchers(HttpMethod.DELETE,"/movies/**" ).permitAll()
                .antMatchers("/error").permitAll()
                .anyRequest().authenticated();

        //To make sure the server is stateless
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
