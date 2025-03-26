package vttp5.batcha.travelgoeasy.server.repository;

import java.util.Date;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PromptRepository 
{
    @Autowired 
    private MongoTemplate template;
    
    private static final String C_GEMINI_PROMPTS = "gemini_prompts";

    /* save a prompt record to MongoDB for potential future use, e.g. analyse data to identify popular destinations or recommend trip durations

    Native MongoDB query:
    db.gemini_prompts.insertOne({
        destination: "destination",
        days_count: 7,
        user_id: 123,
        timestamp: new Date(),
        trip_id: 456
    })
     */

    public Document savePrompt(String destination, Integer daysCount, Integer userId, Integer tripId) 
    {
        Document promptDoc = new Document()
            .append("destination", destination)
            .append("days_count", daysCount)
            .append("user_id", userId)
            .append("timestamp", new Date())
            .append("trip_id", tripId);
        
        Document insertedDoc = template.insert(promptDoc, C_GEMINI_PROMPTS);
        System.out.println("Saved prompt to MongoDB: " + insertedDoc.toJson());
    
        return insertedDoc;
    }


}
