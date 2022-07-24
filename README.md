# REAL TIME BILLING SYSTEMS USING KAKKA STREAMS

Flow chat for this example is given below. 

## Pre-requisites
- KAFKA INSTANCE
- MYSQL SERVER

## Key Notes
To make kafka instance avaialble plz check kafka-instance directory.
For mysql use below command:

docker run --name mysqldb -p 3306:3306 -h 127.0.0.1  -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=billingdb -d mysql:8

The dto`s that are used by kafka topics should be same all across the application in all microservices in order to serialize and deserialize without error.
Kafka by default uses rocksdb to store stateful data . i.e ktable in our case , where we store the status of transaction.
Here are functions are we commonly use to create KTable: aggregate,count,reduce.


### CLient Service
Client Service is mainly responsible to managing customer operations save , update, delete, fetch

### Common-Service
Common service contains all common entities or functions that are required by all microservices


### Transaction Processor
Mainly responsible to filter , process and redirect streams of transaction
It performs following operations:
* Create a state store that enabled us to fetch real time status of any transaction using #INTERACTIVEQUERY
* Filter and process incomming stream 
* Branch incomming stream and redirect them to related topics for further processing or consumption.

Branching stream and redirects to related topics:

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
        
### Transaction Service
Mainly manages tranaction operations like save,update,delete,fetch
Also it act as stream consumer and receives APPROVED transaction for processing

    /**
     * Takes only approved transactions and perform below operations
     * update trans status to "BILLED" and forward the transaction to email service
     * @return
     */
    @Bean
    public Function<KStream<String, TransactionDto>,KStream<String, TransactionDto>> approvedTransProcess(){
        return  kStream -> {

            kStream
                    .mapValues(trans->{

                        log.info("---------APPROVED TRANSACTION PROCESSING START---------");
                        log.info("---------STREAM PAYLOAD---------");
                        log.info(trans.toString());

                        log.info("---------UPDATING STATUS FROM APPROVED -> BILLED---------");
                        trans.setStatus(TransactionStatus.BILLED);
                        transactionService.updateTransaction(trans);

                        /*
                          Forwarding to email service
                         */
                        sendEmail(trans);

                        return trans;
                    });

            return kStream;
        };
    }



* Also other then real time streams it provides a manual way to process remaining non-billable transactons
   
   @GetMapping("/executeBillingCycle/{interval}")
    
 


## FLOW DIAGRAM
![real-time-billing-system](https://user-images.githubusercontent.com/28490692/180665110-49e494e2-901e-49fd-af85-ddc2d34704f0.png)
