package pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.core.Response;
import org.hibernate.Hibernate;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Status;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Visibility;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.*;

import java.util.List;

@Stateless
public class PublicationBean {
    @PersistenceContext
    private EntityManager entityManager;

    public Publication create(String title, String description, String file, User publisher, String author){
        Publication publication = new Publication(title, description, file, publisher, author);

        entityManager.persist(publication);
        return publication;
    }

    public Publication find(int id){

        return entityManager.find(Publication.class, id);
    }
    public List<Publication> findAll(){
        return entityManager.createNamedQuery("getAllPublications", Publication.class).getResultList();
    }

    public List<Comment> findAllComments(Integer id){
        Publication publication = find(id);
        if (publication == null) {
            return null;
        }
        Hibernate.initialize(publication.getComments());
        return publication.getComments();
    }

    public void remove(int id){
        Publication publication = find(id);
        entityManager.remove(publication);
    }

    public void update(Integer id,String title, String description, String file, String author){
        Publication publication = find(id);
        publication.setTitle(title);
        publication.setDescription(description);
        publication.setFile(file);
        publication.setAuthor(author);
        entityManager.merge(publication);
    }


    public void toggleVisibility(Integer id) {
        var publication = find(id);
        if (publication == null) throw new IllegalArgumentException("Publication not found");

        if (publication.getVisibility() == Visibility.VISIBLE) {
            publication.setVisibility(Visibility.INVISIBLE);
        } else {
            publication.setVisibility(Visibility.VISIBLE);
        }

        entityManager.merge(publication);
    }
    //TODO: AI GENERATED TEXT

    //----------------- Ratings ------------------
    public Double findPublicationRating(Integer id){
        Publication publication = find(id);
        if (publication == null) {
            return null;
        }
        Hibernate.initialize(publication.getRatings());
        List<Rating> ratings = publication.getRatings();

        if (ratings == null || ratings.isEmpty()) {
            return 0.0;
        }

        double total = 0;
        for (Rating rating : ratings) {
            total += rating.getValue();
        }

        return total / ratings.size();
    }

    // ------------------------- Tags ----------------
    public Publication subscribeTag(Integer publicationId, Integer tagId){
        Publication publication = find(publicationId);
        if (publication == null) throw new IllegalArgumentException("Publication not found");
        Tag tag = entityManager.find(Tag.class, tagId);
        if (tag == null) throw new IllegalArgumentException("Tag not found");

        // Add the tag to the user if not already present
        if (!publication.getTags().contains(tag)) {
            publication.getTags().add(tag);
        }

        // Add the user to the tag as well to maintain both sides
        if (!tag.getPublications().contains(publication)) {
            tag.getPublications().add(publication);
        }

        //Persist changes
        entityManager.merge(publication);
        entityManager.merge(tag);

        return publication;

    }

    public Publication unsubscribeTag(Integer publicationId, Integer tagId) {
        Publication publication = find(publicationId);
        if (publication == null) throw new IllegalArgumentException("Publication not found");
        Tag tag = entityManager.find(Tag.class, tagId);
        if (tag == null) throw new IllegalArgumentException("Tag not found");

        // Remove association if present
        publication.getTags().remove(tag);
        tag.getPublications().remove(publication);

        entityManager.merge(tag);
        entityManager.merge(publication);

        return publication;

    }
}
