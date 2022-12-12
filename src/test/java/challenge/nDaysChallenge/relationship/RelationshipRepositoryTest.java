package challenge.nDaysChallenge.relationship;

import challenge.nDaysChallenge.domain.Authority;
import challenge.nDaysChallenge.domain.Member;
import challenge.nDaysChallenge.domain.Relationship;
import challenge.nDaysChallenge.domain.RelationshipStatus;
import challenge.nDaysChallenge.dto.request.relationship.ApplyRequestDTO;
import challenge.nDaysChallenge.dto.response.relationship.RelationResponseDTO;
import challenge.nDaysChallenge.repository.MemberRepository;
import challenge.nDaysChallenge.repository.RelationshipRepository;
import challenge.nDaysChallenge.service.RelationshipService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
class RelationshipRepositoryTest {
    @Autowired
    RelationshipRepository relationshipRepository;

    @Autowired
    RelationshipService relationshipService;

    @Autowired
    MemberRepository memberRepository;


    @Autowired
    PasswordEncoder passwordEncoder;


    @DisplayName("관계 저장, 친구 관계 저장")
    @Test
    @Transactional
    @Rollback(value = false)
    void makeRelationship (){
            //given
        Member user = new Member("abc@naver.com","123","나",1,2, Authority.ROLE_USER);
        Member friend = new Member("dbf@naver.com","123","친구1",3,2, Authority.ROLE_USER);
        Member friend2 = new Member("ery@naver.com","123","친구2",2,2, Authority.ROLE_USER);
        List <String> friendList = new ArrayList<>();


        //중복이니까 나중에 수정해야해//
        ApplyRequestDTO dto1 =new ApplyRequestDTO("dbf@naver.com","친구1",3,LocalDateTime.now(),RelationshipStatus.REQUEST.name(),friendList);
        ApplyRequestDTO dto2 =new ApplyRequestDTO("ery@naver.com","친구2",2,LocalDateTime.now(),RelationshipStatus.REQUEST.name(),friendList);
        memberRepository.save(user);         //나
        memberRepository.save(friend);      //친구1
        memberRepository.save(friend2);    //친구2


        relationshipService.saveRelationship(user, friend);
        relationshipService.saveRelationship(user,friend2);

        //when

        //친구 신청 수락 //담아==단축키(옵+커+v)//
        RelationResponseDTO friendInfo = relationshipService.acceptRelationship (user, friend,dto1);


        //confirmFriendList 불러오기//
        List<Relationship> confirmedFriendsList = user.getConfirmedFriendsList();
        //향상된 for 문 each for 문//
        for (Relationship confirmList : confirmedFriendsList) {
            System.out.println("confirmedFriendsList. = " + confirmList.getUser().getNickname());
        }



        //then (넣고 들어온 정보가 같은지 확인을 해줘야해)
        assertThat(friendInfo.getId()).isEqualTo(dto1.getId());
        List<Relationship> relationshipByUserAndStatus = relationshipRepository.findRelationshipByUserAndStatus(user, RelationshipStatus.ACCEPT);
        assertThat(user.getConfirmedFriendsList().size()).isEqualTo(relationshipByUserAndStatus.size());

    }
}



