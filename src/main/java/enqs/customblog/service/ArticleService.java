package enqs.customblog.service;

import enqs.customblog.entity.Article;

import java.util.List;

public interface ArticleService {

     void save(Article article);

     Article findById(int id);

     List<Article> findAll();

     void deleteById(int id);
}
