package SearchType;

import extractContent.OtherColumnExtraction;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Palabra Searcher.
 *
 * @version 1.0 2017年10月17日
 * @author Alex
 *
 */
public class Palabra {

    /**
     * Original List.
     */
    private ArrayList<String> originalList;

    /**
     * Correct List.
     */
    private ArrayList<String> correctList;

    /**
     * Produce Query Relation for 3 type.
     * @param wordText query
     * @throws SQLException SQL Exception
     */
    public void setPalabraSentence(String wordText, String wordPOS, String nextWordPOS) throws SQLException {
        OtherColumnExtraction otherColumnExtraction = new OtherColumnExtraction();
        ArrayList<ArrayList<String>> wordIDListInList = new ArrayList<>();
        ArrayList<String> sentenceIDList;
        String nextWordID;
        originalList = new ArrayList<>();
        correctList = new ArrayList<>();
        // Palabra POS POS
        if (!nextWordPOS.equals("") && !wordPOS.equals("") && !wordText.equals("")) {
            ArrayList<String> wordIDList = otherColumnExtraction.getOtherColumnExtraction(wordText + ":" + wordPOS, "textAndPos_WordID");
            for (String wordId : wordIDList) {
                sentenceIDList = otherColumnExtraction.getOtherColumnExtraction(wordId, "sentenceIDAndPosition");
                for (String sentenceID : sentenceIDList) {
                    if (!otherColumnExtraction.getOtherColumnExtraction(sentenceID, "nextWordID").isEmpty()) {
                        nextWordID = otherColumnExtraction.getOtherColumnExtraction(sentenceID, "nextWordID").get(0);
                        wordIDListInList.add(otherColumnExtraction.getOtherColumnExtraction(nextWordID + ":" + nextWordPOS, "nextWordIDAndPos_WordID"));
                    }
                }
            }
        } else if (!wordPOS.equals("") && !wordText.equals("")) { // Palabra POS
            wordIDListInList.add(otherColumnExtraction.getOtherColumnExtraction(wordText + ":" + wordPOS, "textAndPos_WordID"));
        } else { // Palabra
            wordIDListInList.add(otherColumnExtraction.getOtherColumnExtraction(wordText, "wordID"));
        }
        for (ArrayList<String> wordIDList : wordIDListInList) {
            for (String wordID : wordIDList) {
                sentenceIDList = otherColumnExtraction.getOtherColumnExtraction(wordID, "sentenceID");
                for (String sentenceID : sentenceIDList) {
                    if (otherColumnExtraction.getOtherColumnExtraction(sentenceID, "original").size() != 0) {
                        originalList.add(otherColumnExtraction.getOtherColumnExtraction(sentenceID, "original").get(0));
                    }
                    if (otherColumnExtraction.getOtherColumnExtraction(sentenceID, "correct").size() != 0) {
                        correctList.add(otherColumnExtraction.getOtherColumnExtraction(sentenceID, "correct").get(0));
                    }
                }
            }
        }
    }

    /**
     * Get Original List.
     * @return originalList
     */
    public ArrayList<String> getOriginalList() {
        return originalList;
    }

    /**
     * Get Correct List.
     * @return correctList
     */
    public ArrayList<String> getCorrectList() {
        return correctList;
    }
}
