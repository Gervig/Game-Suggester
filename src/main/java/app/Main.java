package app;

import app.config.HibernateConfig;
import app.populators.RolePopulator;
import app.populators.UserPopulator;
import app.rest.ApplicationConfig;
import app.rest.Routes;
import app.security.daos.RoleDAO;
import app.security.daos.UserDAO;
import app.security.entities.Role;
import app.security.entities.User;
import app.security.rest.SecurityRoutes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        setupDatabase(emf);

        ApplicationConfig
                .getInstance()
                .initiateServer()
                .securityCheck()
                .setRoute(Routes.getRoutes(emf))
                .setRoute(SecurityRoutes.getRoutes(emf))
                .handleException()
                .startServer(7070); //TODO change this to an available port for deployment

    }

    private static void setupDatabase(EntityManagerFactory emf)
    {
        try(EntityManager em = emf.createEntityManager())
        {
            // populates the database with roles
            List<Role> roles = RolePopulator.populate();
            RoleDAO roleDAO = RoleDAO.getInstance(emf);
            roles.forEach(roleDAO::createRole);

            // populates the database with users
            List<User> users = UserPopulator.populate();
            UserDAO userDAO = UserDAO.getInstance(emf);
            users.forEach(userDAO::create);
        }
    }

}
