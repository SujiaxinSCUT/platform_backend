package com.trace.platform.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;


@Configuration
@EnableAuthorizationServer
public class CustomServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("client_user")  //客户端名称
                .secret(passwordEncoder.encode("platform"))  //客户端密码
                .authorizedGrantTypes("password","refresh_token")  //密码模式
                .scopes("select")  //授权范围
                .resourceIds(CustomResourceServerConfigurer.RESOURCE_ID)  //资源服务器的id，这个在资源服务器里有配置。
                .accessTokenValiditySeconds(1800)  //有效时间
                .refreshTokenValiditySeconds(50000)
                .and()
                .withClient("client_admin")  //客户端名称
                .secret(passwordEncoder.encode("platform"))  //客户端密码
                .authorizedGrantTypes("password","refresh_token")  //密码模式
                .scopes("select")  //授权范围
                .resourceIds(CustomResourceServerConfigurer.RESOURCE_ID)  //资源服务器的id，这个在资源服务器里有配置。
                .accessTokenValiditySeconds(1800)  //有效时间
                .refreshTokenValiditySeconds(50000);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients();
        super.configure(security);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager);
        super.configure(endpoints);
    }
}
