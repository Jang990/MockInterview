package com.mock.interview.interviewconversationpair.domain;

import com.mock.interview.interview.infrastructure.lock.proceeding.LockableCustomInterviewEvent;

public record PairStatusChangedToChangingEvent(long interviewId, long pairId) implements LockableCustomInterviewEvent {
    @Override
    public Long getInterviewId() {
        return this.interviewId();
    }
}
