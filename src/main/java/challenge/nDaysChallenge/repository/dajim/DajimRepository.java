package challenge.nDaysChallenge.repository.dajim;

import challenge.nDaysChallenge.domain.dajim.Dajim;
import challenge.nDaysChallenge.domain.dajim.Open;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Repository
@Transactional
public interface DajimRepository extends JpaRepository<Dajim, Long> {

    //다짐리스트 조회
    Optional<Dajim> findAllByNumber();

    //선택한 다짐 조회 (상세)
    Optional<Dajim> findByNumber();

    //다짐 저장
    @Override
    <S extends Dajim> S save(S entity); //save() : insert & update 모두 수행
}
