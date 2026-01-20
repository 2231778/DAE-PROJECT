package pt.ipleiria.estg.dei.ei.daeproject.academics.dtos;

import jakarta.persistence.criteria.CriteriaBuilder;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Rating;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RatingDTO {
    private Integer id;
    private Integer value;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public RatingDTO() {}
    public RatingDTO(Integer id, Integer value, LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.id = id;
        this.value = value;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public static RatingDTO from(Rating rating) {
        return new RatingDTO(rating.getId(),
                rating.getValue(),
                rating.getCreatedAt(),
                rating.getUpdatedAt()
        );
    }

    public static List<RatingDTO> from(List<Rating> ratings) {
        return ratings.stream().map(RatingDTO::from).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}
