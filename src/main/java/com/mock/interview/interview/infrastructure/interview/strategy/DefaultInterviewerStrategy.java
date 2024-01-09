package com.mock.interview.interview.infrastructure.interview.strategy;

import com.mock.interview.interview.infrastructure.interview.dto.InterviewInfo;
import com.mock.interview.interview.infrastructure.interview.dto.InterviewProfile;
import com.mock.interview.interview.infrastructure.interview.dto.Message;
import com.mock.interview.interview.infrastructure.interview.dto.PromptCreationInfo;
import com.mock.interview.interview.infrastructure.interview.gpt.AISpecification;
import com.mock.interview.interview.infrastructure.interview.gpt.InterviewAIRequest;
import com.mock.interview.interview.infrastructure.interview.setting.InterviewSettingCreator;
import com.mock.interview.interview.infrastructure.interview.strategy.exception.AlreadyFinishedInterviewException;
import com.mock.interview.interview.infrastructure.interview.strategy.stage.InterviewProgress;
import com.mock.interview.interview.infrastructure.interview.strategy.stage.InterviewProgressTracker;
import com.mock.interview.interview.infrastructure.interview.dto.MessageHistory;
import com.mock.interview.interview.infrastructure.interview.setting.InterviewSetting;
import com.mock.interview.interview.infrastructure.interview.strategy.stage.InterviewStage;
import lombok.RequiredArgsConstructor;

import java.util.List;

//@Order(1)
//@Component
@RequiredArgsConstructor
public class DefaultInterviewerStrategy implements InterviewerStrategy {
    private final InterviewProgressTracker progressTracker;
    private final InterviewSettingCreator interviewSettingCreator;
    private final DefaultInterviewConcept interviewConcept;

    // TODO: 수정 많이 필요.
    //      -> IT 먼저 끝내고 바꿀 것.

    @Override
    public InterviewAIRequest configStrategy(AISpecification aiSpec, InterviewInfo interviewInfo, MessageHistory history) {
        InterviewProgress currentProgress = progressTracker.getCurrentInterviewProgress(interviewInfo.config(), history);
        String rawStrategy = getRawInterviewStrategy(currentProgress.stage());
        InterviewProfile profile = interviewInfo.profile();

        List<Message> messageHistory = history.getMessages();

        InterviewSetting setting = createSetting(aiSpec, rawStrategy, profile);
        return new InterviewAIRequest(messageHistory, setting);
    }
    private InterviewSetting createSetting(AISpecification aiSpec, String rawStrategy, InterviewProfile profile) {
        PromptCreationInfo creationInfo = new PromptCreationInfo(
                rawStrategy, profile.department(), profile.field(),
                profile.skills().toString(), profile.experience().toString()
        );
        return interviewSettingCreator.create(aiSpec, creationInfo);
    }

    @Override
    public InterviewAIRequest changeTopic(AISpecification aiSpec, InterviewInfo interviewInfo, MessageHistory history) {
        InterviewProgress currentProgress = progressTracker.getCurrentInterviewProgress(interviewInfo.config(), history);
        String rawStrategy = getRawInterviewStrategy(currentProgress.stage());
        rawStrategy += interviewConcept.getChangingTopicCommand();
        InterviewProfile profile = interviewInfo.profile();

        List<Message> messageHistory = history.getMessages();

        InterviewSetting setting = createSetting(aiSpec, rawStrategy, profile);
        return new InterviewAIRequest(messageHistory, setting);
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
            case FINISHED -> throw new AlreadyFinishedInterviewException();
        };
    }
}