package org.example.service;

import jakarta.annotation.Resource;
import org.example.mapper.MessageMapper;
import org.example.model.entity.Message;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 长轮询服务类
 * 用于实现消息的长轮询机制，当有新消息到达时及时通知客户端
 * 长轮询监听用户的所有会话，返回用户ID > last_received_id的所有消息
 * 服务器不标记任何"已推送"状态，由前端负责小红点逻辑
 */
@Service
public class LongPollingService {

    @Resource
    private MessageMapper messageMapper;

    // 存储用户ID与轮询请求的映射关系，用于在新消息到达时唤醒对应的轮询请求
    private final Map<Long, PollingRequest> pollingRequests = new ConcurrentHashMap<>();
    // 用于保护 pollingRequests 的并发访问锁
    private final Lock lock = new ReentrantLock();
    // 线程池，用于异步执行轮询任务
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    // 轮询超时时间：30秒
    private static final long POLLING_TIMEOUT = 30000;

    /**
     * 长轮询获取用户的新消息列表
     * 监听用户的所有会话，返回用户ID > last_received_id的所有消息
     * @param userId 用户ID
     * @param lastReceivedId 客户端最后收到的消息ID
     * @return 新消息列表，如果没有新消息则返回空列表
     */
    public List<Message> longPollNewMessages(Long userId, Long lastReceivedId) {
        // 查询数据库获取用户ID > last_received_id的所有消息
        List<Message> newMessages = messageMapper.selectNewMessages(userId, lastReceivedId);
        
        // 如果有新消息，立即返回
        if (!newMessages.isEmpty()) {
            return newMessages;
        }
        
        // 如果没有新消息，进入异步等待模式
        return waitForNewMessagesAsync(userId, lastReceivedId);
    }

    /**
     * 异步等待新消息
     * 创建一个轮询请求并注册到 pollingRequests 中，等待被唤醒或超时
     * @param userId 用户ID
     * @param lastReceivedId 客户端最后收到的消息ID
     * @return 新消息列表，如果没有新消息则返回空列表
     */
    private List<Message> waitForNewMessagesAsync(Long userId, Long lastReceivedId) {
        PollingRequest request = new PollingRequest();
        
        // 将请求注册到轮询请求映射表中，使用锁保证线程安全
        try {
            lock.lock();
            pollingRequests.put(userId, request);
        } finally {
            lock.unlock();
        }
        
        try {
            // 提交异步任务到线程池执行
            Future<List<Message>> future = executorService.submit(() -> {
                try {
                    // 等待新消息通知或超时
                    boolean hasNewMessage = request.await(POLLING_TIMEOUT);
                    
                    if (hasNewMessage) {
                        // 被唤醒，说明有新消息，查询数据库获取用户ID > last_received_id的所有消息
                        return messageMapper.selectNewMessages(userId, lastReceivedId);
                    }
                    
                    // 超时后返回空列表（表示没有新消息）
                    return List.of();
                } catch (InterruptedException e) {
                    // 线程被中断，恢复中断状态并返回空列表
                    Thread.currentThread().interrupt();
                    return List.of();
                }
            });
            
            // 等待异步任务完成，设置比轮询超时稍长的时间
            return future.get(POLLING_TIMEOUT + 1000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // 发生异常时返回空列表
            return List.of();
        } finally {
            // 清理轮询请求，从映射表中移除
            try {
                lock.lock();
                pollingRequests.remove(userId);
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 通知有新消息到达
     * 唤醒接收者的轮询请求
     * @param toUserId 接收者ID
     */
    public void notifyNewMessage(Long toUserId) {
        try {
            lock.lock();
            // 唤醒接收者的轮询请求
            PollingRequest request = pollingRequests.get(toUserId);
            if (request != null) {
                request.wakeup();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 轮询请求内部类
     * 使用 CountDownLatch 实现等待/唤醒机制
     */
    private static class PollingRequest {
        // 倒计时闩，初始值为1，用于实现等待/唤醒
        private final CountDownLatch latch = new CountDownLatch(1);

        /**
         * 等待被唤醒或超时
         * @param timeout 超时时间（毫秒）
         * @return true表示被唤醒（有新消息），false表示超时
         * @throws InterruptedException 线程被中断
         */
        public boolean await(long timeout) throws InterruptedException {
            return latch.await(timeout, TimeUnit.MILLISECONDS);
        }

        /**
         * 唤醒等待的请求
         */
        public void wakeup() {
            latch.countDown();
        }
    }
}
