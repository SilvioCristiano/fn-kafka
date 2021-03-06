package com.fn.kafka;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.fn.kafka.msg.Customer;
import com.google.gson.Gson;


public class ConsumeCustomerQ {

	 public void consume() {
  	   
	        String authToken = "4Tn[eAAA#qX]R";
	        String tenancyName = "oracle";
	        String username = "silvio.da.silva@oracle.com";
	        String compartmentId = "ocid1.streampool.oc1.sa-saopaulo-1.amaaaaaaeun4owya6swiqoytal5e5z6cdffpi2ulvbkbzikugyk7qajme53a";
	        String topicName = "streaming-tst";

	        Properties properties = new Properties();
	        properties.put("bootstrap.servers", "https://cell-1.streaming.sa-saopaulo-1.oci.oraclecloud.com:9092");
	        properties.put("security.protocol", "SASL_SSL");
	        properties.put("sasl.mechanism", "PLAIN");
	        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-0");
	        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
	        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

	        properties.put("sasl.jaas.config",
	                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\""
	                        + tenancyName + "/"
	                        + username + "/"
	                        + compartmentId + "\" "
	                        + "password=\""
	                        + authToken + "\";"
	        );
	        properties.put("max.partition.fetch.bytes", 1024 * 1024); // limit request size to 1MB per partition

	        Consumer<Long, String> consumer = new KafkaConsumer<>(properties);

	        try {
	            consumer.subscribe(Collections.singletonList( topicName ) );

	            while(true) {
	                Duration duration = Duration.ofMillis(1000);
	                ConsumerRecords<Long, String> consumerRecords = consumer.poll(duration);
	                consumerRecords.forEach(record -> {
	                    System.out.println("Record Key " + record.key());
	                    System.out.println("Record value " + record.value());
	                    System.out.println("Record partition " + record.partition());
	                    System.out.println("Record offset " + record.offset());
	                    
	                    String json = record.value();
	                    Customer config = new Gson().fromJson(json, Customer.class);
	                    String name = config.getName();
	                    int age = config.getAge();
	                    String cpf = config.getCpf();
	                    String status = config.getStatus();
	                    
	                	Customer objCliente = new Customer();
	        			
	        			objCliente.setName(name);
	        			objCliente.setAge(age);
	        			objCliente.setCpf(cpf);
	        			objCliente.setStatus(status);
	                   
	        			Register r = new Register();
	        			try {
							r.insert(objCliente);
						} catch (ClassNotFoundException | SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                });
	                
	                             
	                // commits the offset of record to broker.
	                consumer.commitAsync();
	            }
	        }
	        catch(WakeupException e) {
	            // do nothing, shutting down...
	        }
	        finally {
	            System.out.println("closing consumer");
	            consumer.close();
	        }
	        
	        
	    }
	
}
