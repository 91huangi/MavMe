package com.example.ivan.mavme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/******************************************************************************************
 * Search query handles fuzzy string matching using the cosine similarity algorithm on individual
 * characters in a string
 * @author Ivan Huang
 * ****************************************************************************************/

public class SearchQuery {

    private String query;
    private ArrayList<String> userIDs;

    /**
     * Constructor
     * @param query
     */
    public SearchQuery(String query) {
        this.query=query.toLowerCase();
        this.userIDs = new ArrayList<String>();
    }

    /**
     * For each user in system
     *      For each word in query
     *          For each word in user's name
     *              Return maxScore of W_q,W_u
     *      Sum maxScore
     * It then uses the scores to return an ArrayList of userIDs which are sorted from best score to worst.
     * @return
     */
    public ArrayList<String> search() {

        ArrayList<Double> scores = new ArrayList<Double>();

        if(!query.isEmpty()) {
            String[] qNames = query.split(" ");         // splitting query into words
            int qSize=qNames.length;

            // Foreach user in the database
            for(int i=0; i<DBMgr.numberOfUsers(); i++) {
                User u=DBMgr.getUserByID(DBMgr.indexToID(i));

                // Running direct comparison from word to word
                String[] uNames = u.getName().toLowerCase().split(" ");     // splitting user's name into words
                int uSize = uNames.length;
                double sumScore=0, avgScore;

                // pairwise comparison of query string to user name
                for(int j=0; j<qSize; j++) {
                    double maxScore=0;
                    for(int k=0; k<uSize; k++) {
                        maxScore = Math.max(maxScore, cosineSimilarity(qNames[j], uNames[k]));
                    }
                    sumScore+=maxScore;
                }

                // if the average max cosine similarity score is at least .9, add the user ID to the ArrayList
                avgScore=sumScore/qSize;
                if(avgScore>=.9) {
                    userIDs.add(DBMgr.indexToID(i));
                    scores.add(-avgScore);
                }

            }
        } else {
            userIDs.clear();
        }

        // Sort list based on the scores
        ArrayList<String> sortedUserIDs = Utils.pairSort(userIDs, scores);
        return sortedUserIDs;
    }


    /************CO-SINE SIMILARITY*****************/
    /**
     * author: https://blog.nishtahir.com/2015/09/20/fuzzy-string-matching-using-cosine-similarity/
     *
     *
     * @param terms values to analyze
     * @return a map containing unique
     * terms and their frequency
     */
    public static Map<String, Integer> getTermFrequencyMap(String[] terms) {
        Map<String, Integer> termFrequencyMap = new HashMap<>();
        for (String term : terms) {
            Integer n = termFrequencyMap.get(term);
            n = (n == null) ? 1 : ++n;
            termFrequencyMap.put(term, n);
        }
        return termFrequencyMap;
    }

    /**
     * @param text1
     * @param text2
     * @return cosine similarity of text1 and text2
     */

    public static double cosineSimilarity(String text1, String text2) {
        //Get vectors
        Map<String, Integer> a = getTermFrequencyMap(text1.split(""));
        Map<String, Integer> b = getTermFrequencyMap(text2.split(""));

        //Get unique words from both sequences
        HashSet<String> intersection = new HashSet<>(a.keySet());
        intersection.retainAll(b.keySet());

        double dotProduct = 0, magnitudeA = 0, magnitudeB = 0;

        //Calculate dot product
        for (String item : intersection) {
            dotProduct += a.get(item) * b.get(item);
        }

        //Calculate magnitude a
        for (String k : a.keySet()) {
            magnitudeA += Math.pow(a.get(k), 2);
        }

        //Calculate magnitude b
        for (String k : b.keySet()) {
            magnitudeB += Math.pow(b.get(k), 2);
        }

        //return cosine similarity
        return dotProduct / Math.sqrt(magnitudeA * magnitudeB);
    }
}
