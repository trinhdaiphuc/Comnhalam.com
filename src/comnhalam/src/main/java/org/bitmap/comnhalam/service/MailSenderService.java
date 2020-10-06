package org.bitmap.comnhalam.service;

import org.springframework.stereotype.Service;

public interface MailSenderService {
    void sendMailVerification(String email, String token) throws Exception;
    void sendMailResetPassword(String email, String token) throws Exception;
}
