package controller;

import apptemplate.AppTemplate;
import data.GameData;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import ui.Workspace;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Mendy on 11/11/2016.
 */
public class BuzzWordControllerHomeScreen implements FileController {
    private AppTemplate appTemplate; // shared reference to the application
    private GameData gamedata;    // shared reference to the game being played, loaded or saved
    private Text[]      progress;    // reference to the text area for the word
    private boolean     success;     // whether or not player was successful
    private int         discovered;  // the number of letters already discovered
    private Button gameButton;  // shared reference to the "start game" button
    private Label remains;     // dynamically updated label that indicates the number of remaining guesses
    private boolean     gameover;    // whether or not the current game is already over
    private boolean     savable;
    private Path workFile;

    public BuzzWordControllerHomeScreen(AppTemplate appTemplate, Button gameButton) {
        this(appTemplate);
        this.gameButton = gameButton;
    }

    public BuzzWordControllerHomeScreen (AppTemplate appTemplate) {
        this.appTemplate = appTemplate;
    }

    @Override
    public void handleNewRequest() {
        System.out.println("Homescreen");
        Workspace gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();
        gameWorkspace.activateWorkspace(appTemplate.getGUI().getAppPane());
        gameWorkspace.getWorkspace().getChildren().clear();
        gameWorkspace.updateHomePage();
        gameWorkspace.getWorkspace().getChildren().add(gameWorkspace.homePage);
    }

    @Override
    public void handleSaveRequest() throws IOException {
    }

    @Override
    public void handleLoadRequest() throws IOException {
    }

    @Override
    public void handleExitRequest(){
    }
}