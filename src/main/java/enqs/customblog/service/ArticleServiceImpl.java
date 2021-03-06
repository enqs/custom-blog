package enqs.customblog.service;

import enqs.customblog.dao.ArticleRepository;
import enqs.customblog.entity.Article;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }


    @Override
    public void save(Article article) {
        article.setPublishDate(new Date(System.currentTimeMillis()));
        articleRepository.save(article);
    }

    @Override
    public Article findById(int id) {
        Optional<Article> optionalArticle = articleRepository.findById(id);
        return optionalArticle.orElse(new Article());
    }

    @Override
    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    @Override
    public void deleteById(int id) {
        articleRepository.deleteById(id);
    }
}
