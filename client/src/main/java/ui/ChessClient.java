package ui;

import client.ServerFacade;
import model.GameData;
import java.util.Arrays;

import static ui.EscapeSequences.*;

public class ChessClient {
    private String username = null;
    private String authToken = null;
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;
    private GameData[] games = null;

    private enum State {
        LOGGED_OUT,
        LOGGED_IN
    }

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess. Type 'help' to get started.");
        System.out.print(help());

        java.util.Scanner scanner = new java.util.Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result + RESET_TEXT_COLOR);
            }
            catch (Throwable e) {
                var msg = e.getMessage();
                System.out.print(SET_TEXT_COLOR_RED + msg + RESET_TEXT_COLOR);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "quit" -> "quit";
                case "login" -> login(params);
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);

                default -> help();
            };
        }
        catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        if (state == State.LOGGED_OUT) {
            return """
                    - register <username> <password> <email>
                    - login <username> <password>
                    - quit
                    - help
                    """;
        }
        return """
                - create <game name>
                - list
                - join <game number> <WHITE|BLACK>
                - observe <game number>
                - logout
                - quit
                - help
                """;
    }

    private void assertLoggedIn() throws Exception {
        if (state == State.LOGGED_OUT) {
            throw new Exception("You must log in first");
        }
    }

    public String register(String... params) throws Exception {
        if (params.length == 3) {
            var authData = server.register(params[0], params[1], params[2]);
            authToken = authData.authToken();
            username = authData.username();
            state = State.LOGGED_IN;
            return String.format("Registered and logged in as %s.%n", username);
        }
        throw new Exception("Expected: <username> <password> <email>");
    }

    public String login(String... params) throws Exception {
        if (params.length == 2) {
            var authData = server.login(params[0], params[1]);
            authToken = authData.authToken();
            username = authData.username();
            state = State.LOGGED_IN;
            return String.format("Logged in as %s.%n", username);
        }
        throw new Exception("Expected: <username> <password>");
    }

    public String logout() throws Exception {
        assertLoggedIn();
        server.logout(authToken);
        state = State.LOGGED_OUT;
        authToken = null;
        username = null;
        return String.format("Logged out successfully.%n");
    }

    public String createGame(String... params) throws Exception {
        assertLoggedIn();
        if (params.length >= 1) {
            String gameName = String.join(" ", params);
            var result = server.createGame(authToken, gameName);
            return String.format("Created game: %s%n", gameName);
        }
        throw new Exception("Expected: <game name>");
    }

    public String listGames() throws Exception {
        assertLoggedIn();
        var result = server.listGames(authToken);
        games = result.games();

        var output = new StringBuilder();
        for (int i = 0; i < games.length; i++) {
            var game = games[i];
            output.append(String.format("%d. %s | White: %s | Black: %s%n",
                    i + 1,
                    game.gameName(),
                    game.whiteUsername() != null ? game.whiteUsername() : "(empty)",
                    game.blackUsername() != null ? game.blackUsername() : "(empty)"));
        }
        return output.toString();
    }

    public String joinGame(String... params) throws Exception {
        assertLoggedIn();
        if (params.length == 2) {
            int gameNumber = Integer.parseInt(params[0]);
            String color = params[1].toUpperCase();

            if (games == null || gameNumber < 1 || gameNumber > games.length) {
                throw new Exception("Invalid game number. Use 'list' first.");
            }

            var game = games[gameNumber - 1];
            server.joinGame(authToken, game.gameID(), color);

            return String.format("Joined game as %s%n%s", color, drawBoard(color));
        }
        throw new Exception("Expected: <game number> <WHITE|BLACK>");
    }

    public String observeGame(String... params) throws Exception {
        assertLoggedIn();
        if (params.length == 1) {
            int gameNumber = Integer.parseInt(params[0]);

            if (games == null || gameNumber < 1 || gameNumber > games.length) {
                throw new Exception("Invalid game number. Use 'list' first.");
            }

            var game = games[gameNumber - 1];
            server.joinGame(authToken, game.gameID(), null);

            return String.format("Observing game%n%s", drawBoard("WHITE"));
        }
        throw new Exception("Expected: <game number>");
    }

    private String drawBoard(String perspective) {
        var drawer = new BoardDrawer();
        if (perspective.equals("BLACK")) {
            return drawer.drawBlackPerspective();
        }
        return drawer.drawWhitePerspective();
    }
}
