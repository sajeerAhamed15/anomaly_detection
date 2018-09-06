package com.pickme.anomalydetection.services;

import com.pickme.anomalydetection.model.PaymentTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource("classpath:application.properties")
public class DataHandler {

    @Autowired
    MailService mailService;

    @Value( "${totFailureRatio}" )
    private float maxTotFailureRatio;

    @Value( "${individualErrorRatio}" )
    private float maxIndividualErrorRatio;

    @Value( "${maxPending}" )
    private int maxPending;

    @Value( "${timeWindow}" )
    private int timeWindow;

    @Value( "${timeWindowForPendingRequests}" )
    private int timeWindowForPendingRequests;


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



        //checking for error threshold
        if(failedRatio>maxTotFailureRatio){
            //fire an event
            mailBody+="Reached Maximum fail:total request ratio.\n - Fail ratio = "+failedRatio+"\n\n";
        }
        if(pendingEntries>maxPending){
            //fire an event
            mailBody+="Reached Maximum pending request.\n - Pending Requests = "+pendingEntries+"\n\n";
        }
        for (int i=0;i<errorRatio.size();i++){
            Float error=errorRatio.get(i);
            if(error>maxIndividualErrorRatio){
                //fire an event
                mailBody+="Reached Maximum Individual error request ratio.\n";
                break;
            }
        }
        for (int i=0;i<errorRatio.size();i++){
            Float error=errorRatio.get(i);
            if(error>maxIndividualErrorRatio){
                //fire an event
                mailBody+=" - Response Message: "+errorMessage.get(i)+" = "+error+"\n";
            }
        }

        //mail if any error found
        if (!(mailBody.equals(""))){
            mailBody+="\nTime Window = "+timeWindow+" Minutes\n";
            mailBody+="More details in payment_transactions table";
            log.info("-------------------------------------\n"+mailBody);
            mailService.sendMail(mailBody);
        }
    }

    public void handlePendingRequest(List<PaymentTransaction> list) {
        //if #entries less than ${maxPending} then return
        if(list.size()<maxPending)
            return;


        String mailBody="Pending request increased threshold value (="+maxPending+" per "+timeWindowForPendingRequests+" Minutes)\n";
        for (int i=0;i<list.size();i++) {
            //fire en event
            PaymentTransaction paymentTransaction=list.get(i);
            mailBody+=" - ID: "+paymentTransaction.getId()+" | Trip ID: "+paymentTransaction.getTrip_id()+" | Card ID: "+paymentTransaction.getCard_id()+" | Amount: "+paymentTransaction.getAmount()+"\n";
        }

        mailBody+="\nMore details in payment_transactions table";

        log.info("-------------------------------------\n"+mailBody);
        mailService.sendMail(mailBody);
    }
}
