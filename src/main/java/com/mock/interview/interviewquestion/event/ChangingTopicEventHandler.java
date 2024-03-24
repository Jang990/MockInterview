package com.mock.interview.interviewquestion.event;

import com.mock.interview.interviewconversationpair.infra.ConversationCacheForAiRequest;
import com.mock.interview.interviewquestion.domain.CreationInterviewQuestionService;
import com.mock.interview.interviewquestion.infra.ai.AiQuestionCreator;
import com.mock.interview.interview.infrastructure.lock.proceeding.AiResponseProcessingLock;
import com.mock.interview.interview.domain.exception.InterviewNotFoundException;
import com.mock.interview.interview.domain.model.Interview;
import com.mock.interview.interview.infrastructure.InterviewCacheForAiRequest;
import com.mock.interview.interview.infrastructure.InterviewRepository;
import com.mock.interview.interviewconversationpair.domain.PairStatusChangedToChangingEvent;
import com.mock.interview.interviewconversationpair.domain.exception.InterviewConversationPairNotFoundException;
import com.mock.interview.interviewconversationpair.domain.model.InterviewConversationPair;
import com.mock.interview.interviewconversationpair.infra.InterviewConversationPairRepository;
import com.mock.interview.interviewquestion.infra.InterviewQuestionRepository;
import com.mock.interview.interviewquestion.infra.RecommendedQuestion;
import com.mock.interview.tech.application.TechSavingHelper;
import com.mock.interview.tech.domain.model.TechnicalSubjects;
import com.mock.interview.tech.infrastructure.TechnicalSubjectsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChangingTopicEventHandler {
    private final AiQuestionCreator aiQuestionCreator;
    private final InterviewRepository interviewRepository;
    private final InterviewConversationPairRepository conversationPairRepository;
    private final InterviewCacheForAiRequest interviewCache;
    private final ConversationCacheForAiRequest conversationCache;
    private final InterviewQuestionRepository questionRepository;
    private final TechnicalSubjectsRepository technicalSubjectsRepository;
    private final CreationInterviewQuestionService domainService;

    @Async
    @AiResponseProcessingLock
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(
            classes = PairStatusChangedToChangingEvent.class,
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleChangingTopicRequest(PairStatusChangedToChangingEvent event) {
        long interviewId = event.interviewId();
        InterviewConversationPair conversationPair = conversationPairRepository.findById(event.pairId())
                .orElseThrow(InterviewConversationPairNotFoundException::new);

        RecommendedQuestion recommendedQuestion = AiQuestionHelper.changeTopic(aiQuestionCreator, interviewCache, conversationCache, event.interviewId());
        saveQuestion(interviewId, recommendedQuestion, conversationPair);
    }

    private void saveQuestion(long interviewId, RecommendedQuestion recommendedQuestion, InterviewConversationPair conversationPair) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(InterviewNotFoundException::new);
        List<TechnicalSubjects> techList = TechSavingHelper.saveTechIfNotExist(technicalSubjectsRepository, recommendedQuestion.topic());
        domainService.changeTopic(questionRepository, interview, conversationPair, recommendedQuestion, techList);
    }
}
