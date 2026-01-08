package org.example.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(10);
        return factory;
    }

    // 验证码队列
    public static final String CAPTCHA_QUEUE = "captcha_queue";
    public static final String CAPTCHA_EXCHANGE = "captcha_exchange";
    public static final String CAPTCHA_ROUTING_KEY = "captcha";

    @Bean
    public Queue captchaQueue() {
        return QueueBuilder.durable(CAPTCHA_QUEUE)
                .withArgument("x-message-ttl", 300000)
                .build();
    }

    @Bean
    public TopicExchange captchaExchange() {
        return new TopicExchange(CAPTCHA_EXCHANGE);
    }

    @Bean
    public Binding captchaBinding(Queue captchaQueue, TopicExchange captchaExchange) {
        return BindingBuilder.bind(captchaQueue)
                .to(captchaExchange)
                .with(CAPTCHA_ROUTING_KEY);
    }
}
