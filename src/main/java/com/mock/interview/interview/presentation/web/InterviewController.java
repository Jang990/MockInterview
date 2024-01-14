package com.mock.interview.interview.presentation.web;

import com.mock.interview.candidate.presentation.dto.CandidateProfileForm;
import com.mock.interview.candidate.presentation.dto.InterviewConfigDto;
import com.mock.interview.candidate.presentation.dto.InterviewCandidateForm;
import com.mock.interview.conversation.presentation.dto.InterviewRole;
import com.mock.interview.conversation.presentation.dto.MessageDto;
import com.mock.interview.conversation.presentation.dto.MessageHistoryDto;
import com.mock.interview.interview.application.InterviewService;
import com.mock.interview.interview.infrastructure.lock.creation.InterviewUserLock;
import com.mock.interview.candidate.application.CandidateConfigService;
import com.mock.interview.tech.application.TechnicalSubjectsService;
import com.mock.interview.tech.presentation.dto.TechnicalSubjectsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
public class InterviewController {

    private final CandidateConfigService candidateConfigService;
    private final InterviewService interviewService;
    private final TechnicalSubjectsService technicalSubjectsService;


    @PostMapping("/interview")
    @InterviewUserLock
    public String startInterviewPage(
            Model model,
            CandidateProfileForm profile,
            InterviewConfigDto interviewDetails,
            @AuthenticationPrincipal(expression = "id") Long loginId
    ) {
        model.addAttribute("headerActiveTap", "interview");

        InterviewCandidateForm interviewCandidateForm = new InterviewCandidateForm(profile, interviewDetails);
        List<TechnicalSubjectsResponse> relationalTech = technicalSubjectsService.saveTech(profile.getSkills());
        long candidateConfigId = candidateConfigService.create(interviewCandidateForm, loginId, relationalTech);
        long interviewId = interviewService.create(loginId, candidateConfigId);
        return "redirect:/interview/" + interviewId;
    }

    @GetMapping("/interview/{interviewId}")
    public String interviewPage(
            Model model,
            @AuthenticationPrincipal(expression = "id") Long loginId,
            @PathVariable(name = "interviewId") long interviewId
    ) {
        model.addAttribute("headerActiveTap", "interview");
        model.addAttribute("interviewId", interviewId);
        model.addAttribute("messageHistory", initHistory()); // TODO: 임시코드 - 만약 DB에 있다면 가져와야 한다.
        return "interview/interview-start";
    }

    private MessageHistoryDto initHistory() {
        MessageHistoryDto historyDTO = new MessageHistoryDto();
        historyDTO.getMessages().add(new MessageDto(InterviewRole.INTERVIEWER.toString(), "안녕하세요. 면접을 시작하겠습니다. 준비되셨나요?"));
        historyDTO.getMessages().add(new MessageDto(InterviewRole.USER.toString(), "네. 준비됐습니다."));
        return historyDTO;
    }
}
