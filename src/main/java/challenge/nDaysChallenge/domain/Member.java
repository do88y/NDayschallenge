package challenge.nDaysChallenge.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_number")
    private Long number;

    @OneToMany(mappedBy = "friendNumber")
    private List<Relationship> friends = new ArrayList<>();

    @Column(length = 6 ,nullable = false)
    private String nickname;

    @Column(length = 15, nullable = false)
    private String id;

    @Column(length = 15, nullable = false)
    private String pw;

    @Column(nullable = false)
    private int image;

    private int room_limit;  //챌린지 5개 제한

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Builder
    public Member(String id, String pw, Authority authority) {
        this.id = id;
        this.pw = pw;
        this.authority = authority;
    }

}

