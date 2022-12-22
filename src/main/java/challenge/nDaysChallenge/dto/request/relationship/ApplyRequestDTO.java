package challenge.nDaysChallenge.dto.request.relationship;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ApplyRequestDTO {

  private String  id;                   //친구아이디
  private String nickname;     //친구 닉네임

  private int image;               //친구 프로필사진

  private String relationshipStatus;

  @DateTimeFormat(pattern = "yyyy.mm.dd HH:mm:ss")
  private LocalDateTime requestedDate;    //요청날짜시간



  @Builder
  public ApplyRequestDTO(String id, String nickname, int image, LocalDateTime requestedDate, String relationshipStatus){
    this.id=id;
    this.nickname=nickname;
    this.image=image;
    this.requestedDate=requestedDate;
    this.relationshipStatus=relationshipStatus;
  }
}