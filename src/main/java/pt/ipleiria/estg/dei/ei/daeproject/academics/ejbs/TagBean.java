package pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Visibility;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Tag;

import java.util.List;
import java.util.NoSuchElementException;

@Stateless
public class TagBean {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Tag> findAll() {
        return entityManager
                .createNamedQuery("getAllTags", Tag.class)
                .getResultList();
    }

    public Tag create(String name,String description) {
        Tag tag = new Tag(name,description);
        entityManager.persist(tag);
        return tag;
    }

    public Tag update(Integer id ,String name,String description) {
        Tag tag = entityManager.find(Tag.class, id);
        if(tag == null) {
            return null;
        }
        tag.setName(name);
        tag.setDescription(description);
        return tag;
    }

    public void delete(Integer id) {
        Tag tag = entityManager.find(Tag.class, id);

        if(tag == null) {
            throw new NoSuchElementException();
        }

        entityManager.remove(tag);
    }

    public void toggleVisibility(Integer id) {
        Tag tag = entityManager.find(Tag.class, id);
        if (tag == null) throw new IllegalArgumentException("Tag not found");

        if (tag.getVisibility() == Visibility.VISIBLE) {
            tag.setVisibility(Visibility.INVISIBLE);
        } else {
            tag.setVisibility(Visibility.VISIBLE);
        }

        entityManager.merge(tag);
    }

}
