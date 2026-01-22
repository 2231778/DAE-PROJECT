package pt.ipleiria.estg.dei.ei.daeproject.academics.dtos;

import org.hibernate.Hibernate;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Visibility;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Comment;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Publication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDTO {
    private Integer id;
    private String content;
    private Visibility visibility;
    private LocalDateTime creationDate;
    private LocalDateTime updatedDate;
    private PublicationRefDTO publication;
    private UserRefDTO user;

    public CommentDTO() {}
    public CommentDTO(Integer id, String content, Visibility visibility, LocalDateTime creationDate, LocalDateTime updatedDate,PublicationRefDTO publication, UserRefDTO user) {
            this.id = id;
            this.content = content;
            this.visibility = visibility;
            this.creationDate = creationDate;
            this.updatedDate = updatedDate;
            this.publication = publication;
            this.user = user;
    }



    public static CommentDTO from(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getContent(),
                comment.getVisibility(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                PublicationRefDTO.from(comment.getPublication()),
                UserRefDTO.from(comment.getAuthor())
        );

    }

    public static List<CommentDTO> from(List<Comment> comments) {
        return comments.stream().map(CommentDTO::from).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public UserRefDTO getUser() {
        return user;
    }

    public void setUser(UserRefDTO user) {
        this.user = user;
    }

    public PublicationRefDTO getPublication() {
        return publication;
    }

    public void setPublication(PublicationRefDTO publication) {
        this.publication = publication;
    }
}
