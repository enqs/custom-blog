package enqs.customblog.config;

import enqs.customblog.entity.User;
import enqs.customblog.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    public CustomAuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        request.getSession().setAttribute("user", user);
        //ToDo: Redirect to current page of user
        response.sendRedirect(request.getContextPath() + "/articles");
    }
}
