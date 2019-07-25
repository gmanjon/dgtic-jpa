package es.caib.jpa.backend.control;

import es.caib.jpa.backend.entity.Comment;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Random;

@Stateless
public class CommentsManager  {

    @PersistenceContext
    EntityManager em;

    public static final Random random = new Random();

    public List<Comment> getAll() {
        return em.createNamedQuery("Comment.getAll").getResultList();
    }

    public Comment createRandomComment() {
        Comment post = new Comment();
        post.setContent("asdasdcasd " + random.nextInt());
        em.persist(post);
        return post;
    }

}
