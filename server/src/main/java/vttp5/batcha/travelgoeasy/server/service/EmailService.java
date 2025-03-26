package vttp5.batcha.travelgoeasy.server.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService 
{
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
 
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine)
    {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendEmail(String to, String subject, String template, Context context) throws MessagingException
    {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        // Process the template with the given context
        String htmlContent = templateEngine.process(template, context); // replace placeholders like ${name} in context object with actual data

        // Set email properties, help to build the email & add attachments
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // set true for HTML content

        // Send the email
        mailSender.send(mimeMessage);
    }

}
