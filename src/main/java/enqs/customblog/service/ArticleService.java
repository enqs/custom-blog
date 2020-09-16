package enqs.customblog.service;

import enqs.customblog.entity.Article;

public interface ArticleService {

     void save(Article article);

     Article findById(int id);

}
