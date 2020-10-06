package org.bitmap.comnhalam.repository;

import org.bitmap.comnhalam.model.Product;
import org.bitmap.comnhalam.model.Role;
import org.bitmap.comnhalam.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    List<User> findUsersByCreateOnGreaterThan(Date date);
    List<User> findUserByRoles(Set<Role> roles);
}
