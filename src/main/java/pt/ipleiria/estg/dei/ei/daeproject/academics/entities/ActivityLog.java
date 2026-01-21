package pt.ipleiria.estg.dei.ei.daeproject.academics.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.ActionType;


import java.time.LocalDateTime;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllActivityLog",
                query = "SELECT al FROM ActivityLog al ORDER BY al.timestamp" // JPQL
        ),
        @NamedQuery(
                name = "getUserActivity",
                query = "SELECT al FROM ActivityLog al WHERE al.user.id = :userId ORDER BY al.timestamp DESC "// JPQL
        )
})
@Table(name = "activitieslogs")
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    @Enumerated(EnumType.STRING)
    private ActionType action;
    private String details;
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;
    @ManyToOne(optional = true)
    private User user;
    @ManyToOne(optional = true)
    private Publication publication;

    public ActivityLog() {}
    public ActivityLog(ActionType action, String details,User user, Publication publication) {
        this.action = action;
        this.details = details;
        this.user = user;
        this.publication = publication;
    }

    // This assures the timestmap to be filled before its creation
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
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


}
