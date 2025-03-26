package vttp5.batcha.travelgoeasy.server.controller;

import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;

import jakarta.json.Json;
import vttp5.batcha.travelgoeasy.server.model.EmailRequest;
import vttp5.batcha.travelgoeasy.server.service.EmailService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api/email")
public class EmailController 
{
    private final EmailService emailService;

    public EmailController(EmailService emailService)
    {
        this.emailService = emailService;
    }


    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest emailRequest) 
    {
        Context context = new Context(); // used to render dynamic HTML content

        // Set variables for the template from POST request data
        context.setVariable("name", emailRequest.getName()); // e.g. sets the ${name} in html thymeleaf template
        context.setVariable("message", emailRequest.getMessage());
        context.setVariable("subject", emailRequest.getSubject());

        System.out.println(">>> message: " + emailRequest.getMessage());

        try {
            emailService.sendEmail(emailRequest.getTo(), emailRequest.getSubject(), "emailTemplate", context);
            // return "Email sent successfully!";
            return ResponseEntity.ok(Json.createObjectBuilder()
                                .add("success", "Email sent successfully!")
                                .build().toString());
        } catch (Exception e) {
            // return "Error sending email: " + e.getMessage();
            return ResponseEntity.badRequest().body(Json.createObjectBuilder()
                                 .add("error", e.getMessage())
                                 .build().toString());
        }
    }

    /* {
        "to" : "shamus-sjh@hotmail.com",
        "subject" : "Welcome Email",
        "message" : "Welcome to TravelGoEasy!!",
        "name" : "New Guy"
    } */
    
}
