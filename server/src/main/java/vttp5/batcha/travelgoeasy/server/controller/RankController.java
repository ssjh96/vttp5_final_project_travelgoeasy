package vttp5.batcha.travelgoeasy.server.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vttp5.batcha.travelgoeasy.server.service.RankService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/rank")
public class RankController 
{
    @Autowired
    private RankService rankService;
    
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserRankInfo(@PathVariable String userId) 
    {
        try {
            Map<String, Object> userRankInfo = rankService.getUserRank(Integer.parseInt(userId));
            return ResponseEntity.ok(userRankInfo);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null); // status 500
        }        
    }
    
}
