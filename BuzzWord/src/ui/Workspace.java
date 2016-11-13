package ui;

import apptemplate.AppTemplate;
import components.AppWorkspaceComponent;
import controller.BuzzWordController;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import propertymanager.PropertyManager;
import static ui.HangmanProperties.*;
/**
 * Created by Mendy on 10/31/2016.
 */
public class Workspace extends AppWorkspaceComponent {

    BuzzWordController controller;
    Boolean loggedIn = false;
    AppTemplate app; // the actual application
    AppGUI      gui; // the GUI inside which the application sits

    DropShadow shadow = new DropShadow();
    Boolean createNew = false;

    Label       guiHeadingLabel;
    Label       targetLabel;
    Label       remainingTimeLabel;
    Button      createProfileButton = new Button("Create New Profile");
    Button      profileSettingsButton;
    final ComboBox      modeSelectionButton = new ComboBox<>();
    Button      lvlSelectionButton = new Button("Start Playing");

    Button      nextButton;
    Button      pauseResumeButton;
    Button      replayButton;
    TextField   userField = new TextField();
    PasswordField passwordField = new PasswordField();
    public Pane homePage = new Pane();
    public Pane helpPage = new Pane();
    BorderPane loginPanel = new BorderPane();
    Button create = new Button("Create New");
    Button cancelButton = new Button("Cancel");
    Button login;
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
    Pane levelSelectPage = new Pane();
    Rectangle rect = new Rectangle(800/5, 550);

    public Workspace(AppTemplate initApp)  {
        app = initApp;
        gui = app.getGUI();
        guiHeadingLabel = new Label("BuzzWord!");
        guiHeadingLabel.setLayoutX(400);

        controller = (BuzzWordController) gui.getFileController();
        logInOutButton = new Button("Login");
        login = new Button("Login");
        workspace = new Pane();
        workspace.getChildren().clear();
        int r = 65;
        int y = 130;
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes.length; j++) {
                nodes[i][j] = new Button();
                Button thisButton = nodes[i][j];
                thisButton.setOnAction(e->{
                    System.out.println(thisButton.getText());
                });
                thisButton.setLayoutY(y);
                thisButton.setLayoutX(250 + r);
                thisButton.setStyle(
                        "-fx-background-radius: 5em; " +
                                "-fx-min-width: 60px; " +
                                "-fx-min-height: 60px; " +
                                "-fx-max-width: 60px; " +
                                "-fx-max-height: 60px;"+
                                "-fx-background-color: white"
                );
                thisButton.setEffect(shadow);
                //pane.getChildren().add(nodes[i][j]);
                thisButton.setDisable(true);
                thisButton.setVisible(false);
                r+=85;
            }
            r = 65;
            y+= 75;
        }
        updateHomePage();
        workspace.getChildren().addAll(homePage);
        //gui.getAppPane().getChildren().addAll(homePage);
        modeSelectionButton.getItems().addAll(
                "             English Dictionary",
                "             Places",
                "             Famous People",
                "             Science"
        );

        modeSelectionButton.setValue(new Label("             Select Mode"));
        activateWorkspace(gui.appPane);
        setUpHandlers();

        shadow.setOffsetY(10);
        shadow.setOffsetX(5);
        shadow.setColor(Color.LIGHTSLATEGRAY);
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

        rect.setFill(Color.GRAY);
        Pane pane = new Pane();
        createProfileButton.setPrefSize(250,70);
        createProfileButton.setLayoutX(800/5 - 200);
        createProfileButton.setLayoutY(100);
        logInOutButton.setPrefSize(250,70);
        logInOutButton.setLayoutX(800/5 - 200);
        logInOutButton.setLayoutY(200);

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
        for (int i = 0; i < nodes.length; i++)
            for (int j= 0; j < nodes.length; j++) {
                pane.getChildren().add(nodes[i][j]);
                nodes[i][j].setVisible(true);
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
        if (createNew)
            buttons.getChildren().addAll(create,cancelButton);
        else
            buttons.getChildren().addAll(login,cancelButton);
        buttons.setAlignment(Pos.CENTER);
        panel.getChildren().addAll(guiHeadingLabel,userName, pw, buttons);
        panel.setAlignment(Pos.CENTER);
        loginPanel.getChildren().add(guiHeadingLabel);
        loginPanel.setCenter(panel);
    }

    private void updateHelpPage(){
        PropertyManager propertyManager = PropertyManager.getManager();
        helpPage.prefHeightProperty().bind(gui.getPrimaryScene().heightProperty());
        helpPage.prefWidthProperty().bind(gui.getPrimaryScene().widthProperty());
        Label help = new Label("BuzzWord! Help Page");
        help.setLayoutX(230);
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

        text.setPrefSize(750, 400);
        text.setLayoutY(100);
        text.setLayoutX(50);
        text.setWrapText(true);
        text.setEditable(false);

        helpPage.getChildren().addAll(help,text);
    }

    private void updateLvlSelect(){
        String mode = modeSelectionButton.getValue().toString();
        Label modeLabel = new Label(mode);
        modeLabel.setLayoutX(400);
        modeLabel.setLayoutY(200);
        int label = 1;
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes.length; j++) {
                if (i < 2) {
                    nodes[i][j].setText(label + "");
                    levelSelectPage.getChildren().add(nodes[i][j]);
                    label++;
                    if (j < 5)
                        nodes[i][j].setDisable(false);
                }
//                else
//                    nodes[i][j].setVisible(false);
            }
        }
        levelSelectPage.getChildren().addAll(modeLabel);
    }

    private void setUpHandlers(){
        logInOutButton.setOnAction(e->{
            workspace.getChildren().clear();
            updateLoginPanel();
            workspace.getChildren().add(loginPanel);
        });
        create.setOnAction(e->{
            workspace.getChildren().clear();
            loggedIn = true;
            updateHomePage();
            workspace.getChildren().add(homePage);
        });
        createProfileButton.setOnAction(e->{
            workspace.getChildren().clear();
            createNew = true;
            updateLoginPanel();
            workspace.getChildren().addAll(loginPanel);
        });
        cancelButton.setOnAction(e->{
            workspace.getChildren().clear();
            createNew = false;
            loggedIn = false;
            workspace.getChildren().addAll(homePage);
        });
        login.setOnAction(e ->{
            workspace.getChildren().clear();
            loggedIn = true;
            updateHomePage();
            workspace.getChildren().add(homePage);
        });
        helpButton.setOnAction(e -> {
            workspace.getChildren().clear();
            updateHelpPage();
            workspace.getChildren().add(helpPage);
        });
        lvlSelectionButton.setOnAction(e->{
            workspace.getChildren().clear();
            updateLvlSelect();
            workspace.getChildren().add(levelSelectPage);
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
