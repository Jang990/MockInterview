package com.mock.interview.interview.application;

import com.mock.interview.interview.infra.lock.progress.dto.InterviewLockDto;
import com.mock.interview.interviewconversationpair.application.ConversationConvertor;
import com.mock.interview.interviewconversationpair.domain.model.InterviewConversationPair;
import com.mock.interview.interviewconversationpair.infra.InterviewConversationRepositoryForView;
import com.mock.interview.interviewconversationpair.presentation.dto.ConversationContentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InterviewViewService {
    private final InterviewConversationRepositoryForView conversationRepositoryForView;

    // TODO: 페이징 처리 필요.
    public List<ConversationContentDto> findInterviewHistory(InterviewLockDto interviewUserDto) {
        List<InterviewConversationPair> conversations = conversationRepositoryForView
                .findOrderedByCreatedAt(interviewUserDto.interviewId(), interviewUserDto.userId());

        return conversations.stream().map(
                conversationPair -> ConversationConvertor.convert(
                        conversationPair,
                        conversationPair.getQuestion(),
                        conversationPair.getAnswer()
                )
        ).toList();
    }
}