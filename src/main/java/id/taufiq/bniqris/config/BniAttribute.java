package id.taufiq.bniqris.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "bni")
public class BniAttribute {
    private String baseUrl;
    private String clientId;
    private String clientSecret;
    private String merchantId;
    private String secretKey;
    private boolean logRequest = false;
}
