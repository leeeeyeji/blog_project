package org.example.blog_project.follow;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.blog_project.member.Member;

@Entity
@Getter
@NoArgsConstructor
public class Follow {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user")
    private Member from_user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user")
    private Member to_user;

    @Builder
    public Follow(Member from_user, Member to_user) {
        this.from_user = from_user;
        this.to_user = to_user;
    }
}
