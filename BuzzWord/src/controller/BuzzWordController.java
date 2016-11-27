package controller;

import apptemplate.AppTemplate;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import data.GameAccount;
import data.GameData;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
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
    private String workPath = null;

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
        account.setPassword(pw);
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

        yesNoCancelDialog.show("Exit", "Are you sure you want to quit?");

        if (yesNoCancelDialog.getSelection().equals(YesNoCancelDialogSingleton.YES))
            System.exit(0);

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
        gameWorkspace.gamePlayScreen();
        gameWorkspace.getWorkspace().getChildren().add(gameWorkspace.gamePlayPane);
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
}