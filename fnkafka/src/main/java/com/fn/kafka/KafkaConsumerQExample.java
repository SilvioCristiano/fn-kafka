package com.fn.kafka;

public class KafkaConsumerQExample {
	
	   public static void main(String... args) throws Exception {
	        System.out.println("consumer");
	        ConsumeCustomerQ consumer = new ConsumeCustomerQ();
	        consumer.consume();
	    }

}
