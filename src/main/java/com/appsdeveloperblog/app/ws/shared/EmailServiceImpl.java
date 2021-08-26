package com.appsdeveloperblog.app.ws.shared;

import com.appsdeveloperblog.app.ws.shared.dto.PasswordResetDto;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailServiceImpl {
    @Autowired
    private JavaMailSender sender;

    // The subject line for the email.
    final String SUBJECT = "One last step to complete your registration with PhotoApp";

    final String PASSWORD_RESET_SUBJECT = "Password reset request";

    // The email body for recipients with non-HTML email clients.
    final String TEXTBODY = "Please verify your email address. "
            + "Thank you for registering with our mobile app. To complete registration process and be able to log in,"
            + " open then the following URL in your browser window: "
            + " http://localhost:8080/users/email-verification?token=$tokenValue"
            + " Thank you! And we are waiting for you inside!";


    // The email body for recipients with non-HTML email clients.
    final String PASSWORD_RESET_TEXTBODY = "A request to reset your password "
            + "Hi, $firstName! "
            + "Someone has requested to reset your password with our project. If it were not you, please ignore it."
            + " otherwise please open the link below in your browser window to set a new password:"
            + " http://localhost/users/password-reset?token=$tokenValue"
            + " Thank you!";

    public void sendMail(UserDto user) {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(user.getEmail());
            String textBodyWithToken = TEXTBODY.replace("$tokenValue", user.getEmailVerificationToken());
            helper.setText(textBodyWithToken);
            helper.setSubject(SUBJECT);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        sender.send(message);
    }

    public void sendPasswordResetMail(PasswordResetDto passwordResetDto) {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(passwordResetDto.getUserDetails().getEmail());
            String textBodyWithToken = PASSWORD_RESET_TEXTBODY.replace("$tokenValue", passwordResetDto.getToken());
            helper.setText(textBodyWithToken);
            helper.setSubject(PASSWORD_RESET_SUBJECT);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        sender.send(message);
    }
}
