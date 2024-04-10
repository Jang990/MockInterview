package com.mock.interview.interviewconversationpair.domain.event;

import com.mock.interview.interview.infra.lock.response.LockableInterviewEvent;

public record ConversationStartedEvent(long interviewId, long pairId) implements LockableInterviewEvent {
    @Override
    public Long getInterviewId() {
        return this.interviewId();
    }
}
