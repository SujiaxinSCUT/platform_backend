package com.trace.platform.oauth2;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Profile("simple")
@Configuration
@EnableResourceServer
public class CustomResourceServerConfigurer extends ResourceServerConfigurerAdapter {

    public static final String RESOURCE_ID = "platform";

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.requestMatchers().antMatchers("/trace/**")
                .and()
                .authorizeRequests()
                .antMatchers("/trace/user/**")
                .hasAuthority("ROLE_USER")
                .and()
                .authorizeRequests()
                .antMatchers("/trace/admin/fund/**")
                .hasAuthority("ROLE_ADMIN_FUND")
                .and()
                .authorizeRequests()
                .antMatchers("/trace/admin/quality/**")
                .hasAuthority("ROLE_ADMIN_QUALITY")
                .and()
                .authorizeRequests()
                .antMatchers("/oauth/**").permitAll()
                .and()
                .cors()
                .and()
                .csrf()
                .disable();;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        super.configure(resources);
        resources.resourceId(RESOURCE_ID);
    }
}
