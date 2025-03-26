package vttp5.batcha.travelgoeasy.server.service;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp5.batcha.travelgoeasy.server.repository.PromptRepository;

@Service
public class PromptService 
{
    @Autowired
    private PromptRepository promptRepository;

    public Document recordGeminiPrompt(String destination, Integer dayCount, Integer userId, Integer tripId)
    {
        return promptRepository.savePrompt(destination, dayCount, userId, tripId);
    }
}
