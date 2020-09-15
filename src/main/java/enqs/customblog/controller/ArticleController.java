package enqs.customblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    @GetMapping
    public String showArticles() {
        return "articles";
    }

    @GetMapping("/{id}")
    public String showArticle(@PathVariable int id) {
        return "article";
    }

    @GetMapping("/random")
    public String showRandomArticle() {
        return "article";
    }

    @GetMapping("/new")
    public String showArticleEditor() {
        return "article-editor";
    }

    @GetMapping("/{id}/editor")
    public String showArticleEditor(@PathVariable int id) {
        return "article-editor";
    }
}
