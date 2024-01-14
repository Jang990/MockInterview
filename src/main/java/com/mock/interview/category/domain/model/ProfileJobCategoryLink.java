package com.mock.interview.category.domain.model;

import com.mock.interview.candidate.domain.model.CandidateConfig;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileJobCategoryLink {
    @Id
    @Column(name = "job_link_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_profile_id")
    private CandidateConfig profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_category_id")
    private JobCategory jobCategory;

    public static ProfileJobCategoryLink createLink(CandidateConfig profile, JobCategory jobCategory) {
        ProfileJobCategoryLink link = new ProfileJobCategoryLink();
        link.profile = profile;
        link.jobCategory = jobCategory;
        return link;
    }
}