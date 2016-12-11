package ui;
import BuzzWord.GameMode;
import apptemplate.AppTemplate;
import components.AppWorkspaceComponent;
import controller.BuzzWordController;
import controller.GridGenerator;
import data.GameAccount;
import data.GameData;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import propertymanager.PropertyManager;
import static ui.HangmanProperties.*;
import javafx.scene.input.*;

import java.util.ArrayList;

/**
 * Created by Mendy on 10/31/2016.
 */
public class Workspace extends AppWorkspaceComponent {

    BuzzWordController controller;
    AppTemplate        app; // the actual application
    AppGUI             gui; // the GUI inside which the application sits

    //Panes to represent different screen types
    public Pane homePage = new Pane();
    public Pane helpPage = new Pane();
    public Pane levelSelectPage = new Pane();
    public Pane gamePlayPane = new Pane();
    Pane p = new Pane();
    BorderPane  profilePanel = new BorderPane();
    BorderPane  loginPanel = new BorderPane();

    //nodes within the workspaces different screens
    Label         guiHeadingLabel;
    public Label         remainingTimeLabel;
    public Label         scoreLabel;
    Button        createProfileButton = new Button("Create New Profile");
    Button        profileSettingsButton = new Button ("John Doe");
    Button        lvlSelectionButton = new Button("Start Playing");
    public Button        pauseResumeButton;
    public Button        nextLevelButton;
    TextField     userField = new TextField();
    PasswordField passwordField = new PasswordField();
    Rectangle     rect = new Rectangle(800/5, 700);
    Button        create = new Button("Create New");
    Button        cancelButton = new Button("Cancel");
    Button        login;
    Button        logInOutButton;
    Button        helpButton = new Button("Professaur");
    Button[][]    nodes = new Button[4][4];
    public Label         modeLabel;
    Line[]        connects = new Line[4];
    Line[]        vconnects = new Line[4];
    final ComboBox modeSelectionButton = new ComboBox<>();
    Boolean       createNew = false;
    public Boolean loggedIn = false;
    Label currGuess = new Label();
    public VBox  allGuessedWords = new VBox();
    char[][] grid;
    ArrayList<Button> dragging = new ArrayList<Button>();
    Button updateButton = new Button("Update");
    public Button replay = new Button("Replay");
    Line highlight = new Line();

    public Workspace(AppTemplate initApp)  {
        app = initApp;
        gui = app.getGUI();
        guiHeadingLabel = new Label("BuzzWord!");
        guiHeadingLabel.setLayoutX(350);

        controller = (BuzzWordController) gui.getFileController();
        logInOutButton = new Button("Login");
        login = new Button("Login");
        workspace = new Pane();
        workspace.getChildren().clear();
        int r = 20;
        int y = 160;
        int f = 280 +r;
        int g = y;
        for (int i = 0; i < nodes.length; i++) {
            connects[i] = new Line(250+r, y +30, 250+r + (85*3), y+30);
            connects[i].setFill(Color.GRAY);
            connects[i].setStrokeWidth(4);
            connects[i].setVisible(false);
            vconnects[i] = new Line(f, g +30, f, g+(75*3));
            f+= 85;
            vconnects[i].setFill(Color.GRAY);
            vconnects[i].setStrokeWidth(4);
            vconnects[i].setVisible(false);

            for (int j = 0; j < nodes.length; j++) {
                nodes[i][j] = new Button();
                Button thisButton = nodes[i][j];
                thisButton.setLayoutY(y);
                thisButton.setLayoutX(250 + r);
                thisButton.setStyle(
                        "-fx-background-radius: 5em; " +
                                "-fx-min-width: 60px; " +
                                "-fx-min-height: 60px; " +
                                "-fx-max-width: 60px; " +
                                "-fx-max-height: 60px;"+
                                "-fx-background-color: white;" +
                                "-fx-text-fill: black;"
                );
                //pane.getChildren().add(nodes[i][j]);

                thisButton.setDisable(true);
                thisButton.setVisible(false);
                r+=85;
            }
            r = 20;
            y+= 75;
        }
        updateHomePage();
        workspace.getChildren().addAll(homePage);
        modeSelectionButton.getItems().setAll(GameMode.values());

        modeSelectionButton.setValue("Select Mode");
        activateWorkspace(gui.appPane);
        setUpHandlers();
    }

    public void profilePage(){
        profilePanel.prefHeightProperty().bind(gui.getPrimaryScene().heightProperty());
        profilePanel.prefWidthProperty().bind(gui.getPrimaryScene().widthProperty());
        VBox panel = new VBox();
        HBox userName = new HBox();
        userName.getChildren().addAll(new Label("Profile Name   "), userField);
        userField.setText(controller.getAccount().getName());
        HBox pw = new HBox();
        userName.setAlignment(Pos.CENTER);
        pw.setAlignment(Pos.CENTER);
        pw.getChildren().addAll(new Label("Password        "), passwordField);
        String pwHolder = "";
        for (int i = 0; i < controller.getAccount().length; i++)
            pwHolder += "*";
        passwordField.setText(pwHolder);
        HBox buttons = new HBox();
        buttons.getChildren().addAll(updateButton);
        buttons.setAlignment(Pos.CENTER);
        panel.getChildren().addAll(guiHeadingLabel,userName, pw, buttons);
        panel.setAlignment(Pos.CENTER);
        profilePanel.getChildren().addAll(rect,guiHeadingLabel);
        profilePanel.setCenter(panel);
    }

    public void updateHomePage() {
        logInOutButton.setVisible(true);
        homePage.prefHeightProperty().bind(gui.getPrimaryScene().heightProperty());
        homePage.prefWidthProperty().bind(gui.getPrimaryScene().widthProperty());
        for (int o = 0; o < connects.length; o++) {
            connects[o].setVisible(false);
            vconnects[o].setVisible(false);
            rect.setFill(Color.CADETBLUE);
            Pane pane = new Pane();
            createProfileButton.setPrefSize(250, 70);
            createProfileButton.setLayoutX(800 / 5 - 200);
            createProfileButton.setLayoutY(100);
            logInOutButton.setPrefSize(250, 70);
            logInOutButton.setLayoutX(800 / 5 - 200);
            logInOutButton.setLayoutY(200);

            guiHeadingLabel = new Label("BuzzWord!");
            guiHeadingLabel.setLayoutX(350);
            guiHeadingLabel.setLayoutY(20);
            guiHeadingLabel.setStyle(
                    "-fx-font-size: 32pt;" +
                            "    -fx-font-family: \"Segoe UI Light\";\n" +
                            "    -fx-text-fill: black;\n" +
                            "    -fx-opacity: 1;"
            );
            for (int i = 0; i < nodes.length; i++) {
                pane.getChildren().add(connects[i]);

                pane.getChildren().add(vconnects[i]);
                for (int j = 0; j < nodes.length; j++) {
                    DropShadow shadow = new DropShadow();

                    shadow.setOffsetY(10);
                    shadow.setOffsetX(5);
                    shadow.setColor(Color.LIGHTSLATEGRAY);
                    nodes[i][j].setEffect(shadow);
                    nodes[i][j].setStyle( "-fx-background-radius: 5em; " +
                            "-fx-min-width: 60px; " +
                            "-fx-min-height: 60px; " +
                            "-fx-max-width: 60px; " +
                            "-fx-max-height: 60px;"+
                            "-fx-background-color: white;" +
                            "-fx-text-fill: black;");
                    pane.getChildren().add(nodes[i][j]);
                    nodes[i][j].setVisible(true);
                    nodes[i][j].setDisable(true);
                }
            }
            nodes[0][0].setText("B");
            nodes[0][1].setText("U");
            nodes[0][2].setText("");
            nodes[0][3].setText("");
            nodes[1][0].setText("Z");
            nodes[1][1].setText("Z");
            nodes[1][2].setText("");
            nodes[1][3].setText("");
            nodes[2][2].setText("W");
            nodes[2][3].setText("O");
            nodes[2][0].setText("");
            nodes[2][1].setText("");
            nodes[3][2].setText("R");
            nodes[3][3].setText("D");
            nodes[3][1].setText("");
            nodes[3][0].setText("");
            helpButton.setPrefSize(250, 70);
            helpButton.setLayoutX(800 / 5 - 200);
            helpButton.setLayoutY(100);
            modeSelectionButton.setPrefSize(220, 70);
            modeSelectionButton.setLayoutY(300);
            lvlSelectionButton.setPrefSize(250, 70);
            lvlSelectionButton.setLayoutX(800 / 5 - 200);
            lvlSelectionButton.setLayoutY(400);
            profileSettingsButton.setPrefSize(250, 70);
            profileSettingsButton.setLayoutX(800 / 5 - 200);
            profileSettingsButton.setLayoutY(200);
            if (!loggedIn) {
                logInOutButton.setText("Log in");
                logInOutButton.setLayoutY(200);
                pane.getChildren().addAll(guiHeadingLabel, rect, createProfileButton, logInOutButton);
            } else {
                modeSelectionButton.setVisible(true);
                lvlSelectionButton.setVisible(true);
                logInOutButton.setText("Log Out");
                logInOutButton.setLayoutY(500);
                profileSettingsButton.setText(controller.getAccount().getName());
                pane.getChildren().addAll(guiHeadingLabel, rect, helpButton, profileSettingsButton, modeSelectionButton, lvlSelectionButton, logInOutButton);
            }
            homePage.getChildren().add(pane);
        }
    }

