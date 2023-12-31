import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class Main {
    static Set<String> links = new HashSet<>();
    static HashMap<String, DictEntry> index;
    static Vector<Integer> lens =new Vector<>();
    static Vector<Integer> CommonDocOfWords = new Vector<>();
    static Vector<Integer> CommonFilesOfQuery =new Vector<>();
    static Vector<Double>  CosSimCommonFiles = new Vector<>();

    // TODO: Add file directions for your PC to the array variable below
    static String[] arr = {
            "/path/to/your/file1",
            "/path/to/your/file2",
            // Add more file paths as needed
    };


 //// for Files
    public static void getPageLinks2(File file) {
        // Check if you have already processed the file
        if (!links.contains(file.getAbsolutePath())) {
            try {
                // If not, add it to the processed files set
                links.add(file.getAbsolutePath());

                // Read the file content
                List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

                // Process the lines as needed
                for (String line : lines) {
                    System.out.println(line);
                    // Process the line or extract data from it
                    // ...
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + file.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }

    //// For  URLS
    public static void getPageLinks(String URL) {

            // 4. Check if you have already crawled the URLs
            // (we are intentionally not checking for duplicate content in this example)
            if (!links.contains(URL)) {
                try {
                    // 4. (i) If not add it to the index
                    if (links.add(URL)) {
                        System.out.println(URL);
                    }

                    // 2. Fetch the HTML code
                    Document document = Jsoup.connect(URL).get(); // jsoup jar to extract web data
                    // 3. Parse the HTML to extract links to other URLs
                    Elements linksOnPage = document.select("a[href]");

                    // 5. For each extracted URL... go back to Step 4.
                    for (Element page : linksOnPage) {
                        getPageLinks(page.attr("abs:href"));
                    }
                } catch (IOException e) {
                    System.err.println("For '" + URL + "': " + e.getMessage());
                }
            }
        }


    public static void Get_Files_Query( String[] Line){
        Map<Integer, Integer> frequency = new HashMap<>();
        // Count the frequency of each element in the CommonDoc vector
        for (Integer element : CommonDocOfWords) {
            frequency.put(element, frequency.getOrDefault(element, 0) + 1);
        }
        Vector<Integer> Result = new Vector<>();
        for (Map.Entry<Integer, Integer> entry : frequency.entrySet()) {
            if (entry.getValue() == Line.length) {
                if (index.containsKey(Line[0])) {
                    Posting current = index.get(Line[0]).getpList();
                    while (current != null) {
                        if (current.getdocID() == entry.getKey()) {
                            Result = current.positions;
                            break;
                        }
                        current = current.next;
                    }
                    for (int i = 1; i < Line.length; i++) {
                        Result = fun(Result, Line[i], entry.getKey());
                    }
                    if(!Result.isEmpty()){
                        CommonFilesOfQuery.add(entry.getKey());
                    }
                }
            }

        }
    }
    public static void Get_CS_Files_Query(String Query){
        for (int j = 0; j < CommonFilesOfQuery.size(); j++) {
            try {
                File Obj = new File(arr[CommonFilesOfQuery.get(j) - 1]);
                Scanner myReader = new Scanner(Obj);
                String x = "";
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    String[] LineNew = data.split(" ");
                    for (int i = 0; i < LineNew.length; i++) {
                        String s = LineNew[i].toLowerCase();
                        x += LineNew[i] + " ";
                    }
                }
                myReader.close();
                CosSimCommonFiles.add(CalcCosSimilarity(x,Query));
                x = "";

            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

        }
    }
    public static Double CalcCosSimilarity(String File,String Query){
        double CosSimilarity = 0;
        int CounterFile = 0 , CounterQuery = 0;
        int product = 0; // product
        int sum = 0;
        double Sqrt1 = 0,Sqrt2 = 0;
        String str[] = File.split(" ");
        List<String> FileArray = new ArrayList<String>();
        FileArray = Arrays.asList(str);
        String str2[] = Query.split(" ");
        List<String> QueryArray = new ArrayList<String>();

        QueryArray = Arrays.asList(str2);
        HashSet<String> Words = new HashSet<>(FileArray);
        Words.addAll(QueryArray);

        for(String word:Words){
            for(String w: FileArray) {
                if(word.equals(w)){
                    CounterFile++;
                }
            }
            for(String w: QueryArray) {
                if(word.equals(w)){
                    CounterQuery++;
                }
            }
            Sqrt1 += Math.pow(CounterFile,2.0);
            Sqrt2 += Math.pow(CounterQuery,2.0);
            product = CounterFile * CounterQuery;
            sum += product;
            //System.out.println(sum);
            CounterFile =0;CounterQuery=0;
        }
        Sqrt1 = Math.sqrt(Sqrt1);
        Sqrt2 = Math.sqrt(Sqrt2);
        CosSimilarity = sum/ (Sqrt1 * Sqrt2);
        //CosSimilarity = valuePrecision(CosSimilarity);
        return  CosSimilarity;
    }

    public static Vector<Integer> fun(Vector<Integer> Places, String Word, int docID) {
        Vector<Integer> Result = new Vector<>();
        Vector<Integer> v = new Vector<>();
        if (index.containsKey(Word)) {
            Posting current = index.get(Word).getpList();
            while (current != null) {
                if (current.getdocID() == docID) {
                    v = current.positions;
                    break;
                }
                current = current.next;
            }
        }
        for (int i = 0; i < Places.size(); i++) {
            for (int j = 0; j < v.size(); j++) {
                if (Places.get(i) == v.get(j) - 1) {
                  Result.add(v.get(j));
                }

            }
        }
        return Result;
    }

    public static void main(String[] args) {
        index = new HashMap<String, DictEntry>();
        int DocID = 0;
        for (int j = 0; j <= 9; j++) {
            DocID++;
            try {
                int counter = 0;
                File myObj = new File(arr[j]);
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    String[] Line = data.split(" ");
                    //////////////////////udatteeeeee/////////////
                    lens.add(Line.length);
                    //////////////////////////////
                    for (int i = 0; i < Line.length; i++) {
                        String s = Line[i].toLowerCase();
                        if (s.equals("and") || s.equals("or") || s.equals("the")) {
                            continue;
                        }
                        // Word has not exist in hash map so create an entry for it
                        else if (!index.containsKey(Line[i].toLowerCase())) {
                            Posting P = new Posting(DocID, null, counter);
                            DictEntry E = new DictEntry(1, 1, P);
                            index.put(Line[i].toLowerCase(), E);
                        } else {
                            boolean flag = false;
                            DictEntry E1 = index.get(Line[i].toLowerCase());
                            E1.addTermFreq();
                            Posting current = E1.getpList();
                            ///// update
                            while (current != null) {
                                if (current.getdocID() == DocID) {
                                    current.add_dtf();
                                    current.positions.add(counter);
                                    flag = true;
                                    break;
                                }
                                current = current.next;
                            }
                            if (!flag) {
                                Posting P2 = new Posting(DocID, null, counter);
                                Posting current2 = E1.getpList();
                                while (current2.next != null) {
                                    current2 = current2.next;
                                }
                                current2.setPosting(P2);
                                E1.addDocFreq();
                            }
                            index.put(Line[i].toLowerCase(), E1);
                        }
                        counter++;
                    }
                }
                myReader.close();

            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

        }

        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter word: ");
        String word = myObj.nextLine();  // Read user input
        word = word.toLowerCase();
        String[] Line = word.split(" ");
        for (int i = 0; i < Line.length; i++) {
            if (index.containsKey(Line[i])) {
                System.out.println("DictEntry : " + Line[i]);
                System.out.println("DocFreq: " + index.get(Line[i]).getdoc_freq() + "     " + "TermFreq: " + index.get(Line[i]).getterm_freq());
                System.out.println(" ");
                Posting current = index.get(Line[i]).getpList();
                System.out.println("Files that contain this word : ");
                while (current != null) {
                    System.out.print("DocID: " + current.getdocID() + "    " + "DocTermFreq: " + current.getdtf() + "    " + "Positions: ");
                    for (int j = 0; j < current.positions.size(); j++) {
                        System.out.print(current.positions.get(j) + ",");
                    }
                    ////////////////////////////////////////////////////////////////
                    System.out.println();
                    double termFrequency=(double)current.getdtf()/(double) (lens.get(current.getdocID()-1));
                    double idf = Math.log10(10.0 /(double)index.get(Line[i]).getdoc_freq());
                    double tfidf = termFrequency * idf;
                    //System.out.println("IDF : " +idf);
                    System.out.println("TF-IDF score: " +tfidf);
                    /////////////////////////////////////////////////////////////
                    CommonDocOfWords.add(current.getdocID());
                    current = current.next;

                }
                System.out.println("-----------------------------------------------------");
            } else {
                System.out.println("This word not exist !!!");
            }
        }
        Get_Files_Query(Line);
        System.out.println("Files that have the same Query: " + CommonFilesOfQuery);
        Get_CS_Files_Query(word);
        System.out.println("CosSimilarity Of Files:         " + CosSimCommonFiles);
        HashMap<Double,Integer> RankingOfFileds = new HashMap<Double,Integer>();
        for(int i =0;i<CommonFilesOfQuery.size();i++){
            RankingOfFileds.put(CosSimCommonFiles.get(i),CommonFilesOfQuery.get(i));
        }
        TreeMap<Double, Integer> sortedMap = new TreeMap<Double, Integer>(Collections.reverseOrder());
        sortedMap.putAll(RankingOfFileds);
        System.out.println("Ranking Files:        " +sortedMap);
        System.out.println("--------------------------------------------------");
        getPageLinks("https://stackoverflow.com/questions/6028724/adding-an-external-jar-library-to-intellij-idea");


        /*for (String filePath : arr) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                getPageLinks2(file);
            } else {
                System.err.println("File not found: " + filePath);
            }
        }*/
    }
}
