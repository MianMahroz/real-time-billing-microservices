package com.mahroz.transactionprocessor.processor;

import dto.TransactionDto;
import enums.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Value;
import java.util.function.Function;

import static util.ServiceConstants.TRANS_STATE_STORE_NAME;

@Slf4j
@Service
public class TransactionProcessor implements TransactionTopology {

    @Value("${trans-approved.topic}")
    private String approvedTransTopic;

    @Value("${trans-decline.topic}")
    private String declineTransTopic;

    @Value("${trans-refunded.topic}")
    private String refundedTransTopic;

    @Value("${trans-billed.topic}")
    private String billedTransTopic;

    /**
     * Takes stream of transaction and performs following operations:
     *  - create a kTable to store most recent transaction status
     *  - On each update it also updates the transaction status in KTable
     *  - Because of this user can always get the latest transaction status with querying db
     *  By using INTERACTIVE_QUERY we can fetch real-time value from this kTable store
     * @return
     */
    @Bean
    public Function<KStream<String, TransactionDto>, KStream<String, TransactionDto>> transStateStoreProcessor() {
        return kStream -> {
            log.info("---------UPDATING TRANSACTION STATUS IN STATE STORE(KTable)---------");

            KTable<String, String> uuidStringKTable = kStreamKTableStringFunction.apply(kStream);

            log.info("STORE NAME"+uuidStringKTable.queryableStoreName());
            //then join the stream with its original stream to keep the flow
            return kStream.leftJoin(uuidStringKTable,
                    new ValueJoiner<TransactionDto,String,TransactionDto>(){
                        @Override
                        public TransactionDto apply(TransactionDto transDto, String previousStatus) {
                            log.info("---------STATE STORE RECEIVED UPDATED TRANSACTION STATUS---------");
                            log.info("---------PREVIOUS TRANSACTION STATUS---------"+previousStatus);
                            log.info("---------RECEIVED TRANSACTION STATUS---------"+transDto.getOrderId()+"-"+transDto.getStatus());


                            return transDto;
                        }
                    });
        };
    }


    /**
     * Takes stream of transactions and perform below operations:
     * - Prints incoming payload
     * - filter stream on the basis of TRANSACTION STATUS and forward them to related topics
     * @return
     */
    @Bean
    public Function<KStream<String, TransactionDto>,KStream<String, TransactionDto>> transactionProcess(){
        return  kStream -> {
            kStream
                    .peek((key,v)-> {
                        log.info("---------TRANSACTION PROCESSOR STARTED---------");
                        log.info("---------INITIATING STREAM BRANCHING---------"+key+"-"+v.getStatus());
                    })
                    .split()
                    .branch(approvedTransPredicate, Branched.withConsumer(ks->ks.to(approvedTransTopic)))
                    .branch(declineTransPredicate, Branched.withConsumer(ks->ks.to(declineTransTopic)))
                    .branch(refundedTransPredicate, Branched.withConsumer(ks->ks.to(refundedTransTopic)))
                    .branch(billedTransPredicate, Branched.withConsumer(ks->ks.to(billedTransTopic)));


            return kStream;
        };
    }

}
