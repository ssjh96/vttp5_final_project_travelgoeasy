package vttp5.batcha.travelgoeasy.server.model;

public class JwtResponse 
{
    private String jwtToken;

    public JwtResponse() {
    }

    public JwtResponse(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getJwtToken() { return jwtToken; }

    public void setJwtToken(String jwtToken) { this.jwtToken = jwtToken; }

    @Override
    public String toString() {
        return "jwtToken = " + jwtToken;
    }   
    
}
