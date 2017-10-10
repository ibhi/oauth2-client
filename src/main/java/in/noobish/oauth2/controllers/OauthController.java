package in.noobish.oauth2.controllers;

import java.io.IOException;


import in.noobish.oauth2.OauthHttpClient.OAuthOkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import javax.servlet.http.HttpServletResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;

@RestController
public class OauthController {

    @Value("${oauth.clientId}")
    private String clientId;

    @Value("${oauth.clientSecret}")
    private String clientSecret;

    @Value("${oauth.redirectUri}")
    private String redirectUri;

    @Value("${oauth.authorizationLocation}")
    private String authorizationLocation;

    @Value("${oauth.tokenLocation}")
    private String tokenLocation;

    @Value("${oauth.scope}")
    private String scope;

    private final String RESPONSE_TYPE = "code";

    @RequestMapping("/")
    public void getAuthCode(final HttpServletResponse response) throws OAuthSystemException, IOException {

        OAuthClientRequest request = OAuthClientRequest
                .authorizationLocation(authorizationLocation)
                .setClientId(clientId)
                .setScope(scope)
                .setRedirectURI(redirectUri)
                .setResponseType(RESPONSE_TYPE)
                .buildQueryMessage();

        response.sendRedirect(request.getLocationUri());
    }

    @RequestMapping("/redirect")
    public String getAccessToken(@RequestParam("code") String authCode) throws OAuthSystemException, OAuthProblemException {

        OAuthClientRequest request = OAuthClientRequest
                .tokenLocation(tokenLocation)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectURI(redirectUri)
                .setCode(authCode)
                .buildBodyMessage();

        OAuthClient oAuthClient = new OAuthClient(new OAuthOkHttpClient());

        OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(request, OAuthJSONAccessTokenResponse.class);

        System.out.println(
                "Access Token: " + oAuthResponse.getAccessToken() + ", Expires in: " + oAuthResponse
                        .getExpiresIn());
        return oAuthResponse.getAccessToken();
    }
}
