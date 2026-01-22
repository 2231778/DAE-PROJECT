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

    private List<CommentDTO> comments;
    private List<TagDTO> tags;
    private List<ActivityLogDTO> activityLogs;
    private Integer numberOfComments;
    private Double rating;
    private Integer numberOfReviews;

    public PublicationDTO() {
        this.activityLogs = new ArrayList<ActivityLogDTO>();
        this.comments = new ArrayList<CommentDTO>();
        this.tags = new ArrayList<TagDTO>();
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
            this.tags = new ArrayList<TagDTO>();

    }

    public static PublicationDTO from(Publication publication) {
        if (publication == null) {
            return null;
        }
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

        if (Hibernate.isInitialized(publication.getPublicationActivityLogs())&& !publication.getPublicationActivityLogs().isEmpty()) {
            dto.activityLogs = ActivityLogDTO.from(publication.getPublicationActivityLogs());
        }

        if (Hibernate.isInitialized(publication.getComments())) {
            dto.comments = CommentDTO.from(publication.getComments());
            dto.setNumberOfComments(publication.getComments().size());
        }
        if (Hibernate.isInitialized(publication.getTags())) {
            dto.tags = TagDTO.from(publication.getTags());
        }

        if (Hibernate.isInitialized(publication.getRatings()) && !publication.getRatings().isEmpty()) {
            dto.setRating(publication.getRatings().stream().mapToDouble(r -> r.getValue()).average().orElse(0.0));
            dto.setNumberOfReviews(publication.getRatings().size());
        } else {
            dto.setRating(0.0);        // default 0
            dto.setNumberOfReviews(0); // default 0
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

    public List<TagDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }

    public Integer getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(Integer numberOfComments) {
        this.numberOfComments = numberOfComments;
    }


    public Integer getNumberOfReviews() {
        return numberOfReviews;
    }

    public void setNumberOfReviews(Integer numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

    public Double getRating(){
        return this.rating;
    }
    public void setRating(Double rating) {
        this.rating = rating;
    }
}
