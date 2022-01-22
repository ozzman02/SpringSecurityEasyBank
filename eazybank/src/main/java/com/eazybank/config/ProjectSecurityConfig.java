package com.eazybank.config;

import com.eazybank.filter.AuthoritiesLoggingAfterFilter;
import com.eazybank.filter.AuthoritiesLoggingAtFilter;
import com.eazybank.filter.RequestValidationBeforeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@Configuration
@EnableWebSecurity(debug = true)
public class ProjectSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
        Requirement

            /myAccount - Secured
            /myBalance - Secured
            /myLoans - Secured
            /myCards - Secured
            /notices - Not Secured
            /contact - Not Secured
    */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setMaxAge(3600L);
                        return config;
                    }
                }).and()
                .csrf()
                    .ignoringAntMatchers("/contact")
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
                .addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
                .addFilterAt(new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class)
                .authorizeRequests()
                    .antMatchers("/myAccount").hasRole("USER")
                    .antMatchers("/myBalance").hasAnyRole("USER","ADMIN")
                    .antMatchers("/myLoans").hasRole("ROOT")
                    .antMatchers("/myCards").hasAnyRole("USER","ADMIN")
                    .antMatchers("/user").authenticated()
                    .antMatchers("/notices").permitAll()
                    .antMatchers("/contact").permitAll()
                .and()
                .httpBasic();
    }

}
