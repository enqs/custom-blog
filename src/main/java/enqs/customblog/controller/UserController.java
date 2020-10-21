package enqs.customblog.controller;

import enqs.customblog.entity.User;
import enqs.customblog.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "users/users";
    }

    @GetMapping("/userpage")
    public String showUserPage(Authentication authentication, Model model) {
        return isAuthenticated(authentication) ?
                processUserpageRequest(model, authentication.getName()) :
                accessDenied();
    }

    @GetMapping("/{id}")
    public String showUser(@PathVariable int id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "users/user-details";
    }

    @GetMapping("/new")
    public String showUserEditor(Model model) {
        model.addAttribute("user", new User());
        return "users/user-editor";
    }

    @GetMapping("/edit")
    public String showUserEditor(@RequestParam int id, Model model, Authentication authentication) {
        return isAuthorized(id, authentication) ?
                processEditPageRequest(id, model) :
                accessDenied();
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user, Model model, Authentication authentication) {
        return user.getId() == 0 || isAuthorized(user.getId(), authentication) ?
                processSaveRequest(user, model) :
                accessDenied();
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam int id, Authentication authentication) {
        return isAuthorized(id, authentication) ?
                processDeleteRequest(id) :
                accessDenied();
    }

    private boolean isAuthenticated(Authentication authentication) {
        return Objects.nonNull(authentication);
    }

    private boolean isAuthorized(int id, Authentication authentication) {
        return isAuthenticated(authentication) && hasEditRights(id, authentication);
    }

    private boolean hasEditRights(int id, Authentication authentication) {
        boolean isSameUser = userService.findByUsername(authentication.getName()).getId() == id;
        boolean isAdmin = userService.findByUsername(authentication.getName()).getRole().contains("ADMIN");
        return isSameUser || isAdmin;
    }

    private String accessDenied() {
        return "redirect:/users";
    }

    private String processUserpageRequest(Model model, String username) {
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "users/user-page";
    }

    private String processEditPageRequest(int id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "users/user-editor";
    }

    private String processSaveRequest(User user, Model model) {
        try {
            userService.save(user);
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("editedUser", user);
            model.addAttribute("message", e.getMessage());
            return "users/user-editor";
        }
    }

    private String processDeleteRequest(int id) {
        userService.deleteById(id);
        return "redirect:/users";
    }
}
