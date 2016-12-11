package data;

import BuzzWord.GameMode;
import com.fasterxml.jackson.annotation.*;
/**
 * Created by Mendy on 10/31/2016.
 */
public class GameData {
    @JsonIgnore
    public static final int NUM_LEVELS = 4;
    @JsonIgnore
    GameAccount account;

    @JsonProperty
    GameMode mode;

    @JsonProperty
    boolean[] unlocked_levels;

    int num_unlocked = 0;
    int readingNum = 0;
    int[] personal_bests;

    public GameData(GameAccount account, GameMode mode) {
            this.account = account;
            this.mode = mode;
            unlocked_levels = new boolean[NUM_LEVELS];
            personal_bests = new int[NUM_LEVELS];

            for (int i = 0; i < NUM_LEVELS; i++){
                unlocked_levels[i] = false;
                personal_bests[i] = 0;
            }

            unlocked_levels[0] = true;
    }

    public void unlockNext(){
        num_unlocked++;
        if (num_unlocked < NUM_LEVELS) {
            unlocked_levels[num_unlocked] = true;
            System.out.println("Level " + (num_unlocked) + " unlocked.");
        } else
            System.out.println("No more unlocks!");
    }

    public void reset(){
        readingNum = 0;
    }

    public void readingUnlock (boolean unlock){
        if (readingNum < unlocked_levels.length) {
            if (unlock) {
                unlocked_levels[readingNum] = true;
                if (readingNum != 0)
                    num_unlocked++;
            }
            readingNum++;
        }
    }

    public void readBests(int i){
        if (readingNum < personal_bests.length) {
            personal_bests[readingNum] = i;
            readingNum++;
        }
    }

    public String getMode(){
        return mode.name();
    }

    @JsonGetter
    public boolean[] getUnlocked_levels() {
        return unlocked_levels;
    }

    @JsonGetter
    public int[] getPersonal_bests() {
        return personal_bests;
    }

    public void newPersonalBest(int score, int level){
        personal_bests[level] = score;
    }
}
