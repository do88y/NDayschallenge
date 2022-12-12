package challenge.nDaysChallenge.security;

import challenge.nDaysChallenge.config.SecurityConfig;
import challenge.nDaysChallenge.domain.Member;
import challenge.nDaysChallenge.domain.dajim.Dajim;
import challenge.nDaysChallenge.domain.dajim.Open;
import challenge.nDaysChallenge.domain.room.Category;
import challenge.nDaysChallenge.domain.room.Period;
import challenge.nDaysChallenge.domain.room.Room;
import challenge.nDaysChallenge.domain.room.SingleRoom;
import challenge.nDaysChallenge.dto.request.DajimRequestDto;
import challenge.nDaysChallenge.dto.request.MemberRequestDto;
import challenge.nDaysChallenge.dto.response.DajimFeedResponseDto;
import challenge.nDaysChallenge.dto.response.DajimResponseDto;
import challenge.nDaysChallenge.dto.response.MemberInfoResponseDto;
import challenge.nDaysChallenge.repository.MemberRepository;
import challenge.nDaysChallenge.repository.dajim.DajimFeedRepository;
import challenge.nDaysChallenge.repository.dajim.DajimRepository;
import challenge.nDaysChallenge.repository.room.RoomRepository;
import challenge.nDaysChallenge.repository.room.SingleRoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback(value = false)
public class WithUserDetailsTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    SingleRoomRepository singleRoomRepository;

    @Autowired
    DajimRepository dajimRepository;

    @Autowired
    DajimFeedRepository dajimFeedRepository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    MockMvc mockMvc;

    @BeforeTransaction
    public void 회원가입() {
        MemberRequestDto memberRequestDto = new MemberRequestDto("abc@naver.com", "123", "aaa", 1, 2);
        Member member = memberRequestDto.toMember(passwordEncoder);
        memberRepository.save(member);
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "customUserDetailsService", value = "abc@naver.com")
    public void 시큐리티컨텍스트_유저_꺼내기() throws Exception {
        String currentMemberId = SecurityUtil.getCurrentMemberId(); //시큐리티 컨텍스트에 저장된 id


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        System.out.println(user.getUsername());

        assertThat(currentMemberId).isEqualTo("abc@naver.com");
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "customUserDetailsService", value = "abc@naver.com")
    public void 다짐_작성_후_룸_조회() {
        //멤버 객체 가져오기
        Optional<Member> member = memberRepository.findById(SecurityUtil.getCurrentMemberId());
        Member currentMember = member.get();

        //룸 객체 연결
        SingleRoom singleRoom = new SingleRoom("roomName", new Period(LocalDate.now(),10L), Category.ROUTINE, 2, "reward");
        singleRoomRepository.save(singleRoom);
        singleRoom.addRoom(singleRoom, currentMember);

        //다짐 작성
        DajimRequestDto dajimRequestDto = new DajimRequestDto(null, "다짐 내용", "PUBLIC");
        Dajim newDajim = Dajim.builder()
                .room(singleRoom)
                .member(currentMember)
                .content(dajimRequestDto.getContent())
                .open(Open.valueOf(dajimRequestDto.getOpen()))
                .build();
        Dajim savedDajim = dajimRepository.save(newDajim);

        //룸에서 다짐 조회
        List<Dajim> dajims = dajimRepository.findAllByRoomNumber(1L);

        List<DajimResponseDto> dajimsList = dajims.stream().map(dajim ->
                        new DajimResponseDto(
                                savedDajim.getNumber(),
                                savedDajim.getMember().getNickname(),
                                savedDajim.getMember().getImage(),
                                savedDajim.getContent(),
                                savedDajim.getOpen().toString(),
                                savedDajim.getUpdatedDate()))
                .collect(Collectors.toList());

        assertThat(dajimsList.size()).isEqualTo(1);
        assertThat(dajimsList.get(0).getContent()).isEqualTo(savedDajim.getContent());

        //피드에서 다짐 조회
        List<Dajim> dajimFeed = dajimFeedRepository.findAllByMemberAndOpen(null, Collections.singletonList(1L));

        List<DajimFeedResponseDto> dajimFeedList = dajimFeed.stream().map(d ->
                new DajimFeedResponseDto(
                        d.getNumber(),
                        d.getMember().getNickname(),
                        d.getContent(),
                        d.getEmotions().stream().map(emotion ->
                                        emotion.getStickers().toString())
                                .collect(Collectors.toList()),
                        d.getUpdatedDate()
                )).collect(Collectors.toList());

        assertThat(dajimFeedList.get(0).getContent()).isEqualTo("다짐 내용");
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "customUserDetailsService", value = "abc@naver.com")
    public void 회원_정보_조회() {
        String currentMemberId = SecurityUtil.getCurrentMemberId();
        Optional<Member> member = memberRepository.findById(currentMemberId);
        Member member1 = member.get();

        MemberInfoResponseDto dto = MemberInfoResponseDto.of(member1);

        assertThat(dto.getId()).isEqualTo("abc@naver.com");
    }
}
