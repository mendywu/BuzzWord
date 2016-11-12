package ui;

import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * Created by Mendy on 10/31/2016.
 */
public class LetterNode extends Pane {

    Text letter;

    public LetterNode (Text letter){
        this.getChildren().add(letter);
    }

}
