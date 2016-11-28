package controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Created by Mendy on 11/27/2016.
 */
public class GridGenerator {
    static final int DICTIONARY_LENGTH = 54;
    static final int PLACES_LENGTH = 32;
    static final int SCIENCE_LENGTH = 16;
    static final int FAMOUS_LENGTH = 51;

    char[][] grid = new char[4][4];
    boolean[][] used = new boolean[4][4];
    char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F','G','H','I','J','K','L','M','N','O','P',
            'Q','R','S','T','U','V','W','X','Y','Z',};

    public GridGenerator (){
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid.length; j++) {
                grid[i][j] = '?';
                used[i][j] = false;
            }
    }

    public char[][] getGrid(String mode, int level){
        int length = getModeLength(mode);
        URL wordsResource = getClass().getClassLoader().getResource("words/"+ mode+ " Easy.txt");
        String[] words = new String[level];
        for (int i = 0; i < level; i++){
            int toSkip = new Random().nextInt(length);
            try (Stream<String> lines = Files.lines(Paths.get(wordsResource.toURI()))) {
                words[i] = lines.skip(toSkip).findFirst().get();
                System.out.println(words[i]);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        generate(words);

        return grid;
    }

    private int getModeLength(String mode) {
        int l = 0;
        switch (mode){
            case "Dictionary":
                l = DICTIONARY_LENGTH; break;
            case "Science":
                l = SCIENCE_LENGTH; break;
            case "Famous":
                l = FAMOUS_LENGTH; break;
            case "Places":
                l = PLACES_LENGTH; break;
        }
        return l;
    }

    public void generate(String[] words){
        int randomRow = -1;
        int randomCol = -1;
        String word = "";
        for (int i = 0; i < words.length; i++) {
            word = words[i];
            while (!withinRange(randomRow, randomCol)){
                randomRow = Math.abs(new Random().nextInt(3));
                randomCol = Math.abs(new Random().nextInt(3));
            }
            for (int ch = 0; ch < word.length(); ch++){
                System.out.println(randomRow + " " + randomCol);
                grid[randomCol][randomRow] = Character.toUpperCase(word.charAt(ch));
                used[randomCol][randomRow] = true;
                int a = randomCol;
                int b = randomRow;
                boolean[] done = new boolean[6];
                while (!withinRange(a, b)) {
                    a = randomCol;
                    b = randomRow;
                    int d = randomNum(6);
                    done[d] = true;
                    switch (d) {
                        case 0:
                            a++;
                            break;
                        case 1:
                            b++;
                            break;
                        case 2:
                            a--;
                            break;
                        case 3:
                            b--;
                            break;
                        case 4:
                            a--; b--; break;
                        case 5:
                            a++; b++; break;
                    }

                    if (allDone(done)) {
                        System.out.println("Trapped!");
                        reset();
                        i--;
                        break;
                    }
                }
                randomCol = a;
                randomRow = b;
            }
        }
        fillGrid();
    }

    public boolean withinRange(int rol, int col){
        if (rol < 4 && rol >= 0 && col < 4 && col >=0) {
            if (used[rol][col] != true)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    public int randomNum(int to){
        return new Random().nextInt(to);
    }

    public void fillGrid(){
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid.length; j++)
                if (grid[i][j] == '?' || !Character.isAlphabetic(grid[i][j])){
                    int a = randomNum(alphabet.length);
                    grid[i][j] = alphabet[a];
                }
    }

    public void reset(){
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid.length; j++) {
                grid[i][j] = '?';
                used[i][j] = false;
            }
    }

    public boolean allDone (boolean[] done){
        for (boolean b: done)
            if (!b)
                return false;
        return true;
    }
}
