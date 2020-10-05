package enqs.customblog.controller;

import enqs.customblog.entity.Article;
import enqs.customblog.service.ArticleService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.ui.Model;

import java.sql.Date;
import java.util.List;

//ToDo: If possible extract abstract generic test class for similar controllers
class ArticleControllerTest {

    private Model modelMock;
    private Article sampleArticle;
    private ArticleService articleServiceMock;
    private ArticleController articleController;

    @BeforeEach
    void setUp() {
        //GIVEN
        modelMock = Mockito.mock(Model.class);
        articleServiceMock = Mockito.mock(ArticleService.class);
        articleController = new ArticleController(articleServiceMock);
        sampleArticle = new Article(
                0,
                "TittleFoo",
                "~/Foo.png",
                "Description of Foo",
                "Foo's Content",
                null);
    }

    @Test
    void browseArticles() {
        //ToDo: Write tests for this method when implementing search/explore feature
        Assertions.assertThat(true).isTrue();
    }

    @Test
    void shouldShowAllArticles() {
        //ToDo: update this test with introduction of pagination feature
        //ToDo: Also fix that dates (nulls) here...

        //GIVEN
        List<Article> articles = List.of(
                new Article(1, "Tittle1", "/img1", "Desc1", "Content1", null),
                new Article(2, "Tittle2", "/img2", "Desc2", "Content2", null),
                new Article(3, "Tittle3", "/img3", "Desc3", "Content3", null),
                new Article(4, "Tittle4", "/img4", "Desc4", "Content4", null));
        Mockito.when(articleServiceMock.findAll()).thenReturn(articles);
        articles.forEach(article -> Mockito.when(articleServiceMock.findById(article.getId())).thenReturn(article));
        ArgumentCaptor<String> acString = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> acList = ArgumentCaptor.forClass(List.class);

        //WHEN
        articleController.showArticles(modelMock);

        //THEN
        Mockito.verify(modelMock).addAttribute(acString.capture(), acList.capture());
        Assertions.assertThat(acString.getValue()).isEqualTo("articles");
        //ToDo: Resolve this warning with "captor" annotation ↓↓↓
        Assertions.assertThat(acList.getValue()).containsExactlyInAnyOrderElementsOf(articles);
    }

    @Test
    void shouldAddTargetArticleToModelWhenShowingArticle() {
        //GIVEN
        int targetId = 1;
        Mockito.when(articleServiceMock.findById(1)).thenReturn(sampleArticle);

        //WHEN
        articleController.showArticle(targetId, modelMock);

        //THEN
        Mockito.verify(modelMock).addAttribute("article", sampleArticle);
    }

    @Test
    void shouldNotSaveAnyArticleWhenShowingArticle() {
        //GIVEN
        int targetId = 1;

        //WHEN
        articleController.showArticle(targetId, modelMock);

        //THEN
        Mockito.verify(articleServiceMock, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldNotDeleteAnyArticleWhenShowingArticle() {
        //GIVEN
        int targetId = 1;

        //WHEN
        articleController.showArticle(targetId, modelMock);

        //THEN
        Mockito.verify(articleServiceMock, Mockito.never()).deleteById(ArgumentMatchers.anyInt());
    }

    @Test
    void showRandomArticle() {
        //ToDo: Write tests for this method when implementing random article feature
        Assertions.assertThat(true).isTrue();
    }

    @Test
    void shouldPassNewArticleWithCurrentDateToModel() {
        //GIVEN
        Article newArticle = new Article();
        //ToDo: This fix date's comparison also here...
        newArticle.setPublishDate(new Date(System.currentTimeMillis()));

        //WHEN
        articleController.showArticleEditor(modelMock);

        //THEN
        Mockito.verify(modelMock).addAttribute("article", newArticle);
    }

    @Test
    void shouldNotSaveAnyArticleWhenShowingNewArticleEditor() {

        //WHEN
        articleController.showArticleEditor(modelMock);

        //THEN
        Mockito.verify(articleServiceMock, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldNotDeleteAnyArticleWhenShowingNewArticleEditor() {
        //GIVEN

        //WHEN
        articleController.showArticleEditor(modelMock);

        //THEN
        Mockito.verify(articleServiceMock, Mockito.never()).deleteById(ArgumentMatchers.anyInt());
    }

    @Test
    void shouldPassEditedArticleToModel() {
        //GIVEN
        int targetId = 1;
        Mockito.when(articleServiceMock.findById(1)).thenReturn(sampleArticle);

        //WHEN
        articleController.showArticleEditor(targetId, modelMock);

        //THEN
        Mockito.verify(modelMock).addAttribute("article", sampleArticle);
    }

    @Test
    void shouldNotSaveAnyArticleWhenShowingExistingArticleEditor() {
        //GIVEN
        int targetId = 1;

        //WHEN
        articleController.showArticleEditor(targetId, modelMock);

        //THEN
        Mockito.verify(articleServiceMock, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldNotDeleteAnyArticleShowingExistingArticleEditor() {
        //GIVEN
        int targetId = 1;

        //WHEN
        articleController.showArticleEditor(targetId, modelMock);

        //THEN
        Mockito.verify(articleServiceMock, Mockito.never()).deleteById(ArgumentMatchers.anyInt());
    }

    @Test
    void shouldSaveGivenArticleOnce() {
        //WHEN
        articleController.saveArticle(sampleArticle);

        //THEN
        Mockito.verify(articleServiceMock, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(articleServiceMock).save(sampleArticle);
    }

    @Test
    void shouldNotDeleteWhenSaving() {
        //WHEN
        articleController.saveArticle(sampleArticle);

        //THEN
        Mockito.verify(articleServiceMock, Mockito.never()).deleteById(ArgumentMatchers.anyInt());
    }

    @Test
    void shouldDeleteTargetArticle() {
        //GIVEN
        int targetId = 1;

        //WHEN
        articleController.deleteArticle(targetId);

        //THEN
        Mockito.verify(articleServiceMock).deleteById(targetId);
    }

    @Test
    void shouldNotDeleteArticlesOtherThanTargeted() {
        //GIVEN
        int targetId = 1;

        //WHEN
        articleController.deleteArticle(targetId);

        //THEN
        Mockito.verify(articleServiceMock, Mockito.never()).deleteById(ArgumentMatchers.intThat(integer -> integer != targetId));
    }

    @Test
    void shouldNotSaveWhenDeleting() {
        //GIVEN
        int targetId = 1;

        //WHEN
        articleController.deleteArticle(targetId);

        //THEN
        Mockito.verify(articleServiceMock, Mockito.never()).save(Mockito.any());
    }
}