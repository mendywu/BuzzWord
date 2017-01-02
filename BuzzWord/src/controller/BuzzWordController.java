package controller;

import BuzzWord.GameMode;
import apptemplate.AppTemplate;
import com.fasterxml.jackson.core.*;
import javafx.animation.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import javafx.event.*;
import ui.*;
import data.*;
import javafx.scene.control.Label;
import javafx.util.Duration;
import java.io.IOException;

/**
 * Controls most of the actions in game.
 * Created by Mendy on 11/5/2016.
 */
public class BuzzWordController implements FileController {
    private AppTemplate     appTemplate;    // shared reference to the application
    private GameAccount     account;        // shared reference to the game being played, loaded or saved
    private GameMode        mode;           // reference to game mode being played
    private GameState       state;          // state of the game
    private Workspace       gameWorkspace;  // shared reference to interface of the game
    private String          workPath;       // current path of game
    static Timeline         timer;          // current game thread
    public int              level;          // current level of the game
    boolean                 success;        // if player passed the level
    boolean                 personalBest;   // if player beat their personal best
    int                     score = 0;      // current score
    int                     target = 0;     // target score required to pass
    int                     time = 30;      // time given to play
    public GridGenerator gridGenerator = new GridGenerator(this); // generates a 4x4 grid for gameplay
    public HashSet<String> solution = new HashSet<>();            // the full solution set for current grid

    public BuzzWordController(AppTemplate appTemplate) {
        account = (GameAccount) appTemplate.getDataComponent();
        this.appTemplate = appTemplate;
        state = GameState.NOT_STARTED;
        personalBest = false;
        workPath = null;
    }

    /**
     * Creates a new profile and saves it
     * @param name name of the user
     * @param pw password of the user
     */
    public void createNewProfile(String name, String pw) {
        account = (GameAccount) appTemplate.getDataComponent();
        account.reset();
        account.setUser(name);
        account.length = pw.length();
        String hashPW = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(pw.getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            hashPW = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        account.setPassword(hashPW);
        workPath = "C:\\Users\\Mendy\\Desktop\\BuzzWordProject\\BuzzWord\\saved";
        try {
            appTemplate.getFileComponent().saveData(account, Paths.get(workPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates a player's creditionals and saves it
     * @param name the new name of the user
     * @param pw   the new password of the user
     */
    public void updateProfile (String name, String pw){
        String hashPW = "";
        account.setUser(name);
        account.length = pw.length();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(pw.getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            hashPW = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        account.setPassword(hashPW);
        workPath = "C:\\Users\\Mendy\\Desktop\\BuzzWordProject\\BuzzWord\\saved";
        try {
            Path file = Paths.get(workPath + "\\" + name + ".json");
            file.toFile().delete();
            appTemplate.getFileComponent().saveData(account, Paths.get(workPath));
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show("Updated", "Updated Profile!" );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ensures a user wants to quit and saves his/her progress
     */
    @Override
    public void handleExitRequest() {
        YesNoCancelDialogSingleton yesNoCancelDialog = YesNoCancelDialogSingleton.getSingleton();
        if (state == GameState.IN_PROGRESS)
            ((Workspace)appTemplate.getWorkspaceComponent()).pause();
        yesNoCancelDialog.show("Exit", "Are you sure you want to quit?");


        if (yesNoCancelDialog.getSelection().equals(YesNoCancelDialogSingleton.YES)) {
            try {
                workPath = "C:\\Users\\Mendy\\Desktop\\BuzzWordProject\\BuzzWord\\saved";
                Path file = Paths.get(workPath + "\\" + account.getName() + ".json");
                file.toFile().delete();
                appTemplate.getFileComponent().saveData(account, Paths.get(workPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        } else if (state == GameState.IN_PROGRESS)
            ((Workspace)appTemplate.getWorkspaceComponent()).resume();

    }

    /**
     * return home request
     */
    @Override
    public void handleHomeRequest() {
        YesNoCancelDialogSingleton yesNoCancelDialog = YesNoCancelDialogSingleton.getSingleton();
        if (state == GameState.IN_PROGRESS) {
            ((Workspace) appTemplate.getWorkspaceComponent()).pause();
            yesNoCancelDialog.show("Exit", "Leave game? Your progress will not be saved.");

            if (yesNoCancelDialog.getSelection().equals(YesNoCancelDialogSingleton.YES)) {
                Workspace gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();
                gameWorkspace.getWorkspace().getChildren().clear();
                gameWorkspace.updateHomePage();
                if (state == GameState.IN_PROGRESS)
                    timer.stop();
                gameWorkspace.getWorkspace().getChildren().add(gameWorkspace.homePage);
            } else if (state == GameState.IN_PROGRESS)
                ((Workspace)appTemplate.getWorkspaceComponent()).resume();
        } else {
            Workspace gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();
            gameWorkspace.getWorkspace().getChildren().clear();
            gameWorkspace.updateHomePage();
            gameWorkspace.getWorkspace().getChildren().add(gameWorkspace.homePage);
        }
    }

    /**
     * Handles a request to go to the level select page
     */
    @Override
    public void handleLevelSect() {
        Workspace gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();
        gameWorkspace.getWorkspace().getChildren().clear();
        gameWorkspace.loggedIn = true;

        gameWorkspace.updateHomePage();
        gameWorkspace.updateLvlSelect();
        gameWorkspace.getWorkspace().getChildren().add(gameWorkspace.levelSelectPage);
    }

    /**
     * start game method, for when the player starts playing the game
     */
    @Override
    public void handleGame() {
        Workspace gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();
        gameWorkspace.getWorkspace().getChildren().clear();
        gameWorkspace.loggedIn = true;

        gameWorkspace.updateHomePage();
        gameWorkspace.updateLvlSelect();
        gameWorkspace.gamePlayScreen();

        mode = GameMode.valueOf(gameWorkspace.modeLabel.getText());
        state = GameState.IN_PROGRESS;
        target = (level+2)*30;
        time = 40 + level;

        gameWorkspace.nextLevelButton.setVisible(false);
        gameWorkspace.pauseResumeButton.setDisable(false);
        solution = gridGenerator.ans;
        score = 0;
        timer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                gameWorkspace.remainingTimeLabel.setText("TIME REMAINING: "+ time + " secs");
                if (time == 0) {
                    timer.stop();
                    stopTimer();
                }
                else
                    time--;
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
        gameWorkspace.showGamePlay();
    }

    /**
     * stops the timer, ends the game and records the player's status
     */
    public void stopTimer(){
        AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
        GameData data = account.getModeData(mode);
        success = score >= target;
        System.out.println(success);
        state = success ? GameState.WIN : GameState.LOSS;
        if (level != 4) {
            if (success && !data.getUnlocked_levels()[level])
            data.unlockNext();
        }
        if (data.getPersonal_bests()[level-1] < score) {
            personalBest = true;
            data.newPersonalBest(score, level-1);
        }
        try {
            workPath = "C:\\Users\\Mendy\\Desktop\\BuzzWordProject\\BuzzWord\\saved";
            Path file = Paths.get(workPath + "\\" + account.getName() + ".json");
            file.toFile().delete();
            appTemplate.getFileComponent().saveData(account, Paths.get(workPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (solution.size() > 0) {
            Iterator<String> it = solution.iterator();
            while (it.hasNext()){
                String w = it.next();
                w = w.toUpperCase();
                Label a = new Label(w + "  " + w.length()*10);
                a.setStyle("-fx-text-fill: red");
                gameWorkspace.allGuessedWords.getChildren().add(a);
            }
        }
        gameWorkspace.pauseResumeButton.setDisable(true);
        gameWorkspace.disableAll();
        if (level != 4 && success)
            gameWorkspace.nextLevelButton.setVisible(true);
        if (state == GameState.WIN && personalBest) {
            dialog.show("You Win!", "Congrats, you pass the level with a new personal best of " + score + "!");
        } else if(state == GameState.WIN && !personalBest) {
            dialog.show("You Win!", "Congrats, you pass the level!");
        }
        if (state == GameState.LOSS)
            dialog.show("You Lose!", "Better luck next time!");
        state = GameState.NOT_STARTED;
    }

    /**
     * checks the creditionals of a user and logs him or her in
     * @param name the name of the user
     * @param pw the password of the user
     * @return true if successul
     */
    public boolean logIn(String name, String pw) {
        String path = "C:\\Users\\Mendy\\Desktop\\BuzzWordProject\\BuzzWord\\saved\\" + name + ".json";
        String user = "", password = "";
        boolean success = false;
        JsonFactory jsonFactory = new JsonFactory();
        try (JsonParser jsonParser = jsonFactory.createParser(Files.newInputStream(Paths.get(path)))) {
            while (!jsonParser.isClosed()) {
                JsonToken token = jsonParser.nextToken();
                if (JsonToken.FIELD_NAME.equals(token)) {
                    String fieldname = jsonParser.getCurrentName();
                    switch (fieldname) {
                        case "USER_NAME":
                            jsonParser.nextToken();
                            user = (jsonParser.getValueAsString());
                            break;
                        case "PASSWORD":
                            jsonParser.nextToken();
                            password = jsonParser.getValueAsString();
                            break;
                    }
                }
            }
        } catch (JsonParseException e) {
        } catch (Exception ex) {
        }
        MessageDigest md = null;
        String hashPW = "";
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(pw.getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            hashPW = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
       success = (name.equals(user)) && (password.equals(hashPW));
        if (success){
            workPath = path;
            try {
                handleLoadRequest();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show("Error", "Invalid credentials: Please check your user name or password again!" );
        }
        return success;
    }

    /**
     * Loads a profile, called through the login method
     * @throws IOException
     *      if no such account exists
     */
    @Override
    public void handleLoadRequest() throws IOException {
        appTemplate.getFileComponent().loadData(account, Paths.get(workPath));
    }

    /**
     * pauses the timer/game
     */
    public void pauseTimer() {
        timer.pause();
    }

    /**
     * resumes the timer/game
     */
    public void resumeTimer() {
        timer.play();
    }

    /**
     * checks if the word is valid within the game
     * @param word the word that is selected
     * @return true if the word is valid
     */
    public boolean isValidWord(String word){
        if (solution.contains(word)) {
            int add = word.length() *10;
            score += add;
            solution.remove(word);
            gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();
            gameWorkspace.scoreLabel.setText("Total: "+ score + " pts");
            System.out.println(solution);
            return true;
        }
        return false;
    }

    @Override
    public void handleNewRequest() {
        Workspace gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();
        gameWorkspace.activateWorkspace(appTemplate.getGUI().getAppPane());
    }

    @Override
    public void handleSaveRequest() throws IOException {
    }

    /**
     * restarts a game
     */
    public void restart(){
        timer.stop();
        time = 40 + level;
        score = 0;
        handleHomeRequest();
        handleGame();
    }

    /**
     * set the mode of the game
     * @param mode
     *      the mode of the game
     */
    public void setMode(String mode){
        this.mode = GameMode.valueOf(mode);
    }

    /**
     * returns the current account
     * @return current account
     */
    public GameAccount getAccount() {
        return account;
    }

}