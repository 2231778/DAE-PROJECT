package pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.ActionType;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.RoleType;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Status;
import pt.ipleiria.estg.dei.ei.daeproject.academics.dtos.ActivityLogDTO;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.*;
import pt.ipleiria.estg.dei.ei.daeproject.academics.security.Hasher;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;


@Stateless
public class UserBean {

    @PersistenceContext
    private EntityManager entityManager;
    @EJB
    private ActivityLogBean activityLogBean;

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
        // Log
        activityLogBean.create(ActionType.CREATE,"User Creation",user);
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
        //Log
        activityLogBean.create(ActionType.UPDATE,"User Updated",user);

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

    public String generatePasswordResetToken(String email) {
        User user = findByEmail(email);
        if (user == null) return null;

        String token = java.util.UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);

        entityManager.persist(resetToken);
        return token;
    }

    public boolean resetPasswordWithToken(String token, String newPassword) {
        try {
            PasswordResetToken resetToken = entityManager.createQuery(
                            "SELECT t FROM PasswordResetToken t WHERE t.token = :token",
                            PasswordResetToken.class)
                    .setParameter("token", token)
                    .getSingleResult();

            if (resetToken.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
                entityManager.remove(resetToken);
                return false;
            }

            User user = resetToken.getUser();
            user.setPassword(Hasher.hash(newPassword));
            entityManager.merge(user);

            entityManager.remove(resetToken);

            activityLogBean.create(ActionType.UPDATE, "Password reset via email token", user);

            return true;
        } catch (Exception e) {
            return false;
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

        //Log
        activityLogBean.create(ActionType.UPDATE,"User Email Updated",user);

    }

    public void updatePassword(Integer id, String newPassword){
        var user = find(id);
        if (user == null) throw new IllegalArgumentException("User not found");

        user.setPassword(Hasher.hash(newPassword));
        entityManager.merge(user);

        //Log
        activityLogBean.create(ActionType.UPDATE,"User Password Updated",user);
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

        //Log
        activityLogBean.create(ActionType.UPDATE,"User Status Updated",user);
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

    public User subscribeTag(Integer userId, Integer tagId) {
        Tag tag = entityManager.find(Tag.class, tagId);
        if (tag == null) throw new IllegalArgumentException("Tag not found");

        User user = entityManager.find(User.class, userId);
        if (user == null) throw new IllegalArgumentException("User not found");

        // Add the tag to the user if not already present
        if (!user.getTags().contains(tag)) {
            user.getTags().add(tag);
        }

        // Add the user to the tag as well to maintain both sides
        if (!tag.getUsers().contains(user)) {
            tag.getUsers().add(user);
        }

        // Persist changes
        entityManager.merge(user);
        entityManager.merge(tag);

        //Maybe put Log here

        return user;
    }

    public User unsubscribeTag(Integer userId, Integer tagId) {
        Tag tag = entityManager.find(Tag.class, tagId);
        if (tag == null) throw new IllegalArgumentException("Tag not found");

        User user = entityManager.find(User.class, userId);
        if (user == null) throw new IllegalArgumentException("User not found");

        // Remove association if present
        user.getTags().remove(tag);
        tag.getUsers().remove(user);

        // Persist the changes
        entityManager.merge(user);
        entityManager.merge(tag);

        //Maybe put Log here

        return user;
    }



    public boolean canLogin(String email, String password) {
        User user = findByEmail(email);
        return user != null && Hasher.verify(password, user.getPassword()) && user.getStatus() == Status.Active && !user.isDeleted();
    }

    public User delete(Integer id) {
        User user = find(id);
        if (user == null) throw new IllegalArgumentException("User not found");

        user.setDeleted(true);
        entityManager.merge(user);

        activityLogBean.create(ActionType.DELETE, "User set to deleted", user);
        return user;
    }

}
