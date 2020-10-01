package enqs.customblog.config;

import enqs.customblog.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final PasswordEncoder passwordEncoder;

    public SpringSecurityConfig(UserService userService, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/articles/new").hasAnyRole("ADMIN", "WRITER")
                .antMatchers("/articles/edit").hasAnyRole("ADMIN", "WRITER")
                .antMatchers("/articles/save").hasAnyRole("ADMIN", "WRITER")
                .antMatchers("/articles/delete").hasAnyRole("ADMIN", "WRITER")
                .antMatchers("/**").permitAll()
                .and()
                .formLogin()
                .loginProcessingUrl("/authenticateUser")
                .successHandler(customAuthenticationSuccessHandler)
                .permitAll()
                .and()
                .logout()
                //ToDo: Redirect to current page of user
                .logoutSuccessUrl("/articles")
                .permitAll();
        //ToDo: Access-denied page
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }
}
