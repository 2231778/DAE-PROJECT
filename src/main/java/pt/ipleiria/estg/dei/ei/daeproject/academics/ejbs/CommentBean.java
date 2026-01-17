package pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.core.Response;
import org.hibernate.Hibernate;
import pt.ipleiria.estg.dei.ei.daeproject.academics.dtos.CommentDTO;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Comment;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Publication;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.User;

import java.util.List;

@Stateless
public class CommentBean {
    @PersistenceContext
    private EntityManager entityManager;
    @EJB
    private PublicationBean publicationBean;
    @EJB
    private UserBean userBean;

    public Comment create(String content, Publication publication, User author) {
        //Create the comment
        Comment comment = new Comment(content, publication, author);
        //add comment to the publication
        publication.getComments().add(comment);
        //Add comment to the author( to the user )
        author.getComments().add(comment);

        entityManager.persist(comment);

        return comment;
    }

    public CommentDTO createAndReturnDTO(String content, Integer publicationId, Integer userId) {

        Publication publication = publicationBean.find(publicationId);
        if (publication == null) {
            throw new EntityNotFoundException("Publication not found with ID: " + publicationId);
        }
        User author = userBean.find(userId);
        if (author == null) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }

        Comment comment = new Comment(content, publication, author);

        publication.getComments().add(comment);
        author.getComments().add(comment);

        entityManager.persist(comment);

        Hibernate.initialize(publication.getComments());
        Hibernate.initialize(author.getComments());


        Hibernate.initialize(comment.getPublication());


        return CommentDTO.from(comment);
    }

    public Comment find(Integer id) {
        return entityManager.find(Comment.class, id);
    }

    public CommentDTO update(Integer id,String content) {
        Comment comment = find(id);

        if (comment == null) {
            throw new IllegalArgumentException("Comment not found");
        }

        comment.setContent(content);

        entityManager.merge(comment); // Think is redundant

        Hibernate.initialize(comment.getPublication());

        return CommentDTO.from(comment);
    }

    public void delete(Integer id) {
        Comment comment = find(id);
        if (comment == null) {
            throw new IllegalArgumentException("Comment not found");
        }

        // remove everywhere
        comment.getPublication().getComments().remove(comment);
        comment.getAuthor().getComments().remove(comment);

        entityManager.remove(comment);
    }


}
