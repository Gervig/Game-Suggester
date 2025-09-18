package app.security.daos;

import app.security.entities.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class RoleDAO
{
    private static EntityManagerFactory emf;
    private static RoleDAO instance;

    // singleton **
    public RoleDAO()
    {
    }

    public static RoleDAO getInstance(EntityManagerFactory _emf)
    {
        if(emf == null)
        {
            emf = _emf;
            instance = new RoleDAO();
        }
        return instance;
    }
    // ** singleton


    public Role createRole(Role role){
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.persist(role);
            em.getTransaction().commit();
            return role;
        }
    }

}
