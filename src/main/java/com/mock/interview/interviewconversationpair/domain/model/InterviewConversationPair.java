package com.mock.interview.interviewconversationpair.domain.model;

import com.mock.interview.global.Events;
import com.mock.interview.global.auditing.BaseTimeEntity;
import com.mock.interview.interview.domain.model.Interview;
import com.mock.interview.interviewanswer.domain.model.InterviewAnswer;
import com.mock.interview.interviewconversationpair.domain.event.*;
import com.mock.interview.interviewquestion.domain.model.InterviewQuestion;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterviewConversationPair extends BaseTimeEntity {
    @Id
    @Column(name = "pair_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "interview_id")
    private Interview interview;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private InterviewQuestion question;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private InterviewAnswer answer;

    @Enumerated(EnumType.STRING)
    private PairStatus status;

    public static InterviewConversationPair create(Interview interview) {
        InterviewConversationPair conversationPair = new InterviewConversationPair();
        conversationPair.interview = interview;
        conversationPair.status = PairStatus.START;
        return conversationPair;
    }

    private void waitQuestion() {
        if(status == PairStatus.COMPLETED)
            throw new IllegalStateException();

        status = PairStatus.WAITING_QUESTION;
    }

    public void startConversation() {
        if(status != PairStatus.START)
            throw new IllegalStateException();

        waitQuestion();
        Events.raise(new ConversationStartedEvent(interview.getId(), this.id));
    }

    public void changeTopic() {
        verifyHasQuestionStatus();
        waitQuestion();
        Events.raise(new StatusChangedToChangingEvent(interview.getId(), this.id));
    }

    public void requestAi() {
        verifyHasQuestionStatus();
        waitQuestion();
        Events.raise(new AiQuestionRecommendedEvent(interview.getId(), this.id));
    }

    public void requestAnotherQuestion() {
        verifyHasQuestionStatus();
        waitQuestion();
//        Events.raise(new AnotherQuestionRecommendedEvent(interview.getId(), this.id));
        Events.raise(new ConversationStartedEvent(interview.getId(), this.id));
    }

    public void connectQuestion(InterviewQuestion question) {
        if(status != PairStatus.WAITING_QUESTION)
            throw new IllegalStateException();

        this.question = question;
        status = PairStatus.WAITING_ANSWER;
        Events.raise(new QuestionConnectedEvent(interview.getId(), this.id, question.getId()));
    }

    public void answerQuestion(InterviewAnswer answer) {
        verifyWaitingAnswerStatus();

        this.answer = answer;
        this.status = PairStatus.COMPLETED;
        Events.raise(new ConversationCompletedEvent(interview.getId()));
    }

    private void verifyHasQuestionStatus() {
        if(status != PairStatus.WAITING_ANSWER || question == null)
            throw new IllegalStateException();
    }

    private void verifyWaitingAnswerStatus() {
        if(status != PairStatus.WAITING_ANSWER)
            throw new IllegalStateException();
    }
}
