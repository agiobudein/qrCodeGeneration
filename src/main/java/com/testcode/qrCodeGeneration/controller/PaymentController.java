package com.testcode.qrCodeGeneration.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.testcode.qrCodeGeneration.dto.PaymentRequest;
import com.testcode.qrCodeGeneration.dto.PaymentResponse;
import com.testcode.qrCodeGeneration.dto.ProcessPaymentRequest;
import com.testcode.qrCodeGeneration.model.Merchant;
import com.testcode.qrCodeGeneration.model.PaymentTransaction;
import com.testcode.qrCodeGeneration.model.User;
import com.testcode.qrCodeGeneration.repository.MerchantRepository;
import com.testcode.qrCodeGeneration.repository.PaymentTransactionRepository;
import com.testcode.qrCodeGeneration.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final PaymentTransactionRepository transactionRepository;

    public PaymentController(UserRepository userRepository,
                             MerchantRepository merchantRepository,
                             PaymentTransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping("/generate-qr")
    public ResponseEntity<PaymentResponse> generateQR(@RequestBody PaymentRequest request) throws Exception {
        // Create QR code data
        String qrData = String.format(
                "AMOUNT:%s;CURRENCY:%s;MERCHANT:%d;DESC:%s",
                request.getAmount(),
                request.getCurrency(),
                request.getMerchantId(),
                request.getDescription()
        );

        // Generate QR code
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", output);

        String base64QR = Base64.getEncoder().encodeToString(output.toByteArray());

        // Create response
        PaymentResponse response = new PaymentResponse();
        response.setQrCode(base64QR);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/process")
    @Transactional
    public ResponseEntity<?> processPayment(@RequestBody ProcessPaymentRequest request) {
        // 1. Validate request
        Optional<User> user = userRepository.findById(request.getUserId());
        Optional<Merchant> merchant = merchantRepository.findById(request.getMerchantId());

        if (user.isEmpty() || merchant.isEmpty()) {
            return ResponseEntity.badRequest().body("User or merchant not found");
        }

        // 2. Check user balance
        if (user.get().getBalance().compareTo(request.getAmount()) < 0) {
            return ResponseEntity.badRequest().body("Insufficient balance");
        }

        // 3. Update balances
        user.get().setBalance(user.get().getBalance().subtract(request.getAmount()));
        merchant.get().setBalance(merchant.get().getBalance().add(request.getAmount()));

        // 4. Save transaction
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setMerchantId(request.getMerchantId());
        transaction.setDescription(request.getDescription());
        transaction.setUserId(request.getUserId());
        transactionRepository.save(transaction);

        // 5. Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Payment successful");
        response.put("userBalance", user.get().getBalance());
        response.put("merchantBalance", merchant.get().getBalance());
        response.put("transactionId", transaction.getId());

        return ResponseEntity.ok(response);
    }
}