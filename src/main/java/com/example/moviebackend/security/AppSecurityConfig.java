package com.example.moviebackend.security;

import com.example.moviebackend.security.jwt.JWTAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

/**
 * This class is responsible for the security configuration of the application.
 * It extends the WebSecurityConfigurerAdapter which provides a convenient base class for creating a WebSecurityConfigurer instance.
 * The configuration defines which URL paths should be secured and which should not.
 */
@EnableWebSecurity
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

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
        http.csrf().disable();

        // Only allow certain requests to be authorized
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/users/login").permitAll()
                .antMatchers(HttpMethod.POST, "/users/**").permitAll()
                .antMatchers(HttpMethod.GET, "/movies/**").permitAll()
                .antMatchers(HttpMethod.POST, "/movies/**").permitAll()
                .antMatchers(HttpMethod.DELETE,"/movies/**" ).permitAll()
                .antMatchers("/error").permitAll()
                .anyRequest().authenticated();

        // Add JWTAuthenticationFilter to the security filter chain
        http.addFilterBefore(new JWTAuthenticationFilter(), AnonymousAuthenticationFilter.class);

        // Ensure the server is stateless
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}