package enqs.customblog.service;

import enqs.customblog.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    void save(User user);

    boolean isUsernameAvailable(String username);

}
