package pt.ipleiria.estg.dei.ei.daeproject.academics.security;


import io.jsonwebtoken.Jwts;
import jakarta.annotation.Priority;
import jakarta.ejb.EJB;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;
import pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs.UserBean;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Administrador;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Colaborador;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Responsavel;

import javax.crypto.spec.SecretKeySpec;
import java.security.Principal;

@Provider
@Authenticated
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    @EJB
    private UserBean userBean;
    @Context
    private UriInfo uriInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        var header = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            throw new
                    NotAuthorizedException("Authorization header must be provided");
        }
        // Get token from the HTTP Authorization header
        String token = header.substring("Bearer".length()).trim();
        var user = userBean.findByEmail(getEmail(token));
        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return () -> user.getId().toString(); // always return a String
            }

            @Override
            public boolean isUserInRole(String role) {
                if (user instanceof Administrador && "ADMIN".equals(role)) return true;
                if (user instanceof Colaborador && "COLABORADOR".equals(role)) return true;
                if (user instanceof Responsavel && "RESPONSAVEL".equals(role)) return true;
                return false;
            }

            @Override
            public boolean isSecure() {
                return uriInfo.getAbsolutePath().toString().startsWith("https");
            }

            @Override
            public String getAuthenticationScheme() {
                return "Bearer";
            }
        });
    }

    private String getEmail(String token) {
        var key = new SecretKeySpec(TokenIssuer.SECRET_KEY, TokenIssuer.ALGORITHM);
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject(); // this is now the email
        } catch (Exception e) {
            throw new NotAuthorizedException("Invalid JWT");
        }
    }
}