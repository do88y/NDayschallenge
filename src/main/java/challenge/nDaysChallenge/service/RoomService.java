package challenge.nDaysChallenge.service;

import challenge.nDaysChallenge.domain.*;
import challenge.nDaysChallenge.domain.room.Category;
import challenge.nDaysChallenge.domain.room.GroupRoom;
import challenge.nDaysChallenge.domain.room.Period;
import challenge.nDaysChallenge.domain.room.Room;
import challenge.nDaysChallenge.repository.MemberRepository;
import challenge.nDaysChallenge.repository.RoomMemberRepository;
import challenge.nDaysChallenge.repository.RoomRepository;
import challenge.nDaysChallenge.repository.room.GroupRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor  //final 붙은 필드로만 생성자를 만들어 줌
public class RoomService {

    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final GroupRoomRepository groupRoomRepository;


    @Transactional
    public void saveRoom(Room room) {
        roomRepository.save(room);
    }

    /**
     * 챌린지 생성
     */
    @Transactional
    Long createRoom(Long memberNumber, String name, Period period, Category category) {

        //엔티티 조회
        Room room = roomRepository.findById(memberNumber).get();
        Member member = memberRepository.findById(memberNumber).get();
        RoomMember roomMember = roomMemberRepository.findByMemberNumber(memberNumber);

        //

        //챌린지 생성
        Room newRoom = Room.builder()
                .name(name)
                .period(new Period(period.getTotalDays()))
                .category(category)
                .build();

        //챌린지 저장
        roomRepository.save(newRoom);

        //챌린지 멤버 생성
        RoomMember setRoomMember = RoomMember.builder()
                .member(member)
                .room(room)
                .build();

        //챌린지 멤버 저장
        roomMemberRepository.save(roomMember);

        //roomCount -1
        roomMember.addCount();

        return newRoom.getNumber();
    }

    //챌린지 전체 조회
    public List<Room> findRooms() {
        return roomRepository.findAll();
    }

    //특정 챌린지 조회
    public Optional<Room> findById(Long number) {
        return roomRepository.findById(number);
    }

    /**
     * 챌린지 삭제
     */
    @Transactional
    public void deleteRoom(Long roomNumber) {
        //엔티티 조회
        Room room = roomRepository.findById(roomNumber).get();
        RoomMember roomMember = roomMemberRepository.findByMemberNumber(roomNumber);

        //챌린지 삭제
        roomRepository.delete(room);

        //RoomMember 삭제


        //roomCount +1
        roomMember.addCount();
    }

    /**
     * 단체 챌린지 실패
     */
    @Transactional
    public void failRoom(Long roomNumber) {
        //엔티티 조회
        Room room = roomRepository.findById(roomNumber).get();
        GroupRoom groupRoom = groupRoomRepository.findById(roomNumber).get();

        //그룹 챌린지 멤버 조회
        List<RoomMember> roomMembers = groupRoom.getRoomMemberList();
        int failCount = room.getFailCount();
        int passCount = room.getPassCount();

        if (passCount > failCount) {
            for (RoomMember roomMember : roomMembers) {
                roomRepository.deleteById(roomNumber);

            }
        }

        //roomCount +1
        RoomMember roomMember = roomMemberRepository.findByMemberNumber(roomNumber);
        roomMember.addCount();

    }

    /**
     * 챌린지 갯수 검색
     */
    public int findRoomCount(Long memberNumber) {
        RoomMember roomMember = roomMemberRepository.findByMemberNumber(memberNumber);
        int roomCount = roomMember.getRoomCount();
        return roomCount;
    }


}
