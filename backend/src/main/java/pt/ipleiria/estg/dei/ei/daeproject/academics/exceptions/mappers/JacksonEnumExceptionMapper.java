package pt.ipleiria.estg.dei.ei.daeproject.academics.exceptions.mappers;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class JacksonEnumExceptionMapper
        implements ExceptionMapper<ValueInstantiationException> {

    @Override
    public Response toResponse(ValueInstantiationException ex) {

        Throwable cause = ex.getCause();

        if (cause instanceof IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "error", "Invalid enum value",
                            "message", cause.getMessage()
                    ))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of(
                        "error", "Invalid request body",
                        "message", ex.getMessage()
                ))
                .build();
    }
}
