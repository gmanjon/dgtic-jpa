package es.caib.jpa.backend.boundary;

import es.caib.jpa.backend.entity.Post;

import javax.ejb.Local;
import java.util.List;

@Local
public interface PostManagerLocalFacade {
    List<Post> getAll();
    Post createRandomPost();

    void delete(Post post);

    void testPCOK(Long postId);
    public Post find(Long postId);
}
