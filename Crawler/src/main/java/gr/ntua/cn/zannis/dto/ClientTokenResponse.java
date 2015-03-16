package gr.ntua.cn.zannis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by zannis on 3/15/15.
 */
public class ClientTokenResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;

    @JsonProperty("access_token")
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @JsonProperty("token_type")
    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    @JsonProperty("expires_in")
    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
