package id.taufiq.bniqris.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionNotificationRequest {
    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("customer_pan")
    private String customerPan;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("transaction_datetime")
    private String transactionDatetime;

    @JsonProperty("amount_fee")
    private String amountFee;

    @JsonProperty("rrn")
    private String rrn;

    @JsonProperty("bill_number")
    private String billNumber;

    @JsonProperty("issuer_code")
    private String issuerCode;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("terminal_id")
    private String terminalId;

    @JsonProperty("merchant_id")
    private String merchantId;

    @JsonProperty("stan")
    private String stan;

    @JsonProperty("merchant_name")
    private String merchantName;

    @JsonProperty("approval_code")
    private String approvalCode;

    @JsonProperty("merchant_pan")
    private String merchantPan;

    @JsonProperty("mcc")
    private String mcc;

    @JsonProperty("merchant_city")
    private String merchantCity;

    @JsonProperty("merchant_country")
    private String merchantCountry;

    @JsonProperty("currency_code")
    private String currencyCode;

    @JsonProperty("payment_status")
    private String paymentStatus;

    @JsonProperty("payment_description")
    private String paymentDescription;

    @JsonProperty("amount_mdr")
    private String amountMdr;

    @JsonProperty("billing_id")
    private String billingId;

    @JsonProperty("acquirer_code")
    private String acquirerCode;

    @JsonProperty("nmid")
    private String nmid;

    @JsonProperty("issuer_name")
    private String issuerName;

    @JsonProperty("acquirer_name")
    private String acquirerName;

    @JsonProperty("additional_data")
    private String additionalData;
}
