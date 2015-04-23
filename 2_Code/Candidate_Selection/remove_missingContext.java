/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package candidateGeneration;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

/**
 *
 * @author Anonymous_for_IJCAI
 */
public class remove_missingContext {
    public static String sentence_detect_model="C:\\Users\\Anonymous_for_IJCAI\\Workspace\\trivia\\Code\\knowledgeExtraction\\src\\models\\en-sent.zip";
    public static void main(String[] args) throws FileNotFoundException, IOException {
        InputStream is = new FileInputStream(sentence_detect_model);
        SentenceModel model = new SentenceModel(is);
        SentenceDetectorME sdetector = new SentenceDetectorME(model);
        
        Properties props = new Properties();
        props.put("annotators", "tokenize,ssplit,pos,lemma,ner,parse,dcoref");
        StanfordCoreNLP pi = new StanfordCoreNLP(props);

        File writeFile = new File("C:\\Users\\Anonymous_for_IJCAI\\Workspace\\trivia\\Data\\Candidate_Generation\\good_sentences_new.txt");
        writeFile.createNewFile();
        FileWriter writer = new FileWriter(writeFile); 

        File writeFile2 = new File("C:\\Users\\Anonymous_for_IJCAI\\Workspace\\trivia\\Data\\Candidate_Generation\\bad_sentences_new.txt");
        writeFile2.createNewFile();
        FileWriter writer2 = new FileWriter(writeFile2); 

        
        String folderPath = "C:\\Users\\Anonymous_for_IJCAI\\Workspace\\trivia\\Data\\movieTest\\indivFiles\\";
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
                    //System.out.println("Line: " + input);
                    String sentences[] = sdetector.sentDetect(input);
                    HashMap<Integer, Integer> toRemove = new HashMap<>();
                    Annotation doc = new Annotation(input);
                    pi.annotate(doc);
                    Map<Integer, CorefChain> graph = doc.get(CorefCoreAnnotations.CorefChainAnnotation.class);
                    
                    for (Map.Entry<Integer, CorefChain> entry : graph.entrySet()) {
                        CorefChain c = entry.getValue();
                        
                        if (c.getMentionsInTextualOrder().size() <= 1) {
                            continue;
                        }
                        
                        //System.out.println("Mentions: " + c.toString());
                        String[] sentenceOccurence = c.toString().split(" ");
                        int firstOccurence = -1;
                        for(int i = 0; i < sentenceOccurence.length; i++)
                        {
                            if(firstOccurence == -1 && sentenceOccurence[i].equals("sentence"))
                            {
                                //System.out.println("first occurence : " + sentenceOccurence[i+1]);
                                firstOccurence = Integer.parseInt(sentenceOccurence[i+1].replace(",", "").replace("]", ""));
                                continue;
                            }
                            
                            if(sentenceOccurence[i].equals("sentence"))
                            {
                                //System.out.println("further occurence : "+sentenceOccurence[i+1]);
                                if(Integer.parseInt(sentenceOccurence[i+1].replace(",", "").replace("]", "")) != firstOccurence)
                                {
                                    //System.out.println("Added " + sentenceOccurence[i+1].replace(",", "").replace("]", "") + " for removal");
                                    toRemove.put(Integer.parseInt(sentenceOccurence[i+1].replace(",", "").replace("]", "")), 1);
                                }
                            }
                        }                        
                        //System.out.println(c.toString());
                    }
                    
                    int cand_i = 1;
                    for(String candidate_sentence : sentences)
                    {
                        if(toRemove.containsKey(cand_i))
                        {
                            //System.out.println("REMOVING: " + candidate_sentence + "\n");
                            writer2.write(name + "\t" + candidate_sentence + "\n");
                            continue;
                        }
                        //System.out.println("TAKING: " + candidate_sentence + "\n");
                        writer.write(name + "\t" + candidate_sentence + "\n");
                        cand_i++;
                    }
                    //System.in.read();
                }
                //System.out.println("Line done");
                bufferReader.close();
                //System.in.read();
            }
            writer.flush();
            writer2.flush();
        }
        writer.close();
        writer2.close();
    }
}
