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

/**
 * 帖子状态机配置
 * <p>
 * 状态流转图：
 * <pre>
 *           ┌──────────┐
 *           │PUBLISHED │ (已发布)
 *           └────┬─────┘
 *       HIDE│    │DELETE
 *   ┌───────▼┐   │
 *   │ HIDDEN │   │
 *   │ (隐藏)  │   │
 *   └───┬────┘   │
 *       │RESTORE │
 *       │        │
 *       ▼          ▼
 *   PUBLISHED   DELETED (终态，不可逆)
 * </pre>
 */
@Log4j2
@Configuration
@EnableStateMachineFactory
public class PostStateMachineConfig extends EnumStateMachineConfigurerAdapter<PostStatus, PostStateEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<PostStatus, PostStateEvent> states) throws Exception {
        states
                .withStates()
                .initial(PostStatus.PUBLISHED)
                .end(PostStatus.DELETED)
                .states(EnumSet.allOf(PostStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PostStatus, PostStateEvent> transitions) throws Exception {
        transitions
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

                // 从已发布删除: PUBLISHED -> DELETED
                .withExternal()
                    .source(PostStatus.PUBLISHED).target(PostStatus.DELETED)
                    .event(PostStateEvent.DELETE)
                    .guard(notDeletedGuard())
                    .action(context -> log.info("已发布帖子已删除"))
                    .and()

                // 从隐藏删除: HIDDEN -> DELETED
                .withExternal()
                    .source(PostStatus.HIDDEN).target(PostStatus.DELETED)
                    .event(PostStateEvent.DELETE)
                    .guard(notDeletedGuard())
                    .action(context -> log.info("隐藏帖子已删除"))
        ;
    }

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
