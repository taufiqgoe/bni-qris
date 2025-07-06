package id.taufiq.bniqris.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckTransactionStatusResponse {
    private String code;
    private String message;
    private String requestId;
    private String customerPan;
    private Double amount;
    private String transactionDatetime;
    private Double amountFee;
    private String rrn;
    private String billNumber;
    private String issuerCode;
    private String customerName;
    private String terminalId;
    private String merchantId;
    private String stan;
    private String merchantName;
    private String approvalCode;
    private String merchantPan;
    private String mcc;
    private String merchantCity;
    private String merchantCountry;
    private String currencyCode;
    private String paymentStatus;
    private String paymentDescription;
    private String additionalData;
}
