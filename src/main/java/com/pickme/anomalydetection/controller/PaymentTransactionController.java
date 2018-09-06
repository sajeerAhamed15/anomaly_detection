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

@Component
@PropertySource("classpath:application.properties")
//@RequestMapping("/api/payment_transaction")
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


//    @GetMapping("/all")
//    public Iterable<PaymentTransaction> getAllTransaction() {
//        return paymentTransactionRepository.findAll();
//    }
//
//    @GetMapping("/{id}")
//    public PaymentTransaction getTransactionById(@PathVariable(value = "id") Long Id) {
//        return paymentTransactionRepository.findById(Id)
//                .orElseThrow(() -> new ResourceNotFoundException("PaymentTransaction", "id", Id  ));
//    }
//
//    @GetMapping("/recentByTime/{hours}")
//    public List<PaymentTransaction> getRecentByTime(@PathVariable(value = "hours") int hours) {
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime now = LocalDateTime.now();
//        String temp=dtf.format(now.minusHours(hours));
//        System.out.println(temp);
//        return paymentTransactionRepository.findByTime(temp);
////                .orElseThrow(() -> new ResourceNotFoundException("PaymentTransaction", "hours", hours  ));
//    }
//
//    @GetMapping("/recentByCount/{count}")
//    public List<PaymentTransaction>  getRecentByCount(@PathVariable(value = "count") int count) {
//        return paymentTransactionRepository.findByLimit(count);
//    }
//
//
//    @GetMapping("/recentByCount/{count}/{status}")
//    public List<PaymentTransaction>  getRecentByCountAndStatus(@PathVariable(value = "count") int count,@PathVariable(value = "status") int status) {
//        return paymentTransactionRepository.findByLimitAndStatus(count,status);
//    }
//
//    @GetMapping("/recentByTime/{hours}/{status}")
//    public List<PaymentTransaction> getRecentByTimeAndStatus(@PathVariable(value = "hours") int hours,@PathVariable(value = "status") int status) {
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime now = LocalDateTime.now();
//        String temp=dtf.format(now.minusHours(hours));
//        System.out.println(temp);
//        return paymentTransactionRepository.findByTimeAndStatus(temp,status);
//    }
//
//
//    //default
//    @GetMapping("/recentByCount")
//    public List<PaymentTransaction>  getRecentByCountDefault() {
//        return paymentTransactionRepository.findByLimitAndGroupByError(countWindow);
//    }
//
//    //default(not used)
//    @GetMapping("/recentByTimeGap/{gap_index}")
//    public List<PaymentTransaction> getRecentByTimeGapDefault(@PathVariable(value = "gap_index") int index) {
//        int from=index*timeWindowForPendingRequests;
//        int to=(index-1)*timeWindowForPendingRequests;
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        ZonedDateTime zonedDateTime=ZonedDateTime.now(ZoneId.of(timeZone));
//        String dateFrom=dtf.format(zonedDateTime.minusMinutes(from));
//        String dateTo=dtf.format(zonedDateTime.minusMinutes(to));
//        System.out.println(dateFrom);
//        System.out.println(dateTo);
//        return paymentTransactionRepository.findByTimeGapAndGroupByError(dateFrom,dateTo);
//    }

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

}
