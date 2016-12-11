package controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
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

    class LetterNode {
        public char c;
        public int num;
        public int index;
        public int i;
        public int j;

        public LetterNode (int index, char c, int i, int j){
            this.index = index;
            this.c = c;
            this.i = i;
            this.j = j;
        }
    }

    static final int DICTIONARY_LENGTH = 813;
    static final int PLACES_LENGTH = 55;
    static final int SCIENCE_LENGTH = 226;
    static final int FAMOUS_LENGTH = 267;

    HashSet<String> ans = new HashSet<>();
    TreeSet<String> dictionary = initializeTree("DICTIONARY");
    TreeSet<String> food = initializeTree("FOOD");
    TreeSet<String> animals = initializeTree("ANIMALS");
    TreeSet<String> people = initializeTree("PEOPLE");

    HashSet<String> process = new HashSet<String>();
//    boolean[] copy4 = Arrays.copyOf(visited, 16);
//    boolean[] copy6 = Arrays.copyOf(visited, 16);
//    boolean[] copy5 = Arrays.copyOf(visited, 16);


    char[][] grid = new char[4][4];
    boolean[][] used = new boolean[4][4];
    char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F','G','H','I','J','K','L','M','N','O','P',
            'Q','R','S','T','U','V','W','X','Y','Z',};
    public LinkedList<LetterNode>[] adjGraph;

    public GridGenerator (){
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                grid[i][j] = '?';
                used[i][j] = false;
            }
        }
        adjGraph = new LinkedList[16];
        for (int i = 0; i < 16; i++) {
            adjGraph[i] = new LinkedList<LetterNode>();
            adjGraph[i].add(new LetterNode(i, '-', -1,-1));
        }
    }

    public char[][] getGrid(String mode, int numWords){
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
        numWords = 0;

        int index = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j]);
                adjGraph[index].getFirst().c = grid[i][j];
                System.out.print(index + " ");
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


        for (int s = 0; s < 16; s++) {
            for (int j = 0; j < adjGraph[s].size(); j++) {
                System.out.print(adjGraph[s].get(j).c + " ");
            }
            System.out.println();
        }

        checkWords();
//        boolean[] visited = new boolean[16];
//        for (int i = 0; i < 16; i++) {
//            searchGrid(visited, adjGraph, i, Character.toLowerCase(adjGraph[i].get(0).c) + "");
//            visited = new boolean[16];
//        }
        if (ans.size() < 5) {
            reset();
            ans.clear();
            return getGrid(mode, numWords);
        } else {
            System.out.println(ans + " " + ans.size());
            System.out.println(words);
            ans.clear();
        }

        return grid;
    }

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

    public int randomNum(int to){
        return new Random().nextInt(to);
    }

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

    public boolean allDone (boolean[] done){
        for (boolean b: done)
            if (!b)
                return false;
        return true;
    }

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

    public void searchGrid (boolean[] visited, LinkedList<LetterNode>[] adjList, int v, String originalWord,String word){
        visited[v] = true;
        word = word.toUpperCase();
        if (word.length() == 1) {
            for (int i = 1; i < adjList[v].size(); i++) {
                if (word.charAt(0) == adjList[v].get(i).c && !visited[adjList[v].get(i).index]) {
                    System.out.println(originalWord + " : " + word);
                    ans.add(originalWord);
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

    public boolean checkWords(){
        boolean is = false;
        Iterator<String> it = dictionary.iterator();
        while (it.hasNext()){
            String w = it.next();
            for (int i = 0; i < 16; i++){
                if (Character.toLowerCase(adjGraph[i].get(0).c) == w.charAt(0)) {
                    boolean[] visited = new boolean[16];
                    searchGrid(visited, adjGraph, i, w, w.substring(1));
                }
            }
        }

        return is;
    }
}
