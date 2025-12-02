package pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Status;

@Startup
@Singleton
public class ConfigBean {

    @EJB
    private UserBean userBean;

    @PostConstruct
    public void populateDB() {
        userBean.create("test23", "password", "test@gmail.com","/pictures/test.png", Status.Active);
    }
}