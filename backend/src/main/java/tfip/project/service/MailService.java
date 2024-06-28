package tfip.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class MailService {
    
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendMail(String to, String subject, String body) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(this.fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendPasswordResetMail(String toEmail, String firstName, String resetLink) {
		String subject = "Reset password for your GameBond account";
		String body = """
				<html>
				<body>
				<p>Hi 
				"""+ firstName +"""
				,</p>
				<p>You have recently requested to reset your password.</p>
				<p>To reset your password, click on the button below:</p>
				<p><a href='
                """+ resetLink +"""
                ' style='display: inline-block; padding: 10px 20px; border-radius: 20px; background-color: #886ce4; color: white; text-decoration: none;'>Reset Password</a></p>
				<p>Alternatively, you may also click on the link below:
				<br><a href='
                """+ resetLink +"""
                '>
                """+ resetLink +"""
                </a></p>
				<p>This link will expire 30 minutes after this email was sent.</p>
				<p>If you didn't request to reset your password, you may ignore this email.</p>
				<p>Best Regards,<br>GameBond Team</p>
				</body>
				</html>
				""";
        sendMail(toEmail, subject, body);
    }
}
