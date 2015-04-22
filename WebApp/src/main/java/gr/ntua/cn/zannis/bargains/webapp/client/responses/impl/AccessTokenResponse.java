package gr.ntua.cn.zannis.bargains.webapp.client.responses.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.webapp.client.misc.Const;
import gr.ntua.cn.zannis.bargains.webapp.client.misc.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * The class that wraps the response given from an access token request.
 *
 * @author zannis <zannis.kal@gmail.com>
 */
public class AccessTokenResponse {

    private final Logger log = LoggerFactory.getLogger(AccessTokenResponse.class);

    private String token;
    private String tokenType;
    private Long expiresIn;
    private String refreshToken;
    private String error;
    private String errorDescription;

    @JsonCreator
    public AccessTokenResponse(@JsonProperty("access_token") String token,
                               @JsonProperty("token_type") String tokenType,
                               @JsonProperty("expires_in") Long expiresIn,
                               @JsonProperty("refresh_token") String refreshToken,
                               @JsonProperty("error") String error,
                               @JsonProperty("error_description") String errorDescription) {
        this.token = token;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.error = error;
        this.errorDescription = errorDescription;

        saveToPropertiesFile(Const.TOKEN_FILENAME);
    }

    public AccessTokenResponse() {
    }

    /**
     * Checks if this {@link AccessTokenResponse} object is valid and proceeds to save it
     * in the given file.
     *
     * @param propFileName The destination properties filename.
     */
    public void saveToPropertiesFile(String propFileName) {
        if (!(this.token == null || this.token.isEmpty())) {
            Properties properties = new Properties();
            properties.setProperty("access_token", token);
            properties.setProperty("token_type", tokenType);
            properties.setProperty("expires_in", String.valueOf(expiresIn));
            Utils.savePropertiesToFile(properties, propFileName);
            log.debug("Token saved successfully!");
        } else {
            log.debug("Invalid token given. Nothing was saved");
        }
    }

    @JsonProperty
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @JsonProperty
    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    @JsonProperty
    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @JsonProperty
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @JsonProperty
    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    @JsonProperty
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
