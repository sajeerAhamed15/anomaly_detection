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

    //using *query explained in comment below
    @Query(value = "" +
            "set @pk1 ='';" +
            "set @rn1 =1;" +
            "set @sal ='';" +
            "select *, " +
                "0 as tot_amount, " +
                "COUNT(card_id) AS error_count " +
            "from " +
                "(select *, " +
                    " @rn1\\:=if(@pk1=card_id, if(@sal=id, @rn1, @rn1+1),1) as rank, " +
                    "@pk1\\:=card_id, " +
                    "@sal\\:=id  " +
                "from payment_transactions " +
                "where card_id in (select card_id from payment_transactions where date_created >= :date AND status=3 group by card_id) " +
                "order by id desc)" +
            "data " +
            "where rank <= :limit and status =3 " +
            "group by card_id;",nativeQuery = true)
    List<PaymentTransaction> findBadUsers(@Param("date") String date,@Param("limit")  int badRequestCountWindow);
}

//query to select failed cards in last {1 hour} ::::: select card_id from payment_transactions where date_created >= '<date>' AND status=3 group by card_id

/*query to get transactions of failed cards ranked
set @pk1 ='';
set @rn1 =1;
set @sal ='';
select
    card_id,
    status,
    @rn1 := if(@pk1=card_id, if(@sal=id, @rn1, @rn1+1),1) as rank,
    @pk1 := card_id,
    @sal := id
from payment_transactions
where card_id in (<failed cards>)
order by id desc*/

//query to get card_id and errors in last 10 transaction ::: select card_id,COUNT(*) AS error_count from (<transactions of failed card ranked>) data where rank <= <10> and status =3 group by card_id;