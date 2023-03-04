package challenge.nDaysChallenge.service.dajim;

import challenge.nDaysChallenge.domain.dajim.Dajim;
import challenge.nDaysChallenge.domain.dajim.Open;
import challenge.nDaysChallenge.domain.member.Member;
import challenge.nDaysChallenge.domain.room.Room;
import challenge.nDaysChallenge.dto.request.dajim.DajimUpdateRequestDto;
import challenge.nDaysChallenge.dto.request.dajim.DajimUploadRequestDto;
import challenge.nDaysChallenge.dto.response.dajim.DajimFeedResponseDto;
import challenge.nDaysChallenge.dto.response.dajim.DajimResponseDto;
import challenge.nDaysChallenge.repository.dajim.DajimRepository;
import challenge.nDaysChallenge.repository.room.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DajimFeedService {

    private final DajimRepository dajimRepository;

    //피드 - 전체 다짐 조회 (미로그인)
    @PreAuthorize("isAnonymous()")
    @Transactional(readOnly = true)
    public Slice<DajimFeedResponseDto> viewFeedWithoutLogin(Pageable pageable) {
        Slice<Dajim> dajimPage = dajimRepository.findByOpen(Open.PUBLIC, pageable);

        List<DajimFeedResponseDto> dajimFeedList = dajimPage.getContent().stream()
                .map(dajim -> DajimFeedResponseDto.of(dajim, null))
                .collect(toList());

        return new CustomSliceImpl<>(dajimFeedList, pageable, dajimPage.hasNext());
    }

    //피드 - 전체 다짐 조회 (로그인 시)
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public Slice<DajimFeedResponseDto> viewFeedLoggedIn(Member member, Pageable pageable) {
        Slice<Dajim> dajimPage = dajimRepository.findByOpen(Open.PUBLIC, pageable);

        List<DajimFeedResponseDto> dajimFeedList = dajimPage.getContent().stream()
                .map(dajim -> DajimFeedResponseDto.of(dajim, member))
                .collect(toList());


        return new CustomSliceImpl<>(dajimFeedList,pageable, dajimPage.hasNext());
    }

}

