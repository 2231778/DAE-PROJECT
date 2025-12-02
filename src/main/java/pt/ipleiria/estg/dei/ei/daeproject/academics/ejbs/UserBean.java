package pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Status;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.User;

@Stateless
public class UserBean {
    @PersistenceContext
    private EntityManager entityManager;
    public void create(String name, String password, String email, String profilePicture, Status status) {
        var user = new User(name, password, email, profilePicture, status);
        entityManager.persist(user);
    }
}
