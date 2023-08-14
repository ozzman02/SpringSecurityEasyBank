package com.eazybytes.config;

import com.eazybytes.filter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;


@EnableMethodSecurity
@Configuration
public class ProjectSecurityConfig {

    /*@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }*/

    /*
        1. For GET Methods Spring Security will not provide any CSRF protection that's the reason of ignoring
            /notices from .ignoringRequestMatchers("/contact", "/register").

        2. Previously, whenever we are trying to access the APIs through the browser, we used to directly access these backend REST APIs
           through browser and there used to be a login page which is built up by the Spring Security framework, and we enter our credentials
           and behind the scenes it is going to create a JSESSIONID, and using the same JSESSIONID, we try to access all the subsequent requests
           without entering credentials.

           But now onwards, since we are going to use a separate UI application to log in and access all the REST APIs, we need to let Spring Security framework,
           that, "Please create the JSESSIONID by following this sessionManagement that I have created here." So with this configuration we are telling
           to the Spring Security framework, "Please always create the JSESSIONID after the initial login is completed."

           The same JSESSIONID it is going to send to the UI application and my UI application can leverage the same for all the subsequent requests that it
           is going to make after the initial login. Without these two lines, you have to share the credentials every time you are trying to access the secured API
           from your Angular application.

           Using this requireExplicitSave, we are telling to the Spring Security framework, "I'm not going to take the responsibility of saving the authentication details
           inside the SecurityContextHolder. By default, this value is true. By overriding these defaults we are giving this responsibility of generating the JSESSIONID
           and storing the authentication details into the SecurityContextHolder to the framework.

           Because with these changes, only backend application is capable of sending the CsrfToken values to the UI application and the UI application also has to accept
           the CsrfToken value, and it has to send the same CsrfToken value for every subsequent request that it is going to make.

        3. Spring Security is going to send a cookie with the name "XSRF-TOKEN". UI needs to read it when the user logins. Check CookieCsrfTokenRepository class.

        4. With SessionCreationPolicy we are telling the Spring Framework to not generate any JSESSIONID because
           I'm taking care of everything.

           .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        5. Since Keycloak has been implemented we are no longer handling token creation. We do not need the filters on the CSRF filter.
           AuthenticationProvider is no longer needed.
           PasswordEncoder bean is no longer required.
           .formLogin(Customizer.withDefaults()) no longer required
           .httpBasic(Customizer.withDefaults()); no longer required

    */
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        http
                /*.securityContext((context) -> context.requireExplicitSave(false))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))*/
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.setAllowCredentials(true);
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setExposedHeaders(List.of("Authorization"));
                    config.setMaxAge(3600L);
                    return config;
                })).csrf((csrf) -> csrf.csrfTokenRequestHandler(requestHandler)
                        .ignoringRequestMatchers("/contact", "/register")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                /*.addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class)
                .addFilterAt(new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class)*/
                .authorizeHttpRequests((requests) -> requests
                        /*.requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards", "/user").authenticated()
                        .requestMatchers("/myAccount").hasAuthority("VIEWACCOUNT")
                        .requestMatchers("/myBalance").hasAnyAuthority("VIEWACCOUNT", "VIEWBALANCE")
                        .requestMatchers("/myLoans").hasAuthority("VIEWLOANS")
                        .requestMatchers("/myCards").hasAuthority("VIEWCARDS")
                        .requestMatchers("/user").authenticated()
                        .requestMatchers("/myAccount").hasRole("USER")
                        .requestMatchers("/myBalance").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/myLoans").hasRole("USER")
                        .requestMatchers("/myCards").hasRole("USER")
                        .requestMatchers("/user").authenticated()
                        .requestMatchers("/notices", "/contact", "/register").permitAll())*/
                        .requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards", "/user").authenticated()
                        .requestMatchers("/notices", "/contact", "/register").permitAll())
                /*.formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());*/
                .oauth2ResourceServer(oauth2ResourceServerCustomizer ->
                        oauth2ResourceServerCustomizer.jwt(jwtCustomizer ->
                                jwtCustomizer.jwtAuthenticationConverter(jwtAuthenticationConverter)));
        return http.build();
    }

    /**
     *  Configuration to deny all the requests
     */
        /*
            SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
                http.authorizeHttpRequests(requests -> requests.anyRequest().denyAll())
                    .formLogin(Customizer.withDefaults())
                    .httpBasic(Customizer.withDefaults());
                return http.build();
            }
        */

    /**
     *  Configuration to permit all the requests
     */
        /* SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
                http.authorizeHttpRequests(requests -> requests.anyRequest().permitAll())
                    .formLogin(Customizer.withDefaults())
                    .httpBasic(Customizer.withDefaults());
                return http.build();
             }
        */

}
