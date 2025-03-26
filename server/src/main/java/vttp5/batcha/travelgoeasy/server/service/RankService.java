package vttp5.batcha.travelgoeasy.server.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp5.batcha.travelgoeasy.server.repository.TripRepository;

@Service
public class RankService 
{
    @Autowired
    private TripRepository tripRepository;

    public Map<String, Object> getUserRank(Integer userId) throws Exception
    {
        try {
            int tripCount = tripRepository.countTrips(userId);
            String rank = "";

            if (tripCount >= 25)
            {
                rank = "Grandmaster Planner";
            }
            else if (tripCount >= 20)
            {
                rank = "Master Planner";
            }
            else if (tripCount >= 15)
            {
                rank = "Expert Planner";
            }
            else if (tripCount >= 10)
            {
                rank ="Advanced Planner";
            }
            else if (tripCount >= 5)
            {
                rank ="Intermediate Planner";
            }
            else if (tripCount >= 1)
            {
                rank = "Beginner Planner";
            }
            else
            {
                rank = "Novice Planner";
            }

            Map<String, Object> userRankInfo = new HashMap<>();
            userRankInfo.put("tripCount", tripCount);
            userRankInfo.put("rank", rank);

            return userRankInfo;

        } catch (Exception e) {
            throw new Exception("Error getting count.. " + e.getMessage());
        }
    }
}
