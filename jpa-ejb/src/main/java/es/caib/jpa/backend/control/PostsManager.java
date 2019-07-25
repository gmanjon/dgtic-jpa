package es.caib.jpa.backend.control;

import es.caib.jpa.backend.boundary.PostManagerLocalFacade;
import es.caib.jpa.backend.entity.Comment;
import es.caib.jpa.backend.entity.Post;
import es.caib.jpa.backend.entity.Status;
import org.jboss.ejb3.annotation.LocalBinding;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Random;

@Stateless
@LocalBinding(jndiBinding = PostsManager.JNDI_NAME)
public class PostsManager implements PostManagerLocalFacade {

    public static final String JNDI_NAME = "es.caib.jpa.backend.control.PostsManager";

    @PersistenceContext
    EntityManager em;

    public static final Random RANDOM = new Random();

    public Post find(Long postId) {
        return em.find(Post.class, postId);
    }

    public List<Post> getAll() {
        return em.createNamedQuery(Post.GET_ALL).getResultList();
    }

    public Post createRandomPost() {
        Post post = new Post();
        post.setContent("asdasdcasd " + RANDOM.nextInt());
        post.setStatus(Status.OPEN);
        fillWithRandomComments(post);
        em.persist(post);
        return post;
    }

    public void delete(Post post) {
        em.remove(em.getReference(Post.class, post.getId()));
    }

    public void testPCOK(Long postId) {
        Post post;
        for (int i = 0; i < 100; i++) {
            post = em.find(Post.class, postId);
            post.setContent("New content " + RANDOM.nextInt(100));
        }
    }

    private void fillWithRandomComments(Post post) {
        int numberOfComments = RANDOM.nextInt(10);
        for (int i = 0; i < numberOfComments; i++) {
            Comment comment = new Comment("Comasdfavsd " + i);
            post.addComment(comment);
        }
    }
}
