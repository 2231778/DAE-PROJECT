package pt.ipleiria.estg.dei.ei.daeproject.academics.entities;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Status;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllAdmins",
                query = "SELECT c FROM Administrador c ORDER BY c.name") //JPQL
})
@DiscriminatorValue("ADMIN")
public class Administrador extends User {
    public Administrador() {
        super();
    }

    public Administrador(String name, String password, String email, String profilePicture, Status status) {
        super(name, password, email, profilePicture, status);
    }
}
