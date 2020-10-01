package enqs.customblog.service;

import enqs.customblog.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    List<User> findAll();

    User findById(int id);

    void save(User user);

    boolean isUsernameAvailable(String username);

    void deleteById(int id);
}
