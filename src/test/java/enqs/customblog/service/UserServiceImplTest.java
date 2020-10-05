package enqs.customblog.service;

import enqs.customblog.dao.UserRepository;
import enqs.customblog.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    void shouldSaveUserOneTime() {
        //WHEN
        userService.save(sampleUserFoo);

        //THEN
        List<User> users = userRepository.findAll();
        Assertions.assertThat(users).containsExactly(sampleUserFoo);
    }

    @Test
    void shouldEncryptNewPassword() {
        //GIVEN
        String password = "samplePassword";
        sampleUserFoo.setPassword(password);

        //WHEN
        userService.save(sampleUserFoo);

        //THEN
        Assertions.assertThat(passwordEncoder.matches(password, sampleUserFoo.getPassword())).isTrue();
    }

    @Test
    void shouldNotChangeExistingPasswordWhenNewPasswordIsNull() {
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
    void shouldNotChangeRoleWhenAlreadySpecified() {
        //GIVEN
        String originalRole = sampleUserFoo.getRole();

        //WHEN
        userService.save(sampleUserFoo);

        //THEN
        Assertions.assertThat(sampleUserFoo.getRole()).isEqualTo(originalRole);
    }


    @Test
    void shouldNotModifyNorDeleteOtherEntriesWhenSaveNew() {
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
    void shouldNotModifyNorDeleteOtherEntriesWhenSaveEdited() {
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
    void shouldModifyOnlyTargetEntity() {
        //GIVEN
        userRepository.saveAll(List.of(sampleUserFoo, sampleUserBar, sampleUserBaz));
        userRepository.flush();

        //WHEN
        sampleUserFoo.setFirstName("NewFirstName");
        sampleUserFoo.setLastName("NewLastName");
        userService.save(sampleUserFoo);


        //THEN
        List<User> users = userRepository.findAll();
        Assertions.assertThat(users).containsExactlyInAnyOrderElementsOf(List.of(sampleUserFoo, sampleUserBar, sampleUserBaz));
    }

    @Test
    void shouldFindExistingEntityById() {
        //GIVEN
        userRepository.saveAll(List.of(sampleUserFoo, sampleUserBar, sampleUserBaz));
        int targetId = sampleUserBar.getId();

        //WHEN
        User user = userService.findById(targetId);

        //THEN
        Assertions.assertThat(user).isEqualToComparingFieldByField(sampleUserBar);
    }

    @Test
    void shouldNotFindEntityThatDoesNotExist() {
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
    void shouldFindExactlyAllPersistedEntities() {
        //GIVEN
        userRepository.saveAll(List.of(sampleUserFoo, sampleUserBar, sampleUserBaz));

        //WHEN
        List<User> users = userService.findAll();

        //THEN
        Assertions.assertThat(users).containsExactlyInAnyOrderElementsOf(List.of(sampleUserFoo, sampleUserBar, sampleUserBaz));
    }

    @Test
    void shouldReturnEmptyListWhenNothingPersisted() {
        //GIVEN
        //empty db ;)

        //WHEN
        List<User> users = userService.findAll();

        //THEN
        Assertions.assertThat(users).hasSize(0);
    }

    @Test
    void shouldDeleteOnlyTargetEntity() {
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
    void shouldReturnFalseWhenUsernameIsUnavailable() {
        //GIVEN
        userRepository.saveAll(List.of(sampleUserFoo, sampleUserBar, sampleUserBaz));

        //WHEN
        boolean usernameAvailable = userService.isUsernameAvailable(sampleUserFoo.getUsername());

        //THEN
        Assertions.assertThat(usernameAvailable).isFalse();
    }

    @Test
    void shouldNotModifyNorDeleteOtherEntriesWhenUsernameIsUnavailable() {
        //GIVEN
        userRepository.saveAll(List.of(sampleUserFoo, sampleUserBar));

        //WHEN
        userService.isUsernameAvailable(sampleUserBaz.getUsername());

        //THEN
        List<User> users = userRepository.findAll();
        Assertions.assertThat(users).containsExactlyInAnyOrderElementsOf(List.of(sampleUserFoo, sampleUserBar));
    }

    @Test
    void shouldReturnTrueWhenUsernameIsAvailable() {
        //GIVEN
        userRepository.saveAll(List.of(sampleUserBar, sampleUserBaz));

        //WHEN
        boolean usernameAvailable = userService.isUsernameAvailable(sampleUserFoo.getUsername());

        //THEN
        Assertions.assertThat(usernameAvailable).isTrue();
    }

    @Test
    void shouldNotModifyNorDeleteOtherEntriesWhenUsernameIsAvailable() {
        //GIVEN
        userRepository.saveAll(List.of(sampleUserFoo, sampleUserBar));

        //WHEN
        userService.isUsernameAvailable(sampleUserBaz.getUsername());

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
}