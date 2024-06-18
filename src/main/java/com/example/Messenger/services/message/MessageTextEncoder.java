package com.example.Messenger.services.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static java.awt.SystemColor.text;

@Service
@Deprecated
public class MessageTextEncoder {

    @Value("${encodeType}")
    private String encodeType;
    @Value("${key}")
    private String key;

    public String encode(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
                                            IllegalBlockSizeException, BadPaddingException {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec("12345key12345key".getBytes(), "AES");

            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] bytes = cipher.doFinal(text.getBytes());
            return Base64.getEncoder().encodeToString(bytes);
        }

        public String decode(String encodedText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec("12345key12345key".getBytes(), "AES");

            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] bytes = Base64.getDecoder().decode(encodedText);

            return new String(cipher.doFinal(bytes), StandardCharsets.UTF_8);
        }
}
