package enqs.customblog.service;

import enqs.customblog.dao.ArticleRepository;
import enqs.customblog.entity.Article;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }


    @Override
    public void save(Article article) {
        articleRepository.save(article);
    }

    @Override
    public Article findById(int id) {
        Optional<Article> optionalArticle = articleRepository.findById(id);
        //TODO: Throw not found exception with valid response code
        return optionalArticle.orElse(new Article());
    }
}
