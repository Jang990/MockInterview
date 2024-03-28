package com.mock.interview.interviewquestion.infra.cache;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecommendedQuestionCacheRepository {
    private final RedisTemplate<String, WrapperList> questionRedisTemplate;
    private final String RECOMMENDED_QUESTION_KEY_PREFIX = "INTERVIEW:{%d}:PAIR:{%d}:RECOMMENDED";
    private final long EXPIRED_MINUTE = 1;

    public List<Long> find(long interviewId, long pairId) {
        String key = createKey(interviewId, pairId);
        WrapperList cache = questionRedisTemplate.opsForValue().get(key);
        if(cache == null)
            return Collections.emptyList();
        return cache.list;
    }

    public void save(long interviewId, long pairId, List<Long> questionIds) {
        String key = createKey(interviewId, pairId);
        questionRedisTemplate.opsForValue().set(key, new WrapperList(questionIds), Duration.ofMinutes(EXPIRED_MINUTE));
    }

    private String createKey(long interviewId, long pairId) {
        return String.format(RECOMMENDED_QUESTION_KEY_PREFIX, interviewId, pairId);
    }

    // GenericJackson2JsonRedisSerializer을 사용할 때 List<?>를 바로 넣으면 오류.
    @Getter
    @NoArgsConstructor
    static class WrapperList {
        List<Long> list;

        public WrapperList(List<Long> list) {
            this.list = list;
        }
    }
}