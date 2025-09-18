package app.rest;

import app.dtos.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.*;


public class Routes
{
    // declare static controllers here
    private static Logger logger = LoggerFactory.getLogger(Routes.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static EndpointGroup getRoutes(EntityManagerFactory emf)
    {
        // instantiate controllers with emf here

        return () -> {
            path("", () -> // write path name here
            {
                get("/",ctx -> { // write get path here
                    // write get logic here
                    ctx.json("test");
                });
                // write other http methods here
            });
        };
    }
}