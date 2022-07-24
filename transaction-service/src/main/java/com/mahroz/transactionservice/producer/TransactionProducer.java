package com.mahroz.transactionservice.producer;

import dto.TransactionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class TransactionProducer {

    @Value("${trans-status.topic}")
    private String transTopic;

    private KafkaTemplate<String, TransactionDto> transactionProducer;

    public TransactionProducer(KafkaTemplate<String, TransactionDto> transactionProducer) {
        this.transactionProducer = transactionProducer;
    }

    /**
     * Takes transaction object nad forward it to kafka topic for further processing
     * @param transactionDto
     */
    public void sendTransaction(TransactionDto transactionDto){
        log.info("---------FORWARDING TO TRANSACTION PROCESSOR---------");
        log.info("---------TOPIC---------"+transTopic);

        transactionProducer.send(transTopic, transactionDto.getOrderId(),transactionDto);
    }
}
