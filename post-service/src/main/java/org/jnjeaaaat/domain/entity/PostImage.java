package org.jnjeaaaat.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private Boolean isThumbnail = false;

    @Builder
    protected PostImage(String imageUrl, Post post) {
        this.imageUrl = imageUrl;
        this.post = post;
    }

    public static PostImage of(String imageUrl, Post post) {
        return builder()
                .imageUrl(imageUrl)
                .post(post)
                .build();
    }

    public boolean isThumbnail() {
        return Boolean.TRUE.equals(this.isThumbnail);
    }

}
