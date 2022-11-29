package challenge.nDaysChallenge.domain;

import challenge.nDaysChallenge.domain.room.Room;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_member_number")
    private Long number;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_number")
    private Member member;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "room_number")
    private Room room;


    //==연관관계 메서드==//  RoomMember의 room에 roomNumber값 넣으면서 roomMemberList에도 roomNumber 세팅되게

    public void setMember(Member member) {
        this.member = member;
        member.getRoomMemberList().add(this);
    }
    public void joinRoom(Room groupRoom) {
        this.room = groupRoom;
    }

    //==생성 메서드==//
    public static RoomMember createRoomMember(Member member, Room room) {
        RoomMember roomMember = new RoomMember();
        roomMember.joinRoom(room);
        roomMember.setMember(member);

        return roomMember;
    }

}