package controller;

import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;

/**
 * Generates a grid for BuzzWord gameplay
 * Created by Mendy on 11/27/2016.
 */
public class GridGenerator {

    static final int DICTIONARY_LENGTH = 964;   // num of words in dictionary mode
    static final int PLACES_LENGTH = 192;       // num of words in animal mode
    static final int SCIENCE_LENGTH = 228;      // num of words in food mode
    static final int FAMOUS_LENGTH = 270;       // num of words in people mode

    //initializes all possible words into TreeSets
    TreeSet<String> dictionary = initializeTree("DICTIONARY");
    TreeSet<String> food = initializeTree("FOOD");
    TreeSet<String> animals = initializeTree("ANIMALS");
    TreeSet<String> people = initializeTree("PEOPLE");

    public LinkedList<LetterNode>[] adjGraph;          // adjacency graph for ease in graph searching
    public HashSet<String> ans = new HashSet<>();      // solution set
    boolean[][]     used = new boolean[4][4];          // if a certain index has been used
    char[][]        grid = new char[4][4];             // letter grid
    BuzzWordController controller;                     // controller class for manipulation
    char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F','G','H','I','J','K','L','M','N','O','P',
            'Q','R','S','T','U','V','W','X','Y','Z',};

    public GridGenerator (BuzzWordController controller){
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                grid[i][j] = '?';
                used[i][j] = false;
            }
        }
        this.controller = controller;
        adjGraph = new LinkedList[16];
        for (int i = 0; i < 16; i++) {
            adjGraph[i] = new LinkedList<LetterNode>();
            adjGraph[i].add(new LetterNode(i, '-', -1,-1));
        }
    }

    /**
     * gets the generated grid
     * @param mode the mode
     * @param numWords minimum numbers of words
     * @return grid in char[][] format
     */
    public char[][] getGrid(String mode, int numWords){
        ans.clear();
        System.out.println("NUMBER " + numWords);
        int length = getModeLength(mode);
        URL wordsResource = getClass().getClassLoader().getResource("words/"+ mode + ".txt");
        String words = "";
            int toSkip = new Random().nextInt(length);
            try (Stream<String> lines = Files.lines(Paths.get(wordsResource.toURI()))) {
                words = lines.skip(toSkip).findFirst().get();
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
                System.exit(1);
            }

        generate(words);

        int index = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                //System.out.print(grid[i][j]);
                adjGraph[index].getFirst().c = grid[i][j];
                if (j -1 >= 0)
                    adjGraph[index].add(adjGraph[index-1].getFirst());
                if (j +1 < 4 )
                    adjGraph[index].add(adjGraph[index+1].getFirst());
                if (i-1 >= 0 )
                    adjGraph[index].add(adjGraph[index-4].getFirst());
                if (i+1 < 4 )
                    adjGraph[index].add(adjGraph[index+4].getFirst());
                index++;
            }
            System.out.println();
        }
        checkWords(mode);
        if (ans.size() < numWords) {
            reset();
            ans.clear();
            return getGrid(mode, numWords);
        } else {
            System.out.println(ans + " " + ans.size());
            System.out.println(words);
        }

        return grid;
    }

    /**
     * returns the number of words in the mode
      * @param mode the mode
     * @return number of words in mode
     */
    private static int getModeLength(String mode) {
        int l = 0;
        switch (mode){
            case "DICTIONARY":
                l = DICTIONARY_LENGTH; break;
            case "ANIMALS":
                l = SCIENCE_LENGTH; break;
            case "PEOPLE":
                l = FAMOUS_LENGTH; break;
            case "FOOD":
                l = PLACES_LENGTH; break;
        }
        return l;
    }

    /**
     * generates a grid with a word in it
     * @param word
     *      the word to be generated
     */
    public void generate(String word){
        int randomRow = -1;
        int randomCol = -1;
        try {
            for (int i = 0; i < 1; i++) {
                System.out.println(word);
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
        } catch (Exception ex){
            generate(word);
        }
        fillGrid();
    }

    /**
     * checks if index is within range of the grid
     * @param rol the row index
     * @param col the column index
     * @return true if valid
     */
    public boolean withinRange(int rol, int col){
        if (rol < 4 && rol > -1 && col < 4 && col > -1) {
            if (used[rol][col] != true)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    /**
     * generates random number
     * @param to upper bound number
     * @return a random number
     */
    public int randomNum(int to){
        return new Random().nextInt(to);
    }

    /**
     * fill the grid with random letters
     */
    public void fillGrid(){
        boolean val = new Random().nextInt(3)==0;
        Character[] vowels = {'A', 'E','I', 'O', 'U'};
        boolean cont = new Random().nextInt(2) ==0;
        Character[] con = {'T', 'S', 'C', 'B', 'R','D', 'P', 'K'};
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid.length; j++)
                if (grid[i][j] == '?' || !Character.isAlphabetic(grid[i][j])){
                    if (val) {
                        int w = randomNum(5);
                        grid[i][j] = vowels[w];
                    } else if (cont){
                        int w = randomNum(8);
                        grid[i][j] = con[w];
                    } else {
                        int a = randomNum(alphabet.length);
                        grid[i][j] = alphabet[a];
                    }
                }
    }

    /**
     * resets the grid
     */
    public void reset(){
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid.length; j++) {
                grid[i][j] = '?';
                used[i][j] = false;
            }

        for (int i = 0; i < 16; i++) {
            adjGraph[i].clear();
            adjGraph[i].add(new LetterNode(i, '-', -1,-1));
        }
        for (int i = 0; i < 16; i++) {
            adjGraph[i].add(new LetterNode(i, '-', -1,-1));
        }
    }

    /**
     * if the grid is all used up
     * @param done the vector
     * @return true if all done
     */
    public boolean allDone (boolean[] done){
        for (boolean b: done)
            if (!b)
                return false;
        return true;
    }

    /**
     * intitializes a TreeSet with all words within a mode
     * @param mode the mode
     * @return the TreeSet
     */
    public TreeSet<String> initializeTree(String mode){
        TreeSet<String> set = new TreeSet<String>();
        List<String> list = null;
        URL wordsResource = getClass().getClassLoader().getResource("words/"+ mode + ".txt");
        try (Stream<String> lines = Files.lines(Paths.get(wordsResource.toURI()))) {
            list = lines.collect(Collectors.toList());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            System.exit(1);
        }

        for (int i = 0; i < list.size(); i++){
            //System.out.println(list.get(i));
            set.add(list.get(i));
        }

        return set;
    }

    /**
     * searches a grid the word
     * @param visited
     *      if the index has been visited
     * @param adjList
     *      the adjacency list
     * @param v
     *      the index
     * @param originalWord
     *      the original word
     * @param word
     *      the remaining word
     */
    public void searchGrid (boolean[] visited, LinkedList<LetterNode>[] adjList, int v, String originalWord,String word){
        visited[v] = true;
        word = word.toUpperCase();
        if (word.length() == 1) {
            for (int i = 1; i < adjList[v].size(); i++) {
                if (word.charAt(0) == adjList[v].get(i).c && !visited[adjList[v].get(i).index]) {
                    System.out.println(originalWord + " : " + word);
                    ans.add(originalWord.toLowerCase());
                    return;
                }
            }
            return;
        }
        for (int i = 1; i < adjList[v].size(); i++) {
            if (word.charAt(0) == adjList[v].get(i).c && !visited[adjList[v].get(i).index]) {
                int newV = adjList[v].get(i).index;
                if (word.length() > 1) {
                    searchGrid(visited, adjList, newV, originalWord, word.substring(1));
                }
            }
        }
    }

    /**
     * check for all words within a grid
     * @param mode the mode
     * @return a boolean
     */
    public boolean checkWords(String mode){
        TreeSet<String> modeTree = null;
        switch (mode){
            case "DICTIONARY":
                modeTree = (dictionary);
                break;
            case "FOOD":
                modeTree = (food);
                break;
            case "PEOPLE":
                modeTree = (people);
                break;
            case "ANIMALS":
                modeTree = (animals);
                break;
        }
        boolean is = false;
        Iterator<String> it = modeTree.iterator();
        while (it.hasNext()){
            String w = it.next();
            for (int i = 0; i < 16; i++){
                if (Character.toLowerCase(adjGraph[i].get(0).c) == Character.toLowerCase(w.charAt(0))) {
                    boolean[] visited = new boolean[16];
                    searchGrid(visited, adjGraph, i, w, w.substring(1));
                }
            }
        }

        return is;
    }

    //inner class to hold a letter and its place in the grid
    class LetterNode {
        public char     c;        // the letter
        public int      index;    // the index out of 16 (4x4 grid)
        public int      i;        // the row num in grid
        public int      j;        // the col num in grid

        public LetterNode (int index, char c, int i, int j){
            this.index = index;
            this.c = c;
            this.i = i;
            this.j = j;
        }
    }
}
