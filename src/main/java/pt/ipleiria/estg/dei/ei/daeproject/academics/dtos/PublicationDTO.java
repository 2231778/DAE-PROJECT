package pt.ipleiria.estg.dei.ei.daeproject.academics.dtos;

import org.hibernate.Hibernate;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Visibility;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.ActivityLog;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Publication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PublicationDTO {
    private Integer id;
    private String title;
    private String description;
    private String file;
    private Visibility visibility;

    private LocalDateTime creationDate;
    private LocalDateTime updatedDate;
    private String aiGeneratedSummary;

    private UserDTO publisher;   // lightweight user
    private String author;

    //private List<TagDTO> tags;

    private List<CommentDTO> comments;
    //private List<RattingDTO> tags;
    private List<ActivityLogDTO> activityLogs;

    public PublicationDTO() {
        this.activityLogs = new ArrayList<ActivityLogDTO>();
        this.comments = new ArrayList<CommentDTO>();
    }

    public PublicationDTO(Integer id,String title, String description, String file, Visibility visibility, LocalDateTime creationDate, LocalDateTime    updatedDate, String aiGeneratedSummary, UserDTO publisher, String author){
            this.id = id;
            this.title = title;
            this.description = description;
            this.file = file;
            this.visibility = visibility;
            this.creationDate = creationDate;
            this.updatedDate = updatedDate;
            this.aiGeneratedSummary = aiGeneratedSummary;
            this.publisher = publisher;
            this.author = author;
            this.activityLogs = new ArrayList<ActivityLogDTO>();
            this.comments = new ArrayList<CommentDTO>();
    }

    public static PublicationDTO from(Publication publication) {
        PublicationDTO dto = new  PublicationDTO(
                publication.getId(),
                publication.getTitle(),
                publication.getDescription(),
                publication.getFile(),
                publication.getVisibility(),
                publication.getCreatedAt(),
                publication.getUpdatedAt(),
                publication.getAi_generated_summary(),
                UserDTO.from(publication.getPublisher()),
                publication.getAuthor()

        );

        if (Hibernate.isInitialized(publication.getPublicationActivityLogs())) {
            dto.activityLogs = publication.getPublicationActivityLogs()
                    .stream()
                    .map(ActivityLogDTO::from)
                    .collect(Collectors.toList());
        }

        if (Hibernate.isInitialized(publication.getComments())) {
            dto.comments = publication.getComments()
                    .stream()
                    .map(CommentDTO::from)
                    .collect(Collectors.toList());
        }


        return dto;
    }

    public static List<PublicationDTO> from(List<Publication> publications) {
        return publications.stream().map(PublicationDTO::from).collect(Collectors.toList());
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

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }





    public String getAiGeneratedSummary() {
        return aiGeneratedSummary;
    }

    public void setAiGeneratedSummary(String aiGeneratedSummary) {
        this.aiGeneratedSummary = aiGeneratedSummary;
    }

    public UserDTO getPublisher() {
        return publisher;
    }

    public void setPublisher(UserDTO publisher) {
        this.publisher = publisher;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<ActivityLogDTO> getActivityLogs() {
        return activityLogs;
    }

    public void setActivityLogs(List<ActivityLogDTO> activityLogs) {
        this.activityLogs = activityLogs;
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

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }
}
