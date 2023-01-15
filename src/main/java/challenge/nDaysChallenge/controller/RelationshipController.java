package challenge.nDaysChallenge.controller;
import challenge.nDaysChallenge.domain.Relationship;
import challenge.nDaysChallenge.domain.RelationshipStatus;
import challenge.nDaysChallenge.domain.member.Member;
import challenge.nDaysChallenge.domain.member.MemberAdapter;
import challenge.nDaysChallenge.dto.request.FindFriendsRequestDTO;
import challenge.nDaysChallenge.dto.request.relationship.RelationshipRequestDTO;
import challenge.nDaysChallenge.dto.response.relationship.AcceptResponseDTO;
import challenge.nDaysChallenge.dto.response.relationship.AskResponseDTO;
import challenge.nDaysChallenge.dto.response.FindFriendsResponseDTO;
import challenge.nDaysChallenge.repository.RelationshipRepository;
import challenge.nDaysChallenge.service.RelationshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class RelationshipController {

    private final RelationshipService relationshipService;
    private final RelationshipRepository relationshipRepository;


    //닉네임, 아이디로 검색//
    @GetMapping("/friends/find")
    public ResponseEntity<?> findFriends(@RequestBody FindFriendsRequestDTO findFriendsRequestDTO) {
        String id;
        String nickname;

        try {
            nickname = findFriendsRequestDTO.getNickname();

        } catch (Exception e) {
            throw new NoSuchElementException("닉네임을 입력하지 않았습니다.");
        }

        try {
           id = findFriendsRequestDTO.getId();

        } catch (Exception e) {
            throw new NoSuchElementException("아이디를 입력하지않았습니다.");
        }

        Member member = relationshipService.findFriends(id, nickname);

        FindFriendsResponseDTO foundFriend = new FindFriendsResponseDTO(
                member.getId(),
                member.getNickname(),
                member.getImage());

        return ResponseEntity.ok().body(foundFriend);
    }


    //친구 요청 post//
    @PostMapping("/friends/request")
    public ResponseEntity<?> postViewRequestFriend(@AuthenticationPrincipal MemberAdapter memberAdapter,
                                               @RequestBody RelationshipRequestDTO applyDTO) {

        List<AskResponseDTO> savedRequestFriendsList = relationshipService.saveRelationship(memberAdapter.getMember(),applyDTO);

        return ResponseEntity.ok().body(savedRequestFriendsList);

    }

    //친구 요청 get//
    @GetMapping("/friends/request")
    public ResponseEntity<?> getViewRequestFriend(@AuthenticationPrincipal MemberAdapter memberAdapter){
        //바로 repository 꺼 쓰기//
        List<Relationship> savedRequestFriendsList = relationshipRepository.findRelationshipByFriendAndStatus(memberAdapter.getMember());
        List<AskResponseDTO> askResponseDTOList =RelationshipService.createResponseDTO(savedRequestFriendsList);

        return ResponseEntity.ok().body(askResponseDTOList);
    }


    //친구 수락==친구목록 //
    @PostMapping("/friends/accept")
    public ResponseEntity<?> acceptFriendStatus(@AuthenticationPrincipal MemberAdapter memberAdapter,
                                                @RequestBody RelationshipRequestDTO applyDTO) {

        List<AcceptResponseDTO> acceptRelationship = relationshipService.acceptRelationship(memberAdapter.getMember(), applyDTO);

        return ResponseEntity.ok().body(acceptRelationship);

    }


    @GetMapping("/friends/accept")
    public ResponseEntity<?> getAcceptFriendStatus(@AuthenticationPrincipal MemberAdapter memberAdapter){
        //바로 repository 꺼 쓰기//
        List<Relationship> acceptRelationship = relationshipRepository.findRelationshipByUserAndStatus(memberAdapter.getMember());
        List<AcceptResponseDTO> acceptResponseDTOList = new ArrayList<>();

        for (Relationship relationship : acceptRelationship) {
            AcceptResponseDTO acceptFollowerDTO = AcceptResponseDTO.builder()
                    .id(relationship.getFriend().getId())
                    .nickname(relationship.getFriend().getNickname())
                    .image(relationship.getFriend().getImage())
                    .relationshipStatus(relationship.getStatus().name())
                    .build();

            acceptResponseDTOList.add(acceptFollowerDTO);
        }
        return ResponseEntity.ok().body(acceptResponseDTOList);
    }


    //요청거절 relationship 객채 삭제//
    @DeleteMapping("/friends/request")
    public ResponseEntity<?> deleteFriendStatus( @AuthenticationPrincipal MemberAdapter memberAdapter,
                                                 @RequestBody RelationshipRequestDTO applyDTO ) {

        List<AcceptResponseDTO> friendList = relationshipService.deleteEachRelation(memberAdapter.getMember(), applyDTO);

        return ResponseEntity.ok().body(friendList);
    }
}