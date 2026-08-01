package ru.practicum.shareit.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    boolean existsByDescriptionAndRequestorId(String description, Long requestorId);

    @Query("SELECT r FROM ItemRequest AS r " +
            "JOIN FETCH r.requestor " +
            "WHERE r.requestor.id = :requestorId " +
            "ORDER BY r.created DESC")
    List<ItemRequest> findAllByRequestor(@Param("requestorId") long requestorId);

    @Query("SELECT r FROM ItemRequest AS r " +
            "JOIN FETCH r.requestor " +
            "ORDER BY r.created DESC")
    List<ItemRequest> findAllOrderByCreatedDesc();

}
