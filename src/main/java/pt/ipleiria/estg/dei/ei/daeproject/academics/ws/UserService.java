package pt.ipleiria.estg.dei.ei.daeproject.academics.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.RoleType;
import pt.ipleiria.estg.dei.ei.daeproject.academics.dtos.ActivityLogDTO;
import pt.ipleiria.estg.dei.ei.daeproject.academics.dtos.PasswordDTO;
import pt.ipleiria.estg.dei.ei.daeproject.academics.dtos.UserCreateDTO;
import pt.ipleiria.estg.dei.ei.daeproject.academics.dtos.UserDTO;
import pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs.UserBean;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.ActivityLog;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.User;
import pt.ipleiria.estg.dei.ei.daeproject.academics.security.Authenticated;

import java.util.List;

@Path("users") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
@Authenticated
public class UserService {
    @EJB
    private UserBean userBean;

    @GET
    @Path("/")
    public List<UserDTO> getAllUsers() {
        return UserDTO.from(userBean.findAll());
    }

    @GET
    @Path("/{id}")
    public UserDTO getUserById(@PathParam("id") Integer id) {
        return UserDTO.from(userBean.find(id));
    }

    @POST
    @Path("/")
    @RolesAllowed({"ADMIN"})
    public Response createUser(UserCreateDTO userCreateDTO) {
        try {
            // Optional pre-check: message before hitting DB
            if (userBean.findByEmail(userCreateDTO.getEmail()) != null) {
                return Response.status(Response.Status.CONFLICT) // 409 Conflict
                        .entity("Email already in use")
                        .build();
            }

            userBean.create(
                    userCreateDTO.getName(),
                    userCreateDTO.getPassword(),
                    userCreateDTO.getEmail(),
                    userCreateDTO.getProfilePicture(),
                    userCreateDTO.getRole()
            );

            // Return created user
            var createdUser = userBean.findByEmail(userCreateDTO.getEmail());
            return Response.status(Response.Status.CREATED)
                    .entity(UserDTO.from(createdUser))
                    .build();

        } catch (IllegalArgumentException e) {
            // Invalid role or other validation errors
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();

        } catch (jakarta.persistence.PersistenceException e) {
            // Catch database errors (e.g., race conditions on unique email)
            if (e.getCause() != null && e.getCause().getMessage().contains("email")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Email already in use")
                        .build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Unexpected error: " + e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    public Response updateUser(@PathParam("id") Integer id, UserDTO userDTO) {
        try {
            // Update the user using your UserBean
            userBean.update(
                    id,
                    userDTO.getName(),
                    userDTO.getEmail(),
                    userDTO.getProfilePicture(),
                    userDTO.getRole() != null ? RoleType.valueOf(userDTO.getRole()) : null
            );

            // Return the updated user as DTO
            User updatedUser = userBean.find(id);
            return Response.ok(UserDTO.from(updatedUser)).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to update user: " + e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{id}/status")
    @RolesAllowed({"ADMIN"})
    public Response updateUserStatus(@PathParam("id") Integer id) {
        try {
            userBean.toggleStatus(id);
            var updatedUser = userBean.find(id);
            return Response.ok(UserDTO.from(updatedUser)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to toggle user status: " + e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/password")
    public Response updatePassword(@Context SecurityContext securityContext, PasswordDTO passwordDTO) {
        try {
            // Get the current authenticated user's ID from the SecurityContext
            Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());

            // Update the password for this user
            userBean.updatePassword(userId, passwordDTO.getNewPassword());

            return Response.ok("Password updated successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to update password: " + e.getMessage())
                    .build();
        }
    }


    @PATCH
    @Path("/email")
    public Response updateEmail(@Context SecurityContext securityContext, UserDTO userDTO) {
        try {
            // Get the current authenticated user's ID from the SecurityContext
            Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());

            // Update the email for this user
            userBean.updateEmail(userId, userDTO.getEmail());

            return Response.ok(UserDTO.from(userBean.find(userId))).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to update email: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/my-activity")
    public Response getMyActivities(@Context SecurityContext securityContext) {
        // Get the current authenticated user's ID from the SecurityContext
        Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());

        List<ActivityLog> userActivity = userBean.findUserActivity(userId);

        List<ActivityLogDTO> dto = ActivityLogDTO.from(userActivity);

        if (dto.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.ok(dto).build();
    }
}
