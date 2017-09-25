
import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Wrapper;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.ReliefFAttributeEval;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Trung
 */
public class Execution {
    //createNewSystem newSystem = new createNewSystem();
    String pathContentFeatures = "";
    String pathUserFeatures = "";
    String pathCombineContentandUserFeatures = "";
    String pathArffFile = "";
    public boolean checkDataset(File[] files) throws ParserConfigurationException, SAXException, IOException
    {
        boolean validDataset = false;
        boolean nodeListTweets = false;
        //File[] files = file.listFiles();
        for(File f : files)
        {
            if(f.getName().contains(".DS_Store"))
            {
                f.delete();
            }
            else
            {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                org.w3c.dom.Document doc = dBuilder.parse(f);
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("tweet");
                if(nList.getLength() > 0)
                {
                    nodeListTweets = true;
                    System.out.println("Correct");
                }
                
            }
        }
        if(nodeListTweets)
            validDataset = true;
        return validDataset;
    }
    public double CountNoHashTagAndMention(String str, String function)
    {
        double result = 0;
        if(str.contains("#39;"))
        {
            str = str.replace("#39;", "'");
        }
        String text[] = str.split(" ");
        if(function.equals("#"))
        {
            for (String text1 : text) {
                if (text1.matches("[#]{1}[\\w\\d]{1,}")) {
                    result += 1;
                }
            }
        }
        else if(function.equals("@"))
        {
            for(int i = 0; i < text.length; i++)
            {
                if(text[i].matches("[@]{1}[\\w\\d]{1,}"))
                {
                    result += 1;
                }
            }
        }
        return result;
    }
    public void dialogForMissingFeatures(String text)
    {
        JOptionPane.showMessageDialog(null,text);
    }
    public double getMaxNumber(ArrayList max)
    {
//        double maxValue = 0;
//        System.out.println("Max: "+ Collections.max(max));
//        maxValue = (double) Collections.max(max);
//        for(int i = 0; i < max.size(); i++)
//        {
//            Comparable temp = Collections.max(max);
//            maxValue = (double) temp;
//        }
        double maxValue = 0;
        for(int i = 0 ; i < max.size();i++)
        {
            //System.out.println(max.get(i).toString());
            String temp = max.get(i).toString();
            //System.out.println(temp);
            if(!temp.contains("this Collection"))
            {
                if(Double.parseDouble(temp) > maxValue)
                maxValue = Double.parseDouble(temp);
            }

        }
        return maxValue; 
    }
    public double getNumberOfWordsFromAText(String str)
    {
        double result = 0;
        String text[] = str.split(" ");
        result = text.length;
        return result;
    }
    public double FindRetweet(String test)
    {
        double total = 0;
        String regex = "RT";
        String[] retweet = test.split(" ");
        for(int i = 0; i < retweet.length; i++)
        {
            if(retweet[i].equals(regex))
            {
                total++;
            }
        }
        return total;
    }
    public double getConsecutiveWords(String str)
    {
        double noOfConsecutiveWords = 0;
        String regex = "\\b[A-Z]{2,}\\b";
        String[] temp = str.split(" ");
        for(int i = 0; i< temp.length; i++)
        {
            if(temp[i].matches(regex))
            {
                noOfConsecutiveWords++;
            }
        }
        return noOfConsecutiveWords;
    }
    public boolean checkDate(String dateFromTweet, Date dFrom, Date dTo)
    {
        boolean valid = false;
        try 
        {
            //EEE MMM d HH:mm:ss z yyyy   Sun May 24 00:00:00 NZST 2009
            String dateExtracted = dateFromTweet;
            //DateFormat formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
            SimpleDateFormat parseDate = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
            Date date = parseDate.parse(dateExtracted);
            if(date.after(dFrom) && date.before(dTo))
            {
                valid = true;
            }
        } 
        catch (ParseException ex) 
        {
            Logger.getLogger(createNewSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valid;
    }
    public double getUserInfo(File[] files, String screenName, String biFriendsFiles, String featureName, double noOfFollowing, ArrayList nonFollowerScreenName) throws FileNotFoundException, IOException
    {
        double result = 0;
        String bfPath = biFriendsFiles;
        String newline = System.getProperty("line.separator");
        double noOfBiFriends = 0;
        for(File bfFriends : files)
        {
            if(bfFriends.getName().replace(".csv", "").equals(screenName))
            {
                BufferedReader br = null;
                String line = "";
                br = new BufferedReader(new FileReader(bfPath+"/"+bfFriends.getName()), ','); 
                String[] splitIndex = null; 
                double numberOfMentionNonFollower = 0;
                while ((line = br.readLine()) != null)
                {
                    splitIndex = line.split(",");
                    if(featureName.equals("Age of account"))
                        result = Double.parseDouble(splitIndex[5].replace(" ", ""));
                    if(featureName.equals("Bi-Directional Links Ratio"))
                    {
                        if(Double.parseDouble(splitIndex[4].replace(" ", "")) > 0)
                            noOfBiFriends++;
                    }
                    if(featureName.equals("Fraction of mention non follower"))
                    {
                        for(int i = 0 ; i < nonFollowerScreenName.size();i++)
                        {
                            String screenNameOfFollowerInCSV = splitIndex[1];
                            if(nonFollowerScreenName.get(i).equals(screenNameOfFollowerInCSV))
                            {
                                boolean nonFollower = Boolean.parseBoolean(splitIndex[3]);//5
                                if(!nonFollower)
                                {
                                    numberOfMentionNonFollower++;
                                }
                            }
                        }
                        result = numberOfMentionNonFollower;                        
                    } 
                } 
                //System.out.println(result);
            }
        }
        if(featureName.equals("Bi-Directional Links Ratio"))
            result = noOfBiFriends / noOfFollowing;  
        return result;
    }
    public double getAverageNumber(double a, double b)
    {
        double averageValue = a/b;
        if(String.valueOf(averageValue).equals("NaN"))
                    averageValue = 0;
        return averageValue;
    }
    public double getMinNumber(ArrayList al)
    {
        double minValue = 0;
        for(int i = 0; i <al.size(); i++)
            minValue = (double)Collections.min(al);
        //System.out.println(minValue);
        return  minValue;
    }
    public double getMedianNumber(ArrayList al)
    {
        double median = 0.00;
        Collections.sort(al);
        if(al.size()%2 == 0)
        {
            int indexA = (al.size() -1) / 2;
            int indexB = al.size() / 2;
            if((indexA == 0) && (indexB == 0))
            {
                median = 0;
            }  
            else
            {
                double t1 =  (double) al.get(indexA);
                double t2 = (double) al.get(indexB);
                median =((t1 + t2) / 2);
            }
        }
        else
        {
            int index = (al.size() -1) / 2;
            int index1 = al.size() / 2;
            double a1 =  (double)al.get(index);
            double a2 = (double)al.get(index1);
            if(a1 == 0 && a2 == 0)
            {
                median = 0;
            }
            else
            {
                median =  (double) al.get(index);
            }
        }
        return median;
    }
    public int getTimeBetweenTweets(String date1, String date2)
    {
        int timeBetweenTweet = 0;
        String d1 = date1;
        String d2 = date2;
        DateFormat df = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
        String timeBetweenTweets = "";
        try 
        {
            Date a = df.parse(d1);
            Date b = df.parse(d2);
            Duration d = Duration.between(a.toInstant(), b.toInstant());
            timeBetweenTweets = String.valueOf(Duration.ofHours(d.toMinutes())).replaceAll("[^\\d.]", "");
            timeBetweenTweet =Integer.parseInt(timeBetweenTweets);    
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return timeBetweenTweet;
    }
    public double getStandardDeviation(ArrayList al)
    {
        double sd = 0;
        double mTotalValue = 0;
        double mTotalNumber = 0;
        double mean = 0;
        double variance = 0;
        double totalSquareMeanValue = 0;

        ArrayList storeSquaredValue = new ArrayList();
        for(int i = 0; i < al.size();i++)
        {
            double tempTotalValue = (double)al.get(i);
            mTotalValue += tempTotalValue;
            mTotalNumber++;
        }
        mean = mTotalValue / mTotalNumber;
        for(int i = 0; i < al.size();i++)
        {
            double tempNumber = (double)al.get(i);
            double tempMean = mean;
            double sValueNMean = tempNumber - tempMean;
            double square = Math.pow(sValueNMean, 2);
            storeSquaredValue.add(square);
        }
        for(int i = 0; i < storeSquaredValue.size();i++)
        {
            double tempSquareTotalValue = (double)storeSquaredValue.get(i);
            totalSquareMeanValue += tempSquareTotalValue;
        }
        variance = (1/mTotalNumber)*totalSquareMeanValue;
        if(!Double.isNaN(Math.sqrt(variance)))
        {
            sd = Math.sqrt(variance);
        }
        return sd;
    }
    public double getTweetSimilarityCS(File xf) throws ParserConfigurationException, SAXException, IOException
        {
            double value = 0;
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document xdoc = dBuilder.parse(xf);
            xdoc.getDocumentElement().normalize();
            NodeList nList = xdoc.getElementsByTagName("tweet");
            ArrayList tweetSimilarity = new ArrayList();
            //create Bag-Of-Word
            for(int i = 0 ;i < nList.getLength(); i++)
            {
                
                    Node nNode = nList.item(i);
                    Node nextNode = nList.item(i+1);
                    Element eElement = (Element) nNode;
                    Element nextElement = (Element) nextNode;
                    HashMap<String,Integer> hm = new HashMap<>();
                    if(i <= nList.getLength()-2)
                    {
                        String text1 = eElement.getElementsByTagName("text").item(0).getTextContent();
                        String text2 = nextElement.getElementsByTagName("text").item(0).getTextContent();
                        //String text1 = "Julia loves me more than Linda loves me";
                        //String text2 = " Jane likes me more than Julia loves me";
                        String[] aText1 = text1.split(" ");
                        String[] aText2 = text2.split(" ");

                            //store unique word into hashmap
                        for(int a = 0; a < aText1.length; a++)
                        {
                            //System.out.println(aText1[a]);
                            if(!hm.containsKey(aText1[a].trim()))
                            {
                                hm.put(aText1[a], 0);
                            }
                        }
                        for(int b = 0; b < aText2.length; b++)
                        {
                            if(!hm.containsKey((aText2[b].trim())))
                            {

                                hm.put(aText2[b], 0);
                            }
                        }
                        int hmLength = hm.size();
                        int[] vText1 = new int[hmLength];
                        int[] vText2 = new int[hmLength];
                        //create a vector for each tweet
                        int indexVText1 = 0;
                        for (Map.Entry<String, Integer> entry : hm.entrySet())
                        {
                            int count = 0;
                            for(int n = 0; n < aText1.length;n++)
                            {
                                if(String.valueOf(entry.getKey()).equals(aText1[n]))
                                {
                                    count++;
                                }
                            }
                            vText1[indexVText1] = count; 
                            indexVText1++;
                        }
                        int indexVText2 = 0;
                        for (Map.Entry<String, Integer> entry : hm.entrySet())
                        {
                            int count = 0;
                            for(int n = 0; n < aText2.length;n++)
                            {
                                if(String.valueOf(entry.getKey()).equals(aText2[n]))
                                {
                                    count++;
                                }
                            }
                            vText2[indexVText2] = count; 
                            indexVText2++;
                        }
                    tweetSimilarity.add(calculateCosineSimilarity(vText1,vText2));
                    }
            }
            int noOfPairTweets = tweetSimilarity.size();
            double temp = 0;
            for(int i = 0; i < tweetSimilarity.size(); i ++)
            {
                temp += Double.parseDouble(String.valueOf(tweetSimilarity.get(i)));
                //System.out.println("Temp abcdef "+ temp);
            }
            value = temp / noOfPairTweets;
            return value;
        }
    public double calculateCosineSimilarity(int[] text1, int[] text2)
        {
            double result = 0;
            DecimalFormat df = new DecimalFormat("#.####");
            int textLength = text1.length;
            int temp1 = 0;
            int temp2 = 0;
            int powTemp1 = 0;
            int powTemp2 = 0;
            double sqrtTemp1 = 0;
            double sqrtTemp2 = 0;
            double totalTemp1and2 = 0;
            for(int i = 0; i < textLength;i++)
            {
                temp1 = text1[i];
                temp2 = text2[i];
                powTemp1 += Math.pow(temp1, 2);
                powTemp2 += Math.pow(temp2, 2);
                totalTemp1and2 += temp1*temp2;
            }
            sqrtTemp1 = Math.sqrt(powTemp1);
            sqrtTemp2 = Math.sqrt(powTemp2);
            if(totalTemp1and2 == 0)
            {
                result = 0;
            }
            else
            {
                result = totalTemp1and2 / (sqrtTemp1 * sqrtTemp2);
            }
            //System.out.println("CS " +df.format(result) + " total " + df.format(totalTemp1and2) + " temp1 " + sqrtTemp1 + " temp2 " + sqrtTemp2);
            if(String.valueOf(result).equals("�"))
            {
                result = 0;
            }
            return Double.parseDouble(df.format(result));
        }
    public void extractFeature(ArrayList featureNamesInContentBased, ArrayList featureNamesInUserBased, int numberOfRecentTweets, File[] listofFiles,
            JTable jtbContentBasedFeatures,
            JTable jtbUserbasedfeatures,HashMap hm
            ,HashMap hmSMD, HashMap hmPlaces, HashMap hmName, HashMap hmSpamword, HashMap hmSpamProfile, 
            Date dtpFrom, Date dtpTo, File[] _biFriends, String biFriendsFiles, String spamword)
    {
                // TODO add your handling code here:
        String newline = System.getProperty("line.separator");
        String extension = ".csv";
        String lastUserFeature = "";
        String lastContentFeature = featureNamesInContentBased.get(featureNamesInContentBased.size() - 1).toString();
        if(featureNamesInUserBased.size() >= 1)
        {
            lastUserFeature = featureNamesInUserBased.get(featureNamesInUserBased.size() - 1).toString();
        }
        String featureName = "";
        //System.out.print(lastContentFeature);
        DataOutputStream dataOutput = null;
        DataOutputStream dataOutputUserFeatures = null;
        try 
        {
            //Copntent features
            JFrame parentFrame = new JFrame();
            JFileChooser fileChooserContentFeatures = new JFileChooser();
            fileChooserContentFeatures.setDialogTitle("Specify a path to save the content features");
            int pathSelection = fileChooserContentFeatures.showSaveDialog(parentFrame);
            
            //User features
            JFileChooser fileChooserUserFeatures = new JFileChooser();
            fileChooserUserFeatures.setDialogTitle("Specify a path to save the user features");
            int pathSelectionUser = fileChooserUserFeatures.showSaveDialog(parentFrame);            
            
            //Combine Content and User features
            JFileChooser fileCombine = new JFileChooser();
            fileCombine.setDialogTitle("Specify a path to combine content and user features");
            int pathSelectionCombine = fileCombine.showSaveDialog(parentFrame);             
//            dataOutput = new DataOutputStream(new FileOutputStream(directory+csvContentFeatures+extension,true));
//            dataOutputUserFeatures = new DataOutputStream(new FileOutputStream(directory+csvUserFeatures+extension,true));
    int temp = 0;
            for(File file : listofFiles)
            {
                String contentFeatureValue = "";
                String userFeatureValue = "";
                double noOfSpamTweets = 0;
                if(file.getName().equals(".DS_Store"))
                {
                    file.delete();
                }
                else
                {
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    org.w3c.dom.Document doc = dBuilder.parse(file);
                    doc.getDocumentElement().normalize();
                    NodeList nList = doc.getElementsByTagName("tweet");
                    String nameOfFile = doc.getElementsByTagName("name").item(0).getTextContent(); 
                    double noOfDuplicatedTweets = 0;
                    double totalNumberOfUserWasMentioned = 0;
                    HashMap<String,Integer> hmDuplicatedTweets = new HashMap();
                    ArrayList aHashtags = new ArrayList();
                    ArrayList aHashtagsPerWord = new ArrayList();
                    ArrayList aMentions = new ArrayList();
                    ArrayList aMentionsPerWord = new ArrayList();
                    ArrayList aURLs = new ArrayList();
                    ArrayList aURLsPerWord = new ArrayList();
                    ArrayList aRetweets = new ArrayList();
                    ArrayList aSpamWords = new ArrayList();
                    ArrayList aTime = new ArrayList();
                    ArrayList aTimeBetweenTweets = new ArrayList();
                    ArrayList aCharacters = new ArrayList();
                    ArrayList aWords = new ArrayList();
                    ArrayList aConsecutiveWords = new ArrayList();
                    ArrayList nonFollowerScreenName = new ArrayList();
                    
                    for(int i = 0; i < numberOfRecentTweets; i++) // loop through the recent tweets
                    { 
                        for(int contentFeature = 0 ; contentFeature < featureNamesInContentBased.size(); contentFeature++) // array of features
                        {
                            if(i < nList.getLength())
                            {
                                Node nNode = nList.item(i);
                                Node nextNode = nList.item(i+1);
                                Element eElement = (Element) nNode;
                                Element nextElement = (Element) nextNode;
                                String TimePublic = eElement.getElementsByTagName("created_at").item(0).getTextContent();
                                String timeUserWasMentioned = eElement.getElementsByTagName("in_reply_to_user_id").item(0).getTextContent();
                                //String mentionedNonFollowerScreenName = eElement.getElementsByTagName("in_reply_to_screen_name").item(0).getTextContent(); 
                                nonFollowerScreenName.add(eElement.getElementsByTagName("in_reply_to_screen_name").item(0).getTextContent());
                                if(!timeUserWasMentioned.equals(""))
                                {
                                    totalNumberOfUserWasMentioned++;
                                }
                                if(checkDate(TimePublic, dtpFrom, dtpTo))
                                {
                                    if(i <= nList.getLength()-2)
                                    {
                                        String STweet = nextElement.getElementsByTagName("created_at").item(0).getTextContent();
                                        double tempTimeBetweenTweets = getTimeBetweenTweets(TimePublic,STweet);
                                        aTimeBetweenTweets.add(tempTimeBetweenTweets);                                   
                                    }
                                    String text = eElement.getElementsByTagName("text").item(0).getTextContent(); 
                                    if(!text.isEmpty())
                                    {
                                        if(!hmDuplicatedTweets.containsKey(text))
                                        {
                                            noOfDuplicatedTweets++;
                                            hmDuplicatedTweets.put(text, 0);
                                        }
                                        featureName = featureNamesInContentBased.get(contentFeature).toString();                               
                                        double noOfHashtags = 0;
                                        double noOfMentions = 0;
                                        double _noOfURLs = 0;
                                        double time = 0;
                                        if(featureName.equals("Number of Hashtags"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(0, 2).toString();                                 
                                            noOfHashtags = CountNoHashTagAndMention(text, "#");
                                            aHashtags.add(noOfHashtags);
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += noOfHashtags + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += noOfHashtags + "," + nameOfFile + newline;
                                            }
                                        }  
                                        if(featureName.equals("Number of Hashtags per word"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(1, 2).toString();
                                            double noOfHashtagsPerWord = noOfHashtags / getNumberOfWordsFromAText(text);
                                            aHashtagsPerWord.add(noOfHashtagsPerWord);
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += noOfHashtagsPerWord + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += noOfHashtagsPerWord + "," + nameOfFile + newline;
                                            }
                                        }
                                        if(featureName.equals("Number of Mentions"))
                                        {
                                            String saveFile = "";
                                            saveFile = String.valueOf(jtbContentBasedFeatures.getModel().getValueAt(2, 2));
                                            noOfMentions = CountNoHashTagAndMention(text, "@");
                                            aMentions.add(noOfMentions);
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += noOfMentions + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += noOfMentions + "," + nameOfFile + newline;
                                            }  
                                        }
                                        if(featureName.equals("Number of Mentions per word"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(3, 2).toString();
                                            double noOfMentionssPerWord = noOfMentions / getNumberOfWordsFromAText(text);
                                            aMentionsPerWord.add(noOfMentionssPerWord);
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += noOfMentionssPerWord + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += noOfMentionssPerWord + "," + nameOfFile + newline;
                                            }                                
                                        }
                                        if(featureName.equals("Number of URLs"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(4, 2).toString();
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))  
                                            {
                                                String str[] = text.split(" ");
                                                for(int z = 0; z < str.length; z++)
                                                {
                                                    if(String.valueOf(text.charAt(z)).contains("www") || String.valueOf(text.charAt(z)).contains("http"))
                                                    {
                                                        _noOfURLs++;
                                                    }
                                                }
                                                aURLs.add(_noOfURLs);
                                                contentFeatureValue += _noOfURLs + ",";
                                            }
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                String str[] = text.split(" ");
                                                for(int z = 0; z < str.length; z++)
                                                {
                                                    if(String.valueOf(text.charAt(z)).contains("www") || String.valueOf(text.charAt(z)).contains("http"))
                                                    {
                                                        _noOfURLs++;
                                                    }
                                                } 
                                                aURLs.add(_noOfURLs);
                                                contentFeatureValue += _noOfURLs + "," + nameOfFile + newline;
                                            }
                                        }
                                        if(featureName.equals("Number of URLs per word"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(5, 2).toString();
                                            double noOfURLsPerWord = noOfMentions / getNumberOfWordsFromAText(text);
                                            aURLsPerWord.add(noOfURLsPerWord);
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += noOfURLsPerWord + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += noOfURLsPerWord + "," + nameOfFile + newline;
                                            } 
                                        }
                                        if(featureName.equals("Number of words per tweet"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(6, 2).toString();
                                            double noOfWordsPerTweet = 0;
                                            String str[] = text.split(" ");
                                            noOfWordsPerTweet = str.length;
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += noOfWordsPerTweet + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += noOfWordsPerTweet + "," + nameOfFile + newline;
                                            } 
                                        }
                                        if(featureName.equals("Whether the link points to a Social Media Domain"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(7, 2).toString(); 
                                            String[] aTweet = text.split(" ");
                                            double tweetContainsSMD = 0;
                                            double result = 0;
                                            for(int z = 0; z < aTweet.length;z++)
                                            {
                                                if(hmSMD.containsKey(aTweet[z]))
                                                {
                                                    tweetContainsSMD = 1;
                                                }
                                            }
                                            result = tweetContainsSMD;
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += result + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += result + "," + nameOfFile + newline;
                                            } 
                                        }
                                        if(featureName.equals("Number of unique URLs"))// bo khong làm tạm thoi
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(8, 2).toString(); 
                                            double result = 0;   
                                            HashMap<String,String> tempHM = new HashMap();
                                            String str[] = text.split(" ");                                        
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                            {                                     
                                                for(int z = 0; z < str.length; z++)
                                                {
                                                    if(String.valueOf(text.charAt(z)).contains("www") || String.valueOf(text.charAt(z)).contains("http"))
                                                    {
                                                        tempHM.put(String.valueOf(text.charAt(z)), "none");
                                                    }
                                                }
                                                result = tempHM.size();
                                                contentFeatureValue += result + ",";
                                            }
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                for(int z = 0; z < str.length; z++)
                                                {
                                                    if(String.valueOf(text.charAt(z)).contains("www") || String.valueOf(text.charAt(z)).contains("http"))
                                                    {
                                                        tempHM.put(String.valueOf(text.charAt(z)), "none");
                                                    }
                                                }
                                                result = tempHM.size();
                                                contentFeatureValue += result + "," + nameOfFile + newline;
                                            }
                                        }
                                        if(featureName.equals("Number of exclamation marks"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(9, 2).toString();
                                            double noOfExclamationMarksPerTweet = 0;
                                            for(int z = 0; z < text.length(); z++)
                                            {
                                                if(String.valueOf(text.charAt(z)).equals("!"))noOfExclamationMarksPerTweet++;
                                            }
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += noOfExclamationMarksPerTweet + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += noOfExclamationMarksPerTweet + "," + nameOfFile + newline;
                                            }
                                        }
                                        if(featureName.equals("Number of question marks"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(10, 2).toString();
                                            double noOfQuestionMarksPerTweet = 0;
                                            for(int z = 0; z < text.length(); z++)
                                            {
                                                if(String.valueOf(text.charAt(z)).equals("?"))noOfQuestionMarksPerTweet++;
                                            }
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += noOfQuestionMarksPerTweet + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += noOfQuestionMarksPerTweet + "," + nameOfFile + newline;
                                            }
                                        }
                                        if(featureName.equals("Number of characters"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(11, 2).toString();
                                            double noOfCharactersPerTweet = 0;
                                            noOfCharactersPerTweet = text.length();
                                            aCharacters.add(noOfCharactersPerTweet);
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += noOfCharactersPerTweet + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += noOfCharactersPerTweet + "," + nameOfFile + newline;
                                            }                                
                                        }
                                        if(featureName.equals("Whether the tweet is a retweet"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(12, 2).toString();
                                            double isRetweet = 0;
                                            if(FindRetweet(text) >= 1)
                                            {
                                                isRetweet = 1;
                                            }
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += isRetweet + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += isRetweet + "," + nameOfFile + newline;
                                            }  
                                        }
                                        if(featureName.equals("Number of Retweets"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(13, 2).toString();
                                            double isRetweet = 0;
                                            if(FindRetweet(text) >= 1)
                                            {
                                                isRetweet = FindRetweet(text);
                                                aRetweets.add(isRetweet);
                                            }
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += isRetweet + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += isRetweet + "," + nameOfFile + newline;
                                            } 
                                        }
                                        if(featureName.equals("Number of consecutive words"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(14, 2).toString();
                                            double consecutiveWord = 0; 
                                            consecutiveWord = getConsecutiveWords(text);
                                            aConsecutiveWords.add(aConsecutiveWords);
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += consecutiveWord + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += consecutiveWord + "," + nameOfFile + newline;
                                            } 
                                        }
                                        if(featureName.equals("Number of whitespaces per tweet"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(15, 2).toString();
                                            double noWhiteSpaces = 0; 
                                            for(int z = 0; z < text.length(); z++)
                                            {
                                                if(String.valueOf(text.charAt(z)).equals(" "))
                                                {
                                                   noWhiteSpaces++; 
                                                }
                                            }
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += noWhiteSpaces + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += noWhiteSpaces + "," + nameOfFile + newline;
                                            }                                    
                                        }
                                        if(featureName.equals("Number of capitalization words per tweet"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(16, 2).toString();
                                            double noOfCapitalizationWords = 0;
                                            StringTokenizer st = new StringTokenizer(text);
                                            while(st.hasMoreTokens())
                                            {
                                                String a = st.nextToken();
                                                if(Character.isUpperCase(a.charAt(0)))
                                                {
                                                    noOfCapitalizationWords++;
                                                }
                                            }
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += noOfCapitalizationWords + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += noOfCapitalizationWords + "," + nameOfFile + newline;
                                            }     
                                        }
                                        if(featureName.equals("Number of capitalization words per word on each tweet"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(17, 2).toString();
                                            double noOfCapitalizationWords = 0;
                                            double noOfWords = 0;
                                            StringTokenizer st = new StringTokenizer(text);
                                            noOfWords = st.countTokens();
                                            while(st.hasMoreTokens())
                                            {
                                                String a = st.nextToken();
                                                if(Character.isUpperCase(a.charAt(0)))
                                                {
                                                    noOfCapitalizationWords++;
                                                }
                                            }
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += noOfCapitalizationWords / noOfWords + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                                contentFeatureValue += noOfCapitalizationWords / noOfWords + "," + nameOfFile + newline; 
                                        }
                                        if(featureName.equals("Number of duplicated tweets"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(18, 2).toString();
                                            double result = 0;
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += result + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += result + "," + nameOfFile + newline;
                                            } 
                                        }                                        
                                        if(featureName.equals("Percentage of words not found in a dictionary"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(19, 2).toString();
                                            String[] nonDictionaryWord = text.split(" ");
                                            double noOfNonDictionaryWord = 0;
                                            double result = 0;
                                            for(int z = 0; z < nonDictionaryWord.length;z++)
                                            {
                                                if(hm.containsKey(nonDictionaryWord[z]))
                                                {
                                                    noOfNonDictionaryWord++;
                                                }
                                            }
                                            if(noOfNonDictionaryWord > 0)
                                                result = (noOfNonDictionaryWord / (text.length())) * 100;
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += result + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += result + "," + nameOfFile + newline;
                                            }  
                                        }
                                        if(featureName.equals("Tweets contain places"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(20, 2).toString(); 
                                            String[] aTweet = text.split(" ");
                                            double tweetContainsPlaces = 0;
                                            double result = 0;
                                            for(int z = 0; z < aTweet.length;z++)
                                            {
                                                if(hmPlaces.containsKey(aTweet[z]))
                                                {
                                                    tweetContainsPlaces++;
                                                }
                                            }
                                            result = tweetContainsPlaces;
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += result + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += result + "," + nameOfFile + newline;
                                            }
                                        }
                                        if(featureName.equals("Tweets contain Organization"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(21, 2).toString(); 
                                            String[] aTweet = text.split(" ");
                                            double tweetContainsOrganization = 0;
                                            double result = 0;
                                            for(int z = 0; z < aTweet.length;z++)
                                            {
                                                if(hmPlaces.containsKey(aTweet[z]))
                                                {
                                                    tweetContainsOrganization++;
                                                }
                                            }
                                            result = tweetContainsOrganization;
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += result + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += result + "," + nameOfFile + newline;
                                            } 
                                        }
                                        if(featureName.equals("Tweets contain name"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(22, 2).toString(); 
                                            String[] aTweet = text.split(" ");
                                            double tweetContainsName = 0;
                                            double result = 0;
                                            for(int z = 0; z < aTweet.length;z++)
                                            {
                                                if(hmName.containsKey(aTweet[z]))
                                                {
                                                    tweetContainsName++;
                                                }
                                            }
                                            result = tweetContainsName;
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += result + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += result + "," + nameOfFile + newline;
                                            } 
                                        }
                                        if(featureName.equals("Tweets contain Social Media Domain"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(23, 2).toString(); 
                                            String[] aTweet = text.split(" ");
                                            double tweetContainsSMD = 0;
                                            double result = 0;
                                            for(int z = 0; z < aTweet.length;z++)
                                            {
                                                if(hmSMD.containsKey(aTweet[z]))
                                                {
                                                    tweetContainsSMD++;
                                                }
                                            }
                                            result = tweetContainsSMD;
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += result + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += result + "," + nameOfFile + newline;
                                            } 
                                        }
                                        if(featureName.equals("Number of words"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(24, 2).toString(); 
                                            String[] aTweet = text.split(" ");
                                            double noOfWords = aTweet.length;
                                            aWords.add(noOfWords);
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += noOfWords + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += noOfWords + "," + nameOfFile + newline;
                                            } 
                                        }
                                        if(featureName.equals("Number of spam words per tweet"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(25, 2).toString(); 
                                            String[] aTweet = text.split(" ");
                                            double noOfSpamWords = 0;
                                            double result = 0;
                                            for(int z = 0; z < aTweet.length;z++)
                                            {
                                                if(hmSpamword.containsKey(aTweet[z]))
                                                {
                                                    noOfSpamWords++;
                                                }
                                            }
                                            if(noOfSpamWords >= 1)
                                                aSpamWords.add(noOfSpamWords);
                                            result = noOfSpamWords;
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += result + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += result + "," + nameOfFile + newline;
                                            } 
                                        }
                                        if(featureName.equals("Number of spam words per word on each tweet"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(26, 2).toString(); 
                                            String[] aTweet = text.split(" ");
                                            double noOfSpamWords = 0;
                                            double result = 0;
                                            for(int z = 0; z < aTweet.length;z++)
                                            {
                                                if(hmSpamword.containsKey(aTweet[z]))
                                                {
                                                    noOfSpamWords++;
                                                }
                                            }
                                            result = noOfSpamWords / aTweet.length;
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += result + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += result + "," + nameOfFile + newline;
                                            } 
                                        }
                                        if(featureName.equals("Time of publication"))
                                        {
                                            String saveFile = jtbContentBasedFeatures.getModel().getValueAt(27, 2).toString(); 
                                            time = Integer.parseInt(TimePublic.substring(11, 13));
                                            aTime.add(time);
                                            if(saveFile.equals("true") && !featureName.equals(lastContentFeature))
                                                contentFeatureValue += time + ",";
                                            else if(saveFile.equals("true") && featureName.equals(lastContentFeature))
                                            {
                                                contentFeatureValue += time + "," + nameOfFile + newline;
                                            } 
                                        }
                                    }
                                }// o day
                            } 
                        }
                    }//
                    //double max = execution.getMaxNumber(aHashtags);
                    if(featureNamesInUserBased.size() >= 1)
                    {
                        double noOfFollower = 0;
                        double noOfFollowee = 0; 
                        String spamProfileUsers = "ham";
                        if(hmSpamProfile.containsKey(file.getName()))
                            spamProfileUsers = "spam";
                        for(int userFeature = 0 ; userFeature < featureNamesInUserBased.size(); userFeature++)
                        {
                            featureName = featureNamesInUserBased.get(userFeature).toString();
                            if(featureName.equals("Number of followers"))
                            {
                                noOfFollower = Double.parseDouble(doc.getElementsByTagName("followers_count").item(0).getTextContent());
                                String saveFile = jtbUserbasedfeatures.getModel().getValueAt(0, 1).toString();
                                if(saveFile.equals("true") && !featureName.equals(lastUserFeature))
                                    userFeatureValue += noOfFollower + ",";
                                else
                                {
                                    userFeatureValue += noOfFollower + "," + nameOfFile + "," + spamProfileUsers + newline;
                                } 
                            }
                            if(featureName.equals("Number of followees"))
                            {
                                noOfFollowee = Integer.parseInt(doc.getElementsByTagName("friends_count").item(0).getTextContent());
                                String saveFile = jtbUserbasedfeatures.getModel().getValueAt(1, 1).toString();
                                if(saveFile.equals("true") && !featureName.equals(lastUserFeature))
                                    userFeatureValue += noOfFollowee + ",";
                                else
                                {
                                    userFeatureValue += noOfFollowee + "," + nameOfFile + "," + spamProfileUsers + newline;
                                } 
                            }
                            if(featureName.equals("Reputation"))
                            {
                                double followee = Double.parseDouble(doc.getElementsByTagName("friends_count").item(0).getTextContent());
                                double follower = Double.parseDouble(doc.getElementsByTagName("followers_count").item(0).getTextContent());
                                double reputation = follower / (follower+followee);
                                if(!featureName.equals(lastUserFeature))
                                    userFeatureValue += reputation + ",";
                                else
                                {
                                    userFeatureValue += reputation + "," + nameOfFile + "," + spamProfileUsers + newline;
                                } 
                            }
                            if(featureName.equals("Ratio follower to following"))
                            {
                                double followee = 0;
                                double follower = 0;
                                followee = Double.parseDouble(doc.getElementsByTagName("friends_count").item(0).getTextContent());
                                follower = Double.parseDouble(doc.getElementsByTagName("followers_count").item(0).getTextContent());
                                double ratioFollowerToFollowing = 0;
                                String saveFile = jtbUserbasedfeatures.getModel().getValueAt(3, 1).toString();                                
                                if(follower >=0 && followee >= 0 )
                                {
                                    ratioFollowerToFollowing = follower / followee;
                                    if(!featureName.equals(lastUserFeature))
                                        userFeatureValue += ratioFollowerToFollowing + ",";
                                    else if(saveFile.equals("true") && featureName.equals(lastUserFeature))
                                    {
                                        userFeatureValue += ratioFollowerToFollowing + "," + nameOfFile + "," + spamProfileUsers + newline;
                                    }  
                                }
                                else
                                {
                                    if(!featureName.equals(lastUserFeature))
                                        userFeatureValue += ratioFollowerToFollowing + ",";
                                    else if(saveFile.equals("true") && featureName.equals(lastUserFeature))
                                    {
                                        userFeatureValue += ratioFollowerToFollowing + "," + nameOfFile + "," + spamProfileUsers + newline;
                                    } 
                                }
                            }
                            if(featureName.equals("Age of account") || featureName.equals("Bi-Directional Links Ratio") || featureName.equals("Fraction of mention non follower"))//4
                            {
                                
                                double noOfFollowing = 0;
                                noOfFollowing = Double.parseDouble(doc.getElementsByTagName("friends_count").item(0).getTextContent()); 
                                String screenName = doc.getElementsByTagName("screen_name").item(0).getTextContent();                                
                                Double result = getUserInfo(_biFriends, screenName, biFriendsFiles, featureName, noOfFollowing, nonFollowerScreenName);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile + "," + spamProfileUsers + newline;
                            }
                            if(featureName.equals("Interaction rate"))
                            {
                                double result = 0;
                                double noOfTweets = Double.parseDouble(doc.getElementsByTagName("tweet_count").item(0).getTextContent());                                                              
                                result = totalNumberOfUserWasMentioned / noOfTweets;
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile + "," + spamProfileUsers + newline;                                
                            }
                            
                            if(featureName.equals("Average number of hashtags"))//8
                            {
                                double totalNumberOfHashtags = 0;
                                double noOfTweetsContainHashtags = 0;
                                double result = 0;
                                for(int i = 0; i < aHashtags.size();i++)
                                {
                                    totalNumberOfHashtags += Double.valueOf(aHashtags.get(i).toString());
                                }
                                noOfTweetsContainHashtags = aHashtags.size();
                                result = getAverageNumber(totalNumberOfHashtags, totalNumberOfHashtags);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +"," + spamProfileUsers + newline;
                            } 
                            if(featureName.equals("Maximum number of hashtags"))//9
                            {
                                double result = 0;
                                result = getMaxNumber(aHashtags);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;
                            }
                            if(featureName.equals("Maximum number of hashtags per word"))//10
                            {
                                double result = 0;
                                result = getMaxNumber(aHashtagsPerWord);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;
                            }  
                            if(featureName.equals("Minimum number of hashtags"))//11
                            {                               
                                double result = 0;
                                result = getMinNumber(aHashtags);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;
                            }
                            if(featureName.equals("Minimum number of hashtags per word"))//12
                            {
                                double result = 0;
                                result = getMaxNumber(aHashtagsPerWord);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;
                            }
                            if(featureName.equals("Median number of hashtags"))//13
                            {
                                double result = 0;
                                result = getMedianNumber(aHashtags);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline; 
                            } 
                            if(featureName.equals("Median number of hashtags per word"))//14
                            {
                                double result = 0;
                                result = getMedianNumber(aHashtagsPerWord);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline; 
                            }                            
                            if(featureName.equals("Fraction of hashtags"))//15
                            {
                                double noOfTweets = Double.parseDouble(doc.getElementsByTagName("tweet_count").item(0).getTextContent());
                                double noOfTweetsContainHashtags = aHashtags.size();
                                double result = noOfTweetsContainHashtags / noOfTweets;
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline; 
                            }
                            if(featureName.equals("Average number of mentions"))//16
                            {
                                double totalNumberOfMentions = 0;
                                double noOfTweetsContainMentions = 0;
                                double result = 0;
                                for(int i = 0; i < aMentions.size();i++)
                                {
                                    totalNumberOfMentions += Double.valueOf(aMentions.get(i).toString());
                                }
                                noOfTweetsContainMentions = aMentions.size();
                                result = getAverageNumber(totalNumberOfMentions, noOfTweetsContainMentions);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +"," + spamProfileUsers + newline;
                            }  
                            if(featureName.equals("Maximum number of mentions"))//17
                            {
                                double result = 0;
                                result = getMaxNumber(aMentions);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline; 
                            } 
                            if(featureName.equals("Minimum number of mentions"))//18
                            {
                                double result = 0;
                                result = getMinNumber(aHashtags);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;
                            }
                            if(featureName.equals("Median number of mentions"))//19
                            {
                                double result = 0;
                                result = getMedianNumber(aHashtags);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;
                            }   
                            if(featureName.equals("Fraction of mentions"))//20
                            {
                                double noOfTweets = Double.parseDouble(doc.getElementsByTagName("tweet_count").item(0).getTextContent());
                                double noOfTweetsContainMentions = aMentions.size();
                                double result = noOfTweetsContainMentions / noOfTweets;
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline; 
                            }   
                            if(featureName.equals("Average retweets"))//21
                            {
                                double totalNumberOfRetweet = 0;
                                double noOfTweetsRetweeted = 0;
                                double result = 0;
                                for(int i = 0; i < aRetweets.size();i++)
                                {
                                    totalNumberOfRetweet += Double.valueOf(aRetweets.get(i).toString());
                                }
                                noOfTweetsRetweeted = aRetweets.size();
                                result = getAverageNumber(totalNumberOfRetweet, noOfTweetsRetweeted);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +"," + spamProfileUsers + newline;
                            }
                            if(featureName.equals("Total number of times user was mentioned"))//22
                            {
                                double result = 0;
                                double noOfTweets = Double.parseDouble(doc.getElementsByTagName("tweet_count").item(0).getTextContent());
                                result = totalNumberOfUserWasMentioned;
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +"," + spamProfileUsers + newline;                                
                            }
                            if(featureName.equals("Maximum number of tweet retweets"))//23
                            {
                                double result = 0;
                                result = getMaxNumber(aRetweets);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;  
                            }
                            if(featureName.equals("Minimum number of tweet retweets"))//24
                            {
                                double result = 0;
                                result = getMinNumber(aRetweets);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;  
                            }
                            if(featureName.equals("Median number of tweet retweets"))//25
                            {
                                double result = 0;
                                result = getMedianNumber(aRetweets);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;  
                            } 
                            if(featureName.equals("Average number of URLs"))//26
                            {
                                double totalNumberOfURLs = 0;
                                double noOfTweetsContainURLs= 0;
                                double result = 0;
                                for(int i = 0; i < aURLs.size();i++)
                                {
                                    totalNumberOfURLs += Double.valueOf(aURLs.get(i).toString());
                                }
                                noOfTweetsContainURLs = aURLs.size();
                                result = getAverageNumber(totalNumberOfURLs, noOfTweetsContainURLs);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +"," + spamProfileUsers + newline;
                            } 
                            if(featureName.equals("Average number of URLs per word"))//27
                            {
                                double totalNumberOfURLsPerWord = 0;
                                double noOfTweetsContainURLs= 0;
                                double result = 0;
                                for(int i = 0; i < aURLsPerWord.size();i++)
                                {
                                    totalNumberOfURLsPerWord += Double.valueOf(aURLsPerWord.get(i).toString());
                                }
                                noOfTweetsContainURLs = aURLsPerWord.size();
                                result = getAverageNumber(totalNumberOfURLsPerWord, noOfTweetsContainURLs);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +"," + spamProfileUsers + newline; 
                            } 
                            if(featureName.equals("Maximum number of URLs"))//28
                            {
                                double result = 0;
                                result = getMaxNumber(aURLs);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline; 
                            }
                            if(featureName.equals("Maximum number of URLs per word"))//29
                            {
                                double result = 0;
                                result = getMaxNumber(aURLsPerWord);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;    
                            }
                            if(featureName.equals("Minimum number of URLs"))//30
                            {
                                double result = 0;
                                result = getMinNumber(aURLs);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;
                            } 
                            if(featureName.equals("Minimum number of URLs per word"))//31
                            {
                                double result = 0;
                                result = getMinNumber(aURLsPerWord);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                
                            } 
                            if(featureName.equals("Median number of URLs"))//32
                            {
                                double result = 0;
                                result = getMedianNumber(aURLs);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                
                            }
                            if(featureName.equals("Fraction of URLs"))//33
                            {
                                double noOfTweets = Double.parseDouble(doc.getElementsByTagName("tweet_count").item(0).getTextContent());
                                double noOfTweetsContainURLs = aURLs.size();
                                double result = noOfTweetsContainURLs / noOfTweets;
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                
                            }
                            if(featureName.equals("Median number of URLs per word"))//34
                            {
                                double result = 0;
                                result = getMedianNumber(aURLsPerWord);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            }  
                            if(featureName.equals("Ratio Unique URLs"))//
                            {
                                double result = 0;
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;  
                            }  
                            if(featureName.equals("URL rate"))//36
                            {
                                double result = 0;
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                  
                            } 
                            if(featureName.equals("Number of spam words in screen name"))//37
                            {
                                String screenName = doc.getElementsByTagName("screen_name").item(0).getTextContent();
                                double result = 0;
                                String textScanner = "";
                                Scanner scanner = new Scanner(new File(spamword));
                                while(scanner.hasNext())
                                {
                                    textScanner = scanner.next();

                                    if(screenName.toLowerCase().contains(textScanner.toLowerCase()))
                                    {
                                        result++;
                                    }
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;  
                            }   
                            if(featureName.equals("Fraction of spam tweets"))//38
                            {
                                double noOfTweets = Double.parseDouble(doc.getElementsByTagName("tweet_count").item(0).getTextContent());
                                double noOfTweetsContainSpamWords = aSpamWords.size();
                                double result = noOfTweetsContainSpamWords / noOfTweets;
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;
                            }
                            if(featureName.equals("Length of profile name"))//39
                            {
                                String profileName = doc.getElementsByTagName("screen_name").item(0).getTextContent();
                                double result = profileName.length();
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;
                            }  
                            if(featureName.equals("Average spam tweet count"))//40
                            {
                                double totalNumberOfSpamTweets = 0;
                                double noOfTweetsContainSpamWords= 0;
                                double result = 0;
                                for(int i = 0; i < aSpamWords.size();i++)
                                {
                                    totalNumberOfSpamTweets += Double.valueOf(aSpamWords.get(i).toString());
                                }
                                noOfTweetsContainSpamWords = aURLs.size();
                                result = getAverageNumber(totalNumberOfSpamTweets, noOfTweetsContainSpamWords);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +"," + spamProfileUsers + newline;                                
                            }   
                            if(featureName.equals("Number of tweets early morning"))//41
                            {
                                double startRange = 3;
                                double endRange = 6;
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    double time = (double) aTime.get(i);
                                    if(time >= startRange && time <= endRange)
                                      result++;  
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +"," + spamProfileUsers + newline;                                
                            }
                            if(featureName.equals("Maximum amount of time between tweets"))//42
                            {
                                double result = 0;
                                result = getMaxNumber(aTimeBetweenTweets);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;  
                            }  
                            if(featureName.equals("Minimum amount of time between tweets"))//43
                            {
                                double result = 0;
                                result = getMinNumber(aTimeBetweenTweets);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;     
                            }  
                            if(featureName.equals("Average amount of time between tweets"))//44
                            {
                                double totalNumberOfTweets = 0;
                                double totalTimeBetweenTweets= 0;
                                double result = 0;
                                for(int i = 0; i < aTimeBetweenTweets.size();i++)
                                {
                                    totalNumberOfTweets += Double.valueOf(aTimeBetweenTweets.get(i).toString());
                                }
                                totalTimeBetweenTweets = aTimeBetweenTweets.size();
                                result = getAverageNumber(totalTimeBetweenTweets, totalNumberOfTweets);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +"," + spamProfileUsers + newline;                                 
                            } 
                            if(featureName.equals("Median amount of time between tweets"))//45
                            {
                                double result = 0;
                                result = getMedianNumber(aTimeBetweenTweets);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                
                            }  
                            if(featureName.equals("Standard deviation of time between tweets"))//46
                            {
                                double result = 0;
                                result = getStandardDeviation(aTimeBetweenTweets);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                  
                            }  
                            if(featureName.equals("Maximum number of characters"))//47
                            {
                                double result = 0;
                                result = getMaxNumber(aCharacters);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;   
                            }  
                            if(featureName.equals("Minimum number of characters"))//48
                            {
                                double result = 0;
                                result = getMinNumber(aCharacters);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                  
                            }  
                            if(featureName.equals("Average number of characters"))//49
                            {
                                double totalNumberOfCharacters = 0;
                                double totalNumberOfTweets= 0;
                                double result = 0;
                                for(int i = 0; i < aCharacters.size();i++)
                                {
                                    totalNumberOfCharacters += Double.valueOf(aCharacters.get(i).toString());
                                }
                                totalNumberOfTweets = aTimeBetweenTweets.size();
                                result = getAverageNumber(totalNumberOfCharacters, totalNumberOfTweets);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +"," + spamProfileUsers + newline;                                
                            }     
                            if(featureName.equals("Median number of characters"))//50
                            {
                                double result = 0;
                                result = getMedianNumber(aCharacters);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                
                            }
                            if(featureName.equals("Maximum number of words"))//51
                            {
                                double result = 0;
                                result = getMaxNumber(aWords);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            }
                            if(featureName.equals("Minimum number of words"))//52
                            {
                                double result = 0;
                                result = getMinNumber(aWords);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            } 
                            if(featureName.equals("Average number of words"))//53
                            {
                                double totalNumberOfWords = 0;
                                double totalNumberOfTweets= 0;
                                double result = 0;
                                for(int i = 0; i < aWords.size();i++)
                                {
                                    totalNumberOfWords += Double.valueOf(aWords.get(i).toString());
                                }
                                totalNumberOfTweets = aWords.size();
                                result = getAverageNumber(totalNumberOfWords, totalNumberOfTweets);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +"," + spamProfileUsers + newline;                                 
                            } 
                            if(featureName.equals("Median number of words"))//54
                            {
                                double result = 0;
                                result = getMedianNumber(aWords);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            }           
                            if(featureName.equals("Number of Duplicated tweets"))//55
                            {
                                double result = 0;
                                result = hmDuplicatedTweets.size();
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            } 
                            if(featureName.equals("Fraction of duplicated tweets"))//56
                            {
                                double noOfTweets = Double.parseDouble(doc.getElementsByTagName("tweet_count").item(0).getTextContent());
                                double result = noOfDuplicatedTweets / noOfTweets;
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                
                            } 
                            if(featureName.equals("Maximum number of consecutive words"))//57
                            {
                                double result = 0;
                                result = getMaxNumber(aConsecutiveWords);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline; 
                            } 
                            if(featureName.equals("Length description"))//58
                            {
                                String description = doc.getElementsByTagName("description").item(0).getTextContent();
                                double result = description.length();
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                
                            } 
                            if(featureName.equals("Number of tweets"))//59
                            {
                                String noOfTweets = doc.getElementsByTagName("tweet_count").item(0).getTextContent();
                                double result = noOfTweets.length();
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                  
                            }
                            if(featureName.equals("Max idle duration between posts"))//61
                            {
                                double result = 0;
                                String noOfTweets = doc.getElementsByTagName("tweet_count").item(0).getTextContent();                                
                                result = getMaxNumber(aTime) / Double.parseDouble(noOfTweets);
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                
                            } 
                            if(featureName.equals("Tweet similarity - Cosine similarity"))//63
                            {
                                double result = 0;
                                if(!Double.isNaN(getTweetSimilarityCS(file)))
                                {
                                    result = getTweetSimilarityCS(file); // James
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline; 
                            } 
                            if(featureName.equals("Number of tweets"))//64
                            {
                                double result = 0;
                                result = Double.parseDouble(doc.getElementsByTagName("tweet_count").item(0).getTextContent()); 
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                
                            }
                            if(featureName.equals("Number of tweets posted at 12:00 am"))//65
                            {   
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("0.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            }     
                            if(featureName.equals("Number of tweets posted at 01:00 am"))//66
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("1.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            } 
                            if(featureName.equals("Number of tweets posted at 02:00 am"))//67
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("2.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                  
                            } 
                            if(featureName.equals("Number of tweets posted at 03:00 am"))//68
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("3.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                               
                            } 
                            if(featureName.equals("Number of tweets posted at 04:00 am"))//69
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("4.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            } 
                            if(featureName.equals("Number of tweets posted at 05:00 am"))//70
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("5.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            } 
                            if(featureName.equals("Number of tweets posted at 06:00 am"))//71
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("6.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            } 
                            if(featureName.equals("Number of tweets posted at 07:00 am"))//72
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("7.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            } 
                            if(featureName.equals("Number of tweets posted at 08:00 am"))//73
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("8.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            } 
                            if(featureName.equals("Number of tweets posted at 09:00 am"))//74
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("9.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            } 
                            if(featureName.equals("Number of tweets posted at 10:00 am"))//75
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("10.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            }  
                            if(featureName.equals("Number of tweets posted at 11:00 am"))//76
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("11.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            } 
                            if(featureName.equals("Number of tweets posted at 12:00 pm"))//77
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("12.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            } 
                            if(featureName.equals("Number of tweets posted at 13:00 pm"))//78
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("13.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            } 
                            if(featureName.equals("Number of tweets posted at 14:00 pm"))//79
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("14.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            }
                            if(featureName.equals("Number of tweets posted at 15:00 pm"))//80
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("15.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            }
                            if(featureName.equals("Number of tweets posted at 16:00 pm"))//81
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("16.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            }
                            if(featureName.equals("Number of tweets posted at 17:00 pm"))//82
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("17.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            }
                            if(featureName.equals("Number of tweets posted at 18:00 pm"))//83
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("17.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            } 
                            if(featureName.equals("Number of tweets posted at 19:00 pm"))//84
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("19.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            }
                            if(featureName.equals("Number of tweets posted at 20:00 pm"))//85
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("20.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            }
                            if(featureName.equals("Number of tweets posted at 21:00 pm"))//86
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("21.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            } 
                            if(featureName.equals("Number of tweets posted at 22:00 pm"))//87
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("22.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            }  
                            if(featureName.equals("Number of tweets posted at 23:00 pm"))//88
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("23.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            }
                            if(featureName.equals("Number of tweets posted from 12:00 am - 02:00 am"))//89
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("0.0") && time.equals("2.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            }
                            if(featureName.equals("Number of tweets posted from 03:00 am - 05:00 am"))//90
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("3.0") && time.equals("5.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                 
                            }
                            if(featureName.equals("Number of tweets posted from 06:00 am - 08:00 am"))//91
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("6.0") && time.equals("8.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                   
                            } 
                            if(featureName.equals("Number of tweets posted from 09:00 am - 11:00 am"))//92
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("9.0") && time.equals("11.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                
                            } 
                            if(featureName.equals("Number of tweets posted from 12:00 pm - 14:00 pm"))//93
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("12.0") && time.equals("14.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                  
                            }
                            if(featureName.equals("Number of tweets posted from 15:00 pm - 17:00 pm"))//94
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("15.0") && time.equals("17.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                  
                            }
                            if(featureName.equals("Number of tweets posted from 18:00 pm - 20:00 pm"))//95
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("18.0") && time.equals("20.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                  
                            }   
                            if(featureName.equals("Number of tweets posted from 21:00 pm - 23:00 pm"))//96
                            {
                                double result = 0;
                                for(int i = 0; i < aTime.size(); i++)
                                {
                                    String time = String.valueOf(aTime.get(i));
                                    if(time.equals("21.0") && time.equals("23.0"))
                                        result++;
                                }
                                if(!featureName.equals(lastUserFeature))
                                   userFeatureValue += result + ","; 
                                else
                                    userFeatureValue += result + "," + nameOfFile +","+ spamProfileUsers + newline;                                  
                            }                             
                        }
                    }
                }
                if(pathSelection == JFileChooser.APPROVE_OPTION)
                {
                    File fileToSave = fileChooserContentFeatures.getSelectedFile();
                    dataOutput = new DataOutputStream(new FileOutputStream(fileToSave + extension,true));
                    dataOutput.writeChars(String.valueOf(contentFeatureValue));
                    pathContentFeatures = fileToSave.getCanonicalPath()+extension;
                }
                if(pathSelectionUser == JFileChooser.APPROVE_OPTION)
                {
                    File fileToSave = fileChooserUserFeatures.getSelectedFile();
                    dataOutput = new DataOutputStream(new FileOutputStream(fileToSave + extension,true));
                    
                    dataOutput.writeChars(String.valueOf(userFeatureValue));
                    pathUserFeatures = fileToSave.getCanonicalPath()+extension;
                    temp++;
                    //System.out.println("path " +pathUserFeatures + " " + temp);
                }
                if(pathSelectionCombine == JFileChooser.APPROVE_OPTION)
                {
                    File fileToSave = fileCombine.getSelectedFile();              
                    pathCombineContentandUserFeatures = fileToSave.getCanonicalPath()+extension;
                    pathArffFile = fileToSave.getCanonicalPath()+".arff";
                    //System.out.println(pathCombineContentandUserFeatures);
                }
            }
        } 
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(createNewSystem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(createNewSystem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(createNewSystem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(createNewSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void createArffFile(ArrayList featureNamesInContentBased, ArrayList featureNamesInUserBased, String pathArffFile) throws FileNotFoundException, IOException
    {
        
        String arffFile = "";
        String relationDeclaration = "@relation 'Training'";
        String dataDeclaration = "@data";
        String newline = System.getProperty("line.separator");
        String attributeDeclaration = "";
        String directory = pathArffFile;      
//        DataOutputStream dataOutput = null;
//        dataOutput = new DataOutputStream(new FileOutputStream(directory+csvContentFeatures+extension,true)); 
        for(int i = 0; i < featureNamesInContentBased.size();i++)
        {
            attributeDeclaration += "@attribute '" + featureNamesInContentBased.get(i).toString().replaceAll(" ", "") + "' numeric" + newline;
        }
        for(int i = 0; i < featureNamesInUserBased.size();i++)
        {
            attributeDeclaration += "@attribute '" + featureNamesInUserBased.get(i).toString().replaceAll(" ", "") + "' numeric" + newline;
        }
        attributeDeclaration += "@attribute '" + "class' " + "{ham,spam}";
        arffFile = relationDeclaration + newline + newline + attributeDeclaration + newline + newline + dataDeclaration + newline + combineContentandUserFeatures(featureNamesInContentBased,featureNamesInUserBased,pathContentFeatures,pathUserFeatures,pathCombineContentandUserFeatures);
        //arffFile = cleanUpArffFile(arffFile);
        FileUtils.writeStringToFile(new File(directory),arffFile);
    }
    public String combineContentandUserFeatures(ArrayList alContent, ArrayList alUser,String pathContent, String pathUserBase,String pathCombine) throws FileNotFoundException, IOException
    {
        String output = "";
        String newline = System.getProperty("line.separator");
        //DataOutputStream dataOutput = null;
        //Content Base
        CSVReader reader = new CSVReader(new FileReader(pathContent));
        List<String[]> myDataContent = (ArrayList<String[]>) reader.readAll();
        //User Base
        CSVReader readerUserBase = new CSVReader(new FileReader(pathUserBase));
        List<String[]> myDataUserBase = (ArrayList<String[]>) readerUserBase.readAll();
        //start combining
        for (int i = 0; i < myDataContent.size();i++) 
        {
            String str = Arrays.toString(myDataContent.get(i)).replace("]", "").replace("[", "").replaceAll(" ", "");
            String temp[] = str.split(",");            
            String valueContent = temp[alContent.size()];
            //System.out.println("t " +valueContent);
            String result = str.replace(","+valueContent, "");
            for(int z = 0; z < myDataUserBase.size();z++)
            {
                String strUser = Arrays.toString(myDataUserBase.get(z)).replace("]", "").replace("[", "").replaceAll(" ", "");
                String tempUser[] = strUser.split(","); 
                
               //System.out.println("a " + t);
                String valueUser = tempUser[alUser.size()];
                //System.out.println("abc "+valueUser);// số 65 cua 65 user features
                String resultUser = strUser.replace(","+valueUser, ""); 
                if(valueContent.equals(valueUser))
                {
                    output += result +","+resultUser+newline; 
                    //System.out.println(output);
                }               
            }
        }
        return output;
    }
    public String featureSelection(String fileDest, String algorithm) throws FileNotFoundException, IOException, Exception
    {
        //fileDest = "/Users/Trung/Desktop/training.arff";
        String result = "";
        BufferedReader inputFile = new BufferedReader(new FileReader(fileDest));//
        Instances dataset = new Instances(inputFile);//
        dataset.setClassIndex(dataset.numAttributes()-1);//       
        AttributeSelection filter = new AttributeSelection();//
        
        if(algorithm.equals("CfsSubsetEval"))
        {
            CfsSubsetEval eval = new CfsSubsetEval();
            BestFirst search = new BestFirst();
            filter.setEvaluator(eval);
            filter.setSearch(search);
            filter.SelectAttributes(dataset);
            result = filter.toResultsString();            
        }
        else if(algorithm.equals("InforGain"))
        {
            InfoGainAttributeEval evaluator = new InfoGainAttributeEval();           
            Ranker ranker = new Ranker();
            ranker.setNumToSelect(10);// specify the ranked number
            filter.setEvaluator(evaluator);
            filter.setSearch(ranker);           
            filter.SelectAttributes(dataset);
            result = filter.toResultsString();
        }
        else if(algorithm.equals("RelieF"))
        {
            ReliefFAttributeEval evaluator = new ReliefFAttributeEval();
            Ranker ranker = new Ranker();
            ranker.setNumToSelect(10);
            filter.setEvaluator(evaluator);
            filter.setSearch(ranker);
            filter.SelectAttributes(dataset);
            result = filter.toResultsString();
        }
        else if(algorithm.equals("Wrapper"))
        {
            WrapperSubsetEval evaluator = new WrapperSubsetEval();
            BestFirst search = new BestFirst();
            filter.setEvaluator(evaluator);
            filter.setSearch(search);
            filter.SelectAttributes(dataset);
            result = filter.toResultsString();   
        }
        //result = filter.toResultsString();
        return result;
    }
    public String classification(HashMap<String,String> trainingHM, HashMap<String,String> testingHM,String algorithm, ArrayList aEvaluationMetrics) throws FileNotFoundException, IOException, Exception
    {
        String newline = System.getProperty("line.separator");
        String result = "";
        String nameOfSystem = "";
        String evaluationMetrics = "";
        String trainingNameSystem = "";
        Iterator itTraining = trainingHM.entrySet().iterator();
        while (itTraining.hasNext()) 
        {
            Map.Entry pair = (Map.Entry)itTraining.next();
            nameOfSystem = (String) pair.getValue();  
            trainingNameSystem = (String) pair.getValue();
            DataSource sourceTraining = new DataSource((String) pair.getKey());
            Iterator itTesting = testingHM.entrySet().iterator();
            //testing dataset
            while (itTesting.hasNext())
            {
                Map.Entry pairGetTesting = (Map.Entry) itTesting.next();
                String nameOfTestingSystem = (String) pairGetTesting.getValue();
                if(trainingNameSystem.equalsIgnoreCase(nameOfTestingSystem))
                {
                    DataSource sourceTesting = new DataSource((String) pairGetTesting.getKey());
                    Instances trainingDataset = sourceTraining.getDataSet();
                    Instances testingDataset = sourceTesting.getDataSet();
                    trainingDataset.setClassIndex(trainingDataset.numAttributes()-1);
                    testingDataset.setClassIndex(trainingDataset.numAttributes()-1);
                    if(algorithm.equals("Naive Bayes"))
                    {
                        NaiveBayes nB = new NaiveBayes();
                        nB.buildClassifier(trainingDataset);
                        Evaluation eval = new Evaluation(trainingDataset);
                        eval.evaluateModel(nB, testingDataset); 
                        for(int i = 0; i < aEvaluationMetrics.size(); i++)
                        {
                            if(aEvaluationMetrics.get(i).equals("Accuracy"))
                                evaluationMetrics += "Accuracy: " + eval.pctCorrect() + "   ";
                            if(aEvaluationMetrics.get(i).equals("Precision"))
                                evaluationMetrics += "Precision: " + eval.precision(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("Recall"))
                                evaluationMetrics += "Recall: " + eval.recall(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("FMeasure"))
                                evaluationMetrics += "FMeasure: " + eval.recall(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("TruePositive"))
                                evaluationMetrics += "TruePositive: " + eval.truePositiveRate(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("TrueNegative"))
                                evaluationMetrics += "TrueNegative: " + eval.trueNegativeRate(1) + "   ";  
                            if(aEvaluationMetrics.get(i).equals("FalsePositive"))
                                evaluationMetrics += "FalsePositive: " + eval.falsePositiveRate(1) + "   "; 
                            if(aEvaluationMetrics.get(i).equals("FalseNegative"))
                                evaluationMetrics += "FalseNegative: " + eval.falseNegativeRate(i) + "   ";                               
                        }
                    }
                    if(algorithm.equals("SVM"))
                    {
                        SMO smo = new SMO();
                        smo.buildClassifier(trainingDataset);
                        Evaluation eval = new Evaluation(trainingDataset);
                        eval.evaluateModel(smo, testingDataset); 
                        for(int i = 0; i < aEvaluationMetrics.size(); i++)
                        {
                            if(aEvaluationMetrics.get(i).equals("Accuracy"))
                                evaluationMetrics += "Accuracy: " + eval.pctCorrect() + "   ";
                            if(aEvaluationMetrics.get(i).equals("Precision"))
                                evaluationMetrics += "Precision: " + eval.precision(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("Recall"))
                                evaluationMetrics += "Recall: " + eval.recall(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("FMeasure"))
                                evaluationMetrics += "FMeasure: " + eval.recall(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("TruePositive"))
                                evaluationMetrics += "TruePositive: " + eval.truePositiveRate(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("TrueNegative"))
                                evaluationMetrics += "TrueNegative: " + eval.trueNegativeRate(1) + "   ";  
                            if(aEvaluationMetrics.get(i).equals("FalsePositive"))
                                evaluationMetrics += "FalsePositive: " + eval.falsePositiveRate(1) + "   "; 
                            if(aEvaluationMetrics.get(i).equals("FalseNegative"))
                                evaluationMetrics += "FalseNegative: " + eval.falseNegativeRate(i) + "   ";                               
                        }
                    } 
                    if(algorithm.equals("J48"))
                    {
                        J48 j48 = new J48();
                        j48.buildClassifier(trainingDataset);
                        Evaluation eval = new Evaluation(trainingDataset);
                        eval.evaluateModel(j48, testingDataset); 
                        for(int i = 0; i < aEvaluationMetrics.size(); i++)
                        {
                            if(aEvaluationMetrics.get(i).equals("Accuracy"))
                                evaluationMetrics += "Accuracy: " + eval.pctCorrect() + "   ";
                            if(aEvaluationMetrics.get(i).equals("Precision"))
                                evaluationMetrics += "Precision: " + eval.precision(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("Recall"))
                                evaluationMetrics += "Recall: " + eval.recall(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("FMeasure"))
                                evaluationMetrics += "FMeasure: " + eval.recall(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("TruePositive"))
                                evaluationMetrics += "TruePositive: " + eval.truePositiveRate(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("TrueNegative"))
                                evaluationMetrics += "TrueNegative: " + eval.trueNegativeRate(1) + "   ";  
                            if(aEvaluationMetrics.get(i).equals("FalsePositive"))
                                evaluationMetrics += "FalsePositive: " + eval.falsePositiveRate(1) + "   "; 
                            if(aEvaluationMetrics.get(i).equals("FalseNegative"))
                                evaluationMetrics += "FalseNegative: " + eval.falseNegativeRate(i) + "   ";                               
                        }
                    } 
                    if(algorithm.equals("Random Forest"))
                    {
                        RandomForest rf = new RandomForest();
                        rf.buildClassifier(trainingDataset);
                        Evaluation eval = new Evaluation(trainingDataset);
                        eval.evaluateModel(rf, testingDataset); 
                        for(int i = 0; i < aEvaluationMetrics.size(); i++)
                        {
                            if(aEvaluationMetrics.get(i).equals("Accuracy"))
                                evaluationMetrics += "Accuracy: " + eval.pctCorrect() + "   ";
                            if(aEvaluationMetrics.get(i).equals("Precision"))
                                evaluationMetrics += "Precision: " + eval.precision(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("Recall"))
                                evaluationMetrics += "Recall: " + eval.recall(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("FMeasure"))
                                evaluationMetrics += "FMeasure: " + eval.recall(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("TruePositive"))
                                evaluationMetrics += "TruePositive: " + eval.truePositiveRate(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("TrueNegative"))
                                evaluationMetrics += "TrueNegative: " + eval.trueNegativeRate(1) + "   ";  
                            if(aEvaluationMetrics.get(i).equals("FalsePositive"))
                                evaluationMetrics += "FalsePositive: " + eval.falsePositiveRate(1) + "   "; 
                            if(aEvaluationMetrics.get(i).equals("FalseNegative"))
                                evaluationMetrics += "FalseNegative: " + eval.falseNegativeRate(i) + "   ";                               
                        }
                    }
                    if(algorithm.equals("IBK"))
                    {
                        IBk ibk = new IBk();
                        ibk.buildClassifier(trainingDataset);
                        Evaluation eval = new Evaluation(trainingDataset);
                        eval.evaluateModel(ibk, testingDataset); 
                        for(int i = 0; i < aEvaluationMetrics.size(); i++)
                        {
                            if(aEvaluationMetrics.get(i).equals("Accuracy"))
                                evaluationMetrics += "Accuracy: " + eval.pctCorrect() + "   ";
                            if(aEvaluationMetrics.get(i).equals("Precision"))
                                evaluationMetrics += "Precision: " + eval.precision(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("Recall"))
                                evaluationMetrics += "Recall: " + eval.recall(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("FMeasure"))
                                evaluationMetrics += "FMeasure: " + eval.recall(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("TruePositive"))
                                evaluationMetrics += "TruePositive: " + eval.truePositiveRate(1) + "   ";
                            if(aEvaluationMetrics.get(i).equals("TrueNegative"))
                                evaluationMetrics += "TrueNegative: " + eval.trueNegativeRate(1) + "   ";  
                            if(aEvaluationMetrics.get(i).equals("FalsePositive"))
                                evaluationMetrics += "FalsePositive: " + eval.falsePositiveRate(1) + "   "; 
                            if(aEvaluationMetrics.get(i).equals("FalseNegative"))
                                evaluationMetrics += "FalseNegative: " + eval.falseNegativeRate(i) + "   ";                               
                        }
                    }                    
                }
            }
           result += nameOfSystem + newline + evaluationMetrics + newline;
           nameOfSystem = "";
           evaluationMetrics = "";
        }
        //System.out.println(nameOfSystem);
        return result;
    }
    public String getPathArffFile()
    {
        String path = pathArffFile;
        return path;
    }
}
