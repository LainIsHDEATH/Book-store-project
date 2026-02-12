package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.exception.*;
import com.epam.rd.autocode.spring.project.model.PasswordResetToken;
import com.epam.rd.autocode.spring.project.repo.PasswordResetTokenRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.PasswordResetService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final JavaMailSender mailSender;
    private final PasswordResetTokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.public-base-url}")
    private String publicBaseUrl;

    private final SecureRandom secureRandom = new SecureRandom();
    private final Duration ttl = Duration.ofMinutes(15);

    /**
     * Всегда ведём себя одинаково, чтобы не палить существование email.
     */
    @Override
    @Transactional
    public void requestReset(String email) {
        // чистка мусора
        tokenRepo.deleteByExpiryDateBefore(Instant.now());

        var user = findUserByEmail(email);
        if (user == null) {
            return;
        }
        if (user.blocked) {
            return;
        }

        // один активный токен на email
        tokenRepo.deleteByUserEmail(email);

        PasswordResetToken t = new PasswordResetToken();
        t.setUserEmail(email);
        t.setToken(generateRawToken());
        t.setExpiryDate(Instant.now().plus(ttl));

        tokenRepo.save(t);

        sendPasswordResetEmail(email, t.getToken());
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken t = tokenRepo.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (t.getExpiryDate().isBefore(Instant.now())) {
            tokenRepo.delete(t);
            throw new TokenExpiredException("Token expired");
        }

        var user = findUserByEmail(t.getUserEmail());
        if (user == null) {
            tokenRepo.delete(t);
            throw new NotFoundException("User not found");
        }
        if (user.blocked) {
            tokenRepo.delete(t);
            throw new UserBlockedException("User blocked");
        }

        String encoded = passwordEncoder.encode(newPassword);

        if (user.type == UserType.CLIENT) {
            var c = clientRepository.findByEmail(user.email).orElseThrow();
            c.setPassword(encoded);
            clientRepository.save(c);
        } else {
            var e = employeeRepository.findByEmail(user.email).orElseThrow();
            e.setPassword(encoded);
            employeeRepository.save(e);
        }

        tokenRepo.delete(t);
    }

    private void sendPasswordResetEmail(String to, String rawToken) {
        String link = UriComponentsBuilder.fromUriString(publicBaseUrl)
                .path("/auth/password/reset")
                .queryParam("token", rawToken)
                .build()
                .toUriString();

        String subject = "Password reset";
        String html = """
                <div style="font-family: Arial, sans-serif;">
                  <h2>Password reset</h2>
                  <p>Click the link to set a new password:</p>
                  <p><a href="%s">%s</a></p>
                  <p>This link will expire soon. If you didn’t request this, ignore this email.</p>
                </div>
                """.formatted(link, link);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send password reset email", e);
        }
    }

    private String generateRawToken() {
        byte[] buf = new byte[32];
        secureRandom.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    private FoundUser findUserByEmail(String email) {
        var clientOpt = clientRepository.findByEmail(email);
        if (clientOpt.isPresent()) {
            var c = clientOpt.get();
            boolean blocked = Boolean.TRUE.equals(c.getIsBlocked()); // или c.isBlocked()
            return new FoundUser(email, UserType.CLIENT, blocked);
        }

        var empOpt = employeeRepository.findByEmail(email);
        if (empOpt.isPresent()) {
            var e = empOpt.get();
            boolean blocked = Boolean.TRUE.equals(e.getIsBlocked()); // если есть
            return new FoundUser(email, UserType.EMPLOYEE, blocked);
        }

        return null;
    }

    private enum UserType { CLIENT, EMPLOYEE }

    private static final class FoundUser {
        final String email;
        final UserType type;
        final boolean blocked;

        private FoundUser(String email, UserType type, boolean blocked) {
            this.email = email;
            this.type = type;
            this.blocked = blocked;
        }
    }
}