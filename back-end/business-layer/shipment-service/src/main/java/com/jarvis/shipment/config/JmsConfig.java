package com.jarvis.shipment.config;

import jakarta.jms.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class JmsConfig {
  @Bean
  public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
    return new JmsTemplate(connectionFactory);
  }
}
