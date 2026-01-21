package pt.ipleiria.estg.dei.ei.daeproject.academics.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.ipleiria.estg.dei.ei.daeproject.academics.dtos.PublicationDTO;
import pt.ipleiria.estg.dei.ei.daeproject.academics.dtos.TagDTO;
import pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs.TagBean;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Tag;
import pt.ipleiria.estg.dei.ei.daeproject.academics.security.Authenticated;

import java.util.List;
import java.util.NoSuchElementException;

@Path("tags") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
@Authenticated
public class TagService {
    @PersistenceContext
    private EntityManager entityManager;
    @EJB
    private TagBean tagBean;

    @GET
    @Path("/")
    public Response getAllTags() {
        try {
            List<TagDTO> tags = TagDTO.from(tagBean.findAll());
            return Response.ok(tags).build();  // 200 OK
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve tags: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/")
    @RolesAllowed({"ADMIN","RESPONSAVEL"})
    public Response addTag(TagDTO tagDTO) {
        try {
            Tag tag = tagBean.create(tagDTO.getName(), tagDTO.getDescription());
            return Response.status(Response.Status.CREATED)
                    .entity(TagDTO.from(tag))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({"ADMIN","RESPONSAVEL"})
    public Response updateTag(@PathParam("id") Integer id, TagDTO tagDTO) {
        try {
            Tag updatedTag = tagBean.update(id, tagDTO.getName(), tagDTO.getDescription());
            return Response.ok(TagDTO.from(updatedTag)).build();  // 200 OK
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to update tag: " + e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("{id}/visibility")
    @RolesAllowed({"ADMIN","RESPONSAVEL"})
    public Response updateVisibility( @PathParam("id") Integer id) {
        try {
            tagBean.toggleVisibility(id);
            var updatedTag = entityManager.find(Tag.class, id);
            return Response.ok(TagDTO.from(updatedTag)).build();
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
    @RolesAllowed({"ADMIN","RESPONSAVEL"})
    public Response deleteTagById(@PathParam("id") Integer id) {
        try {
            tagBean.delete(id);
            return Response.noContent().build(); // 204 No Content
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Tag with id " + id + " not found")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to delete tag: " + e.getMessage())
                    .build();
        }
    }



}
