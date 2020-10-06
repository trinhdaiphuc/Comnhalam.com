package org.bitmap.comnhalam.service.Impl;

import org.bitmap.comnhalam.model.ResetPasswordToken;
import org.bitmap.comnhalam.model.User;
import org.bitmap.comnhalam.model.VerificationToken;
import org.bitmap.comnhalam.repository.ResetPasswordTokenRepository;
import org.bitmap.comnhalam.repository.UserRepository;
import org.bitmap.comnhalam.repository.VerificationTokenRepository;
import org.bitmap.comnhalam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private ResetPasswordTokenRepository resetPasswordTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public VerificationToken getVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public void deleteVerificationToken(VerificationToken token) {
        verificationTokenRepository.delete(token);
    }

    @Override
    @Transactional
    public void createVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(user, token);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public VerificationToken getVerificationToken(User user) {
        return verificationTokenRepository.findByUser(user);
    }

    @Override
    public ResetPasswordToken getResetPasswordToken(String token) {
        return resetPasswordTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public void deleteResetPasswordToken(ResetPasswordToken token) {
        resetPasswordTokenRepository.delete(token);
    }

    @Override
    @Transactional
    public void createResetPasswordToken(User user, String token) {
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken(token, user);
        resetPasswordTokenRepository.save(resetPasswordToken);
    }

    @Override
    public ResetPasswordToken getResetPasswordToken(User user) {
        return resetPasswordTokenRepository.findByUser(user);
    }

    @Override
    @Transactional
    public void changeUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
