package pt.ipleiria.estg.dei.ei.daeproject.academics.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Visibility;

import java.time.LocalDateTime;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllComments",
                query = "SELECT c FROM Comment c" // JPQL
        )
})
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Integer id;
    private String content;
    private Visibility visibility;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Publication publication;
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @JsonIgnoreProperties({"comments"}) // ignore the back-reference
    private User author;

    public Comment() {
        this.visibility = Visibility.VISIBLE;
    }
    public Comment(String content, Publication publication, User author){
        this.content = content;
        this.visibility = Visibility.VISIBLE;
        this.publication = publication;
        this.author = author;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
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

    public @NotNull Publication getPublication() {
        return publication;
    }

    public void setPublication(@NotNull Publication publication) {
        this.publication = publication;
    }

    public @NotNull User getAuthor() {
        return author;
    }

    public void setAuthor(@NotNull User author) {
        this.author = author;
    }
}
