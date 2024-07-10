package com.arrow.Arrow.repository;

import com.arrow.Arrow.dto.BetListDTO;
import com.arrow.Arrow.dto.WinnerDTO;
import com.arrow.Arrow.entity.Bet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {

    //@Query("SELECT DISTINCT ad.username FROM Bet b JOIN User ad ON b.user.username = ad.username WHERE CAST(FIND_IN_SET(:selected_numbers, REPLACE(b.selected_numbers, ' ', '')) AS int) > 0")
    @Query(value = "SELECT new com.arrow.Arrow.dto.WinnerDTO(ad.username, sum(b.bet_amount) as total_invested, :selected_numbers)  " +
            "FROM Bet b " +
            "JOIN User ad " +
            "ON b.user.username = ad.username " +
            "WHERE b.slot = :slots " +
            "AND b.bet_time >= :start_time " +
            "AND b.bet_time <= :end_time " +
            "AND CAST(FIND_IN_SET(:selected_numbers, REPLACE(b.selected_numbers, ' ', '')) AS int) > 0 " +
            "group by ad.username")
    List<WinnerDTO> findUsernamesBySelectedNumber(@Param("selected_numbers") String selected_numbers, @Param("start_time") LocalDateTime start_time, @Param("end_time") LocalDateTime end_time,@Param("slots") String slots);

    // Below is the query for fetching the total amount invested by user:
    @Query(value = "SELECT new com.arrow.Arrow.dto.BetListDTO(b.id, b.selected_numbers, b.bet_amount) FROM Bet b " +
            "WHERE b.user.username=:username AND b.slot = :slots ORDER BY b.bet_time DESC")

    Page<BetListDTO> findLatestBet(Pageable pageable, @Param("username") String username, @Param("slots") String slots);
}
