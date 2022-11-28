package challenge.nDaysChallenge.repository;

import challenge.nDaysChallenge.domain.Member;
import challenge.nDaysChallenge.domain.Relationship;
import challenge.nDaysChallenge.domain.RelationshipStatus;
import challenge.nDaysChallenge.security.UserDetailsImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, String > {
    public  Relationship findByUserNumber(Long user_number);
    public List<Relationship> findRelationshipByUserNumberAndStatus(UserDetailsImpl userDetailsImpl, RelationshipStatus status);

    //memberNumber로 수락상태인 관계, 챌린지 5개 이하만 검색-> 그룹 챌린지 멤버 후보에 뿌리기
    public List<Member> findByUserNumberAndStatus(Long user, RelationshipStatus status);

}
