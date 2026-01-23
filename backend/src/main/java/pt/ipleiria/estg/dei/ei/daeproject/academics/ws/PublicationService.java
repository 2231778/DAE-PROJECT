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
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
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

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

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
        return publicationBean.findAllPublicationDTOs();
    }

    @GET
    @Path("/{id}")
    public Response getPublicationById(@PathParam("id") Integer id) {
        PublicationDTO publicationDTO = publicationBean.findPublicationDTO(id);

        if (publicationDTO == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Publication not found")
                    .build();
        }

        return Response.ok(publicationDTO).build();
    }

    @GET
    @Path("/my-publications")
    public Response getMyPublications(@Context SecurityContext securityContext) {
        Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());

        try{
            List<Publication> publications = publicationBean.findMyPublications(userId);
            List<PublicationDTO> dtos = PublicationDTO.from(publications);
            return Response.ok(dtos).build();
        }catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to fetch publications: " + e.getMessage())
                    .build();
        }
    }



    @POST
    @Path("/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createPublication(MultipartFormDataInput form) {
        try {
            Map<String, List<InputPart>> uploadForm = form.getFormDataMap();

            // 1. Extract Text Fields
            String title = uploadForm.get("title").get(0).getBody(String.class, null);
            String description = uploadForm.get("description").get(0).getBody(String.class, null);
            String author = uploadForm.get("author").get(0).getBody(String.class, null);
            int publisherId = Integer.parseInt(uploadForm.get("publisherId").get(0).getBody(String.class, null));

            // 2. Extract the File
            InputPart filePart = uploadForm.get("file").get(0);
            InputStream inputStream = filePart.getBody(InputStream.class, null);
            String fileName = getFileName(filePart);

            // --- FILE SAVING LOGIC START ---
            String uploadDir = "/tmp/uploads";
            java.nio.file.Path uploadPath = Paths.get(uploadDir);

            // Create directory if it doesn't exist
            if (!java.nio.file.Files.exists(uploadPath)) {
                java.nio.file.Files.createDirectories(uploadPath);
            }

            // Create the full file path
            java.nio.file.Path filePath = uploadPath.resolve(fileName);

            // Save the file (Standard Java 7+ way)
            java.nio.file.Files.copy(inputStream, filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            // --- FILE SAVING LOGIC END ---

            User publisher = userBean.find(publisherId);
            if (publisher == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Publisher not found").build();
            }

            Publication newPublication = publicationBean.create(
                    title,
                    description,
                    fileName, // Storing the filename in the DB
                    publisher,
                    author
            );

            return Response.status(Response.Status.CREATED)
                    .entity(PublicationDTO.from(newPublication))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to create publication: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("id") int id) {
        try {
            // 1. Find the publication in the database
            Publication publication = publicationBean.find(id);

            if (publication == null || publication.getFile() == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Publication or file not found")
                        .build();
            }

            // 2. Locate the file on the disk
            String fileName = publication.getFile();
            java.nio.file.Path filePath = java.nio.file.Paths.get("/tmp/uploads").resolve(fileName);

            if (!java.nio.file.Files.exists(filePath)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("File not found on server")
                        .build();
            }

            // 3. Prepare the response with the file stream
            java.io.File file = filePath.toFile();

            return Response.ok(file)
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error downloading file: " + e.getMessage())
                    .build();
        }
    }


    // Helper to extract filename from RESTEasy InputPart
    private String getFileName(InputPart part) {
        String[] contentDisposition = part.getHeaders().getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                return name[1].trim().replaceAll("\"", "");
            }
        }
        return "unknown";
    }

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updatePublication(
            @PathParam("id") Integer id,
            MultipartFormDataInput form
    ) {
        try {
            Map<String, List<InputPart>> uploadForm = form.getFormDataMap();

            // 1. Extract Text Fields
            String title = uploadForm.get("title").get(0).getBody(String.class, null);
            String description = uploadForm.get("description").get(0).getBody(String.class, null);
            String author = uploadForm.get("author").get(0).getBody(String.class, null);
            int publisherId = Integer.parseInt(uploadForm.get("publisherId").get(0).getBody(String.class, null));

            // 2. Extract File (optional)
            String newFileName = null;
            InputPart filePart = uploadForm.get("file") != null ? uploadForm.get("file").get(0) : null;

            if (filePart != null) {
                InputStream inputStream = filePart.getBody(InputStream.class, null);
                newFileName = getFileName(filePart);

                String uploadDir = "/tmp/uploads";
                java.nio.file.Path uploadPath = Paths.get(uploadDir);

                if (!java.nio.file.Files.exists(uploadPath)) {
                    java.nio.file.Files.createDirectories(uploadPath);
                }

                java.nio.file.Path filePath = uploadPath.resolve(newFileName);
                java.nio.file.Files.copy(inputStream, filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                // TODO: delete old file if needed
                Publication oldPub = publicationBean.find(id);
                if (oldPub.getFile() != null) {
                    java.nio.file.Path oldFilePath = uploadPath.resolve(oldPub.getFile());
                    java.nio.file.Files.deleteIfExists(oldFilePath);
                }
            }

            // 3. Find publisher
            User publisher = userBean.find(publisherId);
            if (publisher == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Publisher not found").build();
            }

            // 4. Update publication
            publicationBean.update(
                    id,
                    title,
                    description,
                    newFileName,
                    author
            );

            Publication updatedPublication = publicationBean.find(id);
            return Response.ok(PublicationDTO.from(updatedPublication)).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to update Publication: " + e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{id}/visibility")
    @RolesAllowed({"ADMIN","RESPONSAVEL"})
    public Response updatePublicationVisibility(@PathParam("id") Integer id) {
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

    @PATCH
    @Path("/{idPublication}/comments/{idComment}/visibility")
    public Response updateCommentVisibility(
            @PathParam("idPublication") Integer idPublication,
            @PathParam("idComment") Integer idComment,
            CommentDTO commentDTO) {
        try {
            commentBean.visibility(idComment);
            var updatedComment = commentBean.find(idComment);

            return Response.ok(CommentDTO.from(updatedComment)).build();
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
    @Path("{id}/subscriptions")
    public Response addSubscription(@PathParam("id") Integer id,@Context SecurityContext securityContext, TagDTO tagDTO) {


        try{
            Publication publication = publicationBean.subscribeTag(id,tagDTO.getId());
            List<TagDTO> dto = TagDTO.from(publication.getTags());
            return Response.ok(dto).build();
        }
        catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("{id}/subscriptions")
    @RolesAllowed({"ADMIN","RESPONSAVEL"})
    public Response deleteSubscription(@PathParam("id") Integer id,@Context SecurityContext securityContext, TagDTO tagDTO) {
        try{
            Publication publication = publicationBean.unsubscribeTag(id,tagDTO.getId());
            List<TagDTO> dto = TagDTO.from(publication.getTags());
            return Response.ok(dto).build();
        }
        catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PATCH
    @Path("/{id}/generate-ai-text")
    public Response generateAIText(@PathParam("id") Integer id) {
        try {

            Publication publication = publicationBean.generateAiTextAndWait(id);
            PublicationDTO dto = PublicationDTO.from(publication);
            return Response.ok(dto).build();
        } catch (Exception e) {

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error during AI generation: " + e.getMessage())
                    .build();
        }

    }


}
