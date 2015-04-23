/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package candidateGeneration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

/**
 *
 * @author Anonymous_for_IJCAI
 */
public class SentenceSamples {
    public static String sentence_detect_model="C:\\Users\\Anonymous_for_IJCAI\\Workspace\\trivia\\Code\\knowledgeExtraction\\src\\models\\en-sent.zip";
    public static void main(String[] args) throws FileNotFoundException, IOException {
        InputStream is = new FileInputStream(sentence_detect_model);
        SentenceModel model = new SentenceModel(is);
        SentenceDetectorME sdetector = new SentenceDetectorME(model);
        
        File writeFile = new File("C:\\Users\\Anonymous_for_IJCAI\\Workspace\\trivia\\Data\\wikiText\\HollywoodActors\\celebrities_wiki.txt");
        writeFile.createNewFile();
        FileWriter writer = new FileWriter(writeFile); 

        String folderPath = "C:\\Users\\Anonymous_for_IJCAI\\Workspace\\trivia\\Data\\wikiText\\HollywoodActors\\input\\";
        File[] files = new File(folderPath).listFiles();
        for (File file : files) {
            if(file.isFile()){
                String name = file.getName();
                name = name.replace("_", " ");
                name = name.replace("%28", "(");
                name = name.replace("%29", ")");
                name = name.replace(".txt", "");
                System.out.println("File: " + name);
                
                FileReader inputFile = new FileReader(folderPath + file.getName());
                BufferedReader bufferReader = new BufferedReader(inputFile);
                String input;
                while((input = bufferReader.readLine()) != null)
                {
                    String sentences[] = sdetector.sentDetect(input);
                    for(int i=0;i<sentences.length;i++){
                        //System.out.println(name + "\t" + sentences[i]);
                        writer.write(name + "\t" + sentences[i] + "\n");
                        //writer.write(movieName + "\t" + sentences[i] + "\n");
                    }
                }
                bufferReader.close();
            }
            writer.flush();
        }
        writer.close();
    }
}
