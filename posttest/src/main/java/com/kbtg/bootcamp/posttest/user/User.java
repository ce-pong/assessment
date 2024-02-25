package com.kbtg.bootcamp.posttest.user;

import com.kbtg.bootcamp.posttest.lottery.Lottery;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String userId;

    @OneToMany(mappedBy = "user")
    private List<UserTicket> tickets;

    public User() {
    }

    public User(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<UserTicket> getTickets() {
        return tickets;
    }

    public void setTickets(List<UserTicket> tickets) {
        this.tickets = tickets;
    }
}
