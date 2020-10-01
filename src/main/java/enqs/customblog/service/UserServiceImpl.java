package enqs.customblog.service;

import enqs.customblog.dao.UserRepository;
import enqs.customblog.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  PasswordEncoder passwordEncoder;

//    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(int id) {
        Optional<User> optionalUser = userRepository.findById(id);
        //ToDo: Throw not found exception with valid response code
        return optionalUser.orElse(new User());
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser.orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
    }

    @Override
    public void save(User user) {
        //ToDo: this should cooperate with db or enum
        user.setRole(Objects.isNull(user.getRole()) ? "ROLE_USER" : user.getRole());
        //ToDo: throw valid exception instead of generic one
        user.setPassword(Objects.isNull(user.getPassword()) ?
                userRepository.findById(user.getId()).orElseThrow().getPassword() :
                passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return userRepository.findAll().stream()
                .map(User::getUsername)
                .noneMatch(persisted -> Objects.equals(persisted, username));
    }

    @Override
    public void deleteById(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole())));
    }
}
