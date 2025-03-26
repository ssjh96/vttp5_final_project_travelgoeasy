package vttp5.batcha.travelgoeasy.server.service;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class GeminiService 
{
    @Value("${gemini.api.key}") 
    private String geminiApiKey;

    public static final String geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public String postPrompt(String prompt)
    {
        String url = UriComponentsBuilder.fromUriString(geminiUrl).queryParam("key", geminiApiKey).toUriString();
        
        System.out.println("Gemini Url: " + url);

        // build request entity with enhanced system instruction
        RequestEntity<String> request = RequestEntity.post(url).contentType(MediaType.APPLICATION_JSON).body(formatJsonPrompt(enhancePrompt(prompt)));

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        String jGeminiRespStr = response.getBody();

        return getGeminiAns(jGeminiRespStr);
    }

    private String enhancePrompt(String prompt) {
        
        System.out.println("Enhancing itinerary prompt with system instructions");
        System.out.println("Original Prompt: " + prompt);
        
        // Add system instructions for itinerary generation
        String systemInstruction = """
            You are a travel planning AI assistant. When asked to generate an itinerary, follow these guidelines:
            
            1. Ensure all attractions are REAL locations with accurate names and addresses
            2. Include precise latitude and longitude coordinates for each location
            3. Organize attractions logically by proximity to minimize travel time
            4. Format your response as valid JSON that can be parsed by JavaScript
            5. Do not include any text outside the JSON structure
            6. Include 3-5 attractions per day based on popularity and logical routing
            7. IMPORTANT: Only return valid JSON array with no explanation text. The JSON must follow exactly the format specified in the user's prompt.
            
            USER PROMPT:
            """;
        String enhancedPrompt = systemInstruction + prompt;
        System.out.println("Enhanced Prompt: " + enhancedPrompt);
        return enhancedPrompt;

    }

    // {
    //     "contents": [{
    //       "parts":[{"text": "give me project ideas for angular + spring boot"}]
    //       }]
    //  }
    public String formatJsonPrompt(String prompt)
    {
        JsonObject jFormattedPrompt = Json.createObjectBuilder()
            .add("contents", Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                    .add("parts", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                            .add("text", prompt))))).build();
        
        return jFormattedPrompt.toString();
    }

    // get the 'text' json key
    public String getGeminiAns(String jGeminiRespStr)
    {
        JsonReader jReader = Json.createReader(new StringReader(jGeminiRespStr));
        JsonObject jGeminiResp = jReader.readObject();

        String geminiAns = jGeminiResp
            .getJsonArray("candidates")
            .getJsonObject(0)
            .getJsonObject("content")
            .getJsonArray("parts")
            .getJsonObject(0)
            .getString("text");
        
        return geminiAns;
    }
}