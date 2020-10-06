package org.bitmap.comnhalam.service.Impl;

import org.bitmap.comnhalam.service.MailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class MailSenderServiceImpl implements MailSenderService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendMailVerification(String email, String token) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Xác thực tài khoản tại comnhalam.com");

//        String linkVerification = "http://localhost:8080/confirmRegistration?token=" + token;
        String linkVerification = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("confirmRegistration")
                .queryParam("token", token).toUriString();
        message.setText(linkVerification);
        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Đã xảy ra sự cố không thể gửi email");
        }
    }

    @Override
    public void sendMailResetPassword(String email, String token) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Lấy lại mật khẩu tại comnhalam.com");
//        String linkResetPassword = "http://localhost:8080/change_password?token=" + token;
        String linkResetPassword = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("change_password")
                .queryParam("token", token).toUriString();
        message.setText(linkResetPassword);
        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Đã xảy ra sự cố không thể gửi email");
        }
    }
}
