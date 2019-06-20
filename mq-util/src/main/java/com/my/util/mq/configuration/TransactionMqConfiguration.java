package com.my.util.mq.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.my.util.mq"})
public class TransactionMqConfiguration {
}
