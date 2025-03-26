package vttp5.batcha.travelgoeasy.server.model;

public class EmailRequest 
{
    private String to;
    private String subject;
    private String message;
    private String name;
    
    public EmailRequest() {
    }

    public EmailRequest(String to, String subject, String message, String name) {
        this.to = to;
        this.subject = subject;
        this.message = message;
        this.name = name;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
