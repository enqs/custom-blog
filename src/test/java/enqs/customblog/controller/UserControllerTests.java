package enqs.customblog.controller;

import enqs.customblog.entity.User;
import enqs.customblog.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userServiceMock;

    private User sampleUserFoo;
    private User sampleUserBar;
    private User sampleUserBaz;
    private User[] users;

    @BeforeEach
    void setUp() {
        sampleUserFoo = new User(1, "Username1", "Password1", "ROLE_USER", "Nick1", "FirstName1", "LastName1");
        sampleUserBar = new User(2, "Username2", "Password2", "ROLE_USER", "Nick2", "FirstName2", "LastName2");
        sampleUserBaz = new User(3, "Username3", "Password3", "ROLE_USER", "Nick3", "FirstName3", "LastName3");
        users = new User[]{sampleUserFoo, sampleUserBar, sampleUserBaz};
        Mockito.when(userServiceMock.findAll()).thenReturn(List.of(users));
        Mockito.when(userServiceMock.findById(1)).thenReturn(sampleUserFoo);
        Mockito.when(userServiceMock.findById(2)).thenReturn(sampleUserBar);
        Mockito.when(userServiceMock.findById(3)).thenReturn(sampleUserBaz);
    }

    @Test
    void testShowUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("users/users"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("users"))
                .andExpect(MockMvcResultMatchers.model().attribute("users", Matchers.containsInAnyOrder(users)))
                .andExpect(MockMvcResultMatchers.model().attribute("users", Matchers.hasSize(3)));
    }

    @Test
    void testShowUserPageUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/userpage"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/users"));
    }

    @Test
    @WithMockUser(username = "MockUser")
    void testShowUserPageAuthorized() throws Exception {
        Mockito.when(userServiceMock.findByUsername("MockUser")).thenReturn(sampleUserFoo);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/userpage").sessionAttr("user", new User()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("users/user-page"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.is(sampleUserFoo)));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void testShowUser(int input) throws Exception {
        mockMvc.perform((MockMvcRequestBuilders.get("/users/{id}", input)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void showUserShouldReturnUserDetailsView(int input) throws Exception {
        mockMvc.perform((MockMvcRequestBuilders.get("/users/{id}", input)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.view().name("users/user-details"));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void showUserShouldIncludeUserToModel(int input) throws Exception {
        mockMvc.perform((MockMvcRequestBuilders.get("/users/{id}", input)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void showUserShouldIncludeTargetUsersDataToModel(int input) throws Exception {
        mockMvc.perform((MockMvcRequestBuilders.get("/users/{id}", input)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.is(users[input - 1])));
    }

    @Test
    void showUserEditorNewShouldReturnOkStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/new"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void showUserEditorNewShouldReturnUserEditorTemplate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/new"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.view().name("users/user-editor"));
    }

    @Test
    void showUserEditorNewShouldIncludeUserAttributeToModel() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/new"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"));
    }

    @Test
    void showUserEditorNewShouldIncludeEmptyUserToModel() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/new"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.model()
                        .attribute("user", Matchers.is(new User())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3"})
    void showUserEditorEditShouldReturnRedirectionStatusWhenNotAuthenticated(String input) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/edit").param("id", input))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3"})
    void showUserEditorEditShouldNotShowWhenNotAuthenticated(String input) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/edit").param("id", input))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/users"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2", "3"})
    @WithMockUser(username = "MockUser")
    void showUserEditorEditShouldReturnRedirectionStatusWhenUserEditingOthers(String input) throws Exception {
        Mockito.when(userServiceMock.findByUsername("MockUser")).thenReturn(sampleUserFoo);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/edit").param("id", input).sessionAttr("user", new User()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2", "3"})
    @WithMockUser(username = "MockUser")
    void showUserEditorEditShouldNotShowWhenUserEditingOthers(String input) throws Exception {
        Mockito.when(userServiceMock.findByUsername("MockUser")).thenReturn(sampleUserFoo);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/edit").param("id", input).sessionAttr("user", new User()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/users"));
    }

}