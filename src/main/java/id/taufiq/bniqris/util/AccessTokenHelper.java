package id.taufiq.bniqris.util;

import id.taufiq.bniqris.config.BniAttribute;
import id.taufiq.bniqris.exception.InternalServerErrorException;
import id.taufiq.bniqris.model.dto.BniGetAccessTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
public class AccessTokenHelper {

    private final BniAttribute bniAttribute;
    private final RestClient restClient;

    private String accessToken = null;
    private String refreshToken = null;

    private LocalDateTime accessTokenExpiresAt = null;

    public AccessTokenHelper(BniAttribute bniAttribute, RestClient restClient) {
        this.bniAttribute = bniAttribute;
        this.restClient = restClient;
        generateAccessToken();
    }

    public synchronized String getAccessToken() {
        LocalDateTime now = LocalDateTime.now();
        if (this.accessToken == null || this.refreshToken == null) {
            generateAccessToken();
        } else if (now.isAfter(this.accessTokenExpiresAt)) {
            refreshAccessToken();
        }

        return this.accessToken;
    }

    private void generateAccessToken() {
        String endpoint = "/auth/get-token";
        Map<String, String> requestBody = Map.of(
                "grant_type", "password",
                "username", bniAttribute.getUsername(),
                "password", bniAttribute.getPassword());

        String credential = "%s:%s".formatted(bniAttribute.getClientId(), bniAttribute.getClientSecret());
        String base64Credential = Base64.getEncoder().encodeToString(credential.getBytes(StandardCharsets.UTF_8));

        LocalDateTime now = LocalDateTime.now();

        ResponseEntity<BniGetAccessTokenResponse> entity = restClient.post()
                .uri(endpoint)
                .body(requestBody)
                .header("Authorization", "Basic " + base64Credential)
                .retrieve()
                .toEntity(BniGetAccessTokenResponse.class);

        BniGetAccessTokenResponse body = entity.getBody();
        if (body == null)
            throw new InternalServerErrorException("Failed to get access token: Response body is null. Please check the endpoint or request parameters.");

        log.info("Generated new access token");

        this.accessToken = body.getAccessToken();
        this.accessTokenExpiresAt = now.plusSeconds(Integer.parseInt(body.getExpiresIn()));
        this.refreshToken = body.getRefreshToken();
    }

    private void refreshAccessToken() {
        String endpoint = "/auth/refresh-token";
        Map<String, String> requestBody = Map.of(
                "grant_type", "refresh_token",
                "refresh_token", this.refreshToken);

        LocalDateTime now = LocalDateTime.now();

        ResponseEntity<BniGetAccessTokenResponse> entity = restClient.post()
                .uri(endpoint)
                .body(requestBody)
                .header("Authorization", "Bearer " + this.accessToken)
                .retrieve()
                .toEntity(BniGetAccessTokenResponse.class);

        BniGetAccessTokenResponse body = entity.getBody();
        if (body == null)
            throw new InternalServerErrorException("Failed to refresh access token: Response body is null. Please check the endpoint or request parameters.");

        log.info("Refresh access token");

        this.accessToken = body.getAccessToken();
        this.accessTokenExpiresAt = now.plusSeconds(Integer.parseInt(body.getExpiresIn()));
        this.refreshToken = body.getRefreshToken();
    }
}
