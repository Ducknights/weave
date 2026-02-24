package org.example.config;

import org.example.strings.MQueue;
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
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(MQueue.TOPIC_EXCHANGE);
    }

    // 图片审核队列
    @Bean
    public Queue auditImageQueue() {
        return QueueBuilder.durable(MQueue.AUDIT_QUEUE)
                .withArgument("x-message-ttl", 300000)
                .build();
    }

    // 审核图片队列绑定
    @Bean
    public Binding auditImageBinding(Queue auditImageQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(auditImageQueue)
                .to(topicExchange)
                .with(MQueue.AUDIT_IMAGE_ROUTING_KEY);
    }

    // 视频审核队列
    @Bean
    public Queue auditVideoQueue() {
        return QueueBuilder.durable(MQueue.AUDIT_VIDEO_QUEUE)
                .withArgument("x-message-ttl", 300000)
                .build();
    }

    // 审核视频队列绑定
    @Bean
    public Binding auditVideoBinding(Queue auditVideoQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(auditVideoQueue)
                .to(topicExchange)
                .with(MQueue.AUDIT_VIDEO_ROUTING_KEY);
    }

    // 结果通知队列
    @Bean
    public Queue resultQueue() {
        return QueueBuilder.durable(MQueue.RESULT_QUEUE)
                .withArgument("x-message-ttl", 300000)
                .build();
    }

    // 结果通知队列绑定
    @Bean
    public Binding resultBinding(Queue resultQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(resultQueue)
                .to(topicExchange)
                .with(MQueue.RESULT_ROUTING_KEY);
    }

    // 验证码队列
    @Bean
    public Queue captchaQueue() {
        return QueueBuilder.durable(MQueue.CAPTCHA_QUEUE)
                .withArgument("x-message-ttl", 300000)
                .build();
    }

    // 验证码队列绑定
    @Bean
    public Binding captchaBinding(Queue captchaQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(captchaQueue)
                .to(topicExchange)
                .with(MQueue.CAPTCHA_ROUTING_KEY);
    }
}
