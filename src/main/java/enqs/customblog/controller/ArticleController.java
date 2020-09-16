package enqs.customblog.controller;

import enqs.customblog.entity.Article;
import enqs.customblog.service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public String showArticles() {
        return "articles/articles";
    }

    @GetMapping("/{id}")
    public String showArticle(@PathVariable int id, Model model) {
        Article article = articleService.findById(id);
        model.addAttribute("article", article);
        return "articles/article";
    }

    @GetMapping("/random")
    public String showRandomArticle() {
        return "articles/article";
    }

    @GetMapping("/new")
    public String showArticleEditor(Model model) {
        Article article = new Article();
        article.setPublishDate(new Date(System.currentTimeMillis()));
        model.addAttribute("article", article);
        return "articles/article-editor";
    }

    @GetMapping("/edit")
    public String showArticleEditor(@RequestParam int id, Model model) {
        Article article = articleService.findById(id);
        model.addAttribute("article", article);
        return "articles/article-editor";
    }

    @PostMapping("/save")
    public String saveArticle(@ModelAttribute("article") Article article) {
        articleService.save(article);
        return "redirect:/";
    }
}
