package com.mahroz.transactionprocessor.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.TransactionDto;
import enums.TransactionStatus;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Service;

import java.util.function.Function;

import static util.ServiceConstants.TRANS_STATE_STORE_NAME;


@Service
public interface TransactionTopology {

    Predicate<String, TransactionDto> approvedTransPredicate =(key, value) -> value.getStatus()== TransactionStatus.APPROVED;
    Predicate<String, TransactionDto> declineTransPredicate =(key, value) -> value.getStatus()== TransactionStatus.DECLINED;
    Predicate<String, TransactionDto> refundedTransPredicate =(key, value) -> value.getStatus()== TransactionStatus.REFUNDED;
    Predicate<String, TransactionDto> billedTransPredicate =(key, value) -> value.getStatus()== TransactionStatus.BILLED;

    /**
     * Functional interface to create Ktable
     * It acted as state store , that stores the latest state of the transaction and make it available to user real time
     */
    Function<KStream<String, TransactionDto>, KTable<String, String>> kStreamKTableStringFunction = (input) -> {
        return input.peek((k,v)->System.out.println("MyKEY"+k))
                .groupBy((s, order) -> order.getOrderId(),
                        Grouped.with(null, new JsonSerde<>(TransactionDto.class, new ObjectMapper())))
                .aggregate(
                        String::new,
                        (s, trans, oldStatus) -> trans.getStatus().name().toString(),
                        Materialized.<String, String, KeyValueStore<Bytes, byte[]>>as(TRANS_STATE_STORE_NAME)
                                .withKeySerde(Serdes.String()).
                                withValueSerde(Serdes.String())
                );
    };

}
