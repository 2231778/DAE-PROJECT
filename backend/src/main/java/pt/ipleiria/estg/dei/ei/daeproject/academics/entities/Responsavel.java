package pt.ipleiria.estg.dei.ei.daeproject.academics.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Status;

@Entity
@NamedQueries({
@NamedQuery(
        name = "getAllResponsaveis",
        query = "SELECT r FROM Responsavel r ORDER BY r.name") //JPQL
})
@DiscriminatorValue("RESPONSAVEL")
public class Responsavel extends User{

    public Responsavel() {
        super();
    }
    public Responsavel(String name, String password, String email, String profilePicture, Status status) {
        super(name, password, email, profilePicture, status);
    }
}