    private void updateLoginPanel() {
        userField.clear();
        passwordField.clear();
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
        if (createNew) {
            buttons.getChildren().addAll(create, cancelButton);
            createNew = false;
        } else {
            buttons.getChildren().addAll(login, cancelButton);
        }
        buttons.setAlignment(Pos.CENTER);
        panel.getChildren().addAll(guiHeadingLabel,userName, pw, buttons);
        panel.setAlignment(Pos.CENTER);
        loginPanel.getChildren().addAll(guiHeadingLabel, rect);
        loginPanel.setCenter(panel);
    }

    private void updateHelpPage(){
        PropertyManager propertyManager = PropertyManager.getManager();
        helpPage.prefHeightProperty().bind(gui.getPrimaryScene().heightProperty());
        helpPage.prefWidthProperty().bind(gui.getPrimaryScene().widthProperty());
        Label help = new Label("BuzzWord! Help Page");
        help.setLayoutX(280);
        help.setLayoutY(20);
        help.getStyleClass().setAll(propertyManager.getPropertyValue(HEADING_LABEL));
        help.setStyle("-fx-text-fill: black");
        TextArea text = new TextArea(
                "Welcome to BuzzWord!\n\n" +
                "BuzzWord is a generalization of the popular word game “Boogle” and aims to make the learning process casual " +
                "and fun so that players learn and build up their vocabulary without consciously realizing it. Much like Boggle," +
                " BuzzWord is a game where players are given a network of connected letters, and they aim to identify as many words" +
                "as possible, within a limited amount of time. As players get promoted from one level to the next, the promotion " +
                "criteria to get into the subsequent levels become progressively more difficult.\n\n" +

                "Here's how the game works. After creating your profile, you can select from a variety of different MODES. The Dictionary Mode" +
                        "is probably the most common to play with and will have more words. Once you have selected your desired Mode from the " +
                        "drop down menu on the homepage, the game starts IMMEDIATELY! You will have a total of 80 seconds to guess as many words" +
                        "from the board as you can. Letters are connected only horizontally and vertically, so you cannot select a letter that is" +
                        "not connected to one that you have already selected. In order to win, you must reach the target score. Your score is" +
                        "determined by how many words you can guess and how long they are. Select the words by dragging across them directly with" +
                        "your mouse or typing in the letters on the keyboard.\n\n" +
                        "" +
                        "This game is built by Professaur, Inc. But it was really built by one person so I can just say it was built by me. " +
                        "This is a CSE219 Final Course Project and is not to be expected to work fully."
        );

        text.setPrefSize(500, 450);
        text.setLayoutY(100);
        text.setLayoutX(250);
        text.setWrapText(true);
        text.setEditable(false);

        helpPage.getChildren().addAll(rect, helpButton, profileSettingsButton,help,text);
    }

