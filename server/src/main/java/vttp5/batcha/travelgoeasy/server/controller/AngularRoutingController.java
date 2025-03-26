package vttp5.batcha.travelgoeasy.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class AngularRoutingController 
{
    @RequestMapping({"/",
                    "/Login/**",
                    "/Register/**",
                    "/shared-itinerary/**",
                    "/Home/**",
                    "/Profile/**",
                    "/checkout/**",
                    "/payment-success/**",
                    "/payment-cancel/**",
                    "/create-plan/**",
                    "/all-plans/**",
                    "/view-plan/**",
                    })
    public String fwdToAngularRoute() 
    {
        return "forward:/index.html";
    }        
}
