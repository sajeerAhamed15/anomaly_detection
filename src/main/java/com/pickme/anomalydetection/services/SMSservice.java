package com.pickme.anomalydetection.services;

import com.pickme.anomalydetection.config.Parameters;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SMSservice {


    public Boolean sendMessage(String message)
    {
        String[] contactNumbers= Parameters.allContactNumbers.replace(" ","").split(",");

        for(int i=0;i<contactNumbers.length;i++)
        {
            //url should look like this: "http://35.184.75.146:8004/sendSMS?countryCode=94&number=77xxxxxxx&message=xx"
            String fullURL=Parameters.SMSUrl+"countryCode="+Parameters.countryCode+"&number="+contactNumbers[i]+"&message="+message+"";
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(fullURL, String.class);

            System.out.println(result);
        }
        return true;
    }
}
