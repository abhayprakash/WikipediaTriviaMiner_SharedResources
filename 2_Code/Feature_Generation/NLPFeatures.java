/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package featureExtractor;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.EnglishGrammaticalRelations;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;

/**
 *
 * @author Anonymous_for_IJCAI
 */
public class NLPFeatures {
    static String folderPath = "C:\\Users\\Anonymous_for_IJCAI\\Workspace\\trivia\\Data\\IMDb\\anotherSelected5k\\MORE_DATA\\temp\\";
    static String inputFilePath = folderPath + "trivia.txt";
    static String resultFile_Root = folderPath + "INT_D_rootWord.txt";
    static String resultFile_underRoot = folderPath + "INT_D_underRootWords.txt";
    static String resultFile_subj = folderPath + "INT_D_subjectWords.txt";
    static String resultFile_nerTypePresence = folderPath + "INT_D_nerTypePresent.txt";
    static BufferedWriter bw_root, bw_subj, bw_underRoot, bw_nerType;
    
    static List<String> ners = new ArrayList<>();
    static StanfordCoreNLP pipeline;
    
    static void processLine(String text,int lineId) throws IOException
    {
        bw_root.write(Integer.toString(lineId));
        bw_subj.write(Integer.toString(lineId));
        bw_underRoot.write(Integer.toString(lineId));
        bw_nerType.write(Integer.toString(lineId));
        
        //text = "A gigantic Hong Kong set was constructed in downtown Detroit. The set was so big that the Detroit People Mover track ended up becoming part of the set and shooting had to be adjusted to allow the track to move through the set.  ";//"One of three new television series scheduled for release in 2014 based on DC Comics characters. The others being Constantine (2014) and The Flash (2014).  ";
        HashMap<String, Integer> nerCount = new HashMap<>();
        int superlativePOS = 0;
        
        try{
            Annotation document = new Annotation(text);
            pipeline.annotate(document);

            List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

            for (CoreMap sentence : sentences) {
                SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
                // getting root words
                for(IndexedWord rword: dependencies.getRoots())
                {
                    //System.out.println(rword.lemma());
                    //System.out.println(rword.ner());
                    if(rword.ner().equals("O"))           
                        bw_root.write("\t" + rword.ner()+":"+rword.lemma());
                    //else if(rword.ner().equals("PERSON"))
                    else    
                        bw_root.write("\t" + rword.ner()+":"+rword.originalText());
                    /*
                    else
                        bw_root.write(" entity_" + rword.ner());
                    */
                    // under root
                    for(IndexedWord child: dependencies.getChildren(rword))
                    {
                        //System.out.println("here: " + child.originalText());
                        /*
                        if(child.ner().equals("PERSON"))
                            bw_underRoot.write(" " + child.originalText());
                        else*/ 
                        if(!child.ner().equals("O"))
                            bw_underRoot.write("\t" + child.ner()+":"+child.originalText());
                    }

                    // nsubj | nsubpass words
                    GrammaticalRelation[] subjects = {
                        EnglishGrammaticalRelations.NOMINAL_SUBJECT,
                        EnglishGrammaticalRelations.NOMINAL_PASSIVE_SUBJECT
                        };
                    for(IndexedWord current: dependencies.descendants(rword))
                        for(IndexedWord nsubWord : dependencies.getChildrenWithRelns(current, Arrays.asList(subjects)))
                        {
                            //System.out.println("wow: " + nsubWord.originalText());
                            if(!nsubWord.ner().equals("O"))
                                bw_subj.write("\t" + nsubWord.ner()+":"+nsubWord.originalText());
                            else
                            {
                                //System.out.println(nsubWord.lemma());
                                bw_subj.write("\t" + nsubWord.ner()+":"+nsubWord.lemma());
                            }/*
                            else
                                bw_subj.write(" entity_"+nsubWord.ner());
                            */
                        }                
                }



                // NER Types frequency
                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                    String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);

                    if(pos.equals("JJS") || pos.equals("RBS"))
                        superlativePOS++;

                    nerCount.putIfAbsent(ne, 0);
                    nerCount.put(ne, nerCount.get(ne) + 1);
                }

                //System.out.println("dependency graph:\n" + dependencies);
            }
        }
        catch(Exception e)
        {
            System.out.println("IGNORED:");
        }
        
        bw_nerType.write("\t" + Integer.toString(superlativePOS));

        for (String ne : ners) {
            if(nerCount.containsKey(ne))
                bw_nerType.write("\t" + nerCount.get(ne).toString());
            else
                bw_nerType.write("\t0");
        }
        bw_root.write("\n");
        bw_underRoot.write("\n");
        bw_nerType.write("\n");
        bw_subj.write("\n");
        if(lineId%25 == 0)
        {
            bw_root.flush();
            bw_underRoot.flush();
            bw_nerType.flush();
            bw_subj.flush();
        }
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
        FileWriter fw = new FileWriter(resultFile_Root);
        bw_root = new BufferedWriter(fw);
        fw = new FileWriter(resultFile_subj);
        bw_subj = new BufferedWriter(fw);
        fw = new FileWriter(resultFile_underRoot);
        bw_underRoot = new BufferedWriter(fw);
        fw = new FileWriter(resultFile_nerTypePresence);
        bw_nerType = new BufferedWriter(fw);
        
        ners.add("PERSON");
        ners.add("ORGANIZATION");
        ners.add("DATE");
        ners.add("LOCATION");
        ners.add("MONEY");
        ners.add("TIME");
        
        bw_nerType.write("lineID\tsuperPOS");
        for(String s: ners)
        {
            bw_nerType.write("\t" + s);
        }
        bw_nerType.write("\n");
        
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
        pipeline = new StanfordCoreNLP(props);
        
        FileReader inputFile = new FileReader(inputFilePath);
        BufferedReader bufferReader = new BufferedReader(inputFile);
        String input;
        int lineNum = 0;
        //input = bufferReader.readLine();
        while((input = bufferReader.readLine()) != null)
        {
            processLine(input, lineNum);
            System.out.println(lineNum);
            lineNum++;
            //break;
        }
        bw_root.close();
        bw_subj.close();
        bw_underRoot.close();
        bw_nerType.close();
    }
}

