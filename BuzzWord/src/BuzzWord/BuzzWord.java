package BuzzWord;

import apptemplate.AppTemplate;
import components.AppComponentsBuilder;
import components.AppDataComponent;
import components.AppFileComponent;
import components.AppWorkspaceComponent;
import data.GameAccount;
import data.GameAccountFile;
import ui.Workspace;

/**
 * Created by Mendy on 10/31/2016.
 */
public class BuzzWord extends AppTemplate {
    public static void main(String[] args) {
        launch(args);
    }

    public String getFileControllerClass() {
        return "BuzzWordController";
    }

    @Override
    public AppComponentsBuilder makeAppBuilderHook() {
        return new AppComponentsBuilder() {
            @Override
            public AppDataComponent buildDataComponent() throws Exception {
                return new GameAccount(BuzzWord.this);
            }

            @Override
            public AppFileComponent buildFileComponent() throws Exception {
                return new GameAccountFile();
            }

            @Override
            public AppWorkspaceComponent buildWorkspaceComponent() throws Exception {
                return new Workspace(BuzzWord.this);
            }
        };
    }
}
