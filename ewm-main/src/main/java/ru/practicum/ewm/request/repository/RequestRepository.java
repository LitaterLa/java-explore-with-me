package ru.practicum.ewm.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.request.model.ParticipationRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Integer> {
    boolean existsByRequesterEmail(String email);

    int countByEventIdAndStatus(int id, State state);

    @Query("SELECT r FROM ParticipationRequest r WHERE r.requester.id = :requesterId")
    Page<ParticipationRequest> findAllByRequester(Pageable pageable, @Param("requesterId") int requesterId);

    @Query("SELECT r FROM ParticipationRequest r WHERE r.event.id = :eventId")
    List<ParticipationRequest> findByRequesterIdAndEventId(@Param("eventId") int eventId);

}
