package challenge.nDaysChallenge.service.dajim;

import challenge.nDaysChallenge.domain.Member;
import challenge.nDaysChallenge.domain.dajim.Dajim;
import challenge.nDaysChallenge.domain.dajim.Emotion;
import challenge.nDaysChallenge.domain.dajim.Stickers;
import challenge.nDaysChallenge.dto.request.EmotionRequestDto;
import challenge.nDaysChallenge.dto.response.EmotionResponseDto;
import challenge.nDaysChallenge.repository.MemberRepository;
import challenge.nDaysChallenge.repository.dajim.EmotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmotionService {

    private final EmotionRepository emotionRepository;

    private final MemberRepository memberRepository;

    //이모션 등록
    public EmotionResponseDto uploadEmotion(EmotionRequestDto emotionRequestDto, Member member) { //스티커 등록/변경/삭제
        Dajim dajim = emotionRepository.findByDajimNumberForEmotion(emotionRequestDto.getDajimNumber())
                .orElseThrow(()->new RuntimeException("감정 스티커를 등록할 다짐을 찾을 수 없습니다."));

        Stickers sticker = Stickers.valueOf(emotionRequestDto.getSticker());

        Emotion emotion = Emotion.builder()
                        .member(member)
                        .dajim(dajim)
                        .stickers(sticker)
                        .build();

        emotionRepository.save(emotion);

        dajim.addEmotions(emotion); //다짐 엔티티 이모션리스트에 추가

        EmotionResponseDto emotionResponseDto = new EmotionResponseDto(
                emotion.getDajim().getNumber(),
                emotion.getMember().getNickname(),
                emotion.getStickers().toString());

        return emotionResponseDto;
    }

    //이모션 변경 및 삭제
    public EmotionResponseDto updateEmotion(EmotionRequestDto requestDto, Member member){
        //수정할 이모션 객체 불러오기
        Emotion emotion = emotionRepository.findByEmotionNumber(requestDto.getDajimNumber(), member.getNumber())
                .orElseThrow(()->new RuntimeException("감정 스티커를 불러오는 데 실패했습니다."));

        //스티커 변경 - 타 이모티콘 클릭 시 기존 이모션 삭제
        Long dajimNumber = requestDto.getDajimNumber();
        Optional<Dajim> dajim = emotionRepository.findByDajimNumberForEmotion(dajimNumber);
        dajim.get().getEmotions().remove(emotion);

        Emotion updatedEmotion;

        if (requestDto.getSticker()==null||requestDto.getSticker().equals("")){
            updatedEmotion = emotion.update(null);
        } else {
            updatedEmotion = emotion.update(Stickers.valueOf(requestDto.getSticker()));
            dajim.get().getEmotions().add(updatedEmotion);
        }

        EmotionResponseDto emotionResponseDto = EmotionResponseDto.builder()
                .dajimNumber(updatedEmotion.getDajim().getNumber())
                .memberNickname(updatedEmotion.getMember().getNickname())
                .stickers(updatedEmotion.getStickers().toString())
                .build();

        return emotionResponseDto;
    }

}
