package es.caib.jpa.backend.entity;

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "T_POST")
@NamedQueries(
        @NamedQuery(name = Post.GET_ALL, query = "select distinct t from Post t join fetch t.comments")
)
public class Post {

    public static final String GET_ALL = "es.caib.jpa.backend.entity.Post.GET_ALL";

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "CONTENT")
    private String content;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "POST_ID")
    private List<Comment> comments = new ArrayList<>();

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "STATUS")
    private Status status;

    @OneToOne(mappedBy = "post", fetch = FetchType.LAZY, optional = true, cascade = CascadeType.ALL)
    @LazyToOne(value = LazyToOneOption.NO_PROXY)
    private PostDetails postDetails;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void addComment(Comment comment) {
        comment.setPost(this);
        comments.add(comment);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setPost(null);
    }

    public void addPostDetails(PostDetails postDetails) {
        postDetails.setPost(this);
        this.postDetails = postDetails;
    }

    public void removePostDetails(PostDetails postDetails) {
        postDetails.setPost(null);
        this.postDetails.setPost(null);
    }

    public PostDetails getPostDetails() {
        return postDetails;
    }

    public void setPostDetails(PostDetails postDetails) {
        this.postDetails = postDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
