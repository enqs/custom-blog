package enqs.customblog.service;

import enqs.customblog.dao.ArticleRepository;
import enqs.customblog.entity.Article;
import org.springframework.stereotype.Service;

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
}
