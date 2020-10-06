package org.bitmap.comnhalam.listener;

import org.bitmap.comnhalam.service.LoginAttemptService;
import org.bitmap.comnhalam.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private LoginAttemptService loginAttemptService;

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        // Login failed by BadCredentialsException (Username or password incorrect)
        if (exception.getClass().isAssignableFrom(BadCredentialsException.class)) {
            loginAttemptService.loginFailed(RequestUtils.getClientIP(request));
        }

        // Login failed by Blocked IP
        if (exception.getMessage() != null && exception.getMessage().equals("block_ip")) {
            response.sendRedirect(request.getContextPath() + "/login?message=block_ip");
            return;
        }

        if (exception.getMessage() != null && exception.getMessage().equals("user_is_blocked")) {
            response.sendRedirect(request.getContextPath() + "/login?message=user_is_blocked");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/login?message=user_not_found");
    }
}
