package enqs.customblog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/articles/new").hasAnyRole("ADMIN")
                .antMatchers("/articles/edit").hasAnyRole("ADMIN")
                .antMatchers("/articles/save").hasAnyRole("ADMIN")
                .antMatchers("/articles/delete").hasAnyRole("ADMIN")
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
}
