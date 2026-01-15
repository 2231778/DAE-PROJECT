package pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.RoleType;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Status;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.User;

import java.util.List;

@Startup
@Singleton
public class ConfigBean {

    @EJB
    private UserBean userBean;

    @PostConstruct
    public void populateDB() {

        try {
            userBean.create("test23", "password", "test@gmail.com","/pictures/test.png", RoleType.ADMIN);
            userBean.create("test14", "password", "test1@gmail.com","/pictures/test1.png",RoleType.RESPONSAVEL);
            User user = userBean.find(1);


            System.out.println(user);



        } catch (Exception e) {
            System.err.println("Startup user already exists or failed: " + e.getMessage());
        }
    }
}