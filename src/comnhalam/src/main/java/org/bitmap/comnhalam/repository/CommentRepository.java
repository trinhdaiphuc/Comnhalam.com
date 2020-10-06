package org.bitmap.comnhalam.repository;

import org.bitmap.comnhalam.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProductId(Long id);

}
