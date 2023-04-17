package challenge.nDaysChallenge.domain;

import challenge.nDaysChallenge.domain.member.Member;
import challenge.nDaysChallenge.domain.room.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDate;

import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class Stamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stamp_number")
    private Long number;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "room_number", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Room room;

    private String day;

    private LocalDate latestDate;

    private int usedPassCount;

    private int successCount;

    //==생성 메서드==//
    public static Stamp createStamp(Room room) {
        Stamp stamp = new Stamp();
        stamp.room = room;
        stamp.day = "u".repeat(room.getPeriod().getTotalDays());
        stamp.latestDate = LocalDate.now().minusDays(1L);
        stamp.usedPassCount = 0;
        stamp.successCount = 0;
        return stamp;
    }


    //==비즈니스 로직==//
    //스탬프 찍기
    public Stamp updateStamp(Room room, String day) {
        this.room = room;
        this.latestDate = LocalDate.now();
        this.day = this.day.replaceFirst("u", day);

        return this;
    }

    //마지막 스탬프 찾기
    public String getLatestStamp() {

        if (day.length() > 1) {
            return day.substring(day.length()-1);
        }
        return day;
    }

    //성공 +1
    public void addSuccess() {
        successCount += 1;
    }

    //실패 +1
    public void addPass() {
        usedPassCount += 1;
    }

    public void deleteConnection(){
        this.room=null;
    }
}
