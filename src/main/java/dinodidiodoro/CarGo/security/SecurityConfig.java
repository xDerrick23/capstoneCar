package dinodidiodoro.CarGo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	JWTAuthFilter jwtFilter;
	
	@Autowired
	CorsFilter corsFilter;
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.cors(c -> c.disable());
		http.csrf(c -> c.disable());

		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(corsFilter, JWTAuthFilter.class);
		
		http.authorizeHttpRequests(auth -> {
		    auth.requestMatchers("/auth/**").permitAll(); 
		    auth.requestMatchers("/users/**").authenticated();
		    auth.requestMatchers("/cars").permitAll();
		    auth.requestMatchers(HttpMethod.GET,"/cars/**").permitAll();
		    auth.requestMatchers(HttpMethod.DELETE,"/cars/**").hasAuthority("ADMIN");
		    auth.requestMatchers(HttpMethod.PUT,"/cars/**").hasAuthority("ADMIN");
		    auth.requestMatchers(HttpMethod.POST,"/cars/**").hasAuthority("ADMIN");
		    auth.requestMatchers("/bookings/**").authenticated();
		    auth.requestMatchers("/payments/**").authenticated();
		});

		return http.build();
	}

	@Bean
	PasswordEncoder encoder() {
		return new BCryptPasswordEncoder(11);
	}
}