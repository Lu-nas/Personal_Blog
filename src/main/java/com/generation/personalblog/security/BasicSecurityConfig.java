package com.generation.personalblog.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class BasicSecurityConfig {

	@Autowired
	private JwtAuthFilter authFilter;

	// acessa  e espera um criptografia dos dados internos
	@Bean
	UserDetailsService userDetailsService() {
		
		return new UserDetailsServiceImpl();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// provedor de autenticação // auteração parametros
	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
// implementa a config da interface autenticação e confere se user é valido ou nao
	@Bean
	AuthenticationManager authenticationManeger(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
// substitui o modelo padrao de autenticação do sprin security do formulario em tela
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	    http
	            .sessionManagement(management -> management
	                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            .csrf(csrf -> csrf.disable())
	            .cors(withDefaults());

	    http
	            .authorizeHttpRequests((auth) -> auth
	            		.requestMatchers("/usuarios/ **").permitAll()
	            		.requestMatchers("/usuarios/{id}").permitAll()
	            		.requestMatchers("/usuarios/all").permitAll()
	            		.requestMatchers("/usuarios/atualizar").permitAll()
	                    .requestMatchers("/usuarios/logar").permitAll()
	                    .requestMatchers("/usuarios/cadastrar").permitAll()
	                    .requestMatchers("/error/**").permitAll()
	                    .requestMatchers(HttpMethod.OPTIONS).permitAll()
	                    .anyRequest().authenticated())
	            .authenticationProvider(authenticationProvider())
	            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
	            .httpBasic(withDefaults());

	    return http.build();
	}
}
