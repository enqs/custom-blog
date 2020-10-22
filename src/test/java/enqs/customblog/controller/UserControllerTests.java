package enqs.customblog.controller;

import enqs.customblog.entity.User;
import enqs.customblog.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @BeforeEach
    void setUp() {
        sampleUserFoo = new User(1, "Username1", "Password1", "ROLE_USER", "Nick1", "FirstName1", "LastName1");
        sampleUserBar = new User(2, "Username2", "Password2", "ROLE_USER", "Nick2", "FirstName2", "LastName2");
        sampleUserBaz = new User(3, "Username3", "Password3", "ROLE_USER", "Nick3", "FirstName3", "LastName3");
        Mockito.when(userServiceMock.findAll()).thenReturn(List.of(sampleUserFoo, sampleUserBar, sampleUserBaz));
        Mockito.when(userServiceMock.findById(1)).thenReturn(sampleUserFoo);
        Mockito.when(userServiceMock.findById(2)).thenReturn(sampleUserBar);
        Mockito.when(userServiceMock.findById(3)).thenReturn(sampleUserBaz);
    }

    @Test
    void showUsersShouldReturnOkStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void showUsersShouldReturnUsersTemplate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.view().name("users/users"));
    }

    @Test
    void showUsersShouldIncludeUsersAttributeToModel() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.model().attributeExists("users"));
    }

    @Test
    void showUsersShouldIncludeUsersProvidedByServiceToModel() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.model()
                        .attribute("users", Matchers.containsInAnyOrder(sampleUserFoo, sampleUserBar, sampleUserBaz)));
    }

    @Test
    void showUsersShouldIncludeExactUserAmountProvidedByServiceToModel() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.model()
                        .attribute("users", Matchers.hasSize(3)));
    }


}