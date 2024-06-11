package com.arrow.Arrow.config;

import com.arrow.Arrow.component.JwtAuthenticationEntryPoint;
import com.arrow.Arrow.component.JwtRequestFilter;
import com.arrow.Arrow.component.TimeLimitFilter;
import com.arrow.Arrow.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;


@Configuration
public class SecurityConfigTeer {


    @Autowired
    private JwtAuthenticationEntryPoint point;
    @Autowired
    private JwtRequestFilter filter;

    @Autowired
    TimeLimitFilter timeLimitFilter;



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions().disable())
                .authorizeRequests()
                .requestMatchers("/**","/showMembers**","/css/**", "/js/**","/index**","/api/v1/login/**",

                        "/api/v1/register/**","/api/v1/userDashboard**","/signup","/login",

                        "/*.html**", "/*.js**","/*.css**","/homepage.html**","numberselect**",

                        "numberselect.html**","numberselect.css**").permitAll()
                .requestMatchers("/api/v1/members/**").hasAuthority("ADMIN")
                .requestMatchers("/api/v1/**","/transactions/**").authenticated()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                .and()
                .logout()
                    .permitAll()
                .and()
                .exceptionHandling(ex -> ex.authenticationEntryPoint(point))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(timeLimitFilter, JwtRequestFilter.class);
        return http.build();
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }

    @Bean
    public SecurityService securityService() {
        return new SecurityService();
    }

}