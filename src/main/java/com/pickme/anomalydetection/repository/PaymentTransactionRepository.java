package com.pickme.anomalydetection.repository;

import com.pickme.anomalydetection.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentTransactionRepository extends CrudRepository<PaymentTransaction, Long> {
    //using
    @Query(value = "select *,sum(amount) as tot_amount,count(trip_id) as error_count from payment_transactions where date_created >= :date group by response_message,status",nativeQuery = true)
    List<PaymentTransaction> findByTimeAndGroupByError(@Param("date") String date);

   //using
    @Query(value = "select *,0 as tot_amount,0 as error_count from payment_transactions where date_created >= :dateFrom AND date_created <= :dateTo AND status=0",nativeQuery = true)
    List<PaymentTransaction> findByTimePending(@Param("dateFrom") String dateFrom,@Param("dateTo") String dateTo);

}
