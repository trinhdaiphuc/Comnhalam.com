package org.bitmap.comnhalam.repository;

import org.bitmap.comnhalam.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Set<Tag> findByName(String name);
    @Query("SELECT t FROM Tag t where t.name = :name")
    Tag findWithName(@Param("name") String name);
}
