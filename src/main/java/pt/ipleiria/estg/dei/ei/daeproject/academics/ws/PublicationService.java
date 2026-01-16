package pt.ipleiria.estg.dei.ei.daeproject.academics.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Visibility;
import pt.ipleiria.estg.dei.ei.daeproject.academics.dtos.PublicationDTO;
import pt.ipleiria.estg.dei.ei.daeproject.academics.dtos.UserDTO;
import pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs.PublicationBean;
import pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs.UserBean;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Publication;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.User;
import pt.ipleiria.estg.dei.ei.daeproject.academics.security.Authenticated;

import java.util.List;

@Path("publications") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
@Authenticated
public class PublicationService {
    @EJB
    private PublicationBean publicationBean;
    @EJB
    private UserBean userBean;

    @GET
    @Path("/")
    public List<PublicationDTO> getAllPublications() {
        return PublicationDTO.from(publicationBean.findAll()) ;
    }

    @GET
    @Path("/{id}")
    public Response getPublicationById(@PathParam("id") Integer id) {
        Publication publication = publicationBean.find(id);

        if (publication == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Publication not found")
                    .build();
        }
        PublicationDTO dto = PublicationDTO.from(publication);
        return Response.ok(dto).build();
    }


    //TODO: MAKE THE FILE UPLOAD
    @POST
    @Path("/")
    public Response createPublication(PublicationDTO publicationDTO) {
        try {
            User publisher = userBean.find(publicationDTO.getPublisher().getId());

            if (publisher == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Publisher not found")
                        .build();
            }

            if( publicationDTO.getVisibility() != Visibility.INVISIBLE || publicationDTO.getVisibility() != Visibility.VISIBLE){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("VISIBILITY {"+publicationDTO.getVisibility()+"} is incorrect (Should be ACTIVE OR INACTIVE )")
                        .build();
            }

            Publication newPublication = publicationBean.create(
                    publicationDTO.getTitle(),
                    publicationDTO.getDescription(),
                    publicationDTO.getFile(),
                    publisher,
                    publicationDTO.getAuthor()
            );

            PublicationDTO responseDTO = PublicationDTO.from(newPublication);

            return Response.status(Response.Status.CREATED)
                    .entity(responseDTO)
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to create publication: " + e.getMessage())
                    .build();
        }

    }

    //TODO: UPDATE THE FILE
    @PATCH
    @Path("/{id}")
    public Response updatePublication(@PathParam("id") Integer id,PublicationDTO publicationDTO) {
        try {

             publicationBean.update(
                     id,
                     publicationDTO.getTitle(),
                     publicationDTO.getDescription(),
                     publicationDTO.getFile(),
                     publicationDTO.getAuthor()
             );


             //TODO: ( DELETE THE OLD FILE )
             //TODO: ( ADD THE NEW FILE )

             // Return the updated Publication
            Publication updatedPublication = publicationBean.find(id);
            return Response.ok(PublicationDTO.from(updatedPublication)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to update Publication: " + e.getMessage())
                    .build();
        }


    }

    @PATCH
    @Path("/{id}/visility")
    public Response updateUserStatus(@PathParam("id") Integer id) {
        try {
            publicationBean.toggleVisibility(id);
            var updatedPublication = publicationBean.find(id);
            return Response.ok(PublicationDTO.from(updatedPublication)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to toggle Publication Visibility: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deletePublication(@PathParam("id") Integer id) {
        publicationBean.remove(id);
        return Response.noContent().build(); // 204 No Content
    }
}
