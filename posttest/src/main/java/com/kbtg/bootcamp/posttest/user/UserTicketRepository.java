package com.kbtg.bootcamp.posttest.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTicketRepository extends JpaRepository<UserTicket,Integer> {

    @Query("SELECT ut from UserTicket ut WHERE ut.user.userId =:userId")
    List<UserTicket> findByUserId(@Param("userId") String userId);

    @Query("SELECT ut from UserTicket ut WHERE ut.user.userId =:userId and ut.lottery.ticketId =:ticketId")
    List<UserTicket> findByUserIdAndTicketId(
            @Param("userId") String userId,
            @Param("ticketId") String ticketId);
}
