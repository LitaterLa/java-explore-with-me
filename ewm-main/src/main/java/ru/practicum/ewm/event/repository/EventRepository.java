package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.event.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer>, QuerydslPredicateExecutor<Event> {

    boolean existsByCategoryName(String name);

    Optional<Event> findByIdAndInitiatorId(int id, int userId);

    List<Event> findALlByInitiatorId(int userId, Pageable pageable);

}
