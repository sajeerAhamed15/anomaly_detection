package com.pickme.anomalydetection.controller;

import com.pickme.anomalydetection.config.Parameters;
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
@RequestMapping("/api/payment_transaction")
public class PaymentTransactionController {
    @Autowired
    PaymentTransactionRepository paymentTransactionRepository;


    //get all errors and success concated to the object in list and return
    public List<PaymentTransaction> getRecentByTimeDefault() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String temp=dtf.format(now.minusMinutes(Parameters.timeWindow));
        System.out.println(temp);
        return paymentTransactionRepository.findByTimeAndGroupByError(temp);
//        return paymentTransactionRepository.findByTimeAndGroupByError("2018-07-20 00:16:21");
    }

    //get last NOW.minusMinutes( ${timeWindowForPendingRequests} - 1 minute) gap request -> get all status 0 - return the paymentTransaction object in list
    public List<PaymentTransaction> getRecentByTimePending() {
        int from=Parameters.timeWindowForPendingRequests;
        int to=1;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime zonedDateTime=ZonedDateTime.now(ZoneId.of(Parameters.timeZone));
        String dateFrom=dtf.format(zonedDateTime.minusMinutes(from));
        String dateTo=dtf.format(zonedDateTime.minusMinutes(to));
        System.out.println(dateFrom);
        System.out.println(dateTo);
        return paymentTransactionRepository.findByTimePending(dateFrom,dateTo);
//        return paymentTransactionRepository.findByTimePending("2018-07-20 00:16:21",dateTo);
    }


    //Exposing APIs
    //get all errors and success concated to the object in list and return
    @GetMapping("/recentByTime/error/{minutes}")
    public List<PaymentTransaction> getRecentByTimeDefaultAPI(@PathVariable(value = "minutes") int minutes) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String temp=dtf.format(now.minusMinutes(minutes));
        System.out.println(temp);
        return paymentTransactionRepository.findByTimeAndGroupByError(temp);
//        return paymentTransactionRepository.findByTimeAndGroupByError("2018-07-20 00:16:21");
    }

    //Exposing APIs
    //get last NOW.minusMinutes( ${timeWindowForPendingRequests} - 1 minute) gap request -> get all status 0 - return the paymentTransaction object in list
    @GetMapping("/recentByTime/pending/{minutes}")
    public List<PaymentTransaction> getRecentByTimePendingAPI(@PathVariable(value = "minutes") int minutes) {
        int from=minutes;
        int to=1;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime zonedDateTime=ZonedDateTime.now(ZoneId.of(Parameters.timeZone));
        String dateFrom=dtf.format(zonedDateTime.minusMinutes(from));
        String dateTo=dtf.format(zonedDateTime.minusMinutes(to));
        System.out.println(dateFrom);
        System.out.println(dateTo);
        return paymentTransactionRepository.findByTimePending(dateFrom,dateTo);
//        return paymentTransactionRepository.findByTimePending("2018-07-20 00:16:21",dateTo);
    }

    //get cards with faiure in last ${badRequestTimeWindow} hours and get their last ${badRequestCountWindow}
//    public List<PaymentTransaction> getBadUsers() {
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime now = LocalDateTime.now();
//        String temp=dtf.format(now.minusHours(Parameters.badRequestTimeWindow));
//        System.out.println(temp);
//        return paymentTransactionRepository.findBadUsers(temp,Parameters.badRequestCountWindow);
//    }
}
