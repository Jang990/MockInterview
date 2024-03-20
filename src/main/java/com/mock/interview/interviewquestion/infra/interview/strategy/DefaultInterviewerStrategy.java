package com.mock.interview.interviewquestion.infra.interview.strategy;

import com.mock.interview.interviewquestion.infra.interview.dto.InterviewInfo;
import com.mock.interview.interviewquestion.infra.interview.dto.InterviewProfile;
import com.mock.interview.interviewquestion.infra.interview.dto.PromptCreationInfo;
import com.mock.interview.interviewquestion.infra.interview.gpt.AISpecification;
import com.mock.interview.interviewquestion.infra.interview.setting.PromptCreator;
import com.mock.interview.interviewquestion.infra.interview.strategy.stage.InterviewProgress;
import com.mock.interview.interviewquestion.infra.interview.strategy.stage.InterviewProgressTimeBasedTracker;
import com.mock.interview.interviewquestion.infra.interview.setting.AiPrompt;
import com.mock.interview.interviewquestion.infra.interview.strategy.stage.InterviewStage;
import lombok.RequiredArgsConstructor;

//@Order(1)
//@Component
@RequiredArgsConstructor
public class DefaultInterviewerStrategy implements InterviewerStrategy {
    private final InterviewProgressTimeBasedTracker progressTracker;
    private final PromptCreator promptCreator;
    private final DefaultInterviewConcept interviewConcept;

    // TODO: 수정 많이 필요.
    //      -> IT 먼저 끝내고 바꿀 것.

    @Override
    public AiPrompt configStrategy(AISpecification aiSpec, InterviewInfo interviewInfo) {
        InterviewProgress currentProgress = progressTracker.getCurrentInterviewProgress(interviewInfo.config());
        String rawStrategy = getRawInterviewStrategy(currentProgress.stage());
        InterviewProfile profile = interviewInfo.profile();

        return createSetting(aiSpec, rawStrategy, profile);
    }
    private AiPrompt createSetting(AISpecification aiSpec, String rawStrategy, InterviewProfile profile) {
        PromptCreationInfo creationInfo = new PromptCreationInfo(
                rawStrategy, profile.department(), profile.field(),
                profile.skills().toString(), profile.experience().toString()
        );
        return promptCreator.create(aiSpec, creationInfo);
    }

    @Override
    public AiPrompt changeTopic(AISpecification aiSpec, InterviewInfo interviewInfo) {
        InterviewProgress currentProgress = progressTracker.getCurrentInterviewProgress(interviewInfo.config());
        String rawStrategy = getRawInterviewStrategy(currentProgress.stage());
        rawStrategy += interviewConcept.getChangingTopicCommand();
        InterviewProfile profile = interviewInfo.profile();

        return createSetting(aiSpec, rawStrategy, profile);
    }

    @Override
    public boolean isSupportedDepartment(InterviewInfo interviewInfo) {
        return true;
    }

    private String getRawInterviewStrategy(InterviewStage stage) {
        return switch (stage) {
            case TECHNICAL -> interviewConcept.getTechnical();
            case EXPERIENCE -> interviewConcept.getExperience();
            case PERSONAL -> interviewConcept.getPersonal();
        };
    }
}