package challenge.nDaysChallenge.domain.member;

import ch.qos.logback.classic.db.names.ColumnName;
import challenge.nDaysChallenge.domain.Relationship;
import challenge.nDaysChallenge.domain.Stamp;
import challenge.nDaysChallenge.domain.room.RoomMember;
import challenge.nDaysChallenge.domain.room.SingleRoom;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(name = "idNickname", columnNames = {"id", "nickname"}))
@Builder
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_number")
    private Long number;

    private String id;

    private String pw;

    private String nickname;

    private int image;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    //내가 수락한 친구들만 리스트에 들어가게//
    @Builder.Default
    @OneToMany(mappedBy = "number", cascade = CascadeType.ALL, orphanRemoval = true)
    private  List<Relationship> confirmedFriendsList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SingleRoom> singleRooms = new ArrayList<>();

    public Member update (String nickname, String pw, int image){
        this.nickname = nickname;
        this.pw = pw;
        this.image = image;
        return this;
    }

    //친구 리스트에 추가 메서드//
    public void addFriendList (Relationship member){
        this.confirmedFriendsList.add(member);
    }


    public void addSingleRooms (SingleRoom singleRoom){
        this.singleRooms.add(singleRoom);
    }

}
