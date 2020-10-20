package enqs.customblog.service;

import enqs.customblog.dao.UserRepository;
import enqs.customblog.entity.Article;
import enqs.customblog.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

//ToDo: If possible extract abstract generic test class for similar services
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private UserService userService;
    private User sampleUserFoo;
    private User sampleUserBar;
    private User sampleUserBaz;
    private List<User> users;

    @BeforeEach
    void setUp() {
        //GIVEN
        userRepository.deleteAll();
        userRepository.flush();
        userService = new UserServiceImpl(userRepository, passwordEncoder);
        sampleUserFoo = new User(0, "Username1", "Password1", "POLE_USER", "Nick1", "FirstName1", "LastName1");
        sampleUserBar = new User(0, "Username2", "Password2", "POLE_USER", "Nick2", "FirstName2", "LastName2");
        sampleUserBaz = new User(0, "Username3", "Password3", "POLE_USER", "Nick3", "FirstName3", "LastName3");

    }

    @Test
    void shouldFindExactlyAllPersistedUsers() {
        //GIVEN
        userRepository.saveAll(List.of(sampleUserFoo, sampleUserBar, sampleUserBaz));

        //WHEN
        List<User> users = userService.findAll();

        //THEN
        Assertions.assertThat(users).containsExactlyInAnyOrderElementsOf(List.of(sampleUserFoo, sampleUserBar, sampleUserBaz));
    }

    @Test
    void shouldReturnEmptyListWhenNoUserPersisted() {
        //GIVEN
        //empty db ;)

        //WHEN
        List<User> users = userService.findAll();

        //THEN
        Assertions.assertThat(users).hasSize(0);
    }


    @Test
    void shouldNotModifyNorDeleteUsersWhenFindingAll() {
        //GIVEN
        List<User> users = List.of(this.sampleUserFoo, sampleUserBar, sampleUserBaz);
        userRepository.saveAll(users);

        //WHEN
        userService.findAll();

        //THEN
        Assertions.assertThat(userRepository.findAll()).containsExactlyInAnyOrderElementsOf(users);
    }

    @Test
    void shouldFindExistingUserById() {
        //GIVEN
        userRepository.saveAll(List.of(sampleUserFoo, sampleUserBar, sampleUserBaz));
        int targetId = sampleUserBar.getId();

        //WHEN
        User user = userService.findById(targetId);

        //THEN
        Assertions.assertThat(user).isEqualToComparingFieldByField(sampleUserBar);
    }

    @Test
    void shouldNotDeleteNorModifyUsersWhenFindingUserById() {
        //GIVEN
        users = List.of(sampleUserFoo, sampleUserBar, sampleUserBaz);
        userRepository.saveAll(users);
        int targetId = sampleUserBar.getId();

        //WHEN
        userService.findById(targetId);

        //THEN
        Assertions.assertThat(userRepository.findAll()).containsExactlyInAnyOrderElementsOf(users);
    }

    @Test
    void shouldNotFindUserThatDoesNotExist() {
        //GIVEN
        userRepository.saveAll(List.of(sampleUserFoo, sampleUserBar, sampleUserBaz));
        int targetId = sampleUserBaz.getId() * 10;

        //WHEN
        User user = userService.findById(targetId);

        //THEN
        //ToDo: Should expect exception
        Assertions.assertThat(user).isEqualToComparingFieldByField(new User());
    }

    @Test
    void shouldNotDeleteNorModifyUsersWhenFailingToFindingUserById() {
        //GIVEN
        users = List.of(sampleUserFoo, sampleUserBar, sampleUserBaz);
        userRepository.saveAll(users);
        int targetId = sampleUserBar.getId() * 80;

        //WHEN
        userService.findById(targetId);

        //THEN
        Assertions.assertThat(userRepository.findAll()).containsExactlyInAnyOrderElementsOf(users);
    }

    @Test
    void shouldSaveUserOneTime() {
        //WHEN
        userService.save(sampleUserFoo);

        //THEN
        List<User> users = userRepository.findAll();
        Assertions.assertThat(users).containsExactly(sampleUserFoo);
    }

    @Test
    void shouldEncryptNewUserPasswordWhenSavingUser() {
        //GIVEN
        String password = "samplePassword";
        sampleUserFoo.setPassword(password);

        //WHEN
        userService.save(sampleUserFoo);

        //THEN
        Assertions.assertThat(passwordEncoder.matches(password, sampleUserFoo.getPassword())).isTrue();
    }

    @Test
    void shouldNotChangeExistingUserPasswordWhenNewPasswordIsNull() {
        //GIVEN
        String originalPassword = "samplePassword";
        sampleUserFoo.setPassword(passwordEncoder.encode(originalPassword));
        userRepository.save(sampleUserFoo);
        sampleUserBar.setId(sampleUserFoo.getId());
        sampleUserBar.setPassword(null);

        //WHEN
        userService.save(sampleUserBar);

        //THEN
        User persistedUser = userRepository.findById(sampleUserBar.getId()).orElseThrow();
        Assertions.assertThat(passwordEncoder.matches(originalPassword, persistedUser.getPassword())).isTrue();
    }

    @Test
    void shouldGrantUserRoleWhenNoOtherRoleSpecified() {
        //GIVEN
        sampleUserFoo.setRole(null);

        //WHEN
        userService.save(sampleUserFoo);

        //THEN
        Assertions.assertThat(sampleUserFoo.getRole()).isEqualTo("ROLE_USER");
    }

    @Test
    void shouldNotChangeUserRoleWhenAlreadySpecified() {
        //GIVEN
        String originalRole = sampleUserFoo.getRole();

        //WHEN
        userService.save(sampleUserFoo);

        //THEN
        Assertions.assertThat(sampleUserFoo.getRole()).isEqualTo(originalRole);
    }

    @Test
    void shouldNotModifyNorDeleteOtherUsersWhenSaveNew() {
        //GIVEN
        userRepository.saveAll(List.of(sampleUserFoo, sampleUserBar));
        userRepository.flush();

        //WHEN
        userService.save(sampleUserBaz);

        //THEN
        List<User> users = userRepository.findAll();
        Assertions.assertThat(users).containsExactlyInAnyOrderElementsOf(List.of(sampleUserFoo, sampleUserBar, sampleUserBaz));
    }

    @Test
    void shouldNotModifyNorDeleteOtherUsersWhenSaveEdited() {
        //GIVEN
        userRepository.saveAll(List.of(sampleUserFoo, sampleUserBar));
        userRepository.flush();
        sampleUserBaz.setId(sampleUserBar.getId());

        //WHEN
        userService.save(sampleUserBaz);

        //THEN
        List<User> users = userRepository.findAll();
        Assertions.assertThat(users).containsExactlyInAnyOrderElementsOf(List.of(sampleUserFoo, sampleUserBaz));
    }

    @Test
    void shouldDeleteOnlyTargetUser() {
        //GIVEN
        userRepository.saveAll(List.of(sampleUserFoo, sampleUserBar, sampleUserBaz));
        int targetId = sampleUserBaz.getId();

        //WHEN
        userService.deleteById(targetId);

        //THEN
        List<User> users = userRepository.findAll();
        Assertions.assertThat(users).containsExactlyInAnyOrderElementsOf(List.of(sampleUserFoo, sampleUserBar));
    }

    @Test
    void shouldFindUserByUsername() {
        //GIVEN
        userRepository.saveAll(List.of(sampleUserFoo, sampleUserBar));

        //WHEN
        User foundUser = userService.findByUsername(sampleUserFoo.getUsername());

        //THEN
        Assertions.assertThat(foundUser).isEqualTo(sampleUserFoo);
    }

    @Test
    void shouldNotModifyNorDeleteOtherEntriesWhenFindUserByUsernameSucceeds() {
        //GIVEN
        userRepository.saveAll(List.of(sampleUserFoo, sampleUserBar));

        //WHEN
        userService.findByUsername(sampleUserFoo.getUsername());

        //THEN
        List<User> users = userRepository.findAll();
        Assertions.assertThat(users).containsExactlyInAnyOrderElementsOf(List.of(sampleUserFoo, sampleUserBar));
    }

    @Test
    void shouldThrowExceptionWhenUsernameDoesNotExist() {
        //GIVEN
        userRepository.saveAll(List.of(sampleUserFoo, sampleUserBar));

        Assertions
                //WHEN
                .assertThatThrownBy(() -> userService.findByUsername(sampleUserBaz.getUsername()))

                //THEN
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldNotModifyNorDeleteOtherEntriesWhenFindUserByUsernameFails() {
        //GIVEN
        userRepository.saveAll(List.of(sampleUserFoo, sampleUserBar));

        //WHEN
        try {
            userService.findByUsername(sampleUserBaz.getUsername());
        } catch (Exception ignored) {
        }

        //THEN
        List<User> users = userRepository.findAll();
        Assertions.assertThat(users).containsExactlyInAnyOrderElementsOf(List.of(sampleUserFoo, sampleUserBar));
    }

    @Test
    void shouldReturnValidUserDetails() {
        //GIVEN
        userRepository.save(sampleUserFoo);
        UserDetails expectedUserDetails = new org.springframework.security.core.userdetails.User(
                sampleUserFoo.getUsername(),
                sampleUserFoo.getPassword(),
                List.of(new SimpleGrantedAuthority(sampleUserFoo.getRole())));

        //WHEN
        UserDetails returnedUserDetails = userService.loadUserByUsername(sampleUserFoo.getUsername());

        //THEN
        Assertions.assertThat(returnedUserDetails).isEqualToComparingFieldByField(expectedUserDetails);
    }

    @Test
    void shouldNotDeleteNorModifyWhenLoadsUserDetails() {
        //GIVEN
        userRepository.saveAll(List.of(sampleUserFoo, sampleUserBar, sampleUserBaz));

        //WHEN
        userService.loadUserByUsername(sampleUserFoo.getUsername());

        //THEN
        Assertions.assertThat(userRepository.findAll()).containsExactlyInAnyOrderElementsOf(List.of(sampleUserFoo, sampleUserBar, sampleUserBaz));
    }

    @Test
    void shouldThrowUsernameNotFoundWhenFailedToLoadUserDetails() {
        //GIVEN
        userRepository.save(sampleUserFoo);

        Assertions
                //WHEN
                        .assertThatThrownBy(() -> userService.loadUserByUsername(sampleUserBar.getUsername()))
                //THEN
                        .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldNotDeleteNorModifyWhenFailsToLoadUserDetails() {
        //GIVEN
        userRepository.saveAll(List.of( sampleUserBar, sampleUserBaz));

        //WHEN
        try {
            userService.loadUserByUsername(sampleUserFoo.getUsername());
        } catch (UsernameNotFoundException ignored) {
        }

        //THEN
        Assertions.assertThat(userRepository.findAll()).containsExactlyInAnyOrderElementsOf(List.of(sampleUserBar, sampleUserBaz));
    }

}