package pt.ipleiria.estg.dei.ei.daeproject.academics.entities;

import jakarta.persistence.*;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Visibility;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private Visibility visibility;
    @ManyToMany(mappedBy = "tags")
    private List<Publication> publications;
    @ManyToMany(mappedBy = "tags")
    private List<User> users;

    public Tag() {
        this.publications = new ArrayList<Publication>();
        this.users = new ArrayList<User>();
        this.visibility = Visibility.VISIBLE;
    }

    public Tag(String name, String description) {
        this.name = name;
        this.description = description;
        this.visibility = Visibility.VISIBLE;
        this.publications = new ArrayList<Publication>();
        this.users = new ArrayList<User>();
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

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public List<Publication> getPublications() {
        return publications;
    }

    public void setPublications(List<Publication> publications) {
        this.publications = publications;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
