package enqs.customblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UtilityController {

    @GetMapping("/about")
    public String showAboutPage() {
        return "about";
    }
}
