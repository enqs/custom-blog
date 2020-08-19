package enqs.boardgameblog.helloworld;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloworldController {

    @GetMapping("/helloworld")
    public String getHelloworldPage() {
        return "helloworld";
    }
}
