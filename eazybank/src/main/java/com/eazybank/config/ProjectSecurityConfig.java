package com.eazybank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

@Configuration
public class ProjectSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
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
                .authorizeRequests()
                    .antMatchers("/myAccount").authenticated()
                    .antMatchers("/myBalance").authenticated()
                    .antMatchers("/myLoans").authenticated()
                    .antMatchers("/myCards").authenticated()
                    .antMatchers("/notices").permitAll()
                    .antMatchers("/contact").permitAll()
                .and()
                .formLogin()
                .and()
                .httpBasic();
    }

}
