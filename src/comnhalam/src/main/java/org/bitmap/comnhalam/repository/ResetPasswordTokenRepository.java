package org.bitmap.comnhalam.repository;

import org.bitmap.comnhalam.model.ResetPasswordToken;
import org.bitmap.comnhalam.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {
    ResetPasswordToken findByToken(String token);
    ResetPasswordToken findByUser(User user);
}
