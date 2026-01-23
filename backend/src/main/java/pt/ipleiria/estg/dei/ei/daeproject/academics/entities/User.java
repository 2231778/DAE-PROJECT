package pt.ipleiria.estg.dei.ei.daeproject.academics.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UniqueElements;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Status;

import javax.management.DescriptorKey;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllUsers",
                query = "SELECT u FROM User u ORDER BY u.name"
        ),
        @NamedQuery(
                name = "getUserByEmail",
                query = "SELECT u FROM User u WHERE u.email = :email"
        ),
//        @NamedQuery(
//                name = "getActiveUsers",
//                query = "SELECT u FROM User u WHERE u.status = :status"
//        )
})
@Table(name = "users",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"})
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="ROLE")
public class User extends Versionable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String password;
    @Email
    @NotBlank

    private String email;
    @NotBlank
    private String profilePicture;
    @NotNull
    @Enumerated(EnumType.STRING) // Makes the Enum be like 0 ,1 ,2 ... We could use also the EnumType.String that would make it like a string !!!
    private Status status; // Custom Enum class !!!
    @OneToMany(mappedBy = "publisher", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Publication> publications;
    // Ratings not added here cause there will not be frequent show of the ratings of the user ( might even not make sense)
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Comment> comments;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Rating> ratings;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<ActivityLog> userActivityLogs;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_tags",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @JsonIgnore
    private List<Tag> tags;
    private boolean deleted;

    public User() {
        this.publications = new ArrayList<Publication>();
        this.comments = new ArrayList<Comment>();
        this.ratings = new ArrayList<Rating>();
        this.userActivityLogs = new ArrayList<ActivityLog>();
        this.tags = new ArrayList<Tag>();
        this.deleted = false;
    }

    public User(String name, String password, String email,String profilePicture, Status status) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.profilePicture = profilePicture;
        this.status = status;
        this.publications = new ArrayList<Publication>();
        this.comments = new ArrayList<Comment>();
        this.ratings = new ArrayList<Rating>();
        this.userActivityLogs = new ArrayList<ActivityLog>();
        this.tags = new ArrayList<Tag>();
        this.deleted = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public @NotBlank String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(@NotBlank String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public List<Publication> getPublications() {
        return publications;
    }

    public void setPublications(List<Publication> publications) {
        this.publications = publications;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public List<ActivityLog> getUserActivityLogs() {
        return userActivityLogs;
    }

    public void setUserActivityLogs(List<ActivityLog> userActivityLogs) {
        this.userActivityLogs = userActivityLogs;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", status=" + status +
                '}';
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
