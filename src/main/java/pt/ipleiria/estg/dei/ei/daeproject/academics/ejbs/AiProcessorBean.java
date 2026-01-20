package pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs;

import jakarta.ejb.AsyncResult;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs.ai.AiService;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Publication;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.ActionType;
import java.util.concurrent.Future;

@Stateless
public class AiProcessorBean {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private AiService aiService;

    @EJB
    private ActivityLogBean activityLogBean;

    @Asynchronous
    public Future<Publication> generateAndReturnSummary(int publicationId) {
        Publication publication = entityManager.find(Publication.class, publicationId);
        if (publication == null) {
            throw new IllegalArgumentException("Publication not found: " + publicationId);
        }

        // This is the slow part
        String summary = aiService.generateSummary(publication.getDescription());
        publication.setAi_generated_summary(summary);

        // Log the action
        activityLogBean.create(ActionType.UPDATE, "AI SUMMARY REGENERATED", publication.getPublisher(), publication);

        // Return the completed object in a Future
        return new AsyncResult<>(publication);
    }
}
