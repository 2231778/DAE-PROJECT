package pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.jboss.ejb3.annotation.TransactionTimeout;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.hibernate.Hibernate;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.ActionType;

import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Visibility;
import pt.ipleiria.estg.dei.ei.daeproject.academics.dtos.PublicationDTO;

import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.*;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Stateless
public class PublicationBean {
    @PersistenceContext
    private EntityManager entityManager;
    @EJB
    private ActivityLogBean activityLogBean;
    @EJB
    private AiProcessorBean aiProcessorBean;

    public Publication create(String title, String description, String file, User publisher, String author) {
        Publication publication = new Publication(title, description, file, publisher, author);

        entityManager.persist(publication);

        //Log
        activityLogBean.create(ActionType.CREATE, "PUBLICATION CREATED", publisher, publication);
        return publication;
    }

    public Publication find(int id) {

        return entityManager.find(Publication.class, id);
    }

    public PublicationDTO findPublicationDTO(int id) {
        // 1. Find the entity using your existing internal method.
        Publication publication = find(id);
        if (publication == null) {
            return null;
        }

        // 2. PREPARE: Initialize all lazy collections needed for the DTO.
        // This happens inside the active transaction.
        //Hibernate.initialize(publication.getPublicationActivityLogs());
        Hibernate.initialize(publication.getComments());
        Hibernate.initialize(publication.getTags());
        Hibernate.initialize(publication.getRatings());
        // Add any other lazy collections you need in the DTO...

        // 3. PACKAGE: Pass the fully initialized entity to the DTO factory method.
        return PublicationDTO.from(publication);
    }

    public List<Publication> findAll() {

        return entityManager.createNamedQuery("getAllPublications", Publication.class)
                .setParameter("visibility", Visibility.VISIBLE)
                .getResultList();
    }
    public List<PublicationDTO> findAllPublicationDTOs() {
        // 1. Find the list of entities.
        List<Publication> publications = findAll(); // Uses your existing method

        // 2. PREPARE: Initialize lazy collections for EACH entity in the list.
        for (Publication publication : publications) {
            //Hibernate.initialize(publication.getPublicationActivityLogs());
            Hibernate.initialize(publication.getComments());
            Hibernate.initialize(publication.getTags());
            Hibernate.initialize(publication.getRatings());
        }

        // 3. PACKAGE: Convert the list of prepared entities to a list of DTOs.
        return publications.stream()
                .map(PublicationDTO::from) // Calls the static from() method for each publication
                .collect(Collectors.toList());
    }

    public List<Publication> findMyPublications(Integer userId) {
        return entityManager.createNamedQuery("getMyPublications", Publication.class)
                .setParameter("userId", userId)   // bind the parameter
                .getResultList();
    }

    public List<PublicationDTO> findMyPublicationDTOs(Integer userId) {
        // 1. Find the list of entities.
        List<Publication> publications = findMyPublications(userId);

        // 2. PREPARE: Initialize lazy collections for EACH entity.
        for (Publication publication : publications) {
            //Hibernate.initialize(publication.getPublicationActivityLogs());
            Hibernate.initialize(publication.getComments());
            Hibernate.initialize(publication.getTags());
            Hibernate.initialize(publication.getRatings());
        }

        // 3. PACKAGE: Convert the list to DTOs.
        return publications.stream()
                .map(PublicationDTO::from)
                .collect(Collectors.toList());
    }


    public List<Comment> findAllComments(Integer id) {
        Publication publication = find(id);
        if (publication == null) {
            return null;
        }
        Hibernate.initialize(publication.getComments());
        return publication.getComments();
    }

    public void remove(int id) {
        Publication publication = find(id);
        entityManager.remove(publication);

        //Log
        activityLogBean.create(ActionType.DELETE, "PUBLICATION REMOVED", null);
    }

    public void update(Integer id, String title, String description, String file, String author) {
        Publication publication = find(id);
        publication.setTitle(title);
        publication.setDescription(description);
        publication.setFile(file);
        publication.setAuthor(author);
        entityManager.merge(publication);

        //Log
        activityLogBean.create(ActionType.UPDATE, "PUBLICATION UPDATED", publication.getPublisher(), publication);
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

        //Log
        activityLogBean.create(ActionType.UPDATE, "PUBLICATION VISIBILITY UPDATED", publication.getPublisher(), publication);
    }

    //TODO: AI GENERATED TEXT

    @TransactionTimeout(value = 300, unit = TimeUnit.SECONDS) // 5 minute timeout
    public Publication generateAiTextAndWait(Integer id) throws Exception {

        System.out.println("Calling async processor and waiting for result...");

        // 1. Call the background task
        Future<Publication> futureResult = aiProcessorBean.generateAndReturnSummary(id);

        // 2. Wait here for the result. This is where the time is spent.
        Publication completedPublication = futureResult.get();

        System.out.println("Async task complete. Merging final result.");


        return entityManager.merge(completedPublication);
    }

    //----------------- Ratings ------------------
    public Double findPublicationRating(Integer id) {
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
    public Publication subscribeTag(Integer publicationId, Integer tagId) {
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

        //MAYBE PUT LOG

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

        //MAYBE PUT LOG

        return publication;

    }
}
