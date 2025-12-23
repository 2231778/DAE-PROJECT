package pt.ipleiria.estg.dei.ei.daeproject.academics.entities;

import jakarta.persistence.*;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.ActionType;


import java.time.LocalDateTime;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllActivityLog",
                query = "SELECT al FROM ActivityLog al" // JPQL
        )
})
@Table(name = "activitieslogs")
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private ActionType action;
    private String details;
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;
    @ManyToOne()
    private User user;
    @ManyToOne()
    private Publication publication;

    public ActivityLog() {}
    public ActivityLog(ActionType action, String details) {
        this.action = action;
        this.details = details;
    }

    public Integer getId() {
        return id;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
