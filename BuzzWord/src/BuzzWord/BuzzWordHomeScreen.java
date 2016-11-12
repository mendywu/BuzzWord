package BuzzWord;

import apptemplate.AppTemplate;
import components.AppComponentsBuilder;
import components.AppDataComponent;
import components.AppFileComponent;
import components.AppWorkspaceComponent;
import data.GameAccountFile;
import data.GameData;
import ui.Workspace;

/**
 * Created by Mendy on 11/11/2016.
 */
public class BuzzWordHomeScreen extends AppTemplate {
    public static void main(String[] args) {
        launch(args);
    }

    public String getFileControllerClass() {
        return "BuzzWordControllerHomeScreen";
    }

    @Override
    public AppComponentsBuilder makeAppBuilderHook() {
        return new AppComponentsBuilder() {
            @Override
            public AppDataComponent buildDataComponent() throws Exception {
                return new GameData();
            }

            @Override
            public AppFileComponent buildFileComponent() throws Exception {
                return new GameAccountFile();
            }

            @Override
            public AppWorkspaceComponent buildWorkspaceComponent() throws Exception {
                return new Workspace(BuzzWordHomeScreen.this);
            }
        };
    }
}
