package io.phayao.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.keys.KeyManager;
import org.springframework.security.crypto.keys.StaticKeyGeneratingKeyManager;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.UUID;

@EnableWebSecurity
public class AuthorizationServerConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
    }

    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("messaging-client")
                .clientSecret("secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://localhost:8080/authorized")
                .scope("message.read")
                .scope("message.write")
                .clientSettings(clientSettings -> clientSettings.requireUserConsent(true))
                .build();
        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    @Bean
    public KeyManager keySource() {
        return new StaticKeyGeneratingKeyManager();
    }

    @Bean
    @Primary
    public UserDetailsService users() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("P@ssw0rd")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

}
