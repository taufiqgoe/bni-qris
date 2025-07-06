package id.taufiq.bniqris.service;

import id.taufiq.bniqris.config.BniAttribute;
import id.taufiq.bniqris.exception.InternalServerErrorException;
import id.taufiq.bniqris.model.dto.*;
import id.taufiq.bniqris.util.AccessTokenHelper;
import id.taufiq.bniqris.util.MessageBroker;
import id.taufiq.bniqris.util.SignatureHelper;
import id.taufiq.bniqris.util.UniqueIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class BniService {

    private final BniAttribute bniAttribute;
    private final RestClient restClient;
    private final AccessTokenHelper accessTokenHelper;
    private final MessageBroker messageBroker;
    private final SignatureHelper signatureHelper;

    public BniService(BniAttribute bniAttribute, RestClient restClient, AccessTokenHelper accessTokenHelper, MessageBroker messageBroker, SignatureHelper signatureHelper) {
        this.bniAttribute = bniAttribute;
        this.restClient = restClient;
        this.accessTokenHelper = accessTokenHelper;
        this.messageBroker = messageBroker;
        this.signatureHelper = signatureHelper;
    }

    public GenerateQrCodeResponse generateQrCode(Long amount) {
        LocalDateTime now = LocalDateTime.now();

        String endpoint = "/qr/generate-qr";
        String requestId = UniqueIdGenerator.generateRandomId();
        String qrExpired = now.plusMinutes(30).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        BniGenerateQrCodeRequest requestBody = new BniGenerateQrCodeRequest(
                requestId,
                bniAttribute.getMerchantId(),
                String.valueOf(amount),
                qrExpired);

        String signature = signatureHelper.hmacSHA512("%s:%s:%s".formatted(requestId, bniAttribute.getMerchantId(), qrExpired), bniAttribute.getSecretKey());

        ResponseEntity<BniGenerateQrCodeResponse> entity = restClient.post()
                .uri(endpoint)
                .body(requestBody)
                .header("Authorization", "Bearer " + accessTokenHelper.getAccessToken())
                .header("x-signature", signature)
                .retrieve()
                .toEntity(BniGenerateQrCodeResponse.class);

        BniGenerateQrCodeResponse body = entity.getBody();

        if (body == null) {
            throw new InternalServerErrorException("Failed to generate QR code: Response body is null. Please check the endpoint or request parameters.");
        }
        return new GenerateQrCodeResponse(body.getQrString(), body.getQrExpired().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public TransactionNotificationResponse transactionNotification(TransactionNotificationRequest requestBody, String requestSignature) {
        String signature = signatureHelper.hmacSHA512(
                "%s:%s:%s:%S:%S:%S:%S:%S:%S:%S:%S:%S:%S:%S:%S:%S:%S:%S:%S:%S".formatted(
                        requestBody.getRequestId(),
                        requestBody.getCustomerPan(),
                        requestBody.getAmount(),
                        requestBody.getTransactionDatetime(),
                        requestBody.getRrn(),
                        requestBody.getBillNumber(),
                        requestBody.getIssuerCode(),
                        requestBody.getCustomerName(),
                        requestBody.getTerminalId(),
                        requestBody.getMerchantId(),
                        requestBody.getStan(),
                        requestBody.getMerchantName(),
                        requestBody.getApprovalCode(),
                        requestBody.getMerchantPan(),
                        requestBody.getMcc(),
                        requestBody.getMerchantCity(),
                        requestBody.getMerchantCountry(),
                        requestBody.getCurrencyCode(),
                        requestBody.getPaymentStatus(),
                        requestBody.getPaymentDescription()),
                bniAttribute.getSecretKey());

        if (!signature.equals(requestSignature)) {
            throw new IllegalArgumentException("invalid signature");
        }

        messageBroker.send("bni.qris-transaction-notification", requestBody);
        log.info("Payment processed successfully for requestBody ID: {}", requestBody.getRequestId());
        return new TransactionNotificationResponse("00", "success");
    }

    public CheckTransactionStatusResponse checkTransactionStatus(String billNumber) {
        String endpoint = "/check-status/inquiry";
        String requestId = UniqueIdGenerator.generateRandomId();

        BniCheckTransactionStatusRequest requestBody = new BniCheckTransactionStatusRequest(
                requestId,
                billNumber,
                bniAttribute.getMerchantId());

        String signature = signatureHelper.hmacSHA512("%s:%s:%s".formatted(requestId, bniAttribute.getMerchantId(), billNumber), bniAttribute.getSecretKey());

        ResponseEntity<BniCheckTransactionStatusResponse> entity = restClient.post()
                .uri(endpoint)
                .body(requestBody)
                .header("Authorization", "Bearer " + accessTokenHelper.getAccessToken())
                .header("x-signature", signature)
                .retrieve()
                .toEntity(BniCheckTransactionStatusResponse.class);

        BniCheckTransactionStatusResponse bniCheckTransactionStatusResponse = entity.getBody();

        if (bniCheckTransactionStatusResponse == null) {
            throw new InternalServerErrorException("Failed to check transaction status: Response body is null. Please verify the endpoint, request parameters, or check if the transaction exists.");
        }

        return new CheckTransactionStatusResponse(
                bniCheckTransactionStatusResponse.getCode(),
                bniCheckTransactionStatusResponse.getMessage(),
                bniCheckTransactionStatusResponse.getRequestId(),
                bniCheckTransactionStatusResponse.getCustomerPan(),
                bniCheckTransactionStatusResponse.getAmount(),
                bniCheckTransactionStatusResponse.getTransactionDatetime(),
                bniCheckTransactionStatusResponse.getAmountFee(),
                bniCheckTransactionStatusResponse.getRrn(),
                bniCheckTransactionStatusResponse.getBillNumber(),
                bniCheckTransactionStatusResponse.getIssuerCode(),
                bniCheckTransactionStatusResponse.getCustomerName(),
                bniCheckTransactionStatusResponse.getTerminalId(),
                bniCheckTransactionStatusResponse.getMerchantId(),
                bniCheckTransactionStatusResponse.getStan(),
                bniCheckTransactionStatusResponse.getMerchantName(),
                bniCheckTransactionStatusResponse.getApprovalCode(),
                bniCheckTransactionStatusResponse.getMerchantPan(),
                bniCheckTransactionStatusResponse.getMcc(),
                bniCheckTransactionStatusResponse.getMerchantCity(),
                bniCheckTransactionStatusResponse.getMerchantCountry(),
                bniCheckTransactionStatusResponse.getCurrencyCode(),
                bniCheckTransactionStatusResponse.getPaymentStatus(),
                bniCheckTransactionStatusResponse.getPaymentDescription(),
                bniCheckTransactionStatusResponse.getAdditionalData()
        );
    }
}
