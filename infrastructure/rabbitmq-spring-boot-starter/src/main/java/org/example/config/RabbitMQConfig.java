package org.example.config;

import org.example.constant.MQueue;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(RabbitTemplate.class)
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
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(10);
        return factory;
    }

    // topic交换机
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(MQueue.TOPIC_EXCHANGE);
    }

    // 审核队列
    @Bean
    public Queue auditQueue() {
        return QueueBuilder.durable(MQueue.AUDIT_QUEUE)
                .withArgument("x-message-ttl", 300000)
                .build();
    }
    @Bean
    public Binding auditVideoBinding(Queue auditQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(auditQueue)
                .to(topicExchange)
                .with(MQueue.AUDIT_ROUTING_KEY);
    }

    // 登录事件队列
    @Bean
    public Queue loginEventQueue() {
        return QueueBuilder.durable(MQueue.USER_LOGIN_QUEUE)
                .withArgument("x-message-ttl", 300000)
                .build();
    }
    @Bean
    public Binding loginEventBinding(Queue loginEventQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(loginEventQueue)
                .to(topicExchange)
                .with(MQueue.USER_LOGIN_ROUTING_KEY);
    }

    // 结果队列
    @Bean
    public Queue resultQueue() {
        return QueueBuilder.durable(MQueue.RESULT_QUEUE)
                .withArgument("x-message-ttl", 300000)
                .build();
    }
    @Bean
    public Binding resultBinding(Queue resultQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(resultQueue)
                .to(topicExchange)
                .with(MQueue.RESULT_ROUTING_KEY);
    }

    // 验证队列
    @Bean
    public Queue captchaQueue() {
        return QueueBuilder.durable(MQueue.CAPTCHA_QUEUE)
                .withArgument("x-message-ttl", 300000)
                .build();
    }
    @Bean
    public Binding captchaBinding(Queue captchaQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(captchaQueue)
                .to(topicExchange)
                .with(MQueue.CAPTCHA_ROUTING_KEY);
    }

    // 帖子行为队列
    @Bean
    public Queue postActionQueue1() {
        return QueueBuilder.durable(MQueue.POST_ACTION_QUEUE_1)
                .withArgument("x-message-ttl", 300000)
                .build();
    }
    @Bean
    public Binding postActionBinding(Queue postActionQueue1, TopicExchange topicExchange) {
        return BindingBuilder.bind(postActionQueue1)
                .to(topicExchange)
                .with(MQueue.POST_ACTION_ROUTING_KEY);
    }
    @Bean
    public Queue postActionQueue2() {
        return QueueBuilder.durable(MQueue.POST_ACTION_QUEUE_2)
                .withArgument("x-message-ttl", 300000)
                .build();
    }
    @Bean
    public Binding postActionBinding2(Queue postActionQueue2, TopicExchange topicExchange) {
        return BindingBuilder.bind(postActionQueue2)
                .to(topicExchange)
                .with(MQueue.POST_ACTION_ROUTING_KEY);
    }
    @Bean
    public Queue postActionQueue3() {
        return QueueBuilder.durable(MQueue.POST_ACTION_QUEUE_3)
                .withArgument("x-message-ttl", 300000)
                .build();
    }
    @Bean
    public Binding postActionBinding3(Queue postActionQueue3, TopicExchange topicExchange) {
        return BindingBuilder.bind(postActionQueue3)
                .to(topicExchange)
                .with(MQueue.POST_ACTION_ROUTING_KEY);
    }

    // 帖子同步队列
    @Bean
    public Queue postSyncQueue() {
        return QueueBuilder.durable(MQueue.POST_SYNC_QUEUE)
                .withArgument("x-message-ttl", 300000)
                .build();
    }
    @Bean
    public Binding postSyncBinding(Queue postSyncQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(postSyncQueue)
                .to(topicExchange)
                .with(MQueue.POST_SYNC_ROUTING_KEY);
    }
}
