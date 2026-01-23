package pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.ActionType;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.RoleType;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Publication;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Rating;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Tag;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.User;




@Startup
@Singleton
public class ConfigBean {

    @EJB
    private UserBean userBean;
    @EJB
    private PublicationBean publicationBean;
    @EJB
    private ActivityLogBean activityLogBean;
    @EJB
    private CommentBean commentBean;
    @EJB
    private TagBean tagBean;

    @EJB
    private RatingBean ratingBean;

    @PostConstruct
    public void populateDB() {

        try {
            userBean.create("test23", "password", "test@gmail.com","/pictures/test.png", RoleType.ADMIN);
            userBean.create("test14", "password", "test1@gmail.com","/pictures/test1.png",RoleType.RESPONSAVEL);
            User user = userBean.find(1);

            userBean.create("publisher1", "password", "test2@gmail.com","/pictures/test2.png", RoleType.ADMIN);
            userBean.create("publisher2", "password", "test3@gmail.com","/pictures/test3.png", RoleType.RESPONSAVEL);


            User publisher1 = userBean.find(3);
            User publisher2 = userBean.find(4);

            Publication pub1 = publicationBean.create("3D Modeling Basics",
                    "Learn the fundamentals of 3D modeling using Blender and SketchUp.",
                    "3d_modeling.pdf",
                    publisher1,
                    "Alice Johnson");

            Publication pub2 = publicationBean.create("Introduction to AI",
                    "A beginner-friendly guide to Artificial Intelligence concepts.",
                    "ai_intro.pdf",
                    publisher1,
                    "John Doe");

            publicationBean.create("Advanced Java Patterns",
                    "Deep dive into design patterns for enterprise Java applications.",
                    "java_patterns.pdf",
                    publisher2,
                    "Jane Smith");

            publicationBean.create("Data Structures and Algorithms",
                    "Essential data structures and algorithm techniques explained clearly.",
                    "ds_algo.pdf",
                    publisher2,
                    "Bob Brown");
            // Create some activity logs

            activityLogBean.create(ActionType.CREATE, "Created publication 1", publisher1, pub1);
            activityLogBean.create(ActionType.CREATE, "Created publication 2", publisher2, pub2);
            activityLogBean.create(ActionType.UPDATE, "Updated publication 1", publisher1, pub1);
            activityLogBean.create(ActionType.DELETE, "Deleted a comment on publication 2", publisher2);


            activityLogBean.create(ActionType.CREATE, "Created publication 1", user, pub1);
            activityLogBean.create(ActionType.CREATE, "Created publication 2", user, pub2);

            // ------------------------
            // Comments creation
            // ------------------------
            // Add some comments to pub1 and pub2
            commentBean.create("This is a great introduction to 3D modeling!", pub1, user);
            commentBean.create("Thanks for the Blender tips!", pub1, publisher2);

            commentBean.create("AI concepts explained clearly.", pub2, user);
            commentBean.create("Looking forward to more AI tutorials.", pub2, publisher1);

            // --- Tags ---
            Tag tag3D = tagBean.create("3D Modeling", "All publications related to 3D modeling.");
            Tag tagAI = tagBean.create("Artificial Intelligence", "Publications about AI concepts and applications.");
            Tag tagJava = tagBean.create("Java", "Publications on Java programming, patterns, and best practices.");
            Tag tagDS = tagBean.create("Data Structures", "Posts about algorithms, data structures, and problem-solving.");

            publicationBean.subscribeTag(pub1.getId(), tag3D.getId());
            publicationBean.subscribeTag(pub2.getId(), tagAI.getId());
            publicationBean.subscribeTag(pub2.getId(), tagJava.getId());

            userBean.subscribeTag(user.getId(), tagAI.getId());
            userBean.subscribeTag(user.getId(), tag3D.getId());

            userBean.subscribeTag(publisher1.getId(), tagJava.getId());
            userBean.subscribeTag(publisher2.getId(), tagDS.getId());



            // --- Ratings ---
            // user rates pub1 and pub2
            ratingBean.create(5.0, pub1.getId(), user.getId());
            ratingBean.create(4.0, pub2.getId(), user.getId());

            // publisher1 rates pub1 and pub2
            ratingBean.create(4.0, pub1.getId(), publisher1.getId());
            ratingBean.create(5.0, pub2.getId(), publisher1.getId());

            // publisher2 rates pub1 and pub2
            ratingBean.create(3.0, pub1.getId(), publisher2.getId());
            ratingBean.create(4.0, pub2.getId(), publisher2.getId());

        } catch (Exception e) {
            System.err.println("Startup user already exists or failed: " + e.getMessage());
        }
    }


}