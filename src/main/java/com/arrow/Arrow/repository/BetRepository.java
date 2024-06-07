package com.arrow.Arrow.repository;

import com.arrow.Arrow.dto.WinnerDTO;
import com.arrow.Arrow.entity.Bet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {

    //@Query("SELECT DISTINCT ad.username FROM Bet b JOIN User ad ON b.user.username = ad.username WHERE CAST(FIND_IN_SET(:selected_numbers, REPLACE(b.selected_numbers, ' ', '')) AS int) > 0")
    @Query("SELECT new com.arrow.Arrow.dto.WinnerDTO(ad.username, sum(b.bet_amount) as total_invested, :selected_numbers)  " +
            "FROM Bet b " +
            "JOIN User ad " +
            "ON b.user.username = ad.username " +
            "WHERE b.slot = :slots " +
            "AND CAST(FIND_IN_SET(:selected_numbers, REPLACE(b.selected_numbers, ' ', '')) AS int) > 0 " +
            "group by ad.username")
    List<WinnerDTO> findUsernamesBySelectedNumber(@Param("selected_numbers") String selected_numbers, @Param("slots") String slots);

    // Below is the query for fetching the total amount invested by user:
    //SELECT ad.username, sum(b.bet_amount) as total_invested, :selectedNumber FROM bets_details b JOIN Account_Details ad ON b.username = ad.username WHERE FIND_IN_SET(:selectedNumber, REPLACE(b.selected_numbers, ' ', '')) > 0 group by ad.username;
}
