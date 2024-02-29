package com.mock.interview.interviewconversationpair.event;

import com.mock.interview.interview.domain.ConversationMessageBroker;
import com.mock.interview.interview.presentation.dto.InterviewRole;
import com.mock.interview.interview.presentation.dto.message.QuestionInInterviewDto;
import com.mock.interview.interview.domain.exception.InterviewNotExpiredException;
import com.mock.interview.interview.domain.model.Interview;
import com.mock.interview.interview.infrastructure.InterviewRepository;
import com.mock.interview.interviewanswer.domain.AnsweredInCustomInterviewEvent;
import com.mock.interview.interviewanswer.domain.exception.InterviewAnswerNotFoundException;
import com.mock.interview.interviewanswer.domain.model.InterviewAnswer;
import com.mock.interview.interviewanswer.infra.InterviewAnswerRepository;
import com.mock.interview.interviewconversationpair.domain.exception.InterviewConversationPairNotFoundException;
import com.mock.interview.interviewconversationpair.domain.model.InterviewConversationPair;
import com.mock.interview.interviewconversationpair.infra.InterviewConversationPairRepository;
import com.mock.interview.interviewquestion.domain.exception.InterviewQuestionNotFoundException;
import com.mock.interview.interviewquestion.domain.model.InterviewQuestion;
import com.mock.interview.interviewquestion.event.CreatedRunningInterviewQuestionEvent;
import com.mock.interview.interviewquestion.infra.InterviewQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RunningInterviewQuestionHandler {
    private final InterviewConversationPairRepository interviewConversationPairRepository;
    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final InterviewAnswerRepository answerRepository;
    private final ConversationMessageBroker conversationMessageBroker;

    @EventListener(CreatedRunningInterviewQuestionEvent.class)
    public void handle(CreatedRunningInterviewQuestionEvent event) {
        long interviewId = event.interviewId();
        long questionId = event.questionId();
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(InterviewNotExpiredException::new);
        InterviewQuestion question = interviewQuestionRepository.findById(questionId)
                .orElseThrow(InterviewQuestionNotFoundException::new);

        InterviewConversationPair conversationPair = InterviewConversationPair.startConversation(interview, question);
        interviewConversationPairRepository.save(conversationPair);
        conversationMessageBroker.publish(interviewId,
                new QuestionInInterviewDto(conversationPair.getId(), questionId, InterviewRole.USER.name(), question.getQuestion()));
    }

    @EventListener(AnsweredInCustomInterviewEvent.class)
    public void handle(AnsweredInCustomInterviewEvent event) {
        long answerId = event.answerId();
        long pairId = event.pairId();
        InterviewAnswer answer = answerRepository.findById(answerId)
                .orElseThrow(InterviewAnswerNotFoundException::new);
        InterviewConversationPair conversationPair = interviewConversationPairRepository.findById(pairId)
                .orElseThrow(InterviewConversationPairNotFoundException::new);

        conversationPair.answerQuestion(answer);
    }
}
