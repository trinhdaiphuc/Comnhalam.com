package org.bitmap.comnhalam.repository;

import org.bitmap.comnhalam.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
