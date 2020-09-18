package enqs.customblog.service;

import enqs.customblog.dao.ArticleRepository;
import enqs.customblog.entity.Article;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ArticleServiceImplTest {

    @Autowired
    private ArticleRepository articleRepository;
    private ArticleService articleService;
    private Article sampleArticleFoo;
    private Article sampleArticleBar;
    private Article sampleArticleBaz;

    @BeforeEach
    void setUp() {
        //GIVEN
        articleRepository.deleteAll();
        articleRepository.flush();
        articleService = new ArticleServiceImpl(articleRepository);
        //ToDo: Check why entity date and persisted date are different
        //ToDo: Remove those ugly nulls from articles (after solving this ↑ task)
        sampleArticleFoo = new Article(
                0,
                "TittleFoo",
                "~/Foo.png",
                "Description of Foo",
                "Foo's Content",
                null);
        sampleArticleBar = new Article(
                0,
                "TittleBar",
                "~/Bar.png",
                "Description of Bar",
                "Bar's Content",
                null);
        sampleArticleBaz = new Article(
                0,
                "TittleBaz",
                "~/Baz.png",
                "Description of Baz",
                "Baz's Content",
                null);
    }

    @Test
    void shouldSaveArticleOneTime() {
        //WHEN
        articleService.save(sampleArticleFoo);

        //THEN
        List<Article> articles = articleRepository.findAll();
        Assertions.assertThat(articles).containsExactly(sampleArticleFoo);
    }

    @Test
    void shouldNotModifyNorDeleteOtherEntriesWhenSave() {
        //GIVEN
        articleRepository.saveAll(List.of(sampleArticleFoo, sampleArticleBar));
        articleRepository.flush();

        //WHEN
        articleService.save(sampleArticleBaz);

        //THEN
        List<Article> articles = articleRepository.findAll();
        Assertions.assertThat(articles).containsExactlyInAnyOrderElementsOf(List.of(sampleArticleFoo, sampleArticleBar, sampleArticleBaz));
    }

    @Test
    void shouldModifyOnlyTargetEntity() {
        //GIVEN
        articleRepository.saveAll(List.of(sampleArticleFoo, sampleArticleBar, sampleArticleBaz));
        articleRepository.flush();

        //WHEN
        sampleArticleFoo.setContent("Content after big big changes");
        articleService.save(sampleArticleFoo);


        //THEN
        List<Article> articles = articleRepository.findAll();
        Assertions.assertThat(articles).containsExactlyInAnyOrderElementsOf(List.of(sampleArticleFoo, sampleArticleBar, sampleArticleBaz));
    }

    @Test
    void shouldFindExistingEntityById() {
        //GIVEN
        articleRepository.saveAll(List.of(sampleArticleFoo, sampleArticleBar, sampleArticleBaz));
        int targetId = sampleArticleBar.getId();

        //WHEN
        Article article = articleService.findById(targetId);

        //THEN
        Assertions.assertThat(article).isEqualToComparingFieldByField(sampleArticleBar);
    }

    @Test
    void shouldNotFindEntityThatDoesNotExist() {
        //GIVEN
        articleRepository.saveAll(List.of(sampleArticleFoo, sampleArticleBar, sampleArticleBaz));
        int targetId = sampleArticleBaz.getId() * 10;

        //WHEN
        Article article = articleService.findById(targetId);

        //THEN
        //ToDo: Should expect exception
        Assertions.assertThat(article).isEqualToComparingFieldByField(new Article());
    }

    @Test
    void shouldFindExactlyAllPersistedEntities() {
        //GIVEN
        articleRepository.saveAll(List.of(sampleArticleFoo, sampleArticleBar, sampleArticleBaz));

        //WHEN
        List<Article> articles = articleService.findAll();

        //THEN
        Assertions.assertThat(articles).containsExactlyInAnyOrderElementsOf(List.of(sampleArticleFoo, sampleArticleBar, sampleArticleBaz));
    }

    @Test
    void shouldReturnEmptyListWhenNothingPersisted() {
        //GIVEN
        //empty db ;)

        //WHEN
        List<Article> articles = articleService.findAll();

        //THEN
        Assertions.assertThat(articles).hasSize(0);
    }

    @Test
    void shouldDeleteOnlyTargetEntity() {
        //GIVEN
        articleRepository.saveAll(List.of(sampleArticleFoo, sampleArticleBar, sampleArticleBaz));
        int targetId = sampleArticleBaz.getId();

        //WHEN
        articleService.deleteById(targetId);

        //THEN
        List<Article> articles = articleRepository.findAll();
        Assertions.assertThat(articles).containsExactlyInAnyOrderElementsOf(List.of(sampleArticleFoo, sampleArticleBar));
    }
}