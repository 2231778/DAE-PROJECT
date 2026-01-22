package pt.ipleiria.estg.dei.ei.daeproject.academics.dtos;

import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Visibility;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TagDTO {
    private Integer id;
    private String name;
    private String description;
    private Visibility visibility;

    public TagDTO() {
    }

    public TagDTO(Integer id, String name, String description, Visibility visibility) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.visibility = visibility;
    }


    public static TagDTO from(Tag tag) {
        return new TagDTO(tag.getId(), tag.getName(), tag.getDescription(), tag.getVisibility());
    }

    public static List<TagDTO> from(List<Tag> tags) {
        return tags.stream().map(TagDTO::from).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
