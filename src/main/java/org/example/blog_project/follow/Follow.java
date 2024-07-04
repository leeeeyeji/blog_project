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
    private Member fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user")
    private Member toUser;

    @Builder
    public Follow(Member fromUser, Member toUser) {
        this.fromUser = fromUser;
        this.toUser = toUser;
    }
}
