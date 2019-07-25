package es.caib.jpa.frontend;

import es.caib.jpa.backend.boundary.PostManagerLocalFacade;
import es.caib.jpa.backend.control.PostsManager;
import es.caib.jpa.backend.entity.Post;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.el.MethodExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.Random;

@ManagedBean
@RequestScoped
public class Index {

    public static final Random RANDOM = new Random();

    @EJB(name = PostsManager.JNDI_NAME)
    PostManagerLocalFacade postsManager;

    private List<Post> posts;
    private Post post;

    @PostConstruct
    public void init() {
        posts = postsManager.getAll();
    }

    public void createRandomPost() {
        postsManager.createRandomPost();
        posts = postsManager.getAll();
    }

    public List<Post> getAllPosts() {
        return posts;
    }

    public void delete() {
        postsManager.delete(post);
        posts.remove(post);
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void testPersistenceContextFromEJB() {
        postsManager.testPCOK(posts.get(0).getId());
    }

    public void testPersistenceContextFromManagedBean() {
        Long postId = posts.get(0).getId();
        Post post;
        for (int i = 0; i < 100; i++) {
            post = postsManager.find(postId);
            post.setContent("New content " + RANDOM.nextInt(100));
        }

        // Veréis que aquí no se genera ninguna consulta UIPDATE, esto es porque los cambios se han hecho siempre fuera del
        // contexto de persistencia, y es entonces cuando se hace necesario crear el saveOrUpdate para hacer el merge de las
        // entidades que hemos modificado (porque las hemos modificado fuera del contexto de persistencia)

        // Sin embargo en testPersistenceContextFromEJB no hace falta saveOrUpdate, ni merge ni nada. Todas las modificaciones
        // se realizan dentro del contexto de persistencia y por lo tanto tdo queda cuargado cuando finaliza el método.
    }
}
