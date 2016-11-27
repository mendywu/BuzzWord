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

    public char[][] getDictionaryGrid(int level){
        URL wordsResource = getClass().getClassLoader().getResource("words/words.txt");
        String[] words = new String[level];
        for (int i = 0; i < level; i++){
            int toSkip = new Random().nextInt(330622);
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

    public void generate(String[] words){
        int randomRow = new Random().nextInt(4);
        int randomCol = new Random().nextInt(4);
        while (!withinRange(randomRow, randomCol)){
            randomRow = new Random().nextInt(4);
            randomCol = new Random().nextInt(4);
        }
        System.out.println(randomRow + " " + randomCol);
        String word = "";
        for (int i = 0; i < words.length; i++) {
            word = words[i];
            for (int ch = 0; ch < word.length(); ch++){
                grid[randomCol][randomRow] = Character.toUpperCase(word.charAt(ch));
                used[randomCol][randomRow] = true;
                System.out.println(randomRow + " " + randomCol);
                int a = randomCol;
                int b = randomRow;
                while (!withinRange(a, b)) {
                    a = randomCol;
                    b = randomRow;
                    int d = randomNum(4);
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
                    }
                }
                randomCol = a;
                randomRow = b;
            }
        }
        //fillGrid();
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
                if (grid[i][j] == '?'){
                    int a = randomNum(alphabet.length);
                    grid[i][j] = alphabet[a];
                }
    }
}
