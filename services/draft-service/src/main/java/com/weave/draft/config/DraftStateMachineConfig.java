package com.weave.draft.config;

import com.weave.draft.model.enums.DraftStateEvent;
import com.weave.draft.model.enums.DraftStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

/**
 * 草稿审核状态机配置
 */
@Log4j2
@Configuration
@EnableStateMachineFactory
public class DraftStateMachineConfig extends EnumStateMachineConfigurerAdapter<DraftStatus, DraftStateEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<DraftStatus, DraftStateEvent> states) throws Exception {
        states
                .withStates()
                .initial(DraftStatus.DRAFT)
                .end(DraftStatus.APPROVED)
                .states(EnumSet.allOf(DraftStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<DraftStatus, DraftStateEvent> transitions) throws Exception {
        transitions
                // 提交审核: DRAFT -> PENDING
                .withExternal()
                    .source(DraftStatus.DRAFT).target(DraftStatus.PENDING)
                    .event(DraftStateEvent.SUBMIT)
                    .action(context -> log.info("草稿已提交审核"))
                    .and()

                // 驳回后重新提交审核: REJECTED -> PENDING
                .withExternal()
                    .source(DraftStatus.REJECTED).target(DraftStatus.PENDING)
                    .event(DraftStateEvent.SUBMIT)
                    .action(context -> log.info("驳回草稿重新提交审核"))
                    .and()

                // 审核通过: PENDING -> APPROVED
                .withExternal()
                    .source(DraftStatus.PENDING).target(DraftStatus.APPROVED)
                    .event(DraftStateEvent.APPROVE)
                    .action(context -> log.info("草稿审核通过"))
                    .and()

                // 审核驳回: PENDING -> REJECTED
                .withExternal()
                    .source(DraftStatus.PENDING).target(DraftStatus.REJECTED)
                    .event(DraftStateEvent.REJECT)
                    .action(context -> log.info("草稿审核驳回"))
        ;
    }
}
