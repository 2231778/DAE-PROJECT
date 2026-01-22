package pt.ipleiria.estg.dei.ei.daeproject.academics.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pt.ipleiria.estg.dei.ei.daeproject.academics.dtos.ActivityLogDTO;
import pt.ipleiria.estg.dei.ei.daeproject.academics.dtos.UserDTO;
import pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs.ActivityLogBean;
import pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs.PublicationBean;
import pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs.UserBean;
import pt.ipleiria.estg.dei.ei.daeproject.academics.security.Authenticated;

import java.util.List;

@Path("/activity-log")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Authenticated
public class ActivityLogService {
    @EJB
    private ActivityLogBean activityLogBean;
    @EJB
    private UserBean userBean;
    @EJB
    private PublicationBean publicationBean;
    @Context
    private SecurityContext securityContext;

    @GET
    @Path("/")
    public List<ActivityLogDTO> getActivityLogs() {
        return ActivityLogDTO.from(activityLogBean.findAll());
    }

    @GET
    @Path("/user/{id}")
    @RolesAllowed({"ADMIN"})
    public List<ActivityLogDTO> getActivityLogOfUser(@PathParam("id") int id) {
        return ActivityLogDTO.from(activityLogBean.findUserActivity(id));
    }

    @POST
    @Path("/")
    public Response addActivityLog(ActivityLogDTO activityLogDTO) {
        try{
            var user = userBean.find(activityLogDTO.getUser().getId());
            var publication = publicationBean.find(activityLogDTO.getPublication().getId());

            var createdActivityLog = activityLogBean.create(
                    activityLogDTO.getAction(),activityLogDTO.getDetails(),user,publication);

            return Response.status(Response.Status.CREATED)
                    .entity(ActivityLogDTO.from(createdActivityLog))
                    .build();
        }catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

}
