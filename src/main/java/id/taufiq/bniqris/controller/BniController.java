package id.taufiq.bniqris.controller;

import id.taufiq.bniqris.model.dto.CheckTransactionStatusResponse;
import id.taufiq.bniqris.model.dto.GenerateQrCodeResponse;
import id.taufiq.bniqris.model.dto.TransactionNotificationRequest;
import id.taufiq.bniqris.model.dto.TransactionNotificationResponse;
import id.taufiq.bniqris.service.BniService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("bni-qris")
public class BniController {

    private final BniService bniService;

    public BniController(BniService bniService) {
        this.bniService = bniService;
    }

    @PostMapping("generate-qr-code")
    public GenerateQrCodeResponse generateQrCode(Long amount) {
        return bniService.generateQrCode(amount);
    }

    @PostMapping("transaction-notification")
    public TransactionNotificationResponse transactionNotification(TransactionNotificationRequest requestBody, HttpServletRequest request) {
        String signature = getSignatureOrThrow(request);
        return bniService.transactionNotification(requestBody, signature);
    }

    @PostMapping("check-transaction-status")
    public CheckTransactionStatusResponse checkTransactionStatus(String billNumber) {
        return bniService.checkTransactionStatus(billNumber);
    }

    private String getSignatureOrThrow(HttpServletRequest request) {
        try {
            return request.getHeader("x-signature");
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid x-signature");
        }
    }
}
