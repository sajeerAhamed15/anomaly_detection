package com.pickme.anomalydetection.controller;

import com.pickme.anomalydetection.model.PaymentTransaction;
import com.pickme.anomalydetection.repository.PaymentTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

//@Component
@RestController
@PropertySource("classpath:application.properties")
@RequestMapping("/api/payment_transaction")
public class PaymentTransactionController {
    @Autowired
    PaymentTransactionRepository paymentTransactionRepository;

    @Value( "${timeWindow}" )
    private int timeWindow;

    @Value( "${timeWindowForPendingRequests}" )
    private int timeWindowForPendingRequests;

    @Value( "${countWindow}" )
    private int countWindow;

    @Value( "${timeZone}" )
    private String timeZone;

    @Value( "${maxPending}" )
    private int maxPending;

    //get all errors and success concated to the object in list and return
    public List<PaymentTransaction> getRecentByTimeDefault() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String temp=dtf.format(now.minusMinutes(timeWindow));
        System.out.println(temp);
        return paymentTransactionRepository.findByTimeAndGroupByError("2018-07-20 00:16:21");//temp
    }

    //get last NOW.minusMinutes( ${timeWindowForPendingRequests} - 1 minute) gap request -> get all status 0 - return the paymentTransaction object in list
    public List<PaymentTransaction> getRecentByTimePending() {
        int from=timeWindowForPendingRequests;
        int to=1;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime zonedDateTime=ZonedDateTime.now(ZoneId.of(timeZone));
        String dateFrom=dtf.format(zonedDateTime.minusMinutes(from));
        String dateTo=dtf.format(zonedDateTime.minusMinutes(to));
        System.out.println(dateFrom);
        System.out.println(dateTo);
        return paymentTransactionRepository.findByTimePending("2018-07-20 00:16:21",dateTo);//dateFrom
    }


    //Exposing APIs
    //get all errors and success concated to the object in list and return
    @GetMapping("/recentByTime/error/{minutes}")
    public List<PaymentTransaction> getRecentByTimeDefaultAPI(@PathVariable(value = "minutes") int minutes) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String temp=dtf.format(now.minusMinutes(minutes));
        System.out.println(temp);
        return paymentTransactionRepository.findByTimeAndGroupByError("2018-07-20 00:16:21");//temp
    }

    //Exposing APIs
    //get last NOW.minusMinutes( ${timeWindowForPendingRequests} - 1 minute) gap request -> get all status 0 - return the paymentTransaction object in list
    @GetMapping("/recentByTime/pending/{minutes}")
    public List<PaymentTransaction> getRecentByTimePendingAPI(@PathVariable(value = "minutes") int minutes) {
        int from=minutes;
        int to=1;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime zonedDateTime=ZonedDateTime.now(ZoneId.of(timeZone));
        String dateFrom=dtf.format(zonedDateTime.minusMinutes(from));
        String dateTo=dtf.format(zonedDateTime.minusMinutes(to));
        System.out.println(dateFrom);
        System.out.println(dateTo);
        return paymentTransactionRepository.findByTimePending("2018-07-20 00:16:21",dateTo);//dateFrom
    }

}
