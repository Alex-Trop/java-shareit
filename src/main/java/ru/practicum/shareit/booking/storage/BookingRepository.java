package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= :now " +
            "AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentBookings(@Param("bookerId") long bookerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findPastBookings(@Param("bookerId") long bookerId, @Param("now")LocalDateTime now);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureBookings(@Param("bookerId") long bookerId, @Param("now")LocalDateTime now);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker AS u " +
            "WHERE u.id = :bookerId " +
            "AND b.bookingStatus = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerAndBookingStatusOrderByStartDesc(@Param("bookerId") long bookerId, @Param("status") BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker AS u " +
            "WHERE u.id = :bookerId " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerOrderByStartDesc(@Param("bookerId") long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "JOIN FETCH b.booker " +
            "WHERE i.owner = :userId " +
            "AND b.start <= :now " +
            "AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findOwnerCurrentBookings(@Param("userId") long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "JOIN FETCH b.booker " +
            "WHERE i.owner = :userId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findOwnerPastBookings(@Param("userId") long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "JOIN FETCH b.booker " +
            "WHERE i.owner = :userId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findOwnerFutureBookings(@Param("userId") long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "JOIN FETCH b.booker " +
            "WHERE i.owner = :userId " +
            "AND b.bookingStatus = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findOwnerAllBookingsByBookingStatus(@Param("userId") long userId, @Param("status") BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "JOIN FETCH b.booker " +
            "WHERE i.owner = :userId " +
            "ORDER BY b.start DESC")
    List<Booking> findOwnerAllBookings(@Param("userId") long userId);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "JOIN FETCH b.booker " +
            "WHERE i.id IN :items " +
            "ORDER BY b.start")
    List<Booking> findAllByItemIdInOrderByStart(@Param("items") Set<Long> items);

    boolean existsByItemIdAndBookerIdAndEndBefore(long itemId, long bookerId, LocalDateTime time);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "JOIN FETCH b.booker " +
            "WHERE i.id = :id")
    List<Booking> findAllByItemId(@Param("id") long itemId);
}
