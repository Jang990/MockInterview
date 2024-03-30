package com.mock.interview.interviewconversationpair.presentation.api;

import com.mock.interview.interviewconversationpair.application.ConversationPairService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview/{interviewId}/conversation/pair/{pairId}")
@RequiredArgsConstructor
public class ConversationPairController {
    private final ConversationPairService conversationPairService;

    @PostMapping("/recommendation/another")
    public ResponseEntity<Void> recommendAnotherQuestion(
            @AuthenticationPrincipal(expression = "id") Long loginId,
            @PathVariable(name = "interviewId") long interviewId,
            @PathVariable(name = "pairId") long pairId
    ) {
        conversationPairService.recommendAnotherQuestion(loginId, interviewId, pairId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/question/connection/{questionId}")
    public ResponseEntity<Void> connectQuestion(
            @AuthenticationPrincipal(expression = "id") Long loginId,
            @PathVariable(name = "interviewId") long interviewId,
            @PathVariable(name = "pairId") long pairId,
            @PathVariable(name = "questionId") long questionId
    ) {

        conversationPairService.connectQuestion(loginId, interviewId, pairId, questionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
