package com.mock.interview.interviewconversationpair.event;

import com.mock.interview.interviewconversationpair.domain.ConversationMessageBroker;
import com.mock.interview.interviewconversationpair.domain.QuestionConnectedEvent;
import com.mock.interview.interviewquestion.domain.exception.InterviewQuestionNotFoundException;
import com.mock.interview.interviewquestion.domain.model.InterviewQuestion;
import com.mock.interview.interviewquestion.infra.InterviewQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class QuestionConnectedEventHandler {
    private final ConversationMessageBroker messageBroker;
    private final InterviewQuestionRepository questionRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    @TransactionalEventListener(
            classes = QuestionConnectedEvent.class,
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(QuestionConnectedEvent event) {
        // TODO: 발행 실패 시 복구가 필요?
        InterviewQuestion question = questionRepository.findById(event.questionId())
                .orElseThrow(InterviewQuestionNotFoundException::new);

        messageBroker.publish(
                event.interviewId(), event.pairId(),
                question.getId(), question.getQuestion()
        );
    }
}