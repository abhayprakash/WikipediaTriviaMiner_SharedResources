/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package featureExtractor;

import static featureExtractor.svmLight_FormatWriter.infilePath;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Anonymous_for_IJCAI
 */
public class Get_featureWeights {
    static String IDFeature_Path, IDValue_Path, FeatureValue_Path;
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        if(args.length > 1)
        {
            IDFeature_Path = args[0];
            IDValue_Path = args[1];
            FeatureValue_Path = args[2];
        }
        
        HashMap<Integer,String> id_feature = new HashMap<>();
        FileReader inputFile = new FileReader(IDFeature_Path);
        BufferedReader bufferReader = new BufferedReader(inputFile);
        
        FileWriter fw = new FileWriter(FeatureValue_Path);
        BufferedWriter bw = new BufferedWriter(fw);
                
        String input;
        while((input = bufferReader.readLine()) != null)
        {
            String[] row = input.split("\t");
            id_feature.put(Integer.parseInt(row[0]), row[1]);
        }
        
        FileReader IF = new FileReader(IDValue_Path);
        BufferedReader br = new BufferedReader(IF);
        int count = 0;
        while((input = bufferReader.readLine()) != null)
        {
            count++;
            if(count == 11)
                break;
        }
        
        input = bufferReader.readLine();
        String[] weights = input.split(" ");
        int i = 0;
        for(String w: weights)
        {
            i++;
            if(i == 1)
                continue;
            String pair = w.trim();
            String[] p = pair.split(":");
            bw.write( id_feature.get(Integer.parseInt(p[0])) + "\t" + p[1] + "\n");
        }
        bw.close();
    }
}
