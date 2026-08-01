package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {

    boolean existsByNameAndDescriptionAndAvailableAndOwnerAndRequest(String name,
                                                                     String description,
                                                                     boolean available,
                                                                     long owner,
                                                                     ItemRequest request);

    List<Item> findAllByOwner(long ownerId);

    @Query("SELECT i FROM Item AS i LEFT JOIN FETCH i.request " +
            "WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))")
    List<Item> search(@Param("text") String text);

    boolean existsByOwner(long ownerId);

    @Query("SELECT i FROM Item AS i " +
            "JOIN FETCH i.request " +
            "WHERE i.request.id IN (:idSet)")
    List<Item> findAllByRequestIdIn(@Param("idSet") Set<Long> idSet);

    @Query("SELECT i FROM Item AS i " +
            "JOIN FETCH i.request " +
            "WHERE i.request.id = :id")
    List<Item> findAllByRequestId(@Param("id") long id);

}
