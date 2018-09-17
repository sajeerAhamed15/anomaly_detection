package com.pickme.anomalydetection.services;

import com.pickme.anomalydetection.config.Parameters;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MailService {

    public Boolean sendMailAPI(String message,String header)
    {
        String html="<html><body><h3>"+header+"</h3><p>"+message+"</p></body></html>";

        String[] emailID= Parameters.emailaddress.replace(" ","").split(",");

        for(int i=0;i<emailID.length;i++)
        {
            //url should look like this: "http://35.184.75.146:8004/sendEmail?html=<html><body><h3>header</h3><p>message</p></body></html>&subject=Card Transactions Issue&fromName=PickMe&toAddr=sajeer@pickme.lk"
            String fullURL=Parameters.EmailUrl+"html="+html+"&subject=Card Transactions Issue&fromName=PickMe&toAddr="+emailID[i]+"";
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(fullURL, String.class);

            System.out.println(result);
        }
        return true;
    }
}