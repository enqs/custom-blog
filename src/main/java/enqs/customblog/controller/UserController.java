package enqs.customblog.controller;

import enqs.customblog.entity.User;
import enqs.customblog.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showUsers() {
        return "users/users";
    }

    @GetMapping("/user_page")
    public String showUserPage() {
        return "users/user-page";
    }

    @GetMapping("/{id}")
    public String showUser(@PathVariable int id) {
        return "users/user";
    }

    @GetMapping("/new")
    public String showUserEditor(Model model) {
        model.addAttribute("editedUser", new User());
        return "users/user-editor";
    }

    @GetMapping("/edit")
    public String showUserEditor(@PathVariable int id) {
        return "users/user-editor";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user, Model model) {
        //ToDo: Implement validation of user's fields
        if (user.getId() == 0 && !userService.isUsernameAvailable(user.getUsername())) {
            //ToDo: there should be normal warning instead of this quick fix
            user.setUsername("Select other username");
            model.addAttribute("editedUser", user);
            return "users/user-editor";
        }
        userService.save(user);
        return "redirect:/articles";
    }

}
