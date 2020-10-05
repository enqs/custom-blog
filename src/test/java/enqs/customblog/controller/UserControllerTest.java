package enqs.customblog.controller;

import enqs.customblog.entity.User;
import enqs.customblog.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.ui.Model;

import java.util.List;

//ToDo: If possible extract abstract generic test class for similar controllers
class UserControllerTest {

    private Model modelMock;
    private User sampleUser;
    private UserService userServiceMock;
    private UserController userController;

    @BeforeEach
    void setUp() {
        //GIVEN
        modelMock = Mockito.mock(Model.class);
        userServiceMock = Mockito.mock(UserService.class);
        userController = new UserController(userServiceMock);
        sampleUser = new User(
                0,
                "Username",
                "Password",
                null,
                "Nick",
                "FirstName",
                "LastName");
    }

    @Test
    void searchUsers() {
        //ToDo: Write tests for this method when implementing search/explore feature
        Assertions.assertThat(true).isTrue();
    }

    @Test
    void shouldShowAllUsers() {
        //ToDo: update this test with introduction of pagination feature
        //GIVEN
        List<User> users = List.of(
                new User(1, "Username1", "Password1", null, "Nick1", "FirstName1", "LastName1"),
                new User(2, "Username2", "Password2", null, "Nick2", "FirstName2", "LastName2"),
                new User(3, "Username3", "Password3", null, "Nick3", "FirstName3", "LastName3"),
                new User(4, "Username4", "Password4", null, "Nick4", "FirstName4", "LastName4"),
                new User(5, "Username5", "Password5", null, "Nick5", "FirstName5", "LastName5"));
        Mockito.when(userServiceMock.findAll()).thenReturn(users);
        users.forEach(user -> Mockito.when(userServiceMock.findById(user.getId())).thenReturn(user));
        ArgumentCaptor<String> acString = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> acList = ArgumentCaptor.forClass(List.class);

        //WHEN
        userController.showUsers(modelMock);

        //THEN
        Mockito.verify(modelMock).addAttribute(acString.capture(), acList.capture());
        Assertions.assertThat(acString.getValue()).isEqualTo("users");
        //ToDo: Resolve this warning with "captor" annotation ↓↓↓
        Assertions.assertThat(acList.getValue()).containsExactlyInAnyOrderElementsOf(users);
    }

    @Test
    void shouldAddTargetUserToModel() {
        //GIVEN
        int targetId = 1;
        Mockito.when(userServiceMock.findById(1)).thenReturn(sampleUser);

        //WHEN
        userController.showUser(targetId, modelMock);

        //THEN
        Mockito.verify(modelMock).addAttribute("user", sampleUser);
    }

    @Test
    void shouldPassNewUserModel() {
        //GIVEN
        User newUser = new User();

        //WHEN
        userController.showUserEditor(modelMock);

        //THEN
        Mockito.verify(modelMock).addAttribute("user", newUser);
    }

    @Test
    void shouldPassEditedUserToModel() {
        //GIVEN
        int targetId = 1;
        Mockito.when(userServiceMock.findById(1)).thenReturn(sampleUser);

        //WHEN
        userController.showUserEditor(targetId, modelMock);

        //THEN
        Mockito.verify(modelMock).addAttribute("user", sampleUser);
    }

    @Test
    void shouldNotSaveAnyUserWhenShowingUser() {
        //GIVEN
        int targetId = 1;

        //WHEN
        userController.showUser(targetId, modelMock);

        //THEN
        Mockito.verify(userServiceMock, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldNotSaveAnyUserWhenShowingNewUserEditor() {

        //WHEN
        userController.showUserEditor(modelMock);

        //THEN
        Mockito.verify(userServiceMock, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldNotSaveAnyUserWhenShowingExistingUserEditor() {
        //GIVEN
        int targetId = 1;

        //WHEN
        userController.showUserEditor(targetId, modelMock);

        //THEN
        Mockito.verify(userServiceMock, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldNotDeleteAnyUserWhenShowingUser() {
        //GIVEN
        int targetId = 1;

        //WHEN
        userController.showUser(targetId, modelMock);

        //THEN
        Mockito.verify(userServiceMock, Mockito.never()).deleteById(ArgumentMatchers.anyInt());
    }

    @Test
    void shouldNotDeleteAnyUserWhenShowingNewUserEditor() {
        //GIVEN

        //WHEN
        userController.showUserEditor(modelMock);

        //THEN
        Mockito.verify(userServiceMock, Mockito.never()).deleteById(ArgumentMatchers.anyInt());
    }

    @Test
    void shouldNotDeleteAnyUserShowingExistingUserEditor() {
        //GIVEN
        int targetId = 1;

        //WHEN
        userController.showUserEditor(targetId, modelMock);

        //THEN
        Mockito.verify(userServiceMock, Mockito.never()).deleteById(ArgumentMatchers.anyInt());
    }

    @Test
    void shouldSaveOnceWhenUserWithAvailableUsername() {
        //GIVEN
        Mockito.when(userServiceMock.isUsernameAvailable(Mockito.anyString())).thenReturn(true);

        //WHEN
        userController.saveUser(sampleUser, modelMock);

        //THEN
        Mockito.verify(userServiceMock, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(userServiceMock).save(sampleUser);
    }

    @Test
    void shouldNotSaveWhenUserWithUnavailableUsername() {
        //GIVEN
        Mockito.when(userServiceMock.isUsernameAvailable(Mockito.anyString())).thenReturn(false);

        //WHEN
        userController.saveUser(sampleUser, modelMock);

        //THEN
        Mockito.verify(userServiceMock, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldNotDeleteWhenSavingUserWithAvailableUsername() {
        //WHEN
        Mockito.when(userServiceMock.isUsernameAvailable(Mockito.anyString())).thenReturn(true);
        userController.saveUser(sampleUser, modelMock);

        //THEN
        Mockito.verify(userServiceMock, Mockito.never()).deleteById(ArgumentMatchers.anyInt());
    }

    @Test
    void shouldNotDeleteWhenSavingUserWithUnavailableUsername() {
        //WHEN
        Mockito.when(userServiceMock.isUsernameAvailable(Mockito.anyString())).thenReturn(false);
        userController.saveUser(sampleUser, modelMock);

        //THEN
        Mockito.verify(userServiceMock, Mockito.never()).deleteById(ArgumentMatchers.anyInt());
    }

    @Test
    void shouldDeleteTargetUser() {
        //GIVEN
        int targetId = 1;

        //WHEN
        userController.deleteUser(targetId);

        //THEN
        Mockito.verify(userServiceMock).deleteById(targetId);
    }

    @Test
    void shouldNotDeleteUsersOtherThanTarget() {
        //GIVEN
        int targetId = 1;

        //WHEN
        userController.deleteUser(targetId);

        //THEN
        Mockito.verify(userServiceMock, Mockito.never()).deleteById(ArgumentMatchers.intThat(integer -> integer != targetId));
    }

    @Test
    void shouldNotSaveWhenDeleting() {
        //GIVEN
        int targetId = 1;

        //WHEN
        userController.deleteUser(targetId);

        //THEN
        Mockito.verify(userServiceMock, Mockito.never()).save(Mockito.any());
    }

}