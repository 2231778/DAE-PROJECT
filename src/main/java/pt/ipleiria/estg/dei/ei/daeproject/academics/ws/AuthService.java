package pt.ipleiria.estg.dei.ei.daeproject.academics.ws;

import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pt.ipleiria.estg.dei.ei.daeproject.academics.dtos.AuthDTO;
import pt.ipleiria.estg.dei.ei.daeproject.academics.dtos.UserDTO;
import pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs.UserBean;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.User;
import pt.ipleiria.estg.dei.ei.daeproject.academics.security.Authenticated;
import pt.ipleiria.estg.dei.ei.daeproject.academics.security.TokenIssuer;


@Path("auth")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class AuthService {
    @EJB
    private UserBean userBean;
    @Context
    private SecurityContext securityContext;

    @POST
    @Path("/login")
    public Response authenticate(@Valid AuthDTO auth) {
        if (userBean.canLogin(auth.getEmail(), auth.getPassword())) {
            String token = TokenIssuer.issue(auth.getEmail());
            return Response.ok(token).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @GET
    @Authenticated
    @Path("/user")
    public Response getAuthenticatedUser() {
        Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());
        User user = userBean.find(userId);
        return Response.ok(UserDTO.from(user)).build();

    }


}
