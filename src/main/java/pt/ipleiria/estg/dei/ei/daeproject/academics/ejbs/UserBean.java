package pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.RoleType;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Status;
import pt.ipleiria.estg.dei.ei.daeproject.academics.dtos.ActivityLogDTO;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.*;
import pt.ipleiria.estg.dei.ei.daeproject.academics.security.Hasher;

import java.util.List;


@Stateless
public class UserBean {

    @PersistenceContext
    private EntityManager entityManager;

    public void create(String name, String password, String email,
                       String profilePicture, RoleType role) {

        User user;
        String hashedPassword = Hasher.hash(password);
        switch (role) {
            case ADMIN:
                user = new Administrador(name, hashedPassword, email, profilePicture, Status.Active);
                //TODO: FAZER UPLOAD DA IMAGEM
                break;
            case COLABORADOR:
                user = new Colaborador(name, hashedPassword, email, profilePicture,  Status.Active);
                //TODO: FAZER UPLOAD DA IMAGEM
                break;
            case RESPONSAVEL:
                user = new Responsavel(name, hashedPassword, email, profilePicture,  Status.Active);
                //TODO: FAZER UPLOAD DA IMAGEM
                break;
            default:
                throw new IllegalArgumentException("Invalid role");
        }
        entityManager.persist(user);
    }

    // rOLE is always updated here !!
    public void update(Integer id, String name, String email,
                       String profilePicture, RoleType role) {

        User user = find(id);
        if (user == null) throw new IllegalArgumentException("User not found");

        // Update fields if not null
        if (name != null && !name.isBlank()) user.setName(name);
        if (email != null && !email.isBlank()) user.setEmail(email);
        if (profilePicture != null && !profilePicture.isBlank()) user.setProfilePicture(profilePicture);
        //TODO: FAZER UPLOAD DA IMAGEM

        entityManager.merge(user); // merge changes for normal fields

        // Update role (discriminator column) if provided
        if (role != null) {
            entityManager.createNativeQuery(
                            "UPDATE users SET ROLE = ?1 WHERE id = ?2"
                    )
                    .setParameter(1, role.name()) // "ADMIN", "COLABORADOR", "RESPONSAVEL"
                    .setParameter(2, id)
                    .executeUpdate();
        }
    }

    public void updateEmail(Integer id, String email){
        var user = find(id);
        if (user == null) throw new IllegalArgumentException("User not found");

        // Check if email is already in use
        if (findByEmail(email) != null && !user.getEmail().equals(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        user.setEmail(email);
        entityManager.merge(user);
    }

    public void updatePassword(Integer id, String newPassword){
        var user = find(id);
        if (user == null) throw new IllegalArgumentException("User not found");

        user.setPassword(Hasher.hash(newPassword));
        entityManager.merge(user);
    }

    //TODO:  Recover Password via e-mail.

    public void toggleStatus(Integer id) {
        var user = find(id);
        if (user == null) throw new IllegalArgumentException("User not found");

        if (user.getStatus() == Status.Active) {
            user.setStatus(Status.Inactive);
        } else {
            user.setStatus(Status.Active);
        }

        entityManager.merge(user);
    }


    public User find(Integer id) {
        return entityManager.find(User.class, id);
    }

    public List<User> findAll() {
        return entityManager
                .createNamedQuery("getAllUsers", User.class)
                .getResultList();
    }

    public List<ActivityLog> findUserActivity(Integer id) {


        return entityManager
                .createNamedQuery("getUserActivity", ActivityLog.class)
                .setParameter("userId", id)
                .getResultList();
    }

    public User findByEmail(String email) {
        try {
            return entityManager.createNamedQuery(
                            "getUserByEmail",
                            User.class
                    )
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }



    public boolean canLogin(String email, String password) {
        User user = findByEmail(email);
        return user != null && Hasher.verify(password, user.getPassword());
    }
}
