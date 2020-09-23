package enqs.customblog.config;

import enqs.customblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

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
                //ToDo: Redirect to current page of user
                .defaultSuccessUrl("/articles")
                .permitAll()
                .and()
                .logout()
                //ToDo: Redirect to current page of user
                .logoutSuccessUrl("/articles")
                .permitAll();
        //ToDo: Access-denied page
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }
}
