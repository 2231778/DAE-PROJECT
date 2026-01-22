package pt.ipleiria.estg.dei.ei.daeproject.academics.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllRatings",
                query = "SELECT r FROM Rating r ORDER BY r.value" // JPQL
        ),
        @NamedQuery(
                name = "getRating",
                query = "SELECT r FROM Rating r WHERE r.publication.id = :pubId AND r.user.id = :userId"
        )
})
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    @Min(1)
    @Max(5)
    private Integer value;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;      // Não adicionado na Class User

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "publication_id", nullable = false)
    private Publication publication;    // Não adicionado na Class User

    public Rating() {

    }

    public Rating(Integer value, User user, Publication publication) {
        this.value = value;
        this.user = user;
        this.publication = publication;
    }


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public @NotNull @Min(1) @Max(5) Integer getValue() {
        return value;
    }

    public void setValue(@NotNull @Min(1) @Max(5) Integer value) {
        this.value = value;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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
}
