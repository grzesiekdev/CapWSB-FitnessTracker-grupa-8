package com.capgemini.wsb.fitnesstracker.mail.internal;

import com.capgemini.wsb.fitnesstracker.mail.api.EmailDto;
import com.capgemini.wsb.fitnesstracker.mail.api.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link EmailSender} interface for sending email messages.
 *
 * <p>This class uses the {@link JavaMailSender} provided by Spring to send emails.
 * The configuration for the sender (e.g., host, port, and authentication) is handled
 * via the Spring Mail properties, and the "from" email address is retrieved from the {@link MailProperties}.</p>
 */
@Service
@RequiredArgsConstructor
public class EmailSenderImpl implements EmailSender {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    /**
     * Sends an email using the provided {@link EmailDto}.
     *
     * <p>This method constructs a {@link SimpleMailMessage} object using the details from the
     * {@link EmailDto} (recipient address, subject, and content). It also sets the sender's address
     * from the {@link MailProperties}.</p>
     *
     * @param email an {@link EmailDto} containing the recipient's address, subject, and content of the email.
     */
    @Override
    public void send(EmailDto email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getFrom());
        message.setTo(email.toAddress());
        message.setSubject(email.subject());
        message.setText(email.content());

        mailSender.send(message);
    }
}
