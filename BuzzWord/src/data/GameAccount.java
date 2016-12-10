package data;

import BuzzWord.GameMode;
import apptemplate.AppTemplate;
import components.AppDataComponent;

import java.util.EnumMap;
import com.fasterxml.jackson.annotation.*;

/**
 * Created by Mendy on 10/31/2016.
 */
public class GameAccount implements AppDataComponent{
    @JsonIgnore
    AppTemplate appTemplate;

    @JsonProperty("USER_NAME")
    private String user_name;

    @JsonProperty("PASSWORD")
    private String password;

    @JsonPropertyOrder
    EnumMap<GameMode, GameData> data;

    public GameAccount(AppTemplate appTemplate){
        this(appTemplate, "", "");
    }

    public GameAccount(AppTemplate app, String user, String pw){
        appTemplate = app;
        user_name = user;
        password = pw;
        data = new EnumMap<GameMode, GameData>(GameMode.class);
        data.put(GameMode.DICTIONARY, new GameData(this, GameMode.DICTIONARY));
        data.put(GameMode.PEOPLE, new GameData(this,GameMode.PEOPLE));
        data.put(GameMode.FOOD, new GameData(this,GameMode.FOOD));
        data.put(GameMode.ANIMALS, new GameData(this,GameMode.ANIMALS));
}

    @Override
    public void reset() {
        user_name = null;
        password = null;
        data.clear();
        data.put(GameMode.DICTIONARY, new GameData(this, GameMode.DICTIONARY));
        data.put(GameMode.PEOPLE, new GameData(this,GameMode.PEOPLE));
        data.put(GameMode.FOOD, new GameData(this,GameMode.FOOD));
        data.put(GameMode.ANIMALS, new GameData(this,GameMode.ANIMALS));
    }

    @JsonSetter
    public void setUser(String name){
        user_name = name;
    }

    @JsonSetter
    public void setPassword(String password){
        this.password = password;
    }

    @JsonGetter
    public String getName(){
        return user_name;
    }

    @JsonGetter
    public String getPassword(){
        return password;
    }


    public GameData getModeData(GameMode mode){
        GameData f = data.get(mode);
        return f;
    }
}
