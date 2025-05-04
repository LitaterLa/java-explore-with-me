package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.comment.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findAllById(int id, Pageable pageable);

    Page<Comment> findAllByUserId(int userId, Pageable pageable);

    boolean existsByUserIdAndEventId(int userId, int eventId);
}
