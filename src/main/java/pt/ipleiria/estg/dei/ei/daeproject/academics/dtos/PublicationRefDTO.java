package pt.ipleiria.estg.dei.ei.daeproject.academics.dtos;

import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Publication;

public class PublicationRefDTO {
    private Integer id;
    private String title;
    private String description;
    private String author;

    public PublicationRefDTO() {}
    public PublicationRefDTO(Integer id, String title, String description, String author) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = author;
    }

    public static PublicationRefDTO from(Publication publication) {
        PublicationRefDTO dto = new PublicationRefDTO();
        dto.setId(publication.getId());
        dto.setTitle(publication.getTitle());
        dto.setDescription(publication.getDescription());
        dto.setAuthor(publication.getAuthor());
        return dto;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
