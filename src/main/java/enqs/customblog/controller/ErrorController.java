package enqs.customblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

    @RequestMapping("/404")
    public String showError404() {
        return "errors/404";
    }

    @RequestMapping("/503")
    public String showError503() {
        return "errors/503";
    }
}
