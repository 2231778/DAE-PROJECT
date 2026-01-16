package pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.core.Response;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Status;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Visibility;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Publication;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.User;

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

}
