package com.mock.interview.user.application;

import com.mock.interview.tech.domain.model.TechnicalSubjects;
import com.mock.interview.candidate.presentation.dto.CandidateProfileForm;
import com.mock.interview.candidate.domain.model.CandidateProfile;
import com.mock.interview.candidate.presentation.dto.CandidateProfileOverviewDto;

import java.util.List;

public class UserConvertor {
    public static List<CandidateProfileOverviewDto> entityToOverviewDto(List<CandidateProfile> entityList) {
        return entityList.stream()
                .map((entity) -> new CandidateProfileOverviewDto(
                        entity.getId(),
                        entity.getTitle(), entity.getDepartment().getName(),
                        entity.getField().getName(), entity.getCreatedAt()
                )).toList();
    }

    public static CandidateProfileForm entityToDto(CandidateProfile profile) {
        return new CandidateProfileForm(
                profile.getField().getId(),
                profile.getTechSubjects().stream().map(TechnicalSubjects::getName).toList(),
                profile.getExperience()
        );
    }
}
