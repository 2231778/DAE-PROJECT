package pt.ipleiria.estg.dei.ei.daeproject.academics.entities;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllSubscriptions",
                query = "SELECT r FROM Subscription r" // JPQL
        )
})
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Boolean notify_on_update ;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToMany
    @JoinTable(
            name = "subscription_tags",
            joinColumns = @JoinColumn(name = "subscription_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;


    public Subscription() {
        this.tags = new ArrayList<Tag>();
    }
    public Subscription(User user) {
        this.notify_on_update = true;
        this.user = user;
        this.tags = new ArrayList<Tag>();
    }


    public Integer getId() {
        return id;
    }

    public Boolean getNotify_on_update() {
        return notify_on_update;
    }

    public void setNotify_on_update(Boolean notify_on_update) {
        this.notify_on_update = notify_on_update;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
