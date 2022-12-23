package challenge.nDaysChallenge.controller;

import challenge.nDaysChallenge.domain.MemberAdapter;
import challenge.nDaysChallenge.domain.room.*;
import challenge.nDaysChallenge.dto.request.Room.GroupRoomRequestDTO;
import challenge.nDaysChallenge.dto.request.Room.RoomRequestDTO;
import challenge.nDaysChallenge.dto.response.Room.GroupRoomResponseDto;
import challenge.nDaysChallenge.dto.response.Room.RoomResponseDto;
import challenge.nDaysChallenge.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    //챌린지 리스트(메인)
    @GetMapping("/challenge/list")
    public ResponseEntity<?> roomList(@AuthenticationPrincipal MemberAdapter memberAdapter) {

        List<RoomResponseDto> roomList = new ArrayList<>();
        
        List<SingleRoom> singleRooms = roomService.findSingleRooms(memberAdapter.getMember());
        for (SingleRoom singleRoom : singleRooms) {
            RoomResponseDto roomResponseDto = RoomResponseDto.builder()
                    .number(singleRoom.getNumber())
                    .name(singleRoom.getName())
                    .category(singleRoom.getCategory().name())
                    .reward(singleRoom.getReward())
                    .type(singleRoom.getType().name())
                    .status(singleRoom.getStatus().name())
                    .passCount(singleRoom.getPassCount())
                    .totalDays(singleRoom.getPeriod().getTotalDays())
                    .startDate(singleRoom.getPeriod().getStartDate())
                    .endDate(singleRoom.getPeriod().getEndDate())
                    .build();
            roomList.add(roomResponseDto);
        }

        List<GroupRoom> groupRooms = roomService.findGroupRooms(memberAdapter.getMember());
        for (GroupRoom groupRoom : groupRooms) {
            RoomResponseDto roomResponseDto = RoomResponseDto.builder()
                    .number(groupRoom.getNumber())
                    .name(groupRoom.getName())
                    .category(groupRoom.getCategory().name())
                    .reward(groupRoom.getReward())
                    .type(groupRoom.getType().name())
                    .status(groupRoom.getStatus().name())
                    .passCount(groupRoom.getPassCount())
                    .totalDays(groupRoom.getPeriod().getTotalDays())
                    .startDate(groupRoom.getPeriod().getStartDate())
                    .endDate(groupRoom.getPeriod().getEndDate())
                    .build();
            roomList.add(roomResponseDto);
        }
        
        return ResponseEntity.status(HttpStatus.OK).body(roomList);
    }

    //개인 챌린지 생성
    @PostMapping("/challenge/create")
    public ResponseEntity<?> createSingleRoom(@AuthenticationPrincipal MemberAdapter memberAdapter,
                                        @RequestBody RoomRequestDTO dto) {

        Room room = roomService.singleRoom(memberAdapter.getMember(), dto.getName(), new Period(dto.getStartDate(), dto.getTotalDays()), Category.valueOf(dto.getCategory()), dto.getPassCount(), dto.getReward(), dto.getUsedPassCount(), dto.getSuccessCount());

        RoomResponseDto savedRoom = RoomResponseDto.builder()
                .number(room.getNumber())
                .name(room.getName())
                .category(room.getCategory().name())
                .reward(room.getReward())
                .type(room.getType().name())
                .status(room.getStatus().name())
                .passCount(room.getPassCount())
                .totalDays(room.getPeriod().getTotalDays())
                .startDate(room.getPeriod().getStartDate())
                .endDate(room.getPeriod().getEndDate())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
    }

    //그룹 챌린지 생성
    @PostMapping("/challenge/createGroup")
    public ResponseEntity<?> createGroupRoom(@AuthenticationPrincipal MemberAdapter memberAdapter,
                                             @RequestBody GroupRoomRequestDTO dto) {

        Room room = roomService.groupRoom(memberAdapter.getMember(), dto.getName(), new Period(dto.getStartDate(), dto.getTotalDays()), Category.valueOf(dto.getCategory()), dto.getPassCount(), dto.getReward(), dto.getUsedPassCount(), dto.getSuccessCount(), dto.getGroupMembers());

        GroupRoomResponseDto savedRoom = GroupRoomResponseDto.builder()
                .number(room.getNumber())
                .name(room.getName())
                .category(room.getCategory().name())
                .reward(room.getReward())
                .type(room.getType().name())
                .status(room.getStatus().name())
                .passCount(room.getPassCount())
                .totalDays(room.getPeriod().getTotalDays())
                .startDate(room.getPeriod().getStartDate())
                .endDate(room.getPeriod().getEndDate())
                .groupMembers(dto.getGroupMembers())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
    }

    //챌린지 삭제&실패
    @DeleteMapping("/challenge/{challengeId}")
    public ResponseEntity<?> deleteRoom(@AuthenticationPrincipal MemberAdapter memberAdapter,
                                        @PathVariable("challengeId") Long roomNumber) {

        roomService.deleteRoom(memberAdapter.getMember(), roomNumber);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    //마이페이지 - 완료 챌린지 조회
    @GetMapping("/user/finishedChallenges")
    public ResponseEntity<?> findFinishedRooms(@AuthenticationPrincipal MemberAdapter memberAdapter) {

        List<RoomResponseDto> finishedRooms = new ArrayList<>();

        List<SingleRoom> findSingleRooms = roomService.findFinishedSingleRooms(memberAdapter.getMember());
        for (Room room : findSingleRooms) {
            RoomResponseDto finishedRoom = RoomResponseDto.builder()
                    .number(room.getNumber())
                    .name(room.getName())
                    .category(room.getCategory().name())
                    .reward(room.getReward())
                    .type(room.getType().name())
                    .status(room.getStatus().name())
                    .passCount(room.getPassCount())
                    .totalDays(room.getPeriod().getTotalDays())
                    .startDate(room.getPeriod().getStartDate())
                    .endDate(room.getPeriod().getEndDate())
                    .build();
            finishedRooms.add(finishedRoom);
        }

        List<GroupRoom> findGroupRooms = roomService.findFinishedGroupRooms(memberAdapter.getMember());
        for (Room room : findGroupRooms) {
            RoomResponseDto finishedRoom = RoomResponseDto.builder()
                    .number(room.getNumber())
                    .name(room.getName())
                    .category(room.getCategory().name())
                    .reward(room.getReward())
                    .type(room.getType().name())
                    .status(room.getStatus().name())
                    .passCount(room.getPassCount())
                    .totalDays(room.getPeriod().getTotalDays())
                    .startDate(room.getPeriod().getStartDate())
                    .endDate(room.getPeriod().getEndDate())
                    .build();
            finishedRooms.add(finishedRoom);
        }

        return ResponseEntity.status(HttpStatus.OK).body(finishedRooms);
    }

}
