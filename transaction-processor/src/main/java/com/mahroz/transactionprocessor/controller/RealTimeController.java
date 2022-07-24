package com.mahroz.transactionprocessor.controller;


import enums.TransactionStatus;
import exception_handler.BillingServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static exception_handler.ExceptionMapper.NO_RECORD_FOUND;
import static util.ServiceConstants.TRANS_STATE_STORE_NAME;

@Slf4j
@RestController
@RequestMapping("/trans")
public class RealTimeController {

    private InteractiveQueryService interactiveQueryService;

    public RealTimeController(InteractiveQueryService interactiveQueryService) {
        this.interactiveQueryService = interactiveQueryService;
    }

    @GetMapping("/status/{orderId}")
    public ResponseEntity getTransactionStatus(@PathVariable(name="orderId")String orderId) throws BillingServiceException {

        final ReadOnlyKeyValueStore<String, String> store =
                interactiveQueryService.getQueryableStore(TRANS_STATE_STORE_NAME, QueryableStoreTypes.keyValueStore());
        log.info("---------FETCHING DATA FROM STATE STORE---------");
        log.info("---------ORDERID---------"+orderId);

        //get it from current app store
            return ResponseEntity.ok(TransactionStatus.valueOf(Optional.ofNullable(store.get(orderId))
                    .orElseThrow(() -> new BillingServiceException(NO_RECORD_FOUND))));

    }
}
