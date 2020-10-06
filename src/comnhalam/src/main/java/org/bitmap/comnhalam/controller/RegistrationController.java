package org.bitmap.comnhalam.controller;

import org.bitmap.comnhalam.form.UserRegisterForm;
import org.bitmap.comnhalam.model.Role;
import org.bitmap.comnhalam.model.User;
import org.bitmap.comnhalam.model.VerificationToken;
import org.bitmap.comnhalam.repository.RoleRepository;
import org.bitmap.comnhalam.repository.UserRepository;
import org.bitmap.comnhalam.service.CaptchaService;
import org.bitmap.comnhalam.service.CustomUserDetails;
import org.bitmap.comnhalam.service.MailSenderService;
import org.bitmap.comnhalam.service.UserService;
import org.bitmap.comnhalam.validator.UserRegisterValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Controller
public class RegistrationController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRegisterValidator userRegisterValidator;

    @Autowired
    private UserService userService;

    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    private CaptchaService captchaService;

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder) {
        Object target = dataBinder.getTarget();
        if (target == null)
            return;
        else {
            if (target.getClass() == UserRegisterForm.class)
                dataBinder.setValidator(userRegisterValidator);
        }
    }

    @RequestMapping("/registration")
    @Transactional
    public String registerView(Model model, Authentication authentication) {
        if (!(authentication == null || authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CHANGE_PASSWORD"))))
            return "redirect:/";
        model.addAttribute("userRegisterFrom", new UserRegisterForm());
        return "register";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registerProcessing(Model model,
                                     @ModelAttribute("userRegisterFrom") @Validated UserRegisterForm userRegisterForm,
                                     BindingResult bindingResult,
                                     WebRequest request,
                                     Authentication authentication) {
        if (!(authentication == null || authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CHANGE_PASSWORD"))))
            return "redirect:/";
        String response = request.getParameter("g-recaptcha-response");
        try {
            captchaService.processResponse(response);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        User user = new User(
                userRegisterForm.getFirstName(),
                userRegisterForm.getLastName(),
                userRegisterForm.getEmail(),
                passwordEncoder.encode(userRegisterForm.getPassword()),
                userRegisterForm.getNumberPhone(),
                userRegisterForm.getAddress()
        );

        user.addRole(roleRepository.findByName("ROLE_MEMBER"));
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);
        try {
            mailSenderService.sendMailVerification(user.getEmail(), token);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }

    public void authWithoutPassword(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        Set<Role> roles = user.getRoles();

        for (Role role : roles)
            authorities.add(new SimpleGrantedAuthority(role.getName()));

        CustomUserDetails userDetails = new CustomUserDetails(
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.getFirstName() + " " + user.getLastName(),
                user.getImg());

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @RequestMapping("/confirmRegistration")
    public String confirmRegistration(
            Model model,
            @Param("token") String token
    ) {
        if(token == null || token.isEmpty()) {
            return "confirmRegistration";
        }
        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            model.addAttribute("error", "Không tìm thấy token");
            return "confirmRegistration";
        }
        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            model.addAttribute("error", "Token đã hết hạn");
            userService.deleteVerificationToken(verificationToken);
            return "confirmRegistration";
        }

        user.setEnabled(true);
        userRepository.save(user);
        userService.deleteVerificationToken(verificationToken);
        authWithoutPassword(user);
        return "redirect:/";
    }

    @RequestMapping(value = "/resendVerification", method = RequestMethod.POST)
    @ResponseBody
    public String resendVerification(
            @Param("email") String email,
            WebRequest request
    ) {
        String response = request.getParameter("g-recaptcha-response");
        try {
            captchaService.processResponse(response);
        } catch (RuntimeException e) {
            return e.getMessage();
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return "User not found";
        } else if (user.isEnabled()) {
            return "This account was verified";
        }
        VerificationToken oldToken = userService.getVerificationToken(user);
        if (oldToken != null) {
            userService.deleteVerificationToken(oldToken);
        }
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);
        try {
            mailSenderService.sendMailVerification(user.getEmail(), token);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "success";
    }
}