    public void updateLvlSelect(){
        PropertyManager propertyManager = PropertyManager.getManager();
        String mode = modeSelectionButton.getValue().toString();
        //System.out.println(mode);
        modeLabel = new Label(mode);
        modeLabel.setLayoutX(300);
        modeLabel.setLayoutY(90);
        modeLabel.getStyleClass().setAll(propertyManager.getPropertyValue(HEADING_LABEL));
        int label = 1;
        GameAccount account = controller.getAccount();
        GameData data = account.getModeData(GameMode.valueOf(modeSelectionButton.getValue().toString()));
        boolean[] unlocked = data.getUnlocked_levels();
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes.length; j++) {
                    if (label <= GameData.NUM_LEVELS) {
                        nodes[i][j].setText(label + "");
                        int finalLabel = label;
                        nodes[i][j].setOnAction(e -> {
                            controller.level = finalLabel;
                            controller.handleGame();
                        });
                        levelSelectPage.getChildren().add(nodes[i][j]);
                        if (unlocked[label - 1])
                            nodes[i][j].setDisable(false);
                        label++;
                        nodes[i][j].setStyle(
                                "-fx-background-radius: 5em; " +
                                        "-fx-min-width: 60px; " +
                                        "-fx-min-height: 60px; " +
                                        "-fx-max-width: 60px; " +
                                        "-fx-max-height: 60px;" +
                                        "-fx-background-color: white;" +
                                        "-fx-text-fill: black;"
                        );
                        nodes[i][j].setOnMouseEntered(e -> {
                        });
                    } else
                        nodes[i][j].setVisible(false);
                }
            }

        levelSelectPage.getChildren().addAll(rect, guiHeadingLabel,helpButton, profileSettingsButton, modeLabel);
    }

    public void showGamePlay() {
        workspace.getChildren().clear();
        gamePlayPane.getChildren().clear();
        gamePlayScreen();
        workspace.getChildren().add(gamePlayPane);
    }

    public void gamePlayScreen(){
        workspace.getChildren().clear();
        updateHomePage();
        gamePlayPane.getChildren().clear();
        gamePlayPane.getChildren().add(homePage);
        modeSelectionButton.setVisible(false);
        lvlSelectionButton.setVisible(false);
        logInOutButton.setVisible(false);

        //add lines and change node colors
        for (int i = 0; i < nodes.length; i++) {
            connects[i].setVisible(true);
            vconnects[i].setVisible(true);
            for (int j = 0; j < nodes.length; j++) {
                Button thisButton = nodes[i][j];
                thisButton.setDisable(false);
                thisButton.setStyle(
                        "-fx-background-radius: 5em; " +
                                "-fx-min-width: 60px; " +
                                "-fx-min-height: 60px; " +
                                "-fx-max-width: 60px; " +
                                "-fx-max-height: 60px;"+
                                "-fx-background-color: black;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 15pt;"
                );
            }
        }

        //generate grid for game play
        controller.setMode(modeSelectionButton.getValue().toString());
        grid = controller.gridGenerator.getGrid(modeSelectionButton.getValue().toString(),1);
        for (int a = 0; a < nodes.length; a++)
            for (int y = 0; y < nodes.length; y++) {
                nodes[a][y].setText(grid[a][y]+"");
                nodes[a][y].toFront();
                setUp(a, y);
            }

        //set up remaining time UI
        remainingTimeLabel = new Label ("TIME REMAINING: seconds");
        remainingTimeLabel.toFront();
        remainingTimeLabel.setLayoutX(630);
        remainingTimeLabel.setLayoutY(60);
        remainingTimeLabel.setStyle("-fx-background-color: rgb(0,0,0);" +
                "-fx-text-fill: white;" +
                "-fx-padding: 0.4em;" +
                "-fx-font-size: 12pt");

        //set up guessing progress UI
        ScrollPane guessing = new ScrollPane();
        guessing.setLayoutX(630);
        guessing.setLayoutY(120);
        guessing.setFitToHeight(true);
        currGuess.setText("");
        currGuess.setTextFill(Color.WHITE);
        guessing.setContent(currGuess);
        guessing.setPrefWidth(200);
        guessing.setPrefHeight(50);
        Label level = new Label("Level " + controller.level);
        level.setLayoutX(500);
        level.setLayoutY(470);

        //set up guessed words UI
        ScrollPane words = new ScrollPane();
        words.setLayoutX(630);
        words.setLayoutY(190);
        words.setPrefWidth(200);
        words.setPrefHeight(260);
        allGuessedWords.getChildren().clear();
        words.setContent(allGuessedWords);

        //set up total score UI
        Pane b = new Pane();
        b.setStyle("-fx-background-color: black;");
        b.setPrefWidth(200);
        b.setPrefHeight(50);
        b.setLayoutY(210);
        Label l = new Label("TOTAL:           40");
        l.setStyle("-fx-text-fill: white");
        b.getChildren().add(l);
        //words.getChildren().add(b);
        level.setFont(Font.font("Century Gothic"));

        //total score
        scoreLabel = new Label ("Total: " + 0 + " pts");
        scoreLabel.setLayoutX(670);
        scoreLabel.setLayoutY(450);
        //scoreLabel.setPrefWidth(200);
        //scoreLabel.setPrefHeight(50);
        scoreLabel.setStyle("-fx-background-color: rgb(0,0,0);" +
                "-fx-text-fill: white;" +
                "-fx-padding: 0.4em;" +
                "-fx-font-size: 16pt");

        //initialize target score UI
        Pane target = new Pane();
        target.setStyle("-fx-background-color: white");
        target.getChildren().addAll(new Label("Target: "+ (controller.level+2)*30 + " points"));
        target.setLayoutX(630);
        target.setLayoutY(500);
        target.setPrefWidth(200);
        target.setPrefHeight(50);

        //add pane when paused
        p.setPrefSize(340,310);
        p.setLayoutX(260);
        p.setLayoutY(150);
        p.setStyle("-fx-background-color: rgb(71, 92, 127);");
        Text t = new Text("GAME PAUSED");
        t.setStyle("-fx-font-size: 32pt;" +
                " -fx-font-family: Segoe UI Light;");
        t.setLayoutX(25);
        t.setLayoutY(170);
        p.getChildren().add(t);
        p.setVisible(false);

        //place pause/resume button
        pauseResumeButton = new Button("Pause");
        pauseResumeButton.setOnAction(e->{
            if (pauseResumeButton.getText().equals("Pause")) {
                pauseResumeButton.setText("Resume");
                pause();
            } else {
                pauseResumeButton.setText("Pause");
                resume();
            }
        });
        pauseResumeButton.setLayoutX(300);
        pauseResumeButton.setLayoutY(470);

        //replay
        replay.setLayoutX(400);
        replay.setLayoutY(470);
        replay.setOnAction(e->{controller.restart();});
        replay.setDisable(false);

        //nextLevelButton
        nextLevelButton = new Button("Next Level");
        nextLevelButton.setLayoutX(350);
        nextLevelButton.setLayoutY(520);
        nextLevelButton.setOnAction(e->{
            controller.level = controller.level+1;
            controller.handleGame();
        });
        nextLevelButton.setVisible(false);

        //add all panes into gamePlay
        gamePlayPane.getChildren().add(p);
        gamePlayPane.getChildren().addAll(remainingTimeLabel, scoreLabel, modeLabel, guessing, pauseResumeButton,
                nextLevelButton, replay, level, words, target);//, curr, guess);
    }

    private void setUp(int i, int j){
        nodes[i][j].setOnDragDetected(e->{
            gamePlayPane.startFullDrag();
        });
        nodes[i][j].setOnMouseDragEntered(e-> {
            ((DropShadow) nodes[i][j].getEffect()).setOffsetY(0);
            ((DropShadow) nodes[i][j].getEffect()).setOffsetX(0);
            ((DropShadow) nodes[i][j].getEffect()).setRadius(5);
            ((DropShadow) nodes[i][j].getEffect()).setSpread(2);
            ((DropShadow) nodes[i][j].getEffect()).setColor(Color.RED);
            disableButtons(i,j);
            dragging.add(nodes[i][j]);
            currGuess.setText(currGuess.getText() + grid[i][j]);
        });
        nodes[i][j].setOnMouseDragReleased(e->{
            enableButtons();
            for (int s =0; s < dragging.size(); s++){
                ((DropShadow) dragging.get(s).getEffect()).setRadius(0);
                ((DropShadow) dragging.get(s).getEffect()).setSpread(0);
                ((DropShadow) dragging.get(s).getEffect()).setColor(Color.BLACK);
            }
            dragging.clear();
            if (controller.isValidWord(currGuess.getText().toLowerCase()))
                allGuessedWords.getChildren().add(new Label (currGuess.getText() + "  " +currGuess.getText().length()*10));
            currGuess.setText("");

        });
//        nodes[i][j].setOnKeyPressed(new EventHandler<KeyEvent>() {
//            @Override
//            public void handle(KeyEvent event) {
//                System.out.println(KeyCodeString(event.getCode()));
//                if (KeyCodeString(event.getCode()).equals(nodes[i][j].getText().trim())) {
//                    System.out.println("event key handler");
//                    ((DropShadow) nodes[i][j].getEffect()).setOffsetY(0);
//                    ((DropShadow) nodes[i][j].getEffect()).setOffsetX(0);
//                    ((DropShadow) nodes[i][j].getEffect()).setRadius(5);
//                    ((DropShadow) nodes[i][j].getEffect()).setSpread(2);
//                    ((DropShadow) nodes[i][j].getEffect()).setColor(Color.RED);
//                    dragging.add(nodes[i][j]);
//                    currGuess.setText(currGuess.getText() + grid[i][j]);
//                }
//            }
//        });
//
//        nodes[i][j].setOnKeyReleased(e->{
//            for (int s =0; s < dragging.size(); s++){
//                ((DropShadow) dragging.get(s).getEffect()).setRadius(0);
//                ((DropShadow) dragging.get(s).getEffect()).setSpread(0);
//                ((DropShadow) dragging.get(s).getEffect()).setColor(Color.BLACK);
//            }
//            dragging.clear();
//            System.out.println(currGuess.getText());
//            allGuessedWords.getChildren().add(new Label (currGuess.getText() + "    30"));
//            currGuess.setText("");
//        });
    }

    private void disableButtons(int x, int y) {
        for (int i = 0; i < nodes.length; i++)
            for (int j = 0; j < nodes.length; j++) {
                nodes[i][j].setOnMouseDragEntered(e->{});
            }
        if (x -1 >= 0 && !dragging.contains(nodes[x-1][y])) {
            nodes[x-1][y].setOnMouseDragEntered(e->{
                ((DropShadow)nodes[x-1][y].getEffect()).setOffsetY(0);
                ((DropShadow)nodes[x-1][y].getEffect()).setOffsetX(0);
                ((DropShadow) nodes[x-1][y].getEffect()).setRadius(5);
                ((DropShadow) nodes[x-1][y].getEffect()).setSpread(2);
                ((DropShadow)nodes[x-1][y].getEffect()).setColor(Color.RED);
                dragging.add(nodes[x - 1][y]);
//                highlight = new Line(nodes[x][y].getLayoutX()+30,nodes[x][y].getLayoutY()+25,
//                        nodes[x-1][y].getLayoutX()+30, nodes[x-1][y].getLayoutY()+25);
//                highlight.setStroke(Color.RED);
//                highlight.setStrokeWidth(2);
//                gamePlayPane.getChildren().add(highlight);
//                highlight.toBack();
//                for (int i = 0; i < vconnects.length; i++) {
//                    vconnects[i].toBack();
//                    connects[i].toBack();
//                }
                currGuess.setText(currGuess.getText() + grid[x-1][y]);
                disableButtons(x-1, y);
            });
        }
        if (y -1 >= 0 && !dragging.contains(nodes[x][y-1])) {
            nodes[x][y-1].setOnMouseDragEntered(e->{
                ((DropShadow)nodes[x][y-1].getEffect()).setOffsetY(0);
                ((DropShadow)nodes[x][y-1].getEffect()).setOffsetX(0);
                ((DropShadow) nodes[x][y-1].getEffect()).setRadius(5);
                ((DropShadow) nodes[x][y-1].getEffect()).setSpread(2);
                ((DropShadow)nodes[x][y-1].getEffect()).setColor(Color.RED);
                dragging.add(nodes[x][y-1]);
                currGuess.setText(currGuess.getText() + grid[x][y-1]);
                disableButtons(x, y-1);
            });
        }
        if (y +1 < 4 && !dragging.contains(nodes[x][y+1])){
            nodes[x][y+1].setOnMouseDragEntered(e->{
                ((DropShadow)nodes[x][y+1].getEffect()).setOffsetY(0);
                ((DropShadow)nodes[x][y+1].getEffect()).setOffsetX(0);
                ((DropShadow) nodes[x][y+1].getEffect()).setRadius(5);
                ((DropShadow) nodes[x][y+1].getEffect()).setSpread(2);
                ((DropShadow)nodes[x][y+1].getEffect()).setColor(Color.RED);
                dragging.add(nodes[x ][y+1]);
                currGuess.setText(currGuess.getText() + grid[x][y+1]);
                disableButtons(x, y+1);
            });
        }
        if (x +1 < 4 && !dragging.contains(nodes[x+1][y])){
            nodes[x+1][y].setOnMouseDragEntered(e->{
                ((DropShadow)nodes[x+1][y].getEffect()).setOffsetY(0);
                ((DropShadow)nodes[x+1][y].getEffect()).setOffsetX(0);
                ((DropShadow) nodes[x+1][y].getEffect()).setRadius(5);
                ((DropShadow) nodes[x+1][y].getEffect()).setSpread(2);
                ((DropShadow)nodes[x+1][y].getEffect()).setColor(Color.RED);
                dragging.add(nodes[x + 1][y]);
                currGuess.setText(currGuess.getText() + grid[x+1][y]);
                disableButtons(x+1, y);
            });
        }
    }

    public void disableAll(){
        for (int i = 0; i < nodes.length; i++)
            for (int j = 0; j < nodes.length; j++) {
                nodes[i][j].setDisable(true);
            }
    }

    private void enableButtons(){
        for (int i = 0; i < nodes.length; i++)
            for (int j = 0; j < nodes.length; j++) {
                setUp(i,j);
            }
    }

    public String KeyCodeString(KeyCode k){
        String s = k.toString();
        if(s.startsWith("VK_")){
            s = s.substring(3, s.length());
        }
        s = s.replace("_", " ");

        return s;
    }
    private void setUpHandlers(){
        updateButton.setOnAction(e->{
            String name = userField.getText();
            String pw = passwordField.getText();
            controller.updateProfile(name, pw);
            controller.handleHomeRequest();
        });
        logInOutButton.setOnAction(e->{
            workspace.getChildren().clear();
            modeSelectionButton.setValue("Select Mode");
            if (loggedIn){
                loggedIn = false;
                homePage.getChildren().clear();
                updateHomePage();
                workspace.getChildren().add(homePage);
            } else {
                updateLoginPanel();
                workspace.getChildren().add(loginPanel);
            }
        });
        profileSettingsButton.setOnAction(e->{
            workspace.getChildren().clear();
            profilePage();
            workspace.getChildren().add(profilePanel);
        });
        create.setOnAction(e->{
            workspace.getChildren().clear();
            loggedIn = true;
            System.out.println(userField.getText());
            System.out.println(passwordField.getText());
            controller.createNewProfile(userField.getText(), passwordField.getText());
            homePage.getChildren().clear();
            updateHomePage();
            workspace.getChildren().add(homePage);
        });
        createProfileButton.setOnAction(e->{
            workspace.getChildren().clear();
            loginPanel.getChildren().clear();
            createNew = true;
            updateLoginPanel();
            workspace.getChildren().addAll(loginPanel);
        });
        cancelButton.setOnAction(e->{
            workspace.getChildren().clear();
            homePage.getChildren().clear();
            updateHomePage();
            createNew = false;
            loggedIn = false;
            workspace.getChildren().addAll(homePage);
        });
        login.setOnAction(e ->{
            System.out.println(userField.getText());
            System.out.println(passwordField.getText());
            loggedIn = controller.logIn(userField.getText(), passwordField.getText());
            if (loggedIn) {
                workspace.getChildren().clear();
                homePage.getChildren().clear();
                updateHomePage();
                workspace.getChildren().add(homePage);
            }
        });
        helpButton.setOnAction(e -> {
            workspace.getChildren().clear();
            updateHelpPage();
            workspace.getChildren().add(helpPage);
        });
        lvlSelectionButton.setOnAction(e->{
            System.out.println(modeSelectionButton.getValue().toString());
            if (modeSelectionButton.getValue().toString().equals("Select Mode") ){
                AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                dialog.show("Error", "Please select a Mode first!" );
            } else {
                workspace.getChildren().clear();
                levelSelectPage.getChildren().clear();
                updateLvlSelect();
                workspace.getChildren().add(levelSelectPage);
            }
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
    }

    public void reinitialize() {
        workspace.getChildren().addAll(helpButton);
    }

    public void pause(){
        controller.pauseTimer();
        p.setVisible(true);
    }

    public void resume(){
        controller.resumeTimer();
        p.setVisible(false);
    }
}
