package vttp5.batcha.travelgoeasy.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import vttp5.batcha.travelgoeasy.server.service.GeminiService;
import vttp5.batcha.travelgoeasy.server.service.PromptService;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController 
{
    @Autowired
    private GeminiService geminiService;

    @Autowired
    private PromptService promptService; // save prompt for future analysis

    @PostMapping("/prompt")
    public ResponseEntity<String> sendPrompt(@RequestBody String prompt,
                                            @RequestParam(required = false) String destination,
                                            @RequestParam(required = false) Integer daysCount,
                                            @RequestParam(required = false) Integer userId,
                                            @RequestParam(required = false) Integer tripId) 
    {
        try 
        {
            System.out.println("Posting Prompt to Gemini AI..");
            String response = geminiService.postPrompt(prompt);
            System.out.println("Gemini Response: " + response);

            if (destination != null && daysCount != null && userId != null && tripId != null) {
                System.out.println("Recording prompt in MongoDB: " + destination + ", " + daysCount + " days, userId - " + userId + " tripId - " + tripId);
                promptService.recordGeminiPrompt(destination, daysCount, userId, tripId);
            }

            return ResponseEntity.ok().body(Json.createObjectBuilder().add("geminiResp", response).build().toString());
        } 
        
        catch (Exception e) 
        {
            System.out.println("Error posting prompt: " + e.getMessage());
            return ResponseEntity.badRequest().body(Json.createObjectBuilder().add("error", e.getMessage()).toString());
        }
        
    }
    
}
