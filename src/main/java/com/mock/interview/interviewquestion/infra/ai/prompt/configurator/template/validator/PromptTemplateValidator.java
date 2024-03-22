package com.mock.interview.interviewquestion.infra.ai.prompt.configurator.template.validator;


import com.mock.interview.interviewquestion.infra.ai.dto.InterviewProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @see com.mock.interview.interviewquestion.infra.ai.prompt.fomatter.StringFormatter
 * StringFormatter를 통해 Prompt를 만들 때 템플릿 필수 정보를 설정하는 인터페이스
 */
@Component
public class PromptTemplateValidator {
    @Value("${interview.format.profile.field}")
    private String fieldFormat;

    @Value("${interview.format.profile.skills}")
    private String skillsFormat;

    @Value("${interview.format.profile.department}")
    private String departmentFormat;

    @Value("${interview.format.profile.experience}")
    private String experienceFormat;

    public void verify(String rawTemplate, InterviewProfile profile) {
        if(hasError(rawTemplate, profile))
            throw new InvalidConfigurationException();
    }

    private boolean hasError(String rawTemplate, InterviewProfile profile) {
        return hasTemplateValueError(rawTemplate, fieldFormat, profile.field())
                && hasTemplateValueError(rawTemplate, departmentFormat, profile.department())
                && hasTemplateValueError(rawTemplate, skillsFormat, profile.skills())
                && hasTemplateValueError(rawTemplate, experienceFormat, profile.experience());
    }

    private boolean hasTemplateValueError(String rawTemplate, String format, String value) {
        return rawTemplate.contains(format) && isEmpty(value);
    }

    private boolean hasTemplateValueError(String rawTemplate, String format, List<String> value) {
        return rawTemplate.contains(format) && isEmpty(value);
    }

    private boolean isEmpty(String value) {
        return !StringUtils.hasText(value);
    }

    private boolean isEmpty(List<String> value) {
        return value == null || value.isEmpty() || value.stream().noneMatch(StringUtils::hasText);
    }
}