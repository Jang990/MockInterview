package com.mock.interview.interview.infra;

import com.mock.interview.interview.domain.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    @Query("Select iv From Interview iv " +
            "join fetch iv.candidateConfig " +
            "join fetch iv.candidateConfig.category " +
            "join fetch iv.candidateConfig.position " +
            "left join fetch iv.candidateConfig.techLink " +
            "left join fetch iv.candidateConfig.techLink.technicalSubjects " +
            "Where iv.id = :interviewId And iv.users.id = :userId")
    Optional<Interview> findInterviewSetting(@Param("interviewId") long interviewId, @Param("userId") long userId);


    // TODO: Config 제외로 인한 오류 발생.
    @Query("""
            SELECT iv FROM Interview iv
            JOIN FETCH iv.category
            JOIN FETCH iv.position
            JOIN FETCH iv.candidateConfig
            LEFT JOIN FETCH iv.candidateConfig.techLink
            LEFT JOIN FETCH iv.candidateConfig.techLink.technicalSubjects
            """)
    Optional<Interview> findInterviewSetting(@Param("interviewId") long interviewId);

    @Query("Select iv From Interview iv Where iv.id = :interviewId and iv.users.id = :userId")
    Optional<Interview> findByIdAndUserId(@Param("interviewId") long interviewId, @Param("userId") long userId);

    @Query("Select iv From Interview iv " +
            "join fetch iv.candidateConfig " +
            "left join fetch iv.candidateConfig.techLink " +
            "left join fetch iv.candidateConfig.techLink.technicalSubjects " +
            "Where iv.users.id = :userId")
    List<Interview> findUserInterviewWithProfileAndTech(@Param("userId") long userId);

    @Query("Select iv From Interview iv Where iv.expiredTime > current_timestamp")
    Optional<Interview> findActiveInterview(Long loginId);

    /** 인터뷰 소유자 검증을 위한 쿼리 메소드 */
    boolean existsByIdAndUsersId(long interviewId, long usersId);
}
