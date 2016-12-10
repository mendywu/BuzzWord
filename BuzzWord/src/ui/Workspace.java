package ui;
import BuzzWord.GameMode;
import apptemplate.AppTemplate;
import components.AppWorkspaceComponent;
import controller.BuzzWordController;
import controller.GridGenerator;
import data.GameAccount;
import data.GameData;
import javafx.collections.ObservableList;
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
    Button        createProfileButton = new Button("Create New Profile");
    Button        profileSettingsButton = new Button ("John Doe");
    Button        lvlSelectionButton = new Button("Start Playing");
    Button        pauseResumeButton;
    TextField     userField = new TextField();
    PasswordField passwordField = new PasswordField();
    Rectangle     rect = new Rectangle(800/5, 700);
    Button        create = new Button("Create New");
    Button        cancelButton = new Button("Cancel");
    Button        login;
    Button        logInOutButton;
    Button        helpButton = new Button("Professaur");
    Button[][]    nodes = new Button[4][4];
    Label         modeLabel;
    Line[]        connects = new Line[4];
    Line[]        vconnects = new Line[4];
    final ComboBox modeSelectionButton = new ComboBox<>();
    Boolean       createNew = false;
    public Boolean loggedIn = false;
    Label currGuess = new Label();
    VBox  allGuessedWords = new VBox();
    char[][] grid;
    ArrayList<Button> dragging = new ArrayList<Button>();

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
//                thisButton.setOnAction(e->{
//                    System.out.println(thisButton.getText());
//                });
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
        //gui.getAppPane().getChildren().addAll(homePage);
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
        passwordField.setText(controller.getAccount().getName());
        HBox buttons = new HBox();
        buttons.getChildren().addAll(new Button("Update"));
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
        TextArea text = new TextArea("Lorem ipsum dolor sit amet, labore oblique signiferumque est et. Cu nec democritum repudiandae, mel labore maiestatis et, brute torquatos his ad. Oblique discere pertinacia eam no. Sea cu principes consetetur, in his quis deterruisset" +
                "\n" +
                "Oblique constituam scripserit te has, hinc wisi phaedrum ex sed. Ad consul eripuit quo, cum an ferri animal. Cum in euismod ornatus dissentiet. Ex sit purto choro disputando, quaeque expetenda habemus corpora complectitur.\n" +
                "\n" +
                "Posse harum ut sea, ut vel doming lobortis. Vim eu liber doming interesset, falli abhorreant intellegam ius ad. Sed liber postea ea, id pri molestie senserit repudiare. Pro noster platonem definitionem eu.\n" +
                "\n" +
                "Vim an discere adolescens, cetero tamquam voluptaria vel cu, ius ex nibh errem ancillae. Qui elitr graece ei. Ex est pertinacia temporibus, ut pro tantas dignissim efficiantur. In suscipit reprehendunt vituperatoribus, semper eirmod admodum eos ne.\n" +
                "\n" +
                "In mel oratio impedit, nostrud invidunt conceptam mei ei. Et per ipsum audire, errem persius moderatius ut pro. Tollit cotidieque concludaturque eos ad, cu his erant dolorem phaedrum, est cu vidisse facilisi. Graece nominavi singulis ad i" +
                "\n" +
                "Vix ea vidit quidam, ex summo numquam nam. Recteque vituperata in est. Pro graece oporteat referrentur id, cum eu elitr verear appareat, te est error everti definitiones. Unum dolorum forensibus cu sed, pri at prodesset constituam" +
                "\n" +
                "Cu elitr minimum petentium mea, ex sale legere facete ius, no sea zril argumentum scribentur. Vide novum iracundia ei sea, quidam gubergren vis ne. Sit aeque fastidii voluptatum no. Sit ut graeco salutandi, ne ceteros senserit nam.\n" +
                "\n" +
                "Usu no eripuit dissentias, an pri prima doming lobortis. Iudico dolorum expetenda sea ut, laudem temporibus in vis. Ei facer noster quo, at vel justo integre maiestatis. Eu veniam partiendo eloquentiam vis, qui stet erroribus" +
                "\n" +
                "Ius etiam harum persequeris te. Ut qui habeo vitae tollit. Pro eu facete tractatos disputationi, an magna erant probatus ius. Nam ubique mediocrem vituperatoribus at, mel meis luptatum in, enim verterem nam no. Ei dictas nusquam per" +
                "\n" +
                "Eos modo debet omittantur cu, homero sapientem sit et, vis eu doming periculis sententiae. Pro vidit modus neglegentur ut. Ludus admodum propriae ex pro. Everti laoreet persecuti eos cu, wisi bonorum eu mei. Te nihil appareat vel.");

        text.setPrefSize(500, 400);
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
                                ""
                );
            }
        }

        int lvl = 1;

        //generate grid for game play
        GridGenerator gridGenerator = new GridGenerator();
        grid = gridGenerator.getGrid(getMode(modeSelectionButton.getValue().toString()),lvl);
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

        //initialize target score UI
        Pane target = new Pane();
        target.setStyle("-fx-background-color: white");
        target.getChildren().addAll(new Label("Target: "+ lvl*10 + " point"));
        target.setLayoutX(630);
        target.setLayoutY(470);
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

        //add all panes into gamePlay
        gamePlayPane.getChildren().add(p);
        gamePlayPane.getChildren().addAll(remainingTimeLabel, modeLabel, guessing, pauseResumeButton, level, words, target);//, curr, guess);
    }

    private void setUp(int i, int j){
        nodes[i][j].setOnDragDetected(e->{
            gamePlayPane.startFullDrag();
        });
        nodes[i][j].setOnMouseDragEntered(e->{
            ((DropShadow)nodes[i][j].getEffect()).setOffsetY(0);
            ((DropShadow)nodes[i][j].getEffect()).setOffsetX(0);
            ((DropShadow) nodes[i][j].getEffect()).setRadius(5);
            ((DropShadow) nodes[i][j].getEffect()).setSpread(2);
            ((DropShadow)nodes[i][j].getEffect()).setColor(Color.RED);
            dragging.add(nodes[i][j]);
            currGuess.setText(currGuess.getText() + grid[i][j] + " ");
        });
        nodes[i][j].setOnMouseDragReleased(e->{
            for (int s =0; s < dragging.size(); s++){
                ((DropShadow) dragging.get(s).getEffect()).setRadius(0);
                ((DropShadow) dragging.get(s).getEffect()).setSpread(0);
                ((DropShadow) dragging.get(s).getEffect()).setColor(Color.BLACK);
            }
            dragging.clear();
            System.out.println(currGuess.getText());
            allGuessedWords.getChildren().add(new Label (currGuess.getText() + "    30"));
            currGuess.setText("");

        });
        nodes[i][j].setOnAction(e->{
            ((DropShadow)nodes[i][j].getEffect()).setOffsetY(0);
            ((DropShadow)nodes[i][j].getEffect()).setOffsetX(0);
            ((DropShadow) nodes[i][j].getEffect()).setRadius(5);
            ((DropShadow) nodes[i][j].getEffect()).setSpread(2);
            ((DropShadow)nodes[i][j].getEffect()).setColor(Color.RED);
            currGuess.setText(currGuess.getText() + " " + grid[i][j]);
        });
    }
    private void setUpHandlers(){
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

    public String getMode(String mode){
        String m = "";
        switch (mode){
            case "PLACES":
                m = "Places"; break;
            case "FAMOUS_PEOPLE":
                m = "Famous"; break;
            case "DICTIONARY":
                m = "Dictionary"; break;
            case "SCIENCE":
                m = "Science"; break;
        }
        return m;
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
