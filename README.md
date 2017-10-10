## Demo Project for Java OAuth 2.0 Client using Apache OLTU

### application.properties
Add application.properties file under resources folder with the following fields

```
oauth.clientId=<your-client-id>
oauth.clientSecret=y<our-client-secret>
oauth.redirectUri=http://localhost:8080/redirect
oauth.authorizationLocation=https://accounts.google.com/o/oauth2/v2/auth
oauth.tokenLocation=https://www.googleapis.com/oauth2/v4/token
oauth.scope=profile openid
```