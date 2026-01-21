package pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.ActionType;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.ActivityLog;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Publication;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.User;

import java.util.List;

@Stateless
public class ActivityLogBean {
    @PersistenceContext
    private EntityManager entityManager;

    public ActivityLog create(ActionType action, String details, User user, Publication publication){
        ActivityLog activityLog = new ActivityLog(action, details, user, publication);
        entityManager.persist(activityLog);
        return activityLog;
    }
    public ActivityLog create(ActionType action, String details, User user) {
        return create(action, details, user, null);
    }

    public List<ActivityLog> findUserActivity(Integer id){
        return entityManager
                .createNamedQuery("getUserActivity", ActivityLog.class)
                .setParameter("userId", id)
                .getResultList();
    }

    public List<ActivityLog> findAll(){
        return entityManager.createNamedQuery("getAllActivityLog",ActivityLog.class).getResultList();
    }
}
