
import java.util.ArrayList;


public class CosineSimilarity {

    /**
     * Method to calculate cosine similarity between two documents.
     *
     * @param docVector1 : document vector 1 (a)
     * @param <error>
     * @param docVector2 : document vector 2 (b)
     * @return
     */
    public double cosineSimilarity(ArrayList<Integer> docVector1, ArrayList<Integer> docVector2) {
        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;
        double cosineSimilarity = 0.0;

        for (int i = 0; i < docVector1.size(); i++) //docVector1 and docVector2 must be of same length
        {
            dotProduct += docVector1.get(i)/1000 * docVector2.get(i);  //a.b
            magnitude1 += Math.pow((docVector1.get(i)/1000), 2);  //(a^2)
            magnitude2 += Math.pow((docVector2.get(i)), 2); //(b^2)
        }

        magnitude1 = Math.sqrt(magnitude1);//sqrt(a^2)
        magnitude2 = Math.sqrt(magnitude2);//sqrt(b^2)

        if (magnitude1 != 0.0 && magnitude2 != 0.0) {
            cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
        } else {
            return 0.0;
        }
        return cosineSimilarity;
    }
}
