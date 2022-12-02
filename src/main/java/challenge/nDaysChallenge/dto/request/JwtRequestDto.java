package challenge.nDaysChallenge.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JwtRequestDto {

    private String accessToken;
    private String refreshToken;

}
