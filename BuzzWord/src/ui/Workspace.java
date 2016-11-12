package ui;

import apptemplate.AppTemplate;
import components.AppWorkspaceComponent;
import controller.BuzzWordController;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import propertymanager.PropertyManager;
import java.util.ArrayList;
import static ui.HangmanProperties.*;
/**
 * Created by Mendy on 10/31/2016.
 */
public class Workspace extends AppWorkspaceComponent {

    BuzzWordController controller;
    Boolean loggedIn = false;
    AppTemplate app; // the actual application
    AppGUI      gui; // the GUI inside which the application sits
    Label       guiHeadingLabel;
    Label       targetLabel;
    Label       remainingTimeLabel;
    Button      createProfileButton = new Button("Create New Profile");
    Button      profileSettingsButton;
    Button      modeSelectionButton = new Button("Select Mode");
    Button      lvlSelectionButton = new Button("Start Playing");
    Button      nextButton;
    Button      pauseResumeButton;
    Button      replayButton;
    Button      returnHomeButton;
    TextField   userField = new TextField();
    PasswordField passwordField = new PasswordField();
    Scene       currentScene;
    public Pane homePage = new Pane();
    BorderPane loginPanel = new BorderPane();
    ArrayList<Node> letters;
    Button create = new Button("Create New");
    ArrayList<Node> levels;

    HBox       headPane;          // conatainer to display the heading
    HBox       bodyPane;          // container for the main game displays
    ToolBar    footToolbar;       // toolbar for game buttons
    BorderPane figurePane;        // container to display the namesake graphic of the (potentially) hanging person
    VBox       gameTextsPane;     // container to display the text-related parts of the game
    HBox       guessedLetters;    // text area displaying all the letters guessed so far
    HBox       remainingGuessBox; // container to display the number of remaining guesses
    Button     startGame;         // the button to start playing a game of Hangman
    Button      logInOutButton;
    Button      helpButton = new Button("Professaur");
    Button[][] nodes = new Button[4][4];

    public Workspace(AppTemplate initApp)  {
        app = initApp;
        gui = app.getGUI();
        guiHeadingLabel = new Label("BuzzWord!");
        guiHeadingLabel.setLayoutX(400);

        controller = (BuzzWordController) gui.getFileController();
        logInOutButton = new Button("Login");
        workspace = new Pane();
        workspace.getChildren().clear();
        updateHomePage();
        workspace.getChildren().addAll(homePage);
        //gui.getAppPane().getChildren().addAll(homePage);

        activateWorkspace(gui.appPane);
        setUpHandlers();
    }

    private Pane layoutGUI() {
        PropertyManager propertyManager = PropertyManager.getManager();

        headPane = new HBox();
        headPane.getChildren().add(guiHeadingLabel);
        headPane.setAlignment(Pos.CENTER);

        figurePane = new BorderPane();
        guessedLetters = new HBox();
        guessedLetters.setStyle("-fx-background-color: transparent;");
        remainingGuessBox = new HBox();
        gameTextsPane = new VBox();

        gameTextsPane.getChildren().setAll(remainingGuessBox, guessedLetters);

        bodyPane = new HBox();
        bodyPane.getChildren().addAll(figurePane, gameTextsPane);

        HBox blankBoxLeft  = new HBox();
        HBox blankBoxRight = new HBox();
        HBox.setHgrow(blankBoxLeft, Priority.ALWAYS);
        HBox.setHgrow(blankBoxRight, Priority.ALWAYS);
        footToolbar = new ToolBar(blankBoxLeft, startGame, blankBoxRight);

        workspace = new VBox();
        workspace.getChildren().addAll(homePage,headPane, bodyPane, footToolbar);
        return workspace;
    }

