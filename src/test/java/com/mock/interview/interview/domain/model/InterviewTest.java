package com.mock.interview.interview.domain.model;

import com.mock.interview.category.domain.model.JobCategory;
import com.mock.interview.category.domain.model.JobPosition;
import com.mock.interview.interview.domain.InterviewTimeHolder;
import com.mock.interview.interview.presentation.dto.InterviewConfigForm;
import com.mock.interview.interview.presentation.dto.InterviewType;
import com.mock.interview.user.domain.model.Users;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InterviewTest {

    @Test
    @DisplayName("면접 시간이 0분 이하인 면접 생성 불가능")
    void test1() {
        assertThrows(IllegalArgumentException.class, () ->
                Interview.create(
                        mock(InterviewTimeHolder.class),
                        new InterviewConfigForm(mock(InterviewType.class), 0),
                        mock(Users.class), mock(JobCategory.class), mock(JobPosition.class)
                )
        );
    }

    @Test
    @DisplayName("면접 제목은 카테고리와 포지션 영향을 받음")
    void test2() {
        String categoryName = "MyCategory";
        String positionName = "MyBackendPosition";
        JobCategory category = JobCategory.createCategory(categoryName);
        JobPosition position = JobPosition.create(positionName, category);
        InterviewTimeHolder timeHolder = interviewTimeHolder(LocalDateTime.now());

        Interview interview = Interview.create(
                timeHolder,
                new InterviewConfigForm(mock(InterviewType.class), 1),
                mock(Users.class), category, position
        );

        assertThat(interview.getTitle().getTitle()).containsIgnoringCase(categoryName);
        assertThat(interview.getTitle().getTitle()).containsIgnoringCase(positionName);
    }

    private static InterviewTimeHolder interviewTimeHolder(LocalDateTime now) {
        InterviewTimeHolder timeHolder = mock(InterviewTimeHolder.class);
        when(timeHolder.now()).thenReturn(now);
        return timeHolder;
    }

    @Test
    @DisplayName("카테고리와 관련없는 포지션으로 면접 생성 불가능")
    void test3() {
        JobCategory category = JobCategory.createCategory("MyCategory");
        JobPosition wrongPosition = JobPosition.create(
                "MyPosition", JobCategory.createCategory("NonRelatedCategory"));
        InterviewTimeHolder timeHolder = interviewTimeHolder(LocalDateTime.now());

        assertThrows(IllegalArgumentException.class, () ->
                Interview.create(
                        timeHolder,
                        new InterviewConfigForm(mock(InterviewType.class), 1),
                        mock(Users.class), category, wrongPosition
                )
        );
    }
}