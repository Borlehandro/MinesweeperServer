import api.ServerCommand;
import exceptions.NoResourceInitException;
import model.ExternalCell;
import model.Field;
import model.Pair;
import score.ScoreItem;
import score.ScoreManager;

import java.io.*;
import java.net.Socket;
import java.util.TreeSet;

public class ClientHandler extends Thread {

    private Socket socket;

    // Todo MOVE IT INTO RESOURCES

    public static final String CONSOLE_COMMANDS_INFO = "Available commands: \n"
            + "* New game <field_size> <count_mines> - launch new game with field_size * field_size field and count_mines bombs\n"
            + "* About - get information about the program\n"
            + "* Exit - exit the Minesweeper\n"
            + "* High Scores - get score table\n";

    public static final String MAIN_MENU_COMMANDS_INFO = "Available commands:\n"
            + "* Console - use Minesweeper in console mode\n"
            + "* UI - use Minesweeper with user interface\n"
            + "* About - get information about the program\n"
            + "* Exit - exit the Minesweeper\n";

    public static final String GAME_INFO = "Available commands:\n"
            + "* Check <list of x y pairs> - check (x;y) cell\n"
            + "* Flag <list of x y pairs> - set or remove flag on (x;y) cell\n"
            + "* Exit - exit the game without saving\n";

    public static final String ABOUT = "Simple minesweeper game.\nAlex Borzikov © 2020.\n";


    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        Field field = null;

        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {

            String info = "";

            while ((info = input.readLine()) != null) {
                String[] args = info.split(" ");
                switch (ServerCommand.valueOf(args[0])) {
                    case CONSOLE_HELP -> objectOutputStream.writeUTF(CONSOLE_COMMANDS_INFO);
                    case ABOUT -> objectOutputStream.writeUTF(ABOUT);
                    case MENU_HELP -> objectOutputStream.writeUTF(MAIN_MENU_COMMANDS_INFO);
                    case GAME_HELP -> objectOutputStream.writeUTF(GAME_INFO);
                    case NEW_GAME -> {
                        field = new Field(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                        objectOutputStream.writeUTF(ExternalCell.tableToJson(field.getExternalCells()));
                    }
                    case HIGH_SCORE -> {
                        TreeSet<ScoreItem> scoreItems = new ScoreManager().getScoreTable();
                        objectOutputStream.writeObject(scoreItems);
                    }
                    // Todo test
                    case CHECK -> {
                        if (field != null) {
                            if (field.check(Integer.parseInt(args[1]), Integer.parseInt(args[2]))) {

                                for (int i = 0; i < field.getSize(); ++i) {
                                    for (int j = 0; j < field.getSize(); ++j)
                                        System.err.print(field.getExternalCells()[i][j].getSymbol() + " ");
                                    System.err.println();
                                }

                                objectOutputStream.writeUTF(ExternalCell.tableToJson(field.getExternalCells()));
                            } else objectOutputStream.writeUTF("{}");
                        }
                    }
                    // Todo test
                    case SHOW_FIELD -> {
                        if (field != null) {
                            objectOutputStream.writeUTF(ExternalCell.tableToJson(field.getExternalCells()));
                        }
                    }
                    // Todo test
                    case GET_MARKS -> {
                        if (field != null) {
                            objectOutputStream.writeObject(new Pair<>(field.getMarks(), field.getMarksLimit()));
                        }
                    }
                    // Todo test
                    case IS_COMPLETED -> {
                        if (field != null)
                            objectOutputStream.writeUTF(String.valueOf(field.isCompleted()));
                    }
                }

                objectOutputStream.flush();
                // output.close();

                System.err.println("Response was sent to " + socket.getInetAddress());
            }

            // socket.close();
        } catch (IOException | NoResourceInitException e) {
            e.printStackTrace();
        }
    }

}