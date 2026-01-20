package pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.ActionType;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Visibility;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Tag;

import java.util.List;
import java.util.NoSuchElementException;

@Stateless
public class TagBean {
    @PersistenceContext
    private EntityManager entityManager;
    @EJB
    private ActivityLogBean activityLogBean;

    public List<Tag> findAll() {
        return entityManager
                .createNamedQuery("getAllTags", Tag.class)
                .getResultList();
    }

    public Tag create(String name,String description) {
        Tag tag = new Tag(name,description);
        entityManager.persist(tag);

        //Log
        activityLogBean.create(ActionType.CREATE,"TAG CREATED",null,null);

        return tag;
    }

    public Tag update(Integer id ,String name,String description) {
        Tag tag = entityManager.find(Tag.class, id);
        if(tag == null) {
            return null;
        }
        tag.setName(name);
        tag.setDescription(description);
        entityManager.merge(tag);

        //Log
        activityLogBean.create(ActionType.CREATE,"TAG UPDATED",null,null);

        return tag;
    }

    public void delete(Integer id) {
        Tag tag = entityManager.find(Tag.class, id);

        if(tag == null) {
            throw new NoSuchElementException();
        }

        entityManager.remove(tag);

        //Log
        activityLogBean.create(ActionType.DELETE,"TAG DELETED",null,null);

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
        //LOG
        activityLogBean.create(ActionType.UPDATE,"TAG VISIBILITY UPDATED",null,null);


    }

}
