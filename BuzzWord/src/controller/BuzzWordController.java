package controller;

import BuzzWord.GameMode;
import apptemplate.AppTemplate;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import data.GameAccount;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import data.GameData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import ui.AppMessageDialogSingleton;
import ui.Workspace;
import ui.YesNoCancelDialogSingleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Mendy on 11/5/2016.
 */
public class BuzzWordController implements FileController {
    private AppTemplate appTemplate; // shared reference to the application
    private GameAccount account;    // shared reference to the game being played, loaded or saved
    private GameMode mode;
    private GameState state;
    Workspace gameWorkspace;
    public GridGenerator gridGenerator = new GridGenerator(this);
    public HashSet<String> solution = new HashSet<>();
    public int level;
    private String workPath = null;
    static Timeline timer;
    boolean success;
    boolean personalBest = false;
    int score = 0;
    int target = 0;
    int time = 30;

    public BuzzWordController(AppTemplate appTemplate) {
        account = (GameAccount) appTemplate.getDataComponent();
        this.appTemplate = appTemplate;
        state = GameState.NOT_STARTED;
    }

    @Override
    public void handleNewRequest() {
        System.out.println("it works!");
        Workspace gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();
        gameWorkspace.activateWorkspace(appTemplate.getGUI().getAppPane());
    }

    public Workspace getGameWorkspace(){
        return (Workspace) appTemplate.getWorkspaceComponent();
    }
    @Override
    public void handleSaveRequest() throws IOException {

    }

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

    @Override
    public void handleLoadRequest() throws IOException {
        appTemplate.getFileComponent().loadData(account, Paths.get(workPath));
    }

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

    @Override
    public void handleLevelSect() {
        Workspace gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();
        gameWorkspace.getWorkspace().getChildren().clear();
        gameWorkspace.loggedIn = true;

        gameWorkspace.updateHomePage();
        gameWorkspace.updateLvlSelect();
        gameWorkspace.getWorkspace().getChildren().add(gameWorkspace.levelSelectPage);
    }

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

    public void setMode(String mode){
        this.mode = GameMode.valueOf(mode);
    }

    public GameAccount getAccount() {
        return account;
    }

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
               // System.out.println(jsonParser.getValueAsString());
            }
        } catch (JsonParseException e) {
            //e.printStackTrace();
        } catch (Exception ex) {
            //ex.printStackTrace();
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

    public void pauseTimer() {
        timer.pause();
    }

    public void resumeTimer() {
        timer.play();
    }

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

    public void restart(){
        timer.stop();
        time = 40 + level;
        score = 0;
        handleHomeRequest();
        handleGame();
//        gridGenerator.checkWords(mode.name());
//        gameWorkspace.scoreLabel.setText("Total: " + score + "pts");
//        gameWorkspace.allGuessedWords.getChildren().clear();
    }
}