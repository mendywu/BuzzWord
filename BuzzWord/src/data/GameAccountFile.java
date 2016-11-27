package data;

import BuzzWord.GameMode;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import components.AppDataComponent;
import components.AppFileComponent;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

/**
 * Created by Mendy on 10/31/2016.
 */
public class GameAccountFile implements AppFileComponent {

    public static final String USER_NAME = "USER_NAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String UNLOCKED = "UNLOCKED";
    public static final String PERSONAL_BEST = "PERSONAL_BEST";

    @Override
    public void saveData(AppDataComponent data, Path filePath) throws IOException {
        GameAccount account = (GameAccount) data;
        GameData dictionary = account.getModeData(GameMode.DICTIONARY);
        GameData famous_people = account.getModeData(GameMode.FAMOUS_PEOPLE);
        GameData places = account.getModeData(GameMode.PLACES);
        GameData science = account.getModeData(GameMode.SCIENCE);

        JsonFactory jsonFactory = new JsonFactory();
        File file = new File(filePath + "//" + account.getName() + ".json");
        try (OutputStream out = Files.newOutputStream(file.toPath(), CREATE_NEW)) {
            JsonGenerator generator = jsonFactory.createGenerator(out, JsonEncoding.UTF8);
            generator.useDefaultPrettyPrinter();
            generator.writeStartObject();

            generator.writeStringField(USER_NAME, account.getName());
            generator.writeStringField(PASSWORD, account.getPassword());
            saveGameData(dictionary, generator);
            saveGameData(famous_people, generator);
            saveGameData(places, generator);
            saveGameData(science, generator);
            generator.writeEndObject();

            generator.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void saveGameData(GameData data, JsonGenerator generator){
        try {
            generator.writeObjectFieldStart(data.getMode());
            boolean[] levels = data.getUnlocked_levels();
            int[] bests = data.getPersonal_bests();

            generator.writeFieldName(UNLOCKED);
            generator.writeStartArray(GameData.NUM_LEVELS);
            for (boolean b : levels)
                generator.writeBoolean(b);
            generator.writeEndArray();

            generator.writeFieldName(PERSONAL_BEST);
            generator.writeStartArray(GameData.NUM_LEVELS);
            for (int i : bests)
                generator.writeNumber(i);
            generator.writeEndArray();

            generator.writeEndObject();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadData(AppDataComponent data, Path filePath) throws IOException {
        GameAccount account = (GameAccount) data;
        account.reset();
        int level; boolean unlocked; int personal_best;

        JsonFactory jsonFactory = new JsonFactory();
        JsonParser jsonParser  = jsonFactory.createParser(Files.newInputStream(filePath));

        while (!jsonParser.isClosed()) {
            JsonToken token = jsonParser.nextToken();
            if (JsonToken.FIELD_NAME.equals(token)) {
                String fieldname = jsonParser.getCurrentName();
                switch (fieldname) {
                    case USER_NAME:
                        jsonParser.nextToken();
                        account.setUser(jsonParser.getValueAsString());
                        break;
                    case PASSWORD:
                        jsonParser.nextToken();
                        account.setPassword(jsonParser.getValueAsString());
                        break;
                    case "DICTIONARY":
                        jsonParser.nextToken();
                        jsonParser.nextToken();
                        jsonParser.nextToken();
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY)
                            account.getModeData(GameMode.DICTIONARY).readingUnlock(jsonParser.getValueAsBoolean());

                        jsonParser.nextToken();
                        jsonParser.nextToken();
                        account.getModeData(GameMode.FAMOUS_PEOPLE).reset();
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY)
                                account.getModeData(GameMode.DICTIONARY).readBests(jsonParser.getValueAsInt());
                        break;
                    case "FAMOUS_PEOPLE":
                        jsonParser.nextToken();
                        jsonParser.nextToken();
                        jsonParser.nextToken();
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY)
                            account.getModeData(GameMode.FAMOUS_PEOPLE).readingUnlock(jsonParser.getValueAsBoolean());

                        jsonParser.nextToken();
                        jsonParser.nextToken();
                        account.getModeData(GameMode.FAMOUS_PEOPLE).reset();
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY)
                            account.getModeData(GameMode.FAMOUS_PEOPLE).readBests(jsonParser.getValueAsInt());
                        break;
                    case "PLACES":
                        jsonParser.nextToken();
                        jsonParser.nextToken();
                        jsonParser.nextToken();
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY)
                            account.getModeData(GameMode.PLACES).readingUnlock(jsonParser.getValueAsBoolean());

                        jsonParser.nextToken();
                        jsonParser.nextToken();
                        account.getModeData(GameMode.PLACES).reset();
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY)
                            account.getModeData(GameMode.PLACES).readBests(jsonParser.getValueAsInt());
                        break;
                    case "SCIENCE":
                        jsonParser.nextToken();
                        jsonParser.nextToken();
                        jsonParser.nextToken();
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY)
                            account.getModeData(GameMode.SCIENCE).readingUnlock(jsonParser.getValueAsBoolean());

                        jsonParser.nextToken();
                        jsonParser.nextToken();
                        account.getModeData(GameMode.SCIENCE).reset();
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY)
                            account.getModeData(GameMode.SCIENCE).readBests(jsonParser.getValueAsInt());
                        break;
                    default:
            }
          }

        }
    }

    @Override
    public void exportData(AppDataComponent data, Path filePath) throws IOException {

    }
}
