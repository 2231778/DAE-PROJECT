package pt.ipleiria.estg.dei.ei.daeproject.academics.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Status;

import javax.management.DescriptorKey;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
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
    private List<Publication> publications;
    // Ratings not added here cause there will not be frequent show of the ratings of the user ( might even not make sense)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Subscription> subscriptions;
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Comment> comments;
    @OneToMany(mappedBy = "user")
    private List<Rating> ratings;
    @OneToMany(mappedBy = "user")
    private List<ActivityLog> userActivityLogs;

    public User() {
        this.publications = new ArrayList<Publication>();
        this.subscriptions = new ArrayList<Subscription>();
        this.comments = new ArrayList<Comment>();
        this.ratings = new ArrayList<Rating>();
        this.userActivityLogs = new ArrayList<ActivityLog>();
    }

    public User(String name, String password, String email,String profilePicture, Status status) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.profilePicture = profilePicture;
        this.status = status;
        this.publications = new ArrayList<Publication>();
        this.subscriptions = new ArrayList<Subscription>();
        this.comments = new ArrayList<Comment>();
        this.ratings = new ArrayList<Rating>();
        this.userActivityLogs = new ArrayList<ActivityLog>();
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

    public Integer getId() {
        return id;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
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
}
