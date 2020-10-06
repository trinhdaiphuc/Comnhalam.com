package org.bitmap.comnhalam.service;

import org.bitmap.comnhalam.model.ResetPasswordToken;
import org.bitmap.comnhalam.model.User;
import org.bitmap.comnhalam.model.VerificationToken;
import org.springframework.stereotype.Service;

public interface UserService {
    VerificationToken getVerificationToken(String token);

    void deleteVerificationToken(VerificationToken token);

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(User user);

    ResetPasswordToken getResetPasswordToken(String token);
    void deleteResetPasswordToken(ResetPasswordToken token);
    void createResetPasswordToken(User user, String token);
    ResetPasswordToken getResetPasswordToken(User user);

    void changeUserPassword(User user, String newPassword);
}