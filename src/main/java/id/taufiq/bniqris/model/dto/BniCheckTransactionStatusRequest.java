package id.taufiq.bniqris.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BniCheckTransactionStatusRequest {
    private String requestId;
    private String billNumber;
    private String mid;
}
