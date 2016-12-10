package controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Created by Mendy on 11/27/2016.
 *
 * To Do:
 * Quit button - get it to pause during game play
 * Grid Generator - find a way to increase target words
 * Target word - change it
 */
public class GridGenerator {

    class Node {
        public char c;
        public int i;
        public int j;

        public Node (char c, int i, int j){
            this.c = c;
            this.i = i;
            this.j = j;
        }
    }

    static final int DICTIONARY_LENGTH = 54;
    static final int PLACES_LENGTH = 55;
    static final int SCIENCE_LENGTH = 44;
    static final int FAMOUS_LENGTH = 52;

    char[][] grid = new char[4][4];
    boolean[][] used = new boolean[4][4];
    char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F','G','H','I','J','K','L','M','N','O','P',
            'Q','R','S','T','U','V','W','X','Y','Z',};
    LinkedList[] adjGraph = new LinkedList[16];

    public GridGenerator (){
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                grid[i][j] = '?';
                used[i][j] = false;
            }
        }
        for (int i = 0; i < 16; i++)
            adjGraph[i] = new LinkedList<Node>();
    }

    public char[][] getGrid(String mode, int numWords){
        int length = getModeLength(mode);
        URL wordsResource = getClass().getClassLoader().getResource("words/"+ mode+ " Easy.txt");
        String[] words = new String[numWords];
        for (int i = 0; i < numWords; i++){
            int toSkip = new Random().nextInt(length);
            try (Stream<String> lines = Files.lines(Paths.get(wordsResource.toURI()))) {
                words[i] = lines.skip(toSkip).findFirst().get();
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        generate(words);

        int index = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j] + " ");
                adjGraph[index].add(new Node(grid[i][j], i, j));
                if (i -1 >= 0)
                    adjGraph[index].add(new Node(grid[i-1][j], i, j));
                if (i +1 < 4)
                    adjGraph[index].add(new Node(grid[i+1][j], i, j));
                if (j-1 >= 0)
                    adjGraph[index].add(new Node(grid[i][j-1], i, j));
                if (j+1 < 4)
                    adjGraph[index].add(new Node(grid[i][j+1], i, j));
                index++;
            }
            System.out.println();
        }

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < adjGraph[i].size(); j++) {
                System.out.print(((Node)adjGraph[i].get(j)).c + " ");
            }
            System.out.println();
        }
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
        try {
            for (int i = 0; i < words.length; i++) {
                word = words[i];
                System.out.println(words[i]);
                while (!withinRange(randomRow, randomCol)) {
                    randomRow = Math.abs(new Random().nextInt(3));
                    randomCol = Math.abs(new Random().nextInt(3));
                }
                for (int ch = 0; ch < word.length(); ch++) {
                    System.out.println(randomRow + " " + randomCol);
                    grid[randomCol][randomRow] = Character.toUpperCase(word.charAt(ch));
                    used[randomCol][randomRow] = true;
                    boolean[] done = new boolean[4];
                    int a = randomCol;
                    int b = randomRow;
                    while (!withinRange(a, b)) {
                        a = randomCol;
                        b = randomRow;
                        int d = randomNum(4);
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
        }catch (Exception e){
            //e.printStackTrace();
            System.out.println("Error");
            generate(words);
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
