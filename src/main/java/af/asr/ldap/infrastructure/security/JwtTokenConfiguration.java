package af.asr.ldap.infrastructure.security;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("tokenEnhancer")
public class JwtTokenConfiguration implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(
            OAuth2AccessToken accessToken,
            OAuth2Authentication authentication) {

        Map<String, Object> additionalInfo = new HashMap<>();
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}