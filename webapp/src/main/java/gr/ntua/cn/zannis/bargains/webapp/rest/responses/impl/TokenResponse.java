package gr.ntua.cn.zannis.bargains.webapp.rest.responses.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.webapp.rest.misc.Const;
import gr.ntua.cn.zannis.bargains.webapp.rest.misc.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * The class that wraps the response given from an access token request.
 *
 * @author zannis <zannis.kal@gmail.com>
 */
public class TokenResponse {

    private final Logger log = LoggerFactory.getLogger(TokenResponse.class);

    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String error;
    private String errorDescription;
    private boolean valid;

    @JsonCreator
    public TokenResponse(@JsonProperty("access_token") String accessToken,
                         @JsonProperty("token_type") String tokenType,
                         @JsonProperty("expires_in") Long expiresIn,
                         @JsonProperty("error") String error,
                         @JsonProperty("error_description") String errorDescription) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.error = error;
        this.errorDescription = errorDescription;

        this.setValid(!(this.accessToken == null || this.accessToken.isEmpty()));

        if (this.isValid()) {
            saveToPropertiesFile(Const.TOKEN_FILENAME);
        }
    }

    public TokenResponse() {
    }

    /**
     * Checks if this {@link TokenResponse} object is valid and proceeds to save it
     * in the given file.
     *
     * @param propFileName The destination properties filename.
     */
    public void saveToPropertiesFile(String propFileName) {
        if (this.isValid()) {
            Properties properties = new Properties();
            properties.setProperty("access_token", accessToken);
            properties.setProperty("token_type", tokenType);
            properties.setProperty("expires_in", String.valueOf(expiresIn));
            Utils.savePropertiesToFile(properties, propFileName);
            log.debug("Token saved successfully!");
        } else {
            log.debug("Invalid token given. Nothing was saved");
        }
    }

    @JsonProperty
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
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

    @JsonIgnore
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @JsonIgnore
    public boolean checkValid() {
        this.setValid(this.accessToken != null && this.tokenType != null && this.expiresIn != null);
        return this.isValid();
    }
}
