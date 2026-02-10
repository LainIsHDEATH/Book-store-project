package com.epam.rd.autocode.spring.project.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${app.jwt.secret:very-secret-key-change-me}")
    private String secret;

    @Value("${app.jwt.expiration-ms:3600000}")
    private long expiration;

    private static final Base64.Encoder B64_URL_ENC = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder B64_URL_DEC = Base64.getUrlDecoder();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public String generateTokenFromEmail(String email) {
        try {
            String header = B64_URL_ENC.encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
            long exp = Instant.now().toEpochMilli() + expiration;
            String payloadJson = MAPPER.writeValueAsString(Map.of("sub", email, "exp", exp));
            String payload = B64_URL_ENC.encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            String unsigned = header + "." + payload;
            String sig = sign(unsigned);
            return unsigned + "." + sig;
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot generate JWT", e);
        }
    }

    public String getUserNameFromJwtToken(String token) {
        try {
            Map<?, ?> payload = parsePayload(token);
            return String.valueOf(payload.get("sub"));
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateJwtToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;
            String unsigned = parts[0] + "." + parts[1];
            if (!sign(unsigned).equals(parts[2])) return false;
            Map<?, ?> payload = parsePayload(token);
            long exp = Long.parseLong(String.valueOf(payload.get("exp")));
            return Instant.now().toEpochMilli() < exp;
        } catch (Exception e) {
            return false;
        }
    }

    private Map<?, ?> parsePayload(String token) throws Exception {
        String[] parts = token.split("\\.");
        String payloadJson = new String(B64_URL_DEC.decode(parts[1]), StandardCharsets.UTF_8);
        return MAPPER.readValue(payloadJson, Map.class);
    }

    private String sign(String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] sig = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return B64_URL_ENC.encodeToString(sig);
    }
}
