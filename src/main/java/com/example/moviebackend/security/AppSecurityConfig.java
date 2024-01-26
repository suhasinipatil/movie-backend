package com.example.moviebackend.security;

import com.example.moviebackend.security.jwt.JWTAuthenticationFilter;
import com.example.moviebackend.user.OAuth2LoginAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * This class is responsible for the security configuration of the application.
 * It extends the WebSecurityConfigurerAdapter which provides a convenient base class for creating a WebSecurityConfigurer instance.
 * The configuration defines which URL paths should be secured and which should not.
 */
@EnableWebSecurity
@Configuration
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2LoginAuthenticationSuccessHandler successHandler;
    /**
     * This method is used to configure the HttpSecurity.
     * It defines which requests should be authorized.
     * It also adds the JWTAuthenticationFilter to the security filter chain.
     *
     * @param http HttpSecurity
     * @throws Exception if an error occurs
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable CSRF (Cross Site Request Forgery)
        http.oauth2Login()
                .successHandler(successHandler)
                .and().
                cors().and() // add this line to enable cors
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/users/login").permitAll()
                .antMatchers(HttpMethod.POST, "/users/**").permitAll()
                .antMatchers(HttpMethod.GET, "/movies/**").permitAll()
                .antMatchers(HttpMethod.POST, "/movies/**").permitAll()
                .antMatchers(HttpMethod.DELETE,"/movies/**" ).permitAll()
                .antMatchers("/api/auth/google").permitAll()
                .antMatchers("/error").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JWTAuthenticationFilter(), AnonymousAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // Allow all origins or customize as per your need
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE")); // Allow specific http methods, for instance GET, POST, etc.
        configuration.setAllowedHeaders(List.of("*")); // Allow all headers, or you can specify what headers are allowed
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}