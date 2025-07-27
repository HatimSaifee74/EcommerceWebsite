package com.ecom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig{
    @Autowired
	AuthenticationSuccessHandler successHandler;
    @Autowired
    @Lazy
    AutenticationFailureHandler failureHandler;
	
    private final UserDetailsSeviceImpl userDetailsSeviceImpl;

    SecurityConfig(UserDetailsSeviceImpl userDetailsSeviceImpl) {
        this.userDetailsSeviceImpl = userDetailsSeviceImpl;
    }

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
		}
	@Bean
	public UserDetailsService userDetailsService()	{
		return new UserDetailsSeviceImpl();
	}
	@Bean
	public DaoAuthenticationProvider authenticationProvider()
	{
		DaoAuthenticationProvider authentication=new DaoAuthenticationProvider();
		authentication.setPasswordEncoder(passwordEncoder());
		authentication.setUserDetailsService(userDetailsService());
		return authentication;
		
	}
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf->csrf.disable()).cors(cors->cors.disable()).authorizeHttpRequests(req->req.requestMatchers("/user/**").hasRole("USER").requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/**").permitAll()).formLogin(form->form.loginPage("/signin").loginProcessingUrl("/login").successHandler(successHandler).failureHandler(failureHandler)).logout(logout->logout.permitAll());
		
		return http.build();
	}
}
