package com.pickme.anomalydetection.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Component
@PropertySource("classpath:application.properties")
public class SMSservice {

    @Value( "${sms.url}" )
    private String url;

    @Value( "${sms.countryCode}" )
    private String countryCode;

    @Value( "${sms.contactNumbers}" )
    private String allContactNumbers;


    public Boolean sendMessage(String message)
    {
        String[] contactNumbers=allContactNumbers.replace(" ","").split(",");

        for(int i=0;i<contactNumbers.length;i++)
        {
            //url should look like this: "http://35.184.75.146:8004/sendSMS?countryCode=94&number=77xxxxxxx&message=xx"
            String fullURL=url+"countryCode="+countryCode+"&number="+contactNumbers[i]+"&message="+message;
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(fullURL, String.class);

            System.out.println(result);
        }
        return true;
    }
}
