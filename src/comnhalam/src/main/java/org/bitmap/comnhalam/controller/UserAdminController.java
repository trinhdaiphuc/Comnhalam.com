package org.bitmap.comnhalam.controller;

import org.bitmap.comnhalam.model.Role;
import org.bitmap.comnhalam.model.User;
import org.bitmap.comnhalam.repository.RoleRepository;
import org.bitmap.comnhalam.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserAdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserAdminController.class);

    @RequestMapping("/admin/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @RequestMapping("/admin/active-user/{id}/{checked}")
    @ResponseBody
    public String activeUser(@PathVariable("id") String id,
                             @PathVariable("checked") String checked) {
        Long nId = null;

        try {
            nId = Long.parseUnsignedLong(id);
        } catch (NumberFormatException e) {
            return e.getMessage();
        }

        try {
            User user = userRepository.findById(nId).orElseThrow(() -> new Exception("User not Found"));
            if (checked.equals("yes")) {
                user.setEnabled(true);
            } else {
                user.setEnabled(false);
            }
            userRepository.save(user);
            logger.info("Active User >> " + user.getEmail() + " is " + (user.isEnabled() ? "activate" : "not activate"));
        } catch (Exception e) {
            return e.getMessage();
        }

        return "success";
    }

    @RequestMapping("/admin/active-role/{id}/{role}/{checked}")
    @ResponseBody
    public String activeRole(@PathVariable("id") String id,
                             @PathVariable("role") String role,
                             @PathVariable("checked") String checked) {
        Long nId = null;

        try {
            nId = Long.parseUnsignedLong(id);
        } catch (NumberFormatException e) {
            return e.getMessage();
        }

        try {
            User user = userRepository.findById(nId).orElseThrow(() -> new Exception("User not Found"));
            Role roleUser = roleRepository.findByName(role);
            if (roleUser == null) {
                throw new Exception("Role not found");
            }

            if (checked.equals("yes"))
                user.addRole(roleUser);
            else {
                user.removeRole(roleUser);
            }

            userRepository.save(user);

        } catch (Exception e) {
            return e.getMessage();
        }
        return "success";
    }

    @RequestMapping("/admin/users/{id}")
    public String userProfile(Model model,
                              @PathVariable("id") String id) {
        Long nId = null;

        try {
            nId = Long.parseUnsignedLong(id);
        } catch (NumberFormatException e) {
            model.addAttribute("error", "ID wrong format");
        }

        try {
            User user = userRepository.findById(nId).orElseThrow(() -> new Exception("User not found"));
            model.addAttribute("user", user);
            model.addAttribute("roles", roleRepository.findAll());
        } catch (Exception e) {
            model.addAttribute("error", "User not found");
        }

        return "admin/user";
    }
}
