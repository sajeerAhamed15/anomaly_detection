package com.pickme.anomalydetection.scheduledTasks;

import com.pickme.anomalydetection.controller.PaymentTransactionController;
import com.pickme.anomalydetection.model.PaymentTransaction;
import com.pickme.anomalydetection.services.DataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@PropertySource("classpath:application.properties")
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);


    @Autowired
    DataHandler dataHandler;

    @Autowired
    PaymentTransactionController paymentTransactionController;

    @Scheduled(fixedRateString = "${fixedRate.timeBetweenPendingCount}")
    public void getPendingRequests() {
        log.info("getPendingRequests: "+ "Initiated");
        List<PaymentTransaction> list=paymentTransactionController.getRecentByTimePending();
        dataHandler.handlePendingRequest(list);
    }

    @Scheduled(fixedRateString = "${fixedRate.timeBetweenErrorCount}")
    public void getErrorRequests() {
        log.info("getErrorRequests: "+ "Initiated");
        List<PaymentTransaction> list=paymentTransactionController.getRecentByTimeDefault();
        dataHandler.handleTotalErrorRequest(list);
    }



}
