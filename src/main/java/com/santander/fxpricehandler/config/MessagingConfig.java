package com.santander.fxpricehandler.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.Queue;

@Configuration
@EnableJms
public class MessagingConfig {

    @Bean
    public Queue fxPriceDataQueue() {
        return new ActiveMQQueue("fxPriceData.queue");
    }
}
