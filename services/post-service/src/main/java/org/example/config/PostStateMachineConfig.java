package org.example.config;

import lombok.extern.log4j.Log4j2;
import org.example.model.enums.PostStateEvent;
import org.example.model.enums.PostStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

import java.util.EnumSet;

@Log4j2
@Configuration
@EnableStateMachineFactory
public class PostStateMachineConfig extends EnumStateMachineConfigurerAdapter<PostStatus, PostStateEvent> {

    /**
     * 配置状态
     */
    @Override
    public void configure(StateMachineStateConfigurer<PostStatus, PostStateEvent> states) throws Exception {
        states
                .withStates()
                .initial(PostStatus.PENDING)
                .end(PostStatus.DELETED)
                .states(EnumSet.allOf(PostStatus.class));
    }

    /**
     * 配置状态转换
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<PostStatus, PostStateEvent> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(PostStatus.PENDING).target(PostStatus.PENDING)
                    .event(PostStateEvent.SUBMIT)
                    .action(context -> log.info("帖子已提交审核: {}", context.getMessageHeaders()))
                    .and()

                // 审核通过: PENDING -> PUBLISHED
                .withExternal()
                    .source(PostStatus.PENDING).target(PostStatus.PUBLISHED)
                    .event(PostStateEvent.APPROVE)
                    .action(context -> log.info("帖子审核通过并发布"))
                    .and()

                // 审核拒绝: PENDING -> HIDDEN
                .withExternal()
                    .source(PostStatus.PENDING).target(PostStatus.HIDDEN)
                    .event(PostStateEvent.REJECT)
                    .action(context -> log.info("帖子审核被拒绝，已隐藏"))
                    .and()

                // 隐藏已发布帖子: PUBLISHED -> HIDDEN
                .withExternal()
                    .source(PostStatus.PUBLISHED).target(PostStatus.HIDDEN)
                    .event(PostStateEvent.HIDE)
                    .action(context -> log.info("帖子已隐藏"))
                    .and()

                // 恢复隐藏帖子: HIDDEN -> PUBLISHED
                .withExternal()
                    .source(PostStatus.HIDDEN).target(PostStatus.PUBLISHED)
                    .event(PostStateEvent.RESTORE)
                    .action(context -> log.info("帖子已恢复显示"))
                    .and()

                // 从待审核删除
                .withExternal()
                    .source(PostStatus.PENDING).target(PostStatus.DELETED)
                    .event(PostStateEvent.DELETE)
                    .guard(notDeletedGuard())
                    .action(context -> log.info("待审核帖子已删除"))
                    .and()

                // 从已发布删除
                .withExternal()
                    .source(PostStatus.PUBLISHED).target(PostStatus.DELETED)
                    .event(PostStateEvent.DELETE)
                    .guard(notDeletedGuard())
                    .action(context -> log.info("已发布帖子已删除"))
                    .and()

                // 从隐藏删除
                .withExternal()
                    .source(PostStatus.HIDDEN).target(PostStatus.DELETED)
                    .event(PostStateEvent.DELETE)
                    .guard(notDeletedGuard())
                    .action(context -> log.info("隐藏帖子已删除"))
                    .and()

                // 从待审核隐藏（等同REJECT但无审核含义）
                .withExternal()
                    .source(PostStatus.PENDING).target(PostStatus.HIDDEN)
                    .event(PostStateEvent.HIDE)
                    .action(context -> log.info("待审核帖子已隐藏"))
        ;
    }

    /**
     * 守卫：已经删除的帖子不允许再执行任何操作
     */
    private Guard<PostStatus, PostStateEvent> notDeletedGuard() {
        return context -> {
            PostStatus currentState = context.getStateMachine().getState().getId();
            boolean notDeleted = currentState != PostStatus.DELETED;
            if (!notDeleted) {
                log.warn("帖子已删除，无法操作");
            }
            return notDeleted;
        };
    }
}
