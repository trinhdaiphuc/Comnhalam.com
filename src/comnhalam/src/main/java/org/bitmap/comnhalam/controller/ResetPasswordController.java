package org.bitmap.comnhalam.controller;

import org.bitmap.comnhalam.model.ResetPasswordToken;
import org.bitmap.comnhalam.model.User;
import org.bitmap.comnhalam.repository.UserRepository;
import org.bitmap.comnhalam.service.CaptchaService;
import org.bitmap.comnhalam.service.MailSenderService;
import org.bitmap.comnhalam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

@Controller
public class ResetPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    private CaptchaService captchaService;

    @RequestMapping("/reset_password")
    public String resetPasswordView() {
        return "resetPassword";
    }

    @RequestMapping(value = "/reset_password", method = RequestMethod.POST)
    @ResponseBody
    public String sendEmailReset(@Param("email") String email,
                                 Model model,
                                 WebRequest request) {
        String response = request.getParameter("g-recaptcha-response");
        try {
            captchaService.processResponse(response);
        } catch (Exception e) {
            return e.getMessage();
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return "User not found";
        }

        String token = UUID.randomUUID().toString();
        userService.createResetPasswordToken(user, token);
        try {
            mailSenderService.sendMailResetPassword(email, token);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "success";
    }

    @RequestMapping(value = "/change_password")
    public String changePassword(@Param("token") String token,
                                 Model model) {
        ResetPasswordToken resetPasswordToken = userService.getResetPasswordToken(token);

        Calendar cal = Calendar.getInstance();
        if((resetPasswordToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            model.addAttribute("error", "Token đã hết hạn");
            userService.deleteResetPasswordToken(resetPasswordToken);
            return "resetPassword";
        }

        User user = resetPasswordToken.getUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
                Arrays.asList(new SimpleGrantedAuthority("ROLE_CHANGE_PASSWORD")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        userService.deleteResetPasswordToken(resetPasswordToken);

        return "redirect:/update_password";
    }

    @RequestMapping("/update_password")
    public String updatePasswordView() {
        return "updatePassword";
    }

    @RequestMapping(value = "/update_password", method = RequestMethod.POST)
    public String updatePasswordProcess(@Param("newPassword") String newPassword,
                                        @Param("confirmPassword") String confirmPassword,
                                        Model model,
                                        HttpServletRequest request) {

        if(newPassword == null || confirmPassword == null){
            model.addAttribute("error", "Trường này không được phép để trống");
            return "updatePassword";
        }

        if(!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu không trùng khớp");
            return "updatePassword";
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.changeUserPassword(user, newPassword);
        request.getSession().removeAttribute("JSESSIONID");
        request.getSession().invalidate();
        return "redirect:/";
    }
}
