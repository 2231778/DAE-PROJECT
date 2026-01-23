package pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Hibernate;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.ActionType;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Publication;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Rating;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.User;

import java.util.List;

@Stateless
public class RatingBean {
    @PersistenceContext
    private EntityManager entityManager;
    @EJB
    private UserBean userBean;
    @EJB
    private PublicationBean publicationBean;
    @EJB
    private ActivityLogBean activityLogBean;


    public Rating create(Double value, Integer publicationId, Integer userId) {

        if (!(value >= 0 && value <= 5)) {
            throw new IllegalArgumentException("Value must be between 0 and 5");
        }

        Publication publication = publicationBean.find(publicationId);
        if (publication == null) {
            throw new EntityNotFoundException("Publication not found with ID: " + publicationId);
        }
        User user = userBean.find(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }

        Rating rating = new Rating(value, user, publication);

        //Add in the publication and user entity
        publication.getRatings().add(rating);
        user.getRatings().add(rating);

        entityManager.persist(rating);

        //Log
        activityLogBean.create(ActionType.CREATE,"PUBLICATION RATED",user,publication);

        Hibernate.initialize(user.getRatings());
        Hibernate.initialize(user.getRatings());

        return rating;

    }

    public Rating update(Double value, Integer publicationId, Integer userId) {
        try {
            Rating rating;
            try {
                rating = entityManager.createNamedQuery("getRating", Rating.class)
                        .setParameter("pubId", publicationId)
                        .setParameter("userId", userId)
                        .getSingleResult();
            } catch (NoResultException e) {
                throw new EntityNotFoundException(
                        "Rating not found for publication " + publicationId + " and user " + userId
                );
            }

            rating.setValue(value);
            entityManager.merge(rating);

            //Log
            activityLogBean.create(ActionType.UPDATE,"PUBLICATION RE-RATED",rating.getUser(),rating.getPublication());


            return rating;
        } catch (NoResultException e) {
            throw new EntityNotFoundException("Rating not found for publication " + publicationId + " and user " + userId);
        }
    }


    public void delete(Integer publicationId, Integer userId) {
        Rating rating;
        try {
            rating = entityManager.createNamedQuery("getRating", Rating.class)
                    .setParameter("pubId", publicationId)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new EntityNotFoundException(
                    "Rating not found for publication " + publicationId + " and user " + userId
            );
        }

        entityManager.remove(rating);

        //Log
        activityLogBean.create(ActionType.DELETE,"PUBLICATION RATING REMOVED",rating.getUser(),null);

    }


}
