package com.chateat.chatEAT.config;

import com.chateat.chatEAT.global.exception.BusinessLogicException;
import com.chateat.chatEAT.global.exception.ExceptionCode;
import jakarta.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Predicate;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AES128Config {
    private static final Charset ENCODING_TYPE = StandardCharsets.UTF_8;
    private static final String INSTANCE_TYPE = "AES/CBC/PKCS5Padding";

    @Value("${AES_SECRET_KEY}")
    private String secretKey; // 16bytes = 128bits
    private IvParameterSpec ivParameterSpec;
    private SecretKeySpec secretKeySpec;
    private Cipher cipher;

    @PostConstruct
    public void init() throws NoSuchPaddingException, NoSuchAlgorithmException {
        validation(secretKey);
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = secretKey.getBytes(ENCODING_TYPE);
        secureRandom.nextBytes(keyBytes);
        secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        ivParameterSpec = new IvParameterSpec(keyBytes);
        cipher = Cipher.getInstance(INSTANCE_TYPE);
    }

    // AES 암호화
    public String encryptAes(String plaintext) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encryted = cipher.doFinal(plaintext.getBytes(ENCODING_TYPE));
            return new String(Base64.getEncoder().encode(encryted), ENCODING_TYPE);
        } catch (Exception e) {
            log.debug("decryptAes.encryptAes exception occur plaintext: {}", plaintext);
            throw new BusinessLogicException(ExceptionCode.ENCRYPTION_FAILED);
        }
    }

    // AES 복호화
    public String decryptAes(String plaintext) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decoded = Base64.getDecoder().decode(plaintext.getBytes(ENCODING_TYPE));
            return new String(cipher.doFinal(decoded), ENCODING_TYPE);
        } catch (Exception e) {
            log.debug("AES128Config.decryptAes exception occur plaintext: {}", plaintext);
            throw new BusinessLogicException(ExceptionCode.DECRYPTION_FAILED);
        }
    }

    public void validation(String secretKey) {
        Optional.ofNullable(secretKey)
                .filter(Predicate.not(String::isBlank))
                .filter(Predicate.not(key -> key.length() != 16))
                .orElseThrow(() -> {
                    log.debug("AES128Config.validation exception occur secretKey: {}", secretKey);
                    return new BusinessLogicException(ExceptionCode.SECRET_KEY_INVALID);
                });
    }
}
