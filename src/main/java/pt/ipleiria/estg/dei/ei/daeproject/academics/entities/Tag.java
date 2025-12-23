package pt.ipleiria.estg.dei.ei.daeproject.academics.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllTags",
                query = "SELECT t FROM Tag t ORDER BY t.name" // JPQL
        )
})
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Integer id;
    private String name;
    private String description;
    private Boolean visibility;
    @ManyToMany(mappedBy = "tags")
    private List<Subscription> subscriptions;
    @ManyToMany(mappedBy = "tags")
    private List<Publication> publications;

    public Tag() {
        this.subscriptions = new ArrayList<Subscription>();
        this.publications = new ArrayList<Publication>();
    }

    public Tag(String name, String description, Boolean visibility) {
        this.name = name;
        this.description = description;
        this.visibility = true;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

    public List<Publication> getPublications() {
        return publications;
    }

    public void setPublications(List<Publication> publications) {
        this.publications = publications;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
}
