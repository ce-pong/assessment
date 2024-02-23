package com.kbtg.bootcamp.posttest.lottery;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "lottery")
public class Lottery {

    @Id
    private String ticketId;

    private int price;

    private int amount;
}
