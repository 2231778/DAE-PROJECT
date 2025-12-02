package pt.ipleiria.estg.dei.ei.daeproject.academics.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Status;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllColaboradores",
                query = "SELECT c FROM Colaborador c ORDER BY c.name") //JPQL
})
@DiscriminatorValue("COLABORADOR")
public class Colaborador extends User {
    public Colaborador() {
        super();
    }
    public Colaborador(String name, String password, String email, String profilePicture, Status status) {
        super(name, password, email, profilePicture, status);
    }
}
