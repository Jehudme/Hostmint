package com.hostmint.app.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.hostmint.app.security.*;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer; // <--- The modern Vaadin 25 class
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;
import tech.jhipster.config.JHipsterConstants;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private final Environment env;
    private final JHipsterProperties jHipsterProperties;
    private final RememberMeServices rememberMeServices;

    public SecurityConfiguration(Environment env, RememberMeServices rememberMeServices, JHipsterProperties jHipsterProperties) {
        this.env = env;
        this.rememberMeServices = rememberMeServices;
        this.jHipsterProperties = jHipsterProperties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(withDefaults());

        // 1. JHipster API & Management Rules
        http.authorizeHttpRequests(authz ->
            authz
                .requestMatchers("/vaadinServlet/**")
                .permitAll()
                .requestMatchers("/VAADIN/**")
                .permitAll()
                .requestMatchers("/api/authenticate")
                .permitAll()
                .requestMatchers("/api/register")
                .permitAll()
                .requestMatchers("/api/activate")
                .permitAll()
                .requestMatchers("/api/account/reset-password/init")
                .permitAll()
                .requestMatchers("/api/account/reset-password/finish")
                .permitAll()
                .requestMatchers("/api/admin/**")
                .hasAuthority(AuthoritiesConstants.ADMIN)
                .requestMatchers("/api/**")
                .authenticated()
                .requestMatchers("/v3/api-docs/**")
                .hasAuthority(AuthoritiesConstants.ADMIN)
                .requestMatchers("/management/health")
                .permitAll()
                .requestMatchers("/management/health/**")
                .permitAll()
                .requestMatchers("/management/info")
                .permitAll()
                .requestMatchers("/management/prometheus")
                .permitAll()
                .requestMatchers("/management/**")
                .hasAuthority(AuthoritiesConstants.ADMIN)
        );

        // 2. Allow iframes for the Copilot Visual Editor
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        // 3. JHipster Exception Handling and Remember Me
        http.exceptionHandling(exceptionHandling -> {
            AntPathMatcher pathMatcher = new AntPathMatcher();
            RequestMatcher apiRequestMatcher = request -> pathMatcher.match("/api/**", request.getRequestURI());
            exceptionHandling.defaultAuthenticationEntryPointFor(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                new OrRequestMatcher(apiRequestMatcher)
            );
        });

        http.rememberMe(rememberMe ->
            rememberMe
                .rememberMeServices(rememberMeServices)
                .rememberMeParameter("remember-me")
                .key(jHipsterProperties.getSecurity().getRememberMe().getKey())
        );

        // 4. H2 Console bypass in dev mode
        if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT))) {
            http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .authorizeHttpRequests(authz -> authz.requestMatchers("/h2-console/**").permitAll());
        }

        // ====================================================================
        // THE OFFICIAL VAADIN 25 SECURITY CONFIGURER
        // This injects the proper final 'anyRequest()' routing, disables CSRF
        // exclusively for Vaadin internal endpoints, and provides a default login.
        // ====================================================================
        http.with(VaadinSecurityConfigurer.vaadin(), vaadin -> {
            // Once you create a custom login view with @Route("login"), you can uncomment this:
            // vaadin.loginView("/login");
        });

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web ->
            web
                .ignoring()
                .requestMatchers(
                    "/VAADIN/**",
                    "/favicon.ico",
                    "/robots.txt",
                    "/manifest.webmanifest",
                    "/sw.js",
                    "/offline.html",
                    "/line-awesome/**",
                    "/frontend/**"
                );
    }
}
