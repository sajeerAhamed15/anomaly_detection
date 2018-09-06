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
    @Query(value = "select * from payment_transactions where id = ?1",nativeQuery = true)
    PaymentTransaction findByUniqueId(Long id);

    @Query(value = "select *,sum(amount) as tot_amount,count(trip_id) as error_count from payment_transactions where date_created >= :date",nativeQuery = true)
    List<PaymentTransaction> findByTime(@Param("date") String date);

    @Query(value = "select *,sum(amount) as tot_amount,count(trip_id) as error_count from payment_transactions order by id DESC LIMIT :count",nativeQuery = true)
    List<PaymentTransaction> findByLimit(@Param("count") int count);

    @Query(value = "select *,sum(amount) as tot_amount,count(trip_id) as error_count from (select * from payment_transactions order by id DESC LIMIT :count) as temp where temp.status=:status",nativeQuery = true)
    List<PaymentTransaction> findByLimitAndStatus(@Param("count")int count, @Param("status")int status);

    @Query(value = "select *,sum(amount) as tot_amount,count(trip_id) as error_count from payment_transactions where status=:status AND date_created >= :date",nativeQuery = true)
    List<PaymentTransaction> findByTimeAndStatus(@Param("date") String date,@Param("status") int status);

    @Query(value = "select *,sum(temp.amount) as tot_amount,count(temp.id) as error_count from (select * from payment_transactions order by id DESC LIMIT :count) as temp group by response_message,status",nativeQuery = true)
    List<PaymentTransaction> findByLimitAndGroupByError(@Param("count")int count);

    //using
    @Query(value = "select *,sum(amount) as tot_amount,count(trip_id) as error_count from payment_transactions where date_created >= :date group by response_message,status",nativeQuery = true)
    List<PaymentTransaction> findByTimeAndGroupByError(@Param("date") String date);

    @Query(value = "select *,sum(amount) as tot_amount,count(trip_id) as error_count from payment_transactions where date_created >= :dateFrom AND date_created <= :dateTo group by response_message,status",nativeQuery = true)
    List<PaymentTransaction> findByTimeGapAndGroupByError(@Param("dateFrom") String dateFrom,@Param("dateTo") String dateTo);

    //using
    @Query(value = "select *,0 as tot_amount,0 as error_count from payment_transactions where date_created >= :dateFrom AND date_created <= :dateTo AND status=0",nativeQuery = true)
    List<PaymentTransaction> findByTimePending(@Param("dateFrom") String dateFrom,@Param("dateTo") String dateTo);

}
