package com.example.uam.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import com.example.uam.filter.CORSFilterWebLogicFix;
import com.example.uam.filter.SimpleAuthenticationFilter;
import com.example.uam.security.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{

    /**
     *
     * @return Authentication trust provider used by Spring Security
     */
    @Bean
   // @Lazy(true)
    public AuthenticationTrustResolver getAuthenticationTrustResolver() {
        return new AuthenticationTrustResolverImpl();
    }
     
    

    @Autowired
    @Qualifier("customUserDetailsService")
    CustomUserDetailsService userDetailsService;
    

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(userDetailsService);
    } 

    protected void configure(HttpSecurity http) throws Exception {
        http.logout()
                .invalidateHttpSession(true)
                .deleteCookies("SESSION")
                .clearAuthentication(true)
                .logoutSuccessHandler((new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)));

        http.authorizeRequests()
                .antMatchers("/resources/**").permitAll()

                .antMatchers("/", "/dashboard", "/edituser", "/editusertorole", "/removeusertorole", "/enableuser-*", "/diasableuser-*", "/edituserdetails-*", "/approveuserdetails-*", "/approveeditrole", "/authusertorole", "/createuser", "/authoriseuser", "/modifyUser",
                   "/approveenableuser", "/approvedisableuser", "/authpenduser-*", "/rejpenduser-*", "loadcreateuser", "/createuser", "/createusertorole")
                .access("hasRole('ADMIN')")
                .antMatchers("/*").access("hasRole('ADMIN')")
               
                .anyRequest().authenticated()
                .and().formLogin().loginPage("/login").permitAll()

                .defaultSuccessUrl("/dashboard").failureUrl("/login?error")
                .usernameParameter("ssoId").passwordParameter("password")
                .and().exceptionHandling().accessDeniedPage("/Access_Denied").and().sessionManagement().invalidSessionUrl("/login").maximumSessions(1);

        http.authorizeRequests().antMatchers("/resources/**").permitAll().anyRequest().permitAll();
    }
}
