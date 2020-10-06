package org.bitmap.comnhalam.service;

import org.bitmap.comnhalam.model.Role;
import org.bitmap.comnhalam.model.User;
import org.bitmap.comnhalam.repository.UserRepository;
import org.bitmap.comnhalam.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        String ip = RequestUtils.getClientIP(request);
        if (loginAttemptService.isBlocked(ip)) {
            throw new RuntimeException("block_ip");
        }

        User user = userRepository.findByEmail(s);
        if (user == null) {
            throw new UsernameNotFoundException("user_not_found");
        }

        if(!user.isEnabled()) {
            throw new RuntimeException("user_is_blocked");
        }

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        Set<Role> roles = user.getRoles();

        for (Role role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return new CustomUserDetails(
                user.getEmail(),
                user.getPassword(),
                grantedAuthorities,
                user.getFirstName() + " " + user.getLastName(),
                user.getImg()
        );
    }
}
