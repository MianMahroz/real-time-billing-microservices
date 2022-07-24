package com.mahroz.transactionservice.feign;

import dto.ClientDto;
import enums.BillingInterval;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "client-service", url = "http://localhost:8080")
public interface ClientService {


    @PostMapping("/client/list/{interval}")
    public ResponseEntity<List<ClientDto>> getClients(@RequestBody List<String> clientNames,@PathVariable(name="interval") BillingInterval billingInterval);

    }
