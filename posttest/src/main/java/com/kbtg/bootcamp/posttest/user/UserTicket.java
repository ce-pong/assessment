package com.kbtg.bootcamp.posttest.user;

import com.kbtg.bootcamp.posttest.lottery.Lottery;
import jakarta.persistence.*;

@Entity
@Table(name = "user_ticket")
public class UserTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Lottery lottery ;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
