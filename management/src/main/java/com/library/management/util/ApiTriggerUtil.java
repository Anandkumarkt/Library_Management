package com.library.management.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ApiTriggerUtil {

    @Scheduled(cron = "0 0 10 * * *")
    public void triggerMailApi(){
        System.out.println("APi Trigger activated");
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8080/api/v1/library/mail-trigger";
        Object response = restTemplate.getForObject(apiUrl, String.class);
    }
}
