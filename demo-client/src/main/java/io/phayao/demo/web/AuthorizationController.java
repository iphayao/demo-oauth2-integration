package io.phayao.demo.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.server.HttpServerRequest;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@Controller
public class AuthorizationController {
    private final WebClient webClient;
    private final String messageBaseUri;

    public AuthorizationController(WebClient webClient, @Value("${message.base-uri}") String messageBaseUri) {
        this.webClient = webClient;
        this.messageBaseUri = messageBaseUri;
    }

    @GetMapping(value = "/authorize", params = "grant_type=authorization_code")
    public String authorizedCodeGrant(Model model,
                                      @RegisteredOAuth2AuthorizedClient("messaging-client-authorization-code") OAuth2AuthorizedClient authorizedClient) {
        String[] messages = webClient.get()
                .uri(this.messageBaseUri)
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(String[].class)
                .block();
        System.out.println(messages.length);
        model.addAttribute("messages", messages);
        return "index";
    }

    @GetMapping(value = "/authorize", params = "grant_type=client_credentials")
    public String clientCredentialGrant(Model model) {
        String[] messages = webClient.get()
                .uri(this.messageBaseUri)
                .attributes(clientRegistrationId("messaging-client-client-credentials"))
                .retrieve()
                .bodyToMono(String[].class)
                .block();
        System.out.println(messages.length);
        model.addAttribute("messages", messages);
        return "index";
    }

    @GetMapping(value = "/authorized", params = OAuth2ParameterNames.ERROR)
    public String authorizationFailed(Model model, HttpServerRequest request) {
        String errorCode = request.param(OAuth2ParameterNames.ERROR);
        if(StringUtils.hasText(errorCode)) {
            model.addAttribute("error",
                    new OAuth2Error(
                            errorCode,
                            request.param(OAuth2ParameterNames.ERROR_DESCRIPTION),
                            request.param(OAuth2ParameterNames.ERROR_URI)
                    ));
        }
        return "index";
    }

}
