package id.taufiq.bniqris.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BniTransactionNotificationResponse {
    private String code;
    private String message;
}
