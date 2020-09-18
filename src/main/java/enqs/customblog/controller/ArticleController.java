package enqs.customblog.controller;

import enqs.customblog.entity.Article;
import enqs.customblog.service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public String showArticles(Model model) {
        //ToDo: implement pagination of results
        List<Article> articles = articleService.findAll();
        model.addAttribute("articles", articles);
        return "articles/articles";
    }

    @GetMapping("/browse")
    public String browseArticles() {
        //ToDo: Implement article search/browse feature
        //ToDo: Pagination to results
        return "articles/browse";
    }

    @GetMapping("/{id}")
    public String showArticle(@PathVariable int id, Model model) {
        Article article = articleService.findById(id);
        model.addAttribute("article", article);
        return "articles/article";
    }

    @GetMapping("/random")
    public String showRandomArticle() {
        //ToDo: Implement this feature
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

    @GetMapping("/delete")
    public String deleteArticle(@RequestParam int id) {
        articleService.deleteById(id);
        return "redirect:/";
    }
}
