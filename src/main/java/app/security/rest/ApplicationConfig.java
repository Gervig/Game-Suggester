package app.security.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.config.JavalinConfig;

import static io.javalin.apibuilder.ApiBuilder.path;

public class ApplicationConfig
{
    private static ApplicationConfig applicationConfig;
    private static Javalin app;
    private static JavalinConfig javalinConfig;
    private ObjectMapper objectMapper = new ObjectMapper();
    private ISecurityController securityController = new SecurityController();

    // singleton, private constructor
    private ApplicationConfig()
    {
    }

    public static ApplicationConfig getInstance()
    {
        if (applicationConfig == null)
        {
            applicationConfig = new ApplicationConfig();
        }
        return applicationConfig;
    }

    public ApplicationConfig initiateServer()
    {
        app = Javalin.create(config ->
        {
            // saves the config to a variable so we can add to it later
            javalinConfig = config;
            // sets the default content to json
            config.http.defaultContentType = "application/json";
            // "/api" will be prefixed to all routes
            config.router.contextPath = "/api";
            config.bundledPlugins.enableRouteOverview("/routes");
            config.bundledPlugins.enableDevLogging();
        });
        return applicationConfig;
    }

    public ApplicationConfig setRoute(EndpointGroup route)
    {
        javalinConfig.router.apiBuilder(() ->
        {
            path("/", route);
        });
        return applicationConfig;
    }

    public ApplicationConfig securityCheck()
    {
        app.beforeMatched(securityController.authenticate());
        app.beforeMatched(securityController.authorize());
        return applicationConfig;
    }

    public ApplicationConfig startServer(int port)
    {
        app.start(port);
        return applicationConfig;
    }

    public ApplicationConfig handleException()
    {
        app.exception(Exception.class, (e, ctx) ->
        {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("msg", e.getMessage());
            ctx.status(500);
            ctx.json(node);
        });
        return applicationConfig;
    }

    public static void stopServer()
    {
        app.stop();
        app = null;
    }
}
