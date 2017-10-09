package in.noobish.oauth2.controllers;

import java.io.IOException;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuthProviderType;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import javax.servlet.http.HttpServletResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;

@RestController
public class OauthController {

    @Value("${oauth2.clientId}")
    private String clientId;

    @Value("${oauth2.clientSecret}")
    private String clientSecret;

    @RequestMapping("/")
    public void getAuthCode(HttpServletResponse response) throws OAuthSystemException, IOException {

        OAuthClientRequest request = OAuthClientRequest
                    .authorizationProvider(OAuthProviderType.GOOGLE)
                    .setClientId(clientId)
                    .setScope("profile openid")
                    .setRedirectURI("http://localhost:8080/redirect")
                    .setResponseType("code")
                    .buildQueryMessage();

            response.sendRedirect(request.getLocationUri());
    }

    @RequestMapping("/redirect")
    public String getAccessToken(@RequestParam("code") String authCode) throws OAuthSystemException, OAuthProblemException {

        OAuthClientRequest request = OAuthClientRequest
                .tokenProvider(OAuthProviderType.GOOGLE)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectURI("http://localhost:8080/redirect")
                .setCode(authCode)
                .buildBodyMessage();

        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

        //Facebook is not fully compatible with OAuth 2.0 draft 10, access token response is
        //application/x-www-form-urlencoded, not json encoded so we use dedicated response class for that
        //Own response class is an easy way to deal with oauth providers that introduce modifications to
        //OAuth specification
        OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(request, OAuthJSONAccessTokenResponse.class);

        System.out.println(
                "Access Token: " + oAuthResponse.getAccessToken() + ", Expires in: " + oAuthResponse
                        .getExpiresIn());
        return oAuthResponse.getAccessToken();
    }
}
