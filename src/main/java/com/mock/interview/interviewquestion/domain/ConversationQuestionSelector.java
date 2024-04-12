package com.mock.interview.interviewquestion.domain;

import com.mock.interview.global.Events;
import com.mock.interview.interviewconversationpair.domain.model.InterviewConversationPair;
import com.mock.interview.interviewquestion.domain.event.ConversationQuestionCreatedEvent;
import com.mock.interview.interviewquestion.domain.model.InterviewQuestion;
import com.mock.interview.interviewquestion.presentation.dto.recommendation.RecommendationTarget;
import org.springframework.stereotype.Service;


@Service
public class ConversationQuestionSelector {
    private final int MIN_RECOMMENDED_SIZE = 50;
    private final int SINGLE = 1;
    private final int FIRST_IDX = 0;
    public void select(
            AiQuestionCreator aiCreator, QuestionRecommender recommender,
            long relatedCategoryQuestionSize, long interviewId, InterviewConversationPair pair
    ) {
        InterviewQuestion question;
        if (hasEnoughQuestion(relatedCategoryQuestionSize)) {
            RecommendationTarget target = new RecommendationTarget(interviewId, pair.getId());
            question = recommender.recommend(SINGLE, target).get(FIRST_IDX);
        } else {
            question = aiCreator.create(interviewId, AiQuestionCreator.selectCreationOption(pair));
        }

        Events.raise(new ConversationQuestionCreatedEvent(pair.getId(), question.getId()));
    }

    private boolean hasEnoughQuestion(long relatedCategoryQuestionSize) {
        return relatedCategoryQuestionSize < MIN_RECOMMENDED_SIZE;
    }

}