package com.mock.interview.user.presentation.web;

import com.mock.interview.category.application.JobCategoryService;
import com.mock.interview.category.application.JobPositionService;
import com.mock.interview.category.presentation.CategoryViewer;
import com.mock.interview.interview.application.InterviewService;
import com.mock.interview.interview.infra.InterviewRepositoryForView;
import com.mock.interview.interview.presentation.dto.InterviewOverviewFragment;
import com.mock.interview.interview.presentation.dto.InterviewResponse;
import com.mock.interview.review.presentation.dto.ReviewIndexPageFragment;
import com.mock.interview.user.domain.model.Users;
import com.mock.interview.user.presentation.dto.AccountDto;
import com.mock.interview.user.presentation.dto.AccountForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final InterviewService interviewService;
    private final JobCategoryService categoryService;
    private final JobPositionService positionService;
    private final InterviewRepositoryForView interviewRepositoryForView;

    @GetMapping("auth/login")
    public String loginPage(Model model) {
        model.addAttribute("account", new AccountDto());
        return "/auth/login";
    }

    @GetMapping("auth/sign-up")
    public String signUpPage(Model model) {
        model.addAttribute("account", new AccountForm());
        CategoryViewer.setCategoriesView(model, categoryService, positionService);
        return "/auth/sign-up";
    }

    @GetMapping("/")
    public String indexPage(Model model, @AuthenticationPrincipal Users users) {
        if (users == null) {
            model.addAttribute("activeInterview", new InterviewResponse());
            model.addAttribute("interviewOverviewList", new ArrayList<>());
            model.addAttribute("reviewOverviewList", new ArrayList<>());
            return "index";
        }

        final int maxOverviewListSize = 3;
        List<InterviewOverviewFragment> interviewOverviewList = interviewRepositoryForView
                .findInterviewOverview(users.getId(), PageRequest.of(0, maxOverviewListSize));

        // TODO: 개발 후 제대로 뺄 것.
        List<ReviewIndexPageFragment> reviewOverviewList =
                List.of(
                        new ReviewIndexPageFragment(1, "멀티 프로세싱과 멀티스레딩을 어떤 상황에서 사용하는 것이 적절할까요?", 10),
                        new ReviewIndexPageFragment(3, "FIFO 방식의 스케줄링과 라운드 로빈 방식의 스케줄링을 비교해보세요.", 6)
                );

        model.addAttribute("activeInterview", getActiveInterview(users.getId()));
        model.addAttribute("interviewOverviewList", interviewOverviewList);
        model.addAttribute("reviewOverviewList", reviewOverviewList);

        return "index";
    }

    private InterviewResponse getActiveInterview(long loginId) {
        return interviewService.findActiveInterview(loginId)
                .orElseGet(InterviewResponse::new);
    }
}
