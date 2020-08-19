package enqs.boardgameblog.homepage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.SimpleDateFormat;

@Controller
public class HomepageController {

    @GetMapping("/homepage")
    public String showHomepage(Model model) {
        String currentDate = getFormattedCurrentTimestamp("yyy-MM-dd");
        String currentTime = getFormattedCurrentTimestamp("hh:mm:ss");
        model.addAttribute("currentDate", currentDate);
        model.addAttribute("currentTime", currentTime);
        return "homepage";
    }

    private String getFormattedCurrentTimestamp(String datePattern) {
        return new SimpleDateFormat(datePattern).format(System.currentTimeMillis());
    }
}
