package org.jnjeaaaat.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.jnjeaaaat.entity.BaseEntity;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is null")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImages> postImages;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean isPublic = true;

    private LocalDateTime deletedAt;

    @Builder
    protected Post(Long memberId, String content) {
        this.memberId = memberId;
        this.content = content;
    }

    public void setPostImageUrls(List<String> postImageUrls) {
        this.postImages = postImageUrls.stream()
                .map(imageUrl -> PostImages.of(imageUrl, this))
                .toList();

        this.postImages.get(0).setIsThumbnail(true);
    }
}
