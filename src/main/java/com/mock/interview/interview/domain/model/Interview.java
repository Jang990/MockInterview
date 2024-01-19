package com.mock.interview.interview.domain.model;

import com.mock.interview.global.Events;
import com.mock.interview.global.auditing.BaseTimeEntity;
import com.mock.interview.interview.domain.InterviewStartedEvent;
import com.mock.interview.interview.domain.exception.InterviewAlreadyInProgressException;
import com.mock.interview.interview.domain.exception.IsAlreadyTimeoutInterviewException;
import com.mock.interview.candidate.domain.model.CandidateConfig;
import com.mock.interview.interview.infrastructure.InterviewRepository;
import com.mock.interview.user.domain.model.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Interview extends BaseTimeEntity {
    @Id
    @Column(name = "interview_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private InterviewTitle title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(nullable = false)
    private LocalDateTime expiredTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_profile_id")
    private CandidateConfig candidateConfig;

    public static Interview startInterview(
            InterviewRepository interviewRepository,
            CandidateConfig config, Users user
    ) {
        if (interviewRepository.findActiveInterview(user.getId()).isPresent()) // TODO: QueryDSL로 최적화
            throw new InterviewAlreadyInProgressException();
        Events.raise(new InterviewStartedEvent(user.getId()));

        Interview interview = new Interview();
        interview.title = new InterviewTitle(config.getDepartment().getName(), config.getAppliedJob().getName());
        LocalDateTime now = LocalDateTime.now();
        interview.expiredTime = now.plusMinutes(config.getDurationMinutes());
        interview.isDeleted = false;
        interview.users = user;
        interview.candidateConfig = config;

        return interview;
    }

    // TODO: 사용자가 면접을 강제 종료할 수 있게 만들어야 함.
    public void expire() {
        if(!isInterviewInProgressTime())
            throw new IsAlreadyTimeoutInterviewException();

        expiredTime = LocalDateTime.now();
    }

    private boolean isInterviewInProgressTime() {
        return LocalDateTime.now().isBefore(expiredTime);
    }
}
