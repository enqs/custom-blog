package enqs.customblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public String showUsers() {
        return "users/users";
    }

    @GetMapping("/{id}")
    public String showUser(@PathVariable int id) {
        return "users/user";
    }

    @GetMapping("/new")
    public String showUserEditor() {
        return "users/user-editor";
    }

    @GetMapping("/{id}/editor")
    public String showUserEditor(@PathVariable int id) {
        return "users/user-editor";
    }

}
