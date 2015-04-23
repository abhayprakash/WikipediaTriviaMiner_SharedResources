/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package featureExtractor;

import static featureExtractor.EntityLinker.Out_subjWords;
import static featureExtractor.NLPFeatures.inputFilePath;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Anonymous_for_IJCAI
 */
public class svmLight_FormatWriter {
    static String folderPath = "C:\\Users\\Anonymous_for_IJCAI\\Workspace\\trivia\\Data\\IMDb\\anotherSelected5k\\MORE_DATA\\rankTemp\\";
    static String infilePath = folderPath + "train_features.txt";
    static String outfilePath = folderPath + "train_features_svmLight.txt";
    static String featureMapFilePath = folderPath + "featureMap.txt";
    
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        if(args.length > 1)
        {
            infilePath = args[0];
            outfilePath = args[1];
            featureMapFilePath = args[2];
        }
        
        FileReader inputFile = new FileReader(infilePath);
        BufferedReader bufferReader = new BufferedReader(inputFile);
        FileWriter fw = new FileWriter(outfilePath);
        BufferedWriter bw = new BufferedWriter(fw);
        FileWriter fw2 = new FileWriter(featureMapFilePath);
        BufferedWriter bw2 = new BufferedWriter(fw2);
        
        String input;
        int lineNum = 0;
        input = bufferReader.readLine();
        String[] featureNames = input.split("\t");
        int id = 0, movie_position = 0, rank_position = 0;
        
        for(String f: featureNames)
        {
            id++;
            if(f.equals("MOVIE"))
            {
                continue;
                //movie_position = id;
            }
            else if(f.equals("train_validate_codes"))
            {
                continue;
                //rank_position = id;
            }
            bw2.write(Integer.toString(id) + "\t" + f + "\n");
        }
        bw2.close();
        
        id = 0; movie_position = 0; rank_position = 0;
        for(String f: featureNames)
        {
            id++;
            if(f.equals("MOVIE"))
            {
                movie_position = id;
            }
            else if(f.equals("train_validate_codes"))
            {
                rank_position = id;
            }
            
            if(movie_position != 0 && rank_position != 0)
                break;
        }
        
        HashMap<String, Integer> movie_to_id = new HashMap<>();
        int next_Movie = 1;
        while((input = bufferReader.readLine()) != null)
        {
            String values[] = input.split("\t");
            int MOVIE = 1, RANK = 1;
            
            HashMap<Integer, Double> storedValue = new HashMap<>();
            
            int curr_id = 0, feature_id = 0;
            for(String v: values)
            {
                curr_id++;
                if(curr_id == movie_position)
                {
                    if(movie_to_id.containsKey(v))
                        MOVIE = movie_to_id.get(v);
                    else
                    {
                        MOVIE = next_Movie;
                        movie_to_id.put(v, next_Movie);
                        next_Movie++;
                    }
                }
                else if(curr_id == rank_position)
                {
                    RANK = Integer.parseInt(v);
                }
                else
                {
                    feature_id++;
                    storedValue.put(feature_id, Double.parseDouble(v));
                }
            }
            
            bw.write(RANK + " qid:" + MOVIE);
            
            SortedSet<Integer> keys = new TreeSet<Integer>(storedValue.keySet());
            for (Integer key : keys) { 
               Double value = storedValue.get(key);
               if(value != 0)
                  bw.write(" " + key + ":" + value);
            }
            bw.write("\n");
            lineNum++;
        }
        System.out.println("Done: " + lineNum);
        bw.close();
    }
}
