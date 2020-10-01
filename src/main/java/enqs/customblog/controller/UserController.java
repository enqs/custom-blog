package enqs.customblog.controller;

import enqs.customblog.entity.User;
import enqs.customblog.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showUsers(Model model) {
        //ToDo: Implement pagination
        //ToDo: Implement search utilities
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "users/users";
    }

    @GetMapping("/{id}")
    public String showUser(@PathVariable int id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "users/user-details";
    }

    @GetMapping("/user_page")
    public String showUserPage() {
        return "users/user-page";
    }

    @GetMapping("/new")
    public String showUserEditor(Model model) {
        model.addAttribute("user", new User());
        return "users/user-editor";
    }

    @GetMapping("/edit")
    public String showUserEditor(@RequestParam int id, Model model) {
        //ToDo: block unauthorized user edition: only self-edition or by admin
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "users/user-editor";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user, Model model) {
        //ToDo: block unauthorized user edition: only self-edition or by admin
        //ToDo: Implement validation of user's fields
        //ToDo: Matching passwords
        if (user.getId() == 0 && !userService.isUsernameAvailable(user.getUsername())) {
            //ToDo: there should be normal warning instead of this quick fix
            user.setUsername("Select other username");
            model.addAttribute("editedUser", user);
            return "users/user-editor";
        }
        userService.save(user);
        return "redirect:/users";
    }

    @GetMapping("/delete")
    public String deleteUSer(@RequestParam int id) {
        //ToDo: Prevent unauthorized deletes: only admin and self delete
        userService.deleteById(id);
        return "redirect:/users";
    }

}
