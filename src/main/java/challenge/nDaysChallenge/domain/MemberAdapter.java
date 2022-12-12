package challenge.nDaysChallenge.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class MemberAdapter extends User implements UserDetails{

    private Member member;

    public MemberAdapter(Member member){
        super(member.getId(),
                member.getPw(),
                authorities(member.getAuthority()));
        this.member=member;
    }

    private static Collection<? extends GrantedAuthority> authorities(Authority authority) {
        return Collections.singleton(new SimpleGrantedAuthority("USER"));
    }

    public Member getMember(){
        return member;
    }

    public static MemberAdapter from(Member member) {
        return new MemberAdapter(member);
    }


}
