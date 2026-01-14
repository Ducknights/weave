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

    // 交换机
    public static final String TOPIC_EXCHANGE = "topic_exchange";
    // 队列与路由键
    public static final String CAPTCHA_QUEUE = "captcha_queue";
    public static final String CAPTCHA_ROUTING_KEY = "captcha";

    public static final String USER_QUEUE = "user_queue";
    public static final String USER_ROUTING_KEY = "user";

    @Bean
    public Queue captchaQueue() {
        return QueueBuilder.durable(CAPTCHA_QUEUE)
                .withArgument("x-message-ttl", 300000)
                .build();
    }

    @Bean
    public Queue userQueue() {
        return QueueBuilder.durable(USER_QUEUE)
                .withArgument("x-message-ttl", 300000)
                .build();
    }

    @Bean
    public TopicExchange Exchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public Binding captchaBinding(Queue captchaQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(captchaQueue)
                .to(topicExchange)
                .with(CAPTCHA_ROUTING_KEY);
    }

    @Bean
    public Binding userBinding(Queue userQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(userQueue)
                .to(topicExchange)
                .with(USER_ROUTING_KEY);
    }
}
