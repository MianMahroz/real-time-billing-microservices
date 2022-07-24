package com.mahroz.transactionservice.repo;

import com.mahroz.transactionservice.entity.TransactionEntity;
import enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity,String> {
    @Query("select t from TransactionEntity t where t.status = ?1")
    public List<TransactionEntity> getTransactionsByStatus(TransactionStatus transactionStatus);

}
