package com.fn.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;

public class CompatibleProducer {

    public void produce(String msg) {

    	String authToken = "3M{AAAAA0I;OHGhg<:";
        String tenancyName = "oracle";
        String username = "silvio.da.silva@oracle.com";
        String streamingId = "ocid1.streampool.oc1.sa-saopaulo-1.amaaaaaaeun4owya6swiqoytal5e5z6cdffpi2ulvbkbzikugyk7qajme53a";
        String topicName = "streaming-tst";

   


        Properties properties = new Properties();
        properties.put("bootstrap.servers", "https://cell-1.streaming.us-phoenix-1.oci.oraclecloud.com:9092");
        properties.put("security.protocol", "SASL_SSL");
        properties.put("sasl.mechanism", "PLAIN");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        properties.put("sasl.jaas.config",
                        "org.apache.kafka.common.security.plain.PlainLoginModule required username=\""
                        + tenancyName + "/"
                        + username + "/"
                        + streamingId + "\" "
                        + "password=\""
                        + authToken + "\";"
        );

        properties.put("retries", 5); // retries on transient errors and load balancing disconnection
        properties.put("max.request.size", 1024 * 1024); // limit request size to 1MB
        
        
        KafkaProducer producer = new KafkaProducer<>(properties);

            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, UUID.randomUUID().toString(), msg );
            producer.send(record, (md, ex) -> {
                if( ex != null ) {
                    ex.printStackTrace();
                }
                else {
                    System.out.println(
                            "Sent msg to "
                                    + md.partition()
                                    + " with offset "
                                    + md.offset()
                                    + " at "
                                    + md.timestamp()
                    );
                }
            });
            
        
        producer.flush();
        producer.close();
        System.out.println("produced 1 messages");
    }

}
