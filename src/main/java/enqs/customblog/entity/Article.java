package enqs.customblog.entity;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String tittle;

    private String imageSource;

    private String description;

    private String content;

    private Date publishDate;

    public Article() {
    }

    public Article(int id, String tittle, String imageSource, String description, String content, Date publishDate) {
        this.id = id;
        this.tittle = tittle;
        this.imageSource = imageSource;
        this.description = description;
        this.content = content;
        this.publishDate = publishDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return getId() == article.getId() &&
                Objects.equals(getTittle(), article.getTittle()) &&
                Objects.equals(getImageSource(), article.getImageSource()) &&
                Objects.equals(getDescription(), article.getDescription()) &&
                Objects.equals(getContent(), article.getContent()) &&
                Objects.equals(getPublishDate(), article.getPublishDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTittle(), getImageSource(), getDescription(), getContent(), getPublishDate());
    }
}
