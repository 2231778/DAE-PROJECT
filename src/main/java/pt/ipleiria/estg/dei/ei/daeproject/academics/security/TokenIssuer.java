package pt.ipleiria.estg.dei.ei.daeproject.academics.security;

import io.jsonwebtoken.Jwts;
import jakarta.persistence.criteria.CriteriaBuilder;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TokenIssuer {
    protected static final byte[] SECRET_KEY =
            "veRysup3rstr0nginv1ncible5ecretkeY@academics.dae.ipleiria".getBytes();
    protected static final String ALGORITHM = "HMACSHA384";
    public static final long EXPIRY_MINS = 60L;

    public static String issue(String email) {
        var expiryPeriod = LocalDateTime.now().plusMinutes(EXPIRY_MINS);
        var expirationDateTime = Date.from(
                expiryPeriod.atZone(ZoneId.systemDefault()).toInstant()
        );
        Key key = new SecretKeySpec(SECRET_KEY, ALGORITHM);
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(expirationDateTime)
                .signWith(key)
                .compact();
    }
}