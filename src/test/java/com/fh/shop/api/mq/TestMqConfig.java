package com.fh.shop.api.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestMqConfig {

    public static final String TEST_EX="test_ex";

    public static final String TEST_QUEUE="test_queue";

    public static final String TEST_KEY="test_key";

    @Bean
    public DirectExchange testEx(){
        return new DirectExchange(TEST_EX,true,false);
    }
    @Bean
    public Queue testQueue(){
        return new Queue(TEST_QUEUE,true);
    }
    @Bean
    public Binding testbinding(){
        return BindingBuilder.bind(testQueue()).to(testEx()).with(TEST_KEY);
    }
}
