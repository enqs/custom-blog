package enqs.customblog.service;

import enqs.customblog.dao.UserRepository;
import enqs.customblog.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(int id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElse(new User());
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser.orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
    }

    @Override
    public void save(User user) {
        User preparedUser = isUserNew(user) ? prepareNewUser(user) : preparePersistedUser(user);
        userRepository.save(preparedUser);
        user.setPassword(Objects.isNull(user.getPassword()) ?
                userRepository.findById(user.getId()).orElseThrow().getPassword() :
                passwordEncoder.encode(user.getPassword()));
    }

    @Override
    public void deleteById(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole())));
    }

    private boolean isUserNew(User user) {
        return user.getId() == 0;
    }

    private User prepareNewUser(User user) {
        if (!isUsernameAvailable(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        user.setRole(Objects.isNull(user.getRole()) ? "ROLE_USER" : user.getRole());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return user;
    }

    private boolean isUsernameAvailable(String targetUsername) {
        return userRepository.findAll().stream()
                .map(User::getUsername)
                .noneMatch(persistedUsernames -> Objects.equals(persistedUsernames, targetUsername));
    }

    private User preparePersistedUser(User user) {
        boolean isPasswordChanged = Objects.isNull(user.getPassword()) || user.getPassword().equals("");
        user.setPassword(!isPasswordChanged ?
                passwordEncoder.encode(user.getPassword()) :
                userRepository.findById(user.getId()).orElseThrow().getPassword());
        return user;
    }
}
