package controller;

import apptemplate.AppTemplate;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import data.GameAccount;
import data.GameData;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import jdk.nashorn.internal.parser.JSONParser;
import propertymanager.PropertyManager;
import ui.AppMessageDialogSingleton;
import ui.Workspace;
import ui.YesNoCancelDialogSingleton;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static settings.AppPropertyType.*;

/**
 * Created by Mendy on 11/5/2016.
 */
public class BuzzWordController implements FileController {

    private AppTemplate appTemplate; // shared reference to the application
    private GameAccount account;    // shared reference to the game being played, loaded or saved
    private GameData data;
    private GameState state;
    public int level;
    private String workPath = null;
    static Timeline timer;
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

    @Override
    public void handleSaveRequest() throws IOException {

    }

    public void createNewProfile(String name, String pw) {
        account = (GameAccount) appTemplate.getDataComponent();
        account.reset();
        account.setUser(name);
        String hashPW = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(pw.getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }

            System.out.println("original:" + pw);
            System.out.println("digested(hex):" + sb.toString());
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
        ((Workspace)appTemplate.getWorkspaceComponent()).pause();
        yesNoCancelDialog.show("Exit", "Are you sure you want to quit?");

        if (yesNoCancelDialog.getSelection().equals(YesNoCancelDialogSingleton.YES))
            System.exit(0);
        else
            ((Workspace)appTemplate.getWorkspaceComponent()).resume();

    }

    @Override
    public void handleHomeRequest() {
        Workspace gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();
        gameWorkspace.getWorkspace().getChildren().clear();
        gameWorkspace.updateHomePage();
        gameWorkspace.getWorkspace().getChildren().add(gameWorkspace.homePage);
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
        state = GameState.IN_PROGRESS;
        time = 40;
        timer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                gameWorkspace.remainingTimeLabel.setText("TIME REMAINING: "+ time + " secs");
                if (time == 0) {
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
        dialog.show("You Win!", "Ran out of time!" );
        timer.stop();

    }

    public GameAccount getAccount() {
        return account;
    }

    public GameData getData() {
        return data;
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
        success = (name.equals(user)) && (password.equals(pw));
        System.out.println(name + "  " + user);
        System.out.println(pw + "   " + password);
        System.out.println(success);
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
}