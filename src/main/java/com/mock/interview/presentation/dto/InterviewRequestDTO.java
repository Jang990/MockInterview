package com.mock.interview.presentation.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewRequestDTO {
    private InterviewSettingDto interviewSetting;
    @Valid
    private MessageHistoryDto messageHistory;
}