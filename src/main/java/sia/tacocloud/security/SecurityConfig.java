package sia.tacocloud.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Allow access to design tacos and place orders only for authenticated users with ROLE_USER
                        .requestMatchers("/design", "/orders", "/orders/**").hasRole("USER")
                        // Allow access to H2 console (if you're using it for development)
                        // IMPORTANT: Disable or secure properly for production!
                        .requestMatchers("/h2-console/**").permitAll()
                        // Allow access to static resources, homepage, registration, and login for everyone
                        .requestMatchers("/", "/images/**", "/styles.css", "/register", "/login").permitAll()
                        // Any other request must be authenticated
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login") // Specify your custom login page URL
                        .defaultSuccessUrl("/design", true) // Redirect to /design after successful login
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/") // Redirect to homepage after logout
                        .permitAll()
                )
                // For H2 console to work, you might need to disable CSRF for its path
                // and allow framing. For development only!
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // Allow H2 console to be embedded in a frame
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Or any other encoder
    }

}