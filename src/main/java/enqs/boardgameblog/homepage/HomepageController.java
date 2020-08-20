package enqs.boardgameblog.homepage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.SimpleDateFormat;

@Controller
public class HomepageController {

    @GetMapping("/homepage")
    public String showHomepage(Model model) {
        model.addAttribute("currentDate", getFormattedCurrentTimestamp("yyy-MM-dd"));
        model.addAttribute("currentTime", getFormattedCurrentTimestamp("hh:mm:ss"));
        return "homepage";
    }

    private String getFormattedCurrentTimestamp(String datePattern) {
        return new SimpleDateFormat(datePattern).format(System.currentTimeMillis());
    }
}
