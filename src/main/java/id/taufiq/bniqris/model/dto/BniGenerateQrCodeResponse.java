package id.taufiq.bniqris.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BniGenerateQrCodeResponse {
    @JsonProperty("code")
    private String code;
    @JsonProperty("message")
    private String message;
    @JsonProperty("qr_string")
    private String qrString;
    @JsonProperty("qr_expired")
    private LocalDateTime qrExpired;
    @JsonProperty("bill_number")
    private String billNumber;
    @JsonProperty("nmid")
    private String nmid;
    @JsonProperty("acquire_name")
    private String acquireName;
    @JsonProperty("acquire_code")
    private String acquireCode;
}
