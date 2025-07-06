package id.taufiq.bniqris.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BniGenerateQrCodeRequest {
    @JsonProperty("request_id")
    private String requestId;
    @JsonProperty("merchant_id")
    private String merchantId;
    @JsonProperty("amount")
    private String amount;
    @JsonProperty("qr_expired")
    private String qrExpired;
}
