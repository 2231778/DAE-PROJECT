package pt.ipleiria.estg.dei.ei.daeproject.academics.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Formula;
import org.w3c.dom.Text;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Status;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Visibility;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionException;


@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllPublications",
                query = "SELECT p FROM Publication p WHERE p.visibility = :visibility ORDER BY p.title"
        ),
        @NamedQuery(
                name = "getMyPublications",
                query = "SELECT p FROM Publication p WHERE p.publisher.id = :userId"
        )
})
@Table(name = "publications")
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String description;
    private String file;
    @NotNull
    @Enumerated(EnumType.STRING) // Makes the Enum be like 0 ,1 ,2 ... We could use also the EnumType.String that would make it like a string !!!
    private Visibility visibility; // Custom Enum class !!!
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    @Lob
    private String ai_generated_summary;
    @ManyToOne()
    @NotNull
    private User publisher; //This is the FK of the users
    private String author; //This is the person who created it
    @ManyToMany
    @JoinTable(
            name = "publication_tags",
            joinColumns = @JoinColumn(name = "publication_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @JsonIgnore
    private List<Tag> tags;
    @JsonIgnore
    @OneToMany(mappedBy = "publication",fetch = FetchType.LAZY)
    private List<Comment> comments;
    @JsonIgnore
    @OneToMany(mappedBy = "publication")
    private List<Rating> ratings;
    @OneToMany(mappedBy = "publication")
    @JsonIgnore
    private List<ActivityLog> publicationActivityLogs;



    public Publication() {
        this.tags = new ArrayList<Tag>();
        this.comments = new ArrayList<Comment>();
        this.ratings = new ArrayList<Rating>();
        this.publicationActivityLogs = new ArrayList<ActivityLog>();
        this.visibility = Visibility.VISIBLE; // Created Visible
    }

    public Publication(String title, String description, String file, User publisher, String author) {
        this.title = title;
        this.description = description;
        this.file = file;
        this.visibility = Visibility.VISIBLE; // Created Visible
        this.publisher = publisher;
        this.author = author;
        this.ai_generated_summary = "";
        this.tags = new ArrayList<Tag>();
        this.comments = new ArrayList<Comment>();
        this.ratings = new ArrayList<Rating>();
        this.publicationActivityLogs = new ArrayList<ActivityLog>();
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


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
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

    public String getAi_generated_summary() {
        return ai_generated_summary;
    }

    public void setAi_generated_summary(String ai_generated_summary) {
        this.ai_generated_summary = ai_generated_summary;
    }

    public @NotNull User getPublisher() {
        return publisher;
    }

    public void setPublisher(@NotNull User publisher) {
        this.publisher = publisher;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getId() {
        return id;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public @NotNull List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(@NotNull List<Rating> ratings) {
        this.ratings = ratings;
    }

    public List<ActivityLog> getPublicationActivityLogs() {
        return publicationActivityLogs;
    }

    public void setPublicationActivityLogs(List<ActivityLog> publicationActivityLogs) {
        this.publicationActivityLogs = publicationActivityLogs;
    }
}
