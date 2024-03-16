package clientTests;

import exception.ServiceLogicException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;
import Exception.ClientException;

import java.util.List;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    @BeforeAll
    public static void init() throws ServiceLogicException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port +  "/");
    }

    @BeforeEach
    public void setup() throws ServiceLogicException {
        server.adminService.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerUser() throws ClientException {
        UserData user = new UserData("jax", "12345", "email");
        String result = serverFacade.registerUser(user);
        Assertions.assertEquals(result, "Registered!");
    }

    @Test
    public void registerAlreadyUser() throws ClientException {
        UserData user = new UserData("jax", "12345", "email");
        serverFacade.registerUser(user);
        Assertions.assertThrows(ClientException.class, () -> {
            serverFacade.registerUser(user);
        });
    }

    @Test
    public void loginUser() throws ClientException {
        UserData user = new UserData("jax", "12345", "email");
        serverFacade.registerUser(user);

        LoginRequest request = new LoginRequest("jax", "12345");

        String result = serverFacade.loginUser(request);

        Assertions.assertEquals("Logged in!", result);
    }

    @Test
    public void loginUserBadPassword() throws ClientException {
        LoginRequest request = new LoginRequest("jax", "12345");
        Assertions.assertThrows(ClientException.class, () -> {
            serverFacade.loginUser(request);
        });
    }

    @Test
    public void logoutUser() throws ClientException {
        UserData user = new UserData("jax", "12345", "email");
        serverFacade.registerUser(user);

        LoginRequest request = new LoginRequest("jax", "12345");
        serverFacade.loginUser(request);

        String result = serverFacade.logoutUser();

        Assertions.assertEquals("Logged out!", result);
    }

    @Test
    public void logoutInvalidUser() throws ClientException {
        UserData user = new UserData("jax", "12345", "email");
        serverFacade.registerUser(user);

        LoginRequest request = new LoginRequest("jax", "12345");
        serverFacade.loginUser(request);
        serverFacade.logoutUser();

        String result = serverFacade.logoutUser();

        Assertions.assertEquals("Unauthorized", result);
    }

    @Test
    public void createGame() throws ClientException {
        UserData user = new UserData("jax", "12345", "email");
        serverFacade.registerUser(user);

        LoginRequest request = new LoginRequest("jax", "12345");
        serverFacade.loginUser(request);

        CreateGameRequest req = new CreateGameRequest("Game 1");

        String result = serverFacade.createGame(req);
        Assertions.assertEquals("Game created!", result);
    }

    @Test
    public void createGameInvalidData() throws ClientException {
        UserData user = new UserData("jax", "12345", "email");
        serverFacade.registerUser(user);

        LoginRequest request = new LoginRequest("jax", "12345");
        serverFacade.loginUser(request);

        CreateGameRequest req = new CreateGameRequest(null);

        Assertions.assertThrows(ClientException.class, () -> {
            serverFacade.createGame(req);
        });
    }

    @Test
    public void listGames() throws ClientException {
        UserData user = new UserData("jax", "12345", "email");
        serverFacade.registerUser(user);

        LoginRequest request = new LoginRequest("jax", "12345");
        serverFacade.loginUser(request);
        serverFacade.createGame(new CreateGameRequest("Game 1"));
        serverFacade.createGame(new CreateGameRequest("Game 2"));
        serverFacade.createGame(new CreateGameRequest("Game 3"));

        List<GameListResult> res = serverFacade.listGames();

        Assertions.assertEquals(3, res.size());
    }

    @Test
    public void listNoGames() throws ClientException {
        UserData user = new UserData("jax", "12345", "email");
        serverFacade.registerUser(user);

        LoginRequest request = new LoginRequest("jax", "12345");
        serverFacade.loginUser(request);

        List<GameListResult> res = serverFacade.listGames();

        Assertions.assertEquals(0, res.size());
    }

    @Test
    public void joinGame() throws ClientException {
        UserData user = new UserData("jax", "12345", "email");
        serverFacade.registerUser(user);

        LoginRequest request = new LoginRequest("jax", "12345");
        serverFacade.loginUser(request);
        serverFacade.createGame(new CreateGameRequest("Game 1"));

        String res = serverFacade.joinGame(new JoinGameRequest("WHITE", 1));
        Assertions.assertEquals("Joined game!", res);
    }

    @Test
    public void joinInvalidGame() throws ClientException {
        UserData user = new UserData("jax", "12345", "email");
        serverFacade.registerUser(user);

        LoginRequest request = new LoginRequest("jax", "12345");
        serverFacade.loginUser(request);
        serverFacade.createGame(new CreateGameRequest("Game 1"));

        Assertions.assertThrows(ClientException.class, () -> {
            serverFacade.joinGame(new JoinGameRequest("WHITE", 2));
        });
    }



}