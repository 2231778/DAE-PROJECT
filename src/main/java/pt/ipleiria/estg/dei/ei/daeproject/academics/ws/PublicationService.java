package pt.ipleiria.estg.dei.ei.daeproject.academics.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.hibernate.Hibernate;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Visibility;
import pt.ipleiria.estg.dei.ei.daeproject.academics.dtos.*;
import pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs.CommentBean;
import pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs.PublicationBean;
import pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs.RatingBean;
import pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs.UserBean;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Comment;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Publication;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Rating;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.User;
import pt.ipleiria.estg.dei.ei.daeproject.academics.security.Authenticated;

import java.util.List;
import java.util.Map;

@Path("publications") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
@Authenticated
public class PublicationService {
    @EJB
    private PublicationBean publicationBean;
    @EJB
    private UserBean userBean;
    @EJB
    private CommentBean commentBean;
    @EJB
    private RatingBean ratingBean;

    @GET
    @Path("/")
    public List<PublicationDTO> getAllPublications() {
        return PublicationDTO.from(publicationBean.findAll());
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
    public Response updatePublication(@PathParam("id") Integer id, PublicationDTO publicationDTO) {
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
    @Path("/{id}/visibility")
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

    //---------------------- COMMENTS --------------

    @GET
    @Path("/{id}/comments")
    public Response getPublicationComments(@PathParam("id") Integer id) {
        List<Comment> comments = publicationBean.findAllComments(id);
        if (comments == null) {

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Publication not found"))
                    .build();
        }
        List<CommentDTO> dto = CommentDTO.from(comments);
        return Response.ok(dto).build();
    }

    @POST
    @Path("/{id}/comments")
    public Response createComment(@PathParam("id") Integer id, @Context SecurityContext securityContext, CommentDTO commentDTO) {

        Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());


        try {
            CommentDTO dto = commentBean.createAndReturnDTO(commentDTO.getContent(), id, userId);
            return Response.ok(dto).build();
        } catch (EntityNotFoundException e) {
            // Map the EntityNotFoundException to a 404 Not Found or 400 Bad Request
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            // Catch any other unexpected exceptions and log them
            // Log.error("Unexpected error creating comment", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }


    @PATCH
    @Path("/{idPublication}/comments/{idComment}")
    public Response updateComment(
            @PathParam("idPublication") Integer idPublication,
            @PathParam("idComment") Integer idComment,
            CommentDTO commentDTO) {

        Comment comment = commentBean.find(idComment);
        if (comment == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Comment not found")
                    .build();
        }


        if (!comment.getPublication().getId().equals(idPublication)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Comment does not belong to this publication")
                    .build();
        }

        CommentDTO dto = commentBean.update(idComment, commentDTO.getContent());

        return Response.ok(dto).build();
    }

    @DELETE
    @Path("/{idPublication}/comments/{idComment}")
    public Response deleteComment(
            @PathParam("idPublication") Integer idPublication,
            @PathParam("idComment") Integer idComment) {

        Comment comment = commentBean.find(idComment);
        if (comment == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Comment not found")
                    .build();
        }


        if (!comment.getPublication().getId().equals(idPublication)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Comment does not belong to this publication")
                    .build();
        }


        commentBean.delete(idComment);

        return Response.noContent().build(); // 204 is better for DELETE
    }

    //-------------- Ratings ---------------
    @GET
    @Path("/{id}/rating")
    public Response getRating(@PathParam("id") Integer id) {
        Double rating = publicationBean.findPublicationRating(id);
        if (rating == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Publication not found"))
                    .build();
        }

        return Response.ok(Map.of(
                //"publicationId", id,
                "rating", Math.round(rating * 10) / 10.00 // 2 decimals
        )).build();
    }

    @POST
    @Path("/{id}/rating")
    public Response ratePublication(@PathParam("id") Integer id, @Context SecurityContext securityContext, RatingDTO ratingDTO) {
        Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());

        try {
            Rating rating = ratingBean.create(ratingDTO.getValue(), id, userId);
            RatingDTO dto = RatingDTO.from(rating);
            return Response.ok(dto).build();
        } catch (EntityNotFoundException e) {
            // Map the EntityNotFoundException to a 404 Not Found or 400 Bad Request
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            // Catch any other unexpected exceptions and log them
            // Log.error("Unexpected error creating comment", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{id}/rating")
    public Response updateRating(@PathParam("id") Integer id, @Context SecurityContext securityContext, RatingDTO ratingDTO) {
        Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());

        try {
            Rating updatedRating = ratingBean.update(ratingDTO.getValue(), id, userId);
            RatingDTO dto = RatingDTO.from(updatedRating);
            return Response.ok(dto).build();
        } catch (EntityNotFoundException e) {
            // Map the EntityNotFoundException to a 404 Not Found or 400 Bad Request
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            // Catch any other unexpected exceptions and log them
            // Log.error("Unexpected error creating comment", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}/rating")
    public Response deleteRating(@PathParam("id") Integer id,@Context SecurityContext securityContext) {
        Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());

        ratingBean.delete(id,userId);
        return Response.noContent().build();
    }

    // ------------------------------  Tags ----------------
    @POST
    @Path("/subscriptions")
    public Response addSubscription(@Context SecurityContext securityContext, TagDTO tagDTO) {
        // Get the current authenticated user's ID from the SecurityContext
        Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());

        try{
            Publication publication = publicationBean.subscribeTag(userId,tagDTO.getId());
            List<TagDTO> dto = TagDTO.from(publication.getTags());
            return Response.ok(dto).build();
        }
        catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("/subscriptions")
    public Response deleteSubscription(@Context SecurityContext securityContext, TagDTO tagDTO) {
        Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());
        try{
            Publication publication = publicationBean.unsubscribeTag(userId,tagDTO.getId());
            List<TagDTO> dto = TagDTO.from(publication.getTags());
            return Response.ok(dto).build();
        }
        catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }



}
