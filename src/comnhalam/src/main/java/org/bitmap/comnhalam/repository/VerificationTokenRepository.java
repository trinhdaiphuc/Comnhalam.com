package org.bitmap.comnhalam.repository;

import org.bitmap.comnhalam.model.User;
import org.bitmap.comnhalam.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
}
