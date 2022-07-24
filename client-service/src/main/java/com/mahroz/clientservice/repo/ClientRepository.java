package com.mahroz.clientservice.repo;

import com.mahroz.clientservice.entity.ClientEntity;
import enums.BillingInterval;
import enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity,String> {
    @Query("select c from ClientEntity c where c.client IN (?1) AND c.billing_interval=?2")
    public List<ClientEntity> getClients(List<String> clientNames, BillingInterval billingInterval);

}