    public void updateHomePage(){
        homePage.prefHeightProperty().bind(gui.getPrimaryScene().heightProperty());
        homePage.prefWidthProperty().bind(gui.getPrimaryScene().widthProperty());

        Rectangle rect = new Rectangle(800/5, 550);
        rect.setFill(Color.GRAY);
        Pane pane = new Pane();
        createProfileButton.setPrefSize(250,70);
        createProfileButton.setLayoutX(800/5 - 200);
        createProfileButton.setLayoutY(100);
        logInOutButton.setPrefSize(250,70);
        logInOutButton.setLayoutX(800/5 - 200);
        logInOutButton.setLayoutY(200);
        int r = 65;
        int y = 130;
        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(10);
        shadow.setOffsetX(5);
        shadow.setColor(Color.LIGHTSLATEGRAY);

        guiHeadingLabel = new Label("BuzzWord!");
        guiHeadingLabel.setLayoutX(300);
        guiHeadingLabel.setLayoutY(20);
        guiHeadingLabel.setStyle(
                "-fx-font-size: 32pt;" +
                        "    -fx-font-family: \"Segoe UI Light\";\n" +
                        "    -fx-text-fill: black;\n" +
                        "    -fx-opacity: 1;"
        );
        int label = 1;
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes.length; j++) {
                nodes[i][j] = new Button();
                Button thisButton = nodes[i][j];
                thisButton.setOnAction(e->{
                    System.out.println(thisButton.getText());
                });
                //nodes[i][j].setDisable(true);
                thisButton.setLayoutY(y);
                thisButton.setLayoutX(250 + r);
                thisButton.setStyle(
                        "-fx-background-radius: 5em; " +
                                "-fx-min-width: 60px; " +
                                "-fx-min-height: 60px; " +
                                "-fx-max-width: 60px; " +
                                "-fx-max-height: 60px;"
                );
                thisButton.setEffect(shadow);
                pane.getChildren().add(nodes[i][j]);
                r+=85;
                label++;
            }
            r = 65;
            y+= 75;
        }
        nodes[0][0].setText("B");
        nodes[0][1].setText("U");
        nodes[1][0].setText("Z");
        nodes[1][1].setText("Z");
        nodes[2][2].setText("W");
        nodes[2][3].setText("O");
        nodes[3][2].setText("R");
        nodes[3][3].setText("D");
        helpButton.setPrefSize(250,70);
        helpButton.setLayoutX(800/5 - 200);
        helpButton.setLayoutY(100);
        modeSelectionButton.setPrefSize(250,70);
        modeSelectionButton.setLayoutX(800/5 - 200);
        modeSelectionButton.setLayoutY(200);
        lvlSelectionButton.setPrefSize(250,70);
        lvlSelectionButton.setLayoutX(800/5 - 200);
        lvlSelectionButton.setLayoutY(300);
        if (!loggedIn)
            pane.getChildren().addAll(guiHeadingLabel,rect, createProfileButton, logInOutButton);
        else
            pane.getChildren().addAll(guiHeadingLabel, rect, helpButton, modeSelectionButton, lvlSelectionButton);

       // homePage.setBackground(new Background(new BackgroundFill(Color.web("#718ffc"), CornerRadii.EMPTY, Insets.EMPTY)));
        //objects.getChildren().addAll(guiHeadingLabel);
        homePage.getChildren().add(pane);
        //homePage.setTop(objects);
    }

    private void updateLoginPanel() {
        loginPanel.prefHeightProperty().bind(gui.getPrimaryScene().heightProperty());
        loginPanel.prefWidthProperty().bind(gui.getPrimaryScene().widthProperty());
        VBox panel = new VBox();
        HBox userName = new HBox();
        userName.getChildren().addAll(new Label("Profile Name   "), userField);
        HBox pw = new HBox();
        userName.setAlignment(Pos.CENTER);
        pw.setAlignment(Pos.CENTER);
        pw.getChildren().addAll(new Label("Password        "), passwordField);
        HBox buttons = new HBox();
        buttons.getChildren().addAll(create, new Button("Cancel"));
        buttons.setAlignment(Pos.CENTER);
        panel.getChildren().addAll(guiHeadingLabel,userName, pw, buttons);
        panel.setAlignment(Pos.CENTER);
        loginPanel.getChildren().add(guiHeadingLabel);
        loginPanel.setCenter(panel);
    }

    private void setUpHandlers(){
        logInOutButton.setOnAction(e->{
            workspace.getChildren().clear();
            updateHomePage();
            workspace.getChildren().add(homePage);
        });
        create.setOnAction(e->{
            workspace.getChildren().clear();
            loggedIn = true;
            updateHomePage();
            workspace.getChildren().add(homePage);
        });
        createProfileButton.setOnAction(e->{
            workspace.getChildren().clear();
            updateLoginPanel();
            workspace.getChildren().addAll(loginPanel);
        });
    }


    @Override
    public void initStyle() {
        PropertyManager propertyManager = PropertyManager.getManager();

        gui.getAppPane().setId(propertyManager.getPropertyValue(ROOT_BORDERPANE_ID));
        gui.getToolbarPane().getStyleClass().setAll(propertyManager.getPropertyValue(SEGMENTED_BUTTON_BAR));
        gui.getToolbarPane().setId(propertyManager.getPropertyValue(TOP_TOOLBAR_ID));

        ObservableList<Node> toolbarChildren = gui.getToolbarPane().getChildren();
        toolbarChildren.get(0).getStyleClass().add(propertyManager.getPropertyValue(FIRST_TOOLBAR_BUTTON));
        toolbarChildren.get(toolbarChildren.size() - 1).getStyleClass().add(propertyManager.getPropertyValue(LAST_TOOLBAR_BUTTON));

        workspace.getStyleClass().add(CLASS_BORDERED_PANE);
        guiHeadingLabel.getStyleClass().setAll(propertyManager.getPropertyValue(HEADING_LABEL));
    }

    @Override
    public void reloadWorkspace() {
        //workspace.getChildren().clear();
        //workspace.getChildren().add(helpButton);
    }

    public void reinitialize() {
        workspace.getChildren().addAll(helpButton);
    }
}
