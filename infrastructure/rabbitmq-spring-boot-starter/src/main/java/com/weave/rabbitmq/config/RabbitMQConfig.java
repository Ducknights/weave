package com.weave.rabbitmq.config;

import com.weave.rabbitmq.constant.MQueue;
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

    // 交换机
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(MQueue.TOPIC_EXCHANGE);
    }
    @Bean
    public TopicExchange dlxExchange() {
        return new TopicExchange(MQueue.DLX_EXCHANGE);
    }

    /**
     * 构建带死信配置的持久化队列
     */
    private static Queue durableQueueWithDlq(String queueName, String dlqRoutingKey) {
        return QueueBuilder.durable(queueName)
                .withArgument("x-message-ttl", 300000)
                .withArgument("x-dead-letter-exchange", MQueue.DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey)
                .build();
    }

    /**
     * 构建无死信配置的持久化队列
     */
    private static Queue durableQueue(String queueName) {
        return QueueBuilder.durable(queueName)
                .withArgument("x-message-ttl", 300000)
                .build();
    }

    // 验证码队列
    @Bean
    public Queue captchaQueue() {
        return durableQueue(MQueue.CAPTCHA_QUEUE);
    }
    @Bean
    public Binding captchaBinding(Queue captchaQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(captchaQueue).to(topicExchange).with(MQueue.CAPTCHA_ROUTING_KEY);
    }

    // 用户缓存队列
    @Bean
    public Queue userInfoQueue() {
        return durableQueueWithDlq(MQueue.USER_CACHE_QUEUE, MQueue.USER_CACHE_DLQ_ROUTING_KEY);
    }

    @Bean
    public Binding userInfoBinding(Queue userInfoQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(userInfoQueue).to(topicExchange).with(MQueue.USER_CACHE_ROUTING_KEY);
    }

    @Bean
    public Queue userInfoDlq() {
        return QueueBuilder.durable(MQueue.USER_CACHE_DLQ).build();
    }

    @Bean
    public Binding userInfoDlqBinding(Queue userInfoDlq, TopicExchange dlxExchange) {
        return BindingBuilder.bind(userInfoDlq).to(dlxExchange).with(MQueue.USER_CACHE_DLQ_ROUTING_KEY);
    }

    // 审核队列
    @Bean
    public Queue auditQueue() {
        return durableQueueWithDlq(MQueue.AUDIT_QUEUE, MQueue.AUDIT_DLQ_ROUTING_KEY);
    }

    @Bean
    public Binding auditVideoBinding(Queue auditQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(auditQueue).to(topicExchange).with(MQueue.AUDIT_ROUTING_KEY);
    }

    @Bean
    public Queue auditDlq() {
        return QueueBuilder.durable(MQueue.AUDIT_DLQ).build();
    }

    @Bean
    public Binding auditDlqBinding(Queue auditDlq, TopicExchange dlxExchange) {
        return BindingBuilder.bind(auditDlq).to(dlxExchange).with(MQueue.AUDIT_DLQ_ROUTING_KEY);
    }

    // 结果队列

    @Bean
    public Queue resultQueue() {
        return durableQueueWithDlq(MQueue.RESULT_QUEUE, MQueue.RESULT_DLQ_ROUTING_KEY);
    }

    @Bean
    public Binding resultBinding(Queue resultQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(resultQueue).to(topicExchange).with(MQueue.RESULT_ROUTING_KEY);
    }

    @Bean
    public Queue resultDlq() {
        return QueueBuilder.durable(MQueue.RESULT_DLQ).build();
    }

    @Bean
    public Binding resultDlqBinding(Queue resultDlq, TopicExchange dlxExchange) {
        return BindingBuilder.bind(resultDlq).to(dlxExchange).with(MQueue.RESULT_DLQ_ROUTING_KEY);
    }

    // 帖子行为队列

    @Bean
    public Queue postActionQueue1() {
        return durableQueueWithDlq(MQueue.POST_ACTION_QUEUE_1, MQueue.POST_ACTION_DLQ_ROUTING_KEY);
    }

    @Bean
    public Binding postActionBinding(Queue postActionQueue1, TopicExchange topicExchange) {
        return BindingBuilder.bind(postActionQueue1).to(topicExchange).with(MQueue.POST_ACTION_ROUTING_KEY);
    }

    @Bean
    public Queue postActionDlq1() {
        return QueueBuilder.durable(MQueue.POST_ACTION_DLQ_1).build();
    }

    @Bean
    public Binding postActionDlqBinding1(Queue postActionDlq1, TopicExchange dlxExchange) {
        return BindingBuilder.bind(postActionDlq1).to(dlxExchange).with(MQueue.POST_ACTION_DLQ_ROUTING_KEY);
    }

    @Bean
    public Queue postActionQueue2() {
        return durableQueueWithDlq(MQueue.POST_ACTION_QUEUE_2, MQueue.POST_ACTION_DLQ_ROUTING_KEY);
    }

    @Bean
    public Binding postActionBinding2(Queue postActionQueue2, TopicExchange topicExchange) {
        return BindingBuilder.bind(postActionQueue2).to(topicExchange).with(MQueue.POST_ACTION_ROUTING_KEY);
    }

    @Bean
    public Queue postActionDlq2() {
        return QueueBuilder.durable(MQueue.POST_ACTION_DLQ_2).build();
    }

    @Bean
    public Binding postActionDlqBinding2(Queue postActionDlq2, TopicExchange dlxExchange) {
        return BindingBuilder.bind(postActionDlq2).to(dlxExchange).with(MQueue.POST_ACTION_DLQ_ROUTING_KEY);
    }

    @Bean
    public Queue postActionQueue3() {
        return durableQueueWithDlq(MQueue.POST_ACTION_QUEUE_3, MQueue.POST_ACTION_DLQ_ROUTING_KEY);
    }

    @Bean
    public Binding postActionBinding3(Queue postActionQueue3, TopicExchange topicExchange) {
        return BindingBuilder.bind(postActionQueue3).to(topicExchange).with(MQueue.POST_ACTION_ROUTING_KEY);
    }

    @Bean
    public Queue postActionDlq3() {
        return QueueBuilder.durable(MQueue.POST_ACTION_DLQ_3).build();
    }

    @Bean
    public Binding postActionDlqBinding3(Queue postActionDlq3, TopicExchange dlxExchange) {
        return BindingBuilder.bind(postActionDlq3).to(dlxExchange).with(MQueue.POST_ACTION_DLQ_ROUTING_KEY);
    }

    // 帖子同步队列

    @Bean
    public Queue postSyncQueue() {
        return durableQueueWithDlq(MQueue.POST_SYNC_QUEUE, MQueue.POST_SYNC_DLQ_ROUTING_KEY);
    }

    @Bean
    public Binding postSyncBinding(Queue postSyncQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(postSyncQueue).to(topicExchange).with(MQueue.POST_SYNC_ROUTING_KEY);
    }

    @Bean
    public Queue postSyncDlq() {
        return QueueBuilder.durable(MQueue.POST_SYNC_DLQ).build();
    }

    @Bean
    public Binding postSyncDlqBinding(Queue postSyncDlq, TopicExchange dlxExchange) {
        return BindingBuilder.bind(postSyncDlq).to(dlxExchange).with(MQueue.POST_SYNC_DLQ_ROUTING_KEY);
    }

    // 草稿发布队列
    @Bean
    public Queue draftPublishQueue() {
        return durableQueueWithDlq(MQueue.DRAFT_PUBLISH_QUEUE, MQueue.DRAFT_PUBLISH_DLQ_ROUTING_KEY);
    }

    @Bean
    public Binding draftPublishBinding(Queue draftPublishQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(draftPublishQueue).to(topicExchange).with(MQueue.DRAFT_PUBLISH_ROUTING_KEY);
    }

    @Bean
    public Queue draftPublishDlq() {
        return QueueBuilder.durable(MQueue.DRAFT_PUBLISH_DLQ).build();
    }

    @Bean
    public Binding draftPublishDlqBinding(Queue draftPublishDlq, TopicExchange dlxExchange) {
        return BindingBuilder.bind(draftPublishDlq).to(dlxExchange).with(MQueue.DRAFT_PUBLISH_DLQ_ROUTING_KEY);
    }

    // 草稿发布结果回执队列
    @Bean
    public Queue draftPublishResultQueue() {
        return durableQueueWithDlq(MQueue.DRAFT_PUBLISH_RESULT_QUEUE, MQueue.DRAFT_PUBLISH_RESULT_DLQ_ROUTING_KEY);
    }

    @Bean
    public Binding draftPublishResultBinding(Queue draftPublishResultQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(draftPublishResultQueue).to(topicExchange).with(MQueue.DRAFT_PUBLISH_RESULT_ROUTING_KEY);
    }

    @Bean
    public Queue draftPublishResultDlq() {
        return QueueBuilder.durable(MQueue.DRAFT_PUBLISH_RESULT_DLQ).build();
    }

    @Bean
    public Binding draftPublishResultDlqBinding(Queue draftPublishResultDlq, TopicExchange dlxExchange) {
        return BindingBuilder.bind(draftPublishResultDlq).to(dlxExchange).with(MQueue.DRAFT_PUBLISH_RESULT_DLQ_ROUTING_KEY);
    }

    // 帖子缓存队列
    @Bean
    public Queue postCacheQueue() {
        return durableQueueWithDlq(MQueue.POST_CACHE_QUEUE, MQueue.POST_CACHE_DLQ_ROUTING_KEY);
    }

    @Bean
    public Binding postCacheBinding(Queue postCacheQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(postCacheQueue).to(topicExchange).with(MQueue.POST_CACHE_ROUTING_KEY);
    }

    @Bean
    public Queue postCacheDlq() {
        return QueueBuilder.durable(MQueue.POST_CACHE_DLQ).build();
    }

    @Bean
    public Binding postCacheDlqBinding(Queue postCacheDlq, TopicExchange dlxExchange) {
        return BindingBuilder.bind(postCacheDlq).to(dlxExchange).with(MQueue.POST_CACHE_DLQ_ROUTING_KEY);
    }

    // 聊天推送队列
    @Bean
    public Queue messagePushQueue() {
        return durableQueue(MQueue.CHAT_PUSH_QUEUE);
    }

    @Bean
    public Binding messagePushBinding(Queue messagePushQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(messagePushQueue).to(topicExchange).with(MQueue.CHAT_PUSH_ROUTING_KEY);
    }
}
