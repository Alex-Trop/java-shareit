package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking AS b " +
            "JOIN b.item " +
            "JOIN b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= :now " +
            "AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> getCurrentBookings(@Param("bookerId") long bookerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN b.item " +
            "JOIN b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> getPastBookings(@Param("bookerId") long bookerId, @Param("now")LocalDateTime now);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN b.item " +
            "JOIN b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> getFutureBookings(@Param("bookerId") long bookerId, @Param("now")LocalDateTime now);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN b.item " +
            "JOIN b.booker AS u " +
            "WHERE u.id = :bookerId " +
            "AND b.bookingStatus = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerAndBookingStatusOrderByStartDesc(@Param("bookerId") long bookerId, @Param("status") BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN b.item " +
            "JOIN b.booker AS u " +
            "WHERE u.id = :bookerId " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerOrderByStartDesc(@Param("bookerId") long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN b.item AS i " +
            "JOIN b.booker " +
            "WHERE i.owner = :userId " +
            "AND b.start <= :now " +
            "AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> getOwnerCurrentBookings(@Param("userId") long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN b.item AS i " +
            "JOIN b.booker " +
            "WHERE i.owner = :userId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> getOwnerPastBookings(@Param("userId") long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN b.item AS i " +
            "JOIN b.booker " +
            "WHERE i.owner = :userId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> getOwnerFutureBookings(@Param("userId") long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN b.item AS i " +
            "JOIN b.booker " +
            "WHERE i.owner = :userId " +
            "AND b.bookingStatus = :status " +
            "ORDER BY b.start DESC")
    List<Booking> getOwnerAllBookingsByBookingStatus(@Param("userId") long userId, @Param("status") BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN b.item AS i " +
            "JOIN b.booker " +
            "WHERE i.owner = :userId " +
            "ORDER BY b.start DESC")
    List<Booking> getOwnerAllBookings(@Param("userId") long userId);

    List<Booking> findAllByItemIdInOrderByStart(Set<Long> items);

    boolean existsByItemIdAndBookerIdAndEndBefore(long itemId, long bookerId, LocalDateTime time);

    List<Booking> findAllByItemId(long itemId);
}
