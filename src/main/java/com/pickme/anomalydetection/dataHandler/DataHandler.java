package com.pickme.anomalydetection.dataHandler;

import com.pickme.anomalydetection.config.Parameters;
import com.pickme.anomalydetection.model.PaymentTransaction;
import com.pickme.anomalydetection.services.MailService;
import com.pickme.anomalydetection.services.SMSservice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataHandler {

    @Autowired
    MailService mailService;

    @Autowired
    SMSservice smsService;


    private static final Logger log = LoggerFactory.getLogger(DataHandler.class);

    public void handleTotalErrorRequest(List<PaymentTransaction> list) {
        String mailBody="";
        ArrayList<String> errorMessage=new ArrayList<>();
        ArrayList<Integer> errorCount=new ArrayList<>();
        int totalEntries=0;
        int pendingEntries=0;
        int failedEntries=0;
        int successEntries=0;
        for (PaymentTransaction paymentTransaction : list) {
            totalEntries += paymentTransaction.getError_count();
            if(paymentTransaction.getStatus()==0){
                //pending
                pendingEntries+=paymentTransaction.getError_count();
            }else if(paymentTransaction.getStatus()==1){
                //success
                successEntries+=paymentTransaction.getError_count();
            }else if(paymentTransaction.getStatus()==3){
                //error
                //omitting error_message=null (coin transaction)
                if(paymentTransaction.getResponse_message()==null)
                    continue;
                failedEntries+=paymentTransaction.getError_count();
                errorMessage.add(paymentTransaction.getResponse_code());
                errorCount.add(paymentTransaction.getError_count());
            }
        }

        if(totalEntries==0)
            return;

        //finding ratio
        float successRatio=(float) successEntries/totalEntries;
        float pendingRatio=(float) pendingEntries/totalEntries;
        float failedRatio=(float) failedEntries/totalEntries;



        ArrayList<Float> errorRatio=new ArrayList<>();
        for (Integer anErrorCount : errorCount) {
            float error = (float) anErrorCount / totalEntries;
            errorRatio.add(error);
        }



        //checking for tot error threshold
        if(failedRatio> Parameters.maxTotFailureRatio){
            //fire an event
            mailBody+="Fail:Total request ratio.<br/> - Fail ratio = "+String.format("%.1f", failedRatio*100)+"%<br/><br/>";
        }

        //checking for indiviudual error threshold
        for (int i=0;i<errorRatio.size();i++){
            Float error=errorRatio.get(i);
            if(error>Parameters.maxIndividualErrorRatio){
                //fire an event
                mailBody+="Individual error request ratio.<br/>";
                break;
            }
        }
        for (int i=0;i<errorRatio.size();i++){
            Float error=errorRatio.get(i);
            if(error>Parameters.maxIndividualErrorRatio){
                //fire an event
                mailBody+=" - Response message: "+errorMessage.get(i)+" = "+String.format("%.1f", error*100) +"%<br/>";
            }
        }

        //mail if any error found
        if (!(mailBody.equals(""))){
            mailBody+="<br/>More details in payment_transactions table";
            log.info("-------------------------------------\n"+mailBody);
            mailService.sendMailAPI(mailBody, "Reached maximum error ratio (Time Window = "+Parameters.timeWindow+" Minutes)");
            smsService.sendMessage("Card anomaly detected.\nFail Ratio = "+String.format("%.1f", failedRatio*100) +"%\nPlease Check your mail for more details.");
        }
    }

    public void handlePendingRequest(List<PaymentTransaction> list) {
        //if #entries less than ${maxPending} then return
        if(list.size()<Parameters.maxPending)
            return;


        String mailHead="Pending request increased threshold value (="+Parameters.maxPending+" per "+Parameters.timeWindowForPendingRequests+" Minutes)<br/>";
        String mailBody="";
        for (int i=0;i<list.size();i++) {
           //fire en event
            PaymentTransaction paymentTransaction=list.get(i);
            mailBody+=" - ID: "+paymentTransaction.getId()+" | Trip ID: "+paymentTransaction.getTrip_id()+" | Card ID: "+paymentTransaction.getCard_id()+" | Amount: "+paymentTransaction.getAmount()+"<br/>";
        }

        mailBody+="<br/>More details in payment_transactions table";

        log.info("-------------------------------------\n"+mailBody);
        mailService.sendMailAPI(mailBody,mailHead);
        smsService.sendMessage("Card anomaly detected\nPending Transaction = "+list.size()+"\nCheck your mail for more details");
    }

//    public void handleBadUsers(List<PaymentTransaction> list) {
//        String mailHead="Continuous failure from the following cards<br/>";
//        String mailBody="";
//        for (int i=0;i<list.size();i++) {
//            //fire en event
//            PaymentTransaction paymentTransaction=list.get(i);
//            if(paymentTransaction.getError_count()>5)
//                mailBody+=" - Card ID: "+paymentTransaction.getCard_id()+" | Total amount: "+paymentTransaction.getAmount()+"<br/>";
//        }
//
//        mailBody+="<br/>More details in payment_transactions table";
//
//        log.info("-------------------------------------\n"+mailBody);
//        mailService.sendMailAPI(mailBody,mailHead);
//        smsService.sendMessage("Continuous failure from the following cards\nCheck your mail for more details");
//    }
}
