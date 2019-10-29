
import java.io.*;
import java.sql.SQLException;
import java.util.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author SIDDI BOY
 */
public class DocIndexer {

    private String pathToIndex;
    private String pathToDocumentCollection;
    int indexDocCount = 0;

    /**
     * @param pathToIndex- doc index path
     * @param pathToDocumentCollection - doc collection path
     */
    public DocIndexer(String pathToIndex, String pathToDocumentCollection) {

        this.pathToIndex = pathToIndex;
        this.pathToDocumentCollection = pathToDocumentCollection;
    }

    /*
*given it's URL this methods read the text files
     */
    public static String fileReader(String filename) throws IOException {

        String filetext = null;
        BufferedReader reader = null;

        File inFile = new File(filename);

        //READING FROM USERS FILE
        reader = new BufferedReader(new FileReader(inFile));
        String line = null;

        int numLine = 0;

        while ((line = reader.readLine()) != null) {

            filetext = filetext + " " + line;
        }

        reader.close();
        return filetext;
    }

    /**
     * Method to index the documents only using the content of the document
     * "docid" field is used for indexing, since Lucene Dosen't retrieve the
     * documents in the indexed order
     *
     * @param docNo- document number of the document to be indexed
     * @throws IOException
     */
    public void indexDocs() throws IOException {
        File folder = new File(pathToDocumentCollection);
        File[] listOfFiles = folder.listFiles();
        int noOfFiles = listOfFiles.length;
        System.out.println("Number of files : " + noOfFiles);

        IndexWriter iW;

        try {
            NIOFSDirectory dir = new NIOFSDirectory(new File(pathToIndex));
            iW = new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_34, new StandardAnalyzer(Version.LUCENE_34)));

            for (int i = 0; i < noOfFiles; i++) {
                if (listOfFiles[i].isFile()) {
                    String docName = listOfFiles[i].getName();
                    System.out.println("doc name: " + docName + " length - " + listOfFiles[i].length());
                    if (listOfFiles[i].length() > 1) {
                        String filesInText = fileReader(pathToDocumentCollection + docName);
                        System.out.println("Added to index : " + docName);

                        StringReader strRdElt = new StringReader(filesInText.replaceAll("\\d+(?:[.,]\\d+)*\\s*", ""));
                        StringReader docId = new StringReader(docName.substring(0, docName.length() - 4)); // give a unique doc Id here
                        Document doc = new Document();

                        doc.add(new Field("doccontent", strRdElt, Field.TermVector.YES));
                        doc.add(new Field("docid", docId, Field.TermVector.YES));
                        iW.addDocument(doc);
                        indexDocCount++;
                    }
                }
            }

            System.out.println("no of documents added to index : " + indexDocCount);

            iW.close();
            // dir.close() ;
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method calculates the TF-IDF score for each terms in the indexed
     * documents
     *
     * @param numberOfDocs
     * @return - Hashmap of TF-IDF score per each term in document wise
     * @throws CorruptIndexException
     * @throws ParseException
     */
    public void tfIdfScore(int numberOfDocs) throws CorruptIndexException, ParseException, SQLException {

        int noOfDocs = indexDocCount;

        try {

            IndexReader re = IndexReader.open(NIOFSDirectory.open(new File(pathToIndex)), true);

            int i = 0;
            Cryptcon con = new Cryptcon();
            for (int k = 0; k < numberOfDocs; k++) {
                int freq[];
                TermFreqVector termsFreq;
                TermFreqVector termsFreqDocId;

                String terms[];
                ArrayList<Integer> frequency = new ArrayList();

                termsFreq = re.getTermFreqVector(k, "doccontent");
                termsFreqDocId = re.getTermFreqVector(k, "docid");
                System.out.println(termsFreqDocId.getTerms()[0]);
                String name = termsFreqDocId.getTerms()[0];
                freq = termsFreq.getTermFrequencies();

                terms = termsFreq.getTerms();

                int noOfTerms = terms.length;

                DefaultSimilarity simi = new DefaultSimilarity();
                con.create(name);
                for (i = 0; i < noOfTerms; i++) {
                    int noofDocsContainTerm = re.docFreq(new Term("doccontent", terms[i]));
                    float tf = simi.tf(freq[i]);
                    float idf = simi.idf(noofDocsContainTerm, noOfDocs);
                    float tfidf = tf * idf * 1000;
                    frequency.add((int) tfidf);

                    System.out.println(terms[i] + "     " + tf * idf);

                }
                con.insert(name, terms, frequency);
            }
            con.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getTFIDF() throws IOException, CorruptIndexException, ParseException, ClassNotFoundException, SQLException {
        int noOfDocs = indexDocCount;
        tfIdfScore(noOfDocs);
    }

    public static void main(String args[]) throws IOException, CorruptIndexException, ParseException, ClassNotFoundException, SQLException {
        DocIndexer dc = new DocIndexer("/home/ubuntu/doc4/", "/home/ubuntu/doc3/");
        dc.indexDocs();
        dc.getTFIDF();
    }
}
