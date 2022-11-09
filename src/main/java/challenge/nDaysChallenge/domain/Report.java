package challenge.nDaysChallenge.domain;

import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class Report {

    @Id @GeneratedValue
    @Column(name = "report_number")
    private Long number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dajim_number")
    private Dajim dajim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_number")
    private Dajim_comment comment;

    private int cause;

    private boolean isDajim;

    private String content;

}
