/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package candidateGeneration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Anonymous_for_IJCAI
 */
public class readabilityIndex {
    static String folderPath = "C:\\Users\\Anonymous_for_IJCAI\\Workspace\\trivia\\Data\\IMDb\\anotherSelected5k\\STRICT_NEG\\";
    static String inputFilePath = "trivia.txt";
    static String resultFilePath = "fogIndex.txt";
    public static void main(String[] args) throws IOException
    {
        FileWriter fw = new FileWriter(folderPath + resultFilePath);
        BufferedWriter bw = new BufferedWriter(fw);
        
        FileReader inputFile = new FileReader(folderPath + inputFilePath);
        BufferedReader bufferReader = new BufferedReader(inputFile);
        String input;
        int lineNum = 0;
        
        while((input = bufferReader.readLine()) != null)
        {
            //NOTE: WHEN I WILL TAKE MULTIPLE LINES' TRIVIA, then I will have to do it for each sentence
            String[] words = input.replace("\"", "").trim().split(" ");
            int countWords = words.length;
            int complexWords = 0;
            for(String w: words)
            {
                if(countSyllables(w) > 2)
                    complexWords++;
            }
            double fogIndex = 0.4 * (countWords + 100*(complexWords/countWords));
            bw.write(fogIndex + "\n");
        }
        bw.close();
    }
    
    public static int countSyllables(String word)
    {
        char[] vowels = { 'a', 'e', 'i', 'o', 'u', 'y' };
        char[] currentWord = word.toCharArray();
        int numVowels = 0;
        boolean lastWasVowel = false;
        for (char wc : currentWord) {
            boolean foundVowel = false;
            for (char v : vowels)
            {
                if ((v == wc) && lastWasVowel)
                {
                    foundVowel = true;
                    lastWasVowel = true;
                    break;
                }
                else if (v == wc && !lastWasVowel)
                {
                    numVowels++;
                    foundVowel = true;
                    lastWasVowel = true;
                    break;
                }
            }
            
            if (!foundVowel)
                lastWasVowel = false;
        }
        
        if (word.length() > 2 && word.substring(word.length() - 2) == "es")
            numVowels--;
        
        else if (word.length() > 1 && word.substring(word.length() - 1) == "e")
            numVowels--;
        return numVowels;
    }
}
