package controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

    static final int DICTIONARY_LENGTH = 601;
    static final int PLACES_LENGTH = 55;
    static final int SCIENCE_LENGTH = 226;
    static final int FAMOUS_LENGTH = 267;

    static final TreeSet<String> dictionary = null;
    static final TreeSet<String> food = null;
    static final TreeSet<String> animals = null;
    static final TreeSet<String> people = null;


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
        TreeSet<String> dictionary = initializeTree("DICTIONARY");
        TreeSet<String> food = initializeTree("FOOD");
        TreeSet<String> animals = initializeTree("ANIMALS");
        TreeSet<String> people = initializeTree("PEOPLE");
    }

    public char[][] getGrid(String mode, int numWords){
        int length = getModeLength(mode);
        URL wordsResource = getClass().getClassLoader().getResource("words/"+ mode + ".txt");
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

//        boolean[] visited = new boolean[16];
//        for (int i = 0; i < 16; i ++)
//            visited[i] = false;
//        searchGrid(visited, adjGraph, 0, "");
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

    public TreeSet initializeTree(String mode){
        TreeSet<String> set = new TreeSet<>();
        List<String> list = null;
        URL wordsResource = getClass().getClassLoader().getResource("words/"+ mode + ".txt");
        try (Stream<String> lines = Files.lines(Paths.get(wordsResource.toURI()))) {
            list = lines.collect(Collectors.toList());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            System.exit(1);
        }

        for (int i = 0; i < list.size(); i++){
            System.out.println(list.get(i));
            set.add(list.get(i));
        }

        return set;
    }

    public void searchGrid (boolean[] visited, LinkedList<LetterNode>[] adjList, int v, String word){
        visited[v] = true;
        LinkedList<LetterNode> queue = new LinkedList<LetterNode>();

        queue.add(adjList[v].getFirst());

        while (queue.size() != 0)
        {
            char letter = queue.poll().c;
            System.out.print(letter+" ");
            Iterator<LetterNode> i = adjList[v].listIterator();
            while (i.hasNext())
            {
                LetterNode n = i.next();
                if (!visited[n.index])
                {
                    visited[n.index] = true;
                    queue.add(n);
                }
            }
        }
    }
}
