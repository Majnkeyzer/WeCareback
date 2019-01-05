package bios.springframework.spring5webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import bios.springframework.spring5webapp.auth.CustomSimpleUrlAuthenticationSuccessHandler;
import bios.springframework.spring5webapp.auth.RestAuthenticationEntryPoint;

import javax.sql.DataSource;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private CustomSimpleUrlAuthenticationSuccessHandler mySuccessHandler = new CustomSimpleUrlAuthenticationSuccessHandler();
    private SimpleUrlAuthenticationFailureHandler myFailureHandler = new SimpleUrlAuthenticationFailureHandler();

    @Autowired
    private CorsFilter corsFilter;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .usersByUsernameQuery("select username,password from auth where username=?");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
             .addFilterBefore(corsFilter, ChannelProcessingFilter.class)
             .csrf().disable()
             .exceptionHandling()
             .authenticationEntryPoint(restAuthenticationEntryPoint)
             .and()
             .authorizeRequests()
             .antMatchers("/login").permitAll()
             .and()
             .formLogin()
             .successHandler(mySuccessHandler)
             .failureHandler(myFailureHandler)
             .and()
             .logout();
    }

    private String getUserQuery() {
        return "SELECT username, password"
                + "FROM auth "
                + "WHERE username = username";
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
