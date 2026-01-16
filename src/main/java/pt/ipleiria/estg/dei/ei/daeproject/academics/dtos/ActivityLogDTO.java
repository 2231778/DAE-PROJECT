package pt.ipleiria.estg.dei.ei.daeproject.academics.dtos;

import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.ActionType;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ActivityLogDTO {
    private Integer id;
    private ActionType action;
    private String details;
    private LocalDateTime timestamp;
    private UserDTO user;
    private PublicationDTO publication;

    public ActivityLogDTO() {}
    public ActivityLogDTO(Integer id, ActionType action, String details, LocalDateTime timestamp, UserDTO user , PublicationDTO publication ){
        this.id = id;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
        this.user = user;
        this.publication = publication;
    }

    public static ActivityLogDTO from(ActivityLog activityLog) {
        return new ActivityLogDTO(
                activityLog.getId(),
                activityLog.getAction(),
                activityLog.getDetails(),
                activityLog.getTimestamp(),
                UserDTO.from(activityLog.getUser()),
                PublicationDTO.from(activityLog.getPublication())
        );
    }

    public static List<ActivityLogDTO> from(List<ActivityLog> activityLogs) {
        return activityLogs.stream().map(ActivityLogDTO::from).collect(Collectors.toList());
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public PublicationDTO getPublication() {
        return publication;
    }

    public void setPublication(PublicationDTO publication) {
        this.publication = publication;
    }
}
