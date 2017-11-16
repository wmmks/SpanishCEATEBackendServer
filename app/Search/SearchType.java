package Search;

import extractContent.OtherColumnExtraction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Type Searcher.
 * 根據不同類型搜尋之方式。
 * @version 1.0 2017年10月17日
 * @author Alex
 *
 */
class SearchType {
    /**
     * Original List.
     */
    private List<List<Map<String, String>>> originalList;

    /**
     * Correct List.
     */
    private List<List<Map<String, String>>> correctList;

    /**
     * Produce Query Relation for 3 type.
     * @param wordText query
     * @throws SQLException SQL Exception
     */
    void setPalabraSentence(String wordText, String wordPOS, String nextWordPOS) throws SQLException {
        OtherColumnExtraction otherColumnExtraction = new OtherColumnExtraction();
        List<List<String>> wordIDListInList = new ArrayList<>();
        List<List<String>> wordIDListInListByOriginal;
        List<List<String>> wordIDListInListByCorrect;
        List<String> sentenceIDInListByOriginal = new ArrayList<>();
        List<String> sentenceIDInListByCorrect = new ArrayList<>();
        List<String> sentenceIDByOriginal, sentenceIDByCorrect;
        String nextWordID;
        originalList = new ArrayList<>();
        correctList = new ArrayList<>();
        // Palabra POS POS
        if (!nextWordPOS.equals("") && !wordPOS.equals("") && !wordText.equals("")) {
            List<String> wordIDList = otherColumnExtraction.getOtherColumnExtraction(wordText + ":" + wordPOS, "textAndPos_WordID");
            for (String wordId : wordIDList) {
                sentenceIDByOriginal = otherColumnExtraction.getOtherColumnExtraction(wordId, "sentenceIDAndPositionByOriginal");
                for (String sentenceID : sentenceIDByOriginal) {
                    if (!otherColumnExtraction.getOtherColumnExtraction(sentenceID, "nextWordIDByOriginal").isEmpty()) {
                        nextWordID = otherColumnExtraction.getOtherColumnExtraction(sentenceID, "nextWordIDByOriginal").get(0).toString();
                        if (!otherColumnExtraction.getOtherColumnExtraction(nextWordID + ":" + nextWordPOS, "nextWordIDAndPos_WordID").isEmpty()) {
                            sentenceIDInListByOriginal.add(sentenceID.split(":")[0]);
                        }
                    }
                }
                sentenceIDByCorrect = otherColumnExtraction.getOtherColumnExtraction(wordId, "sentenceIDAndPositionByCorrect");
                for (String sentenceID : sentenceIDByCorrect) {
                    if (!otherColumnExtraction.getOtherColumnExtraction(sentenceID, "nextWordIDByCorrect").isEmpty()) {
                        nextWordID = otherColumnExtraction.getOtherColumnExtraction(sentenceID, "nextWordIDByCorrect").get(0).toString();
                        if (!otherColumnExtraction.getOtherColumnExtraction(nextWordID + ":" + nextWordPOS, "nextWordIDAndPos_WordID").isEmpty()) {
                            sentenceIDInListByCorrect.add(sentenceID.split(":")[0]);
                        }
                    }
                }
            }
        } else if (!wordPOS.equals("") && !wordText.equals("")) { // Palabra POS
            wordIDListInList.add(otherColumnExtraction.getOtherColumnExtraction(wordText + ":" + wordPOS, "textAndPos_WordID"));
            wordIDListInListByOriginal = wordIDListInList;
            wordIDListInListByCorrect = wordIDListInList;
            for (List<String> wordIDList : wordIDListInListByOriginal) {
                for (String wordID : wordIDList) {
                    sentenceIDInListByOriginal.addAll(otherColumnExtraction.getOtherColumnExtraction(wordID, "sentenceIDByOriginal"));
                }
            }
            for (List<String> wordIDList : wordIDListInListByCorrect) {
                for (String wordID : wordIDList) {
                    sentenceIDInListByCorrect.addAll(otherColumnExtraction.getOtherColumnExtraction(wordID, "sentenceIDByCorrect"));
                }
            }
        } else { // Palabra
            wordIDListInList.add(otherColumnExtraction.getOtherColumnExtraction(wordText, "wordID"));
            wordIDListInListByOriginal = wordIDListInList;
            wordIDListInListByCorrect = wordIDListInList;
            for (List<String> wordIDList : wordIDListInListByOriginal) {
                for (String wordID : wordIDList) {
                    sentenceIDInListByOriginal.addAll(otherColumnExtraction.getOtherColumnExtraction(wordID, "sentenceIDByOriginal"));
                }
            }
            for (List<String> wordIDList : wordIDListInListByCorrect) {
                for (String wordID : wordIDList) {
                    sentenceIDInListByCorrect.addAll(otherColumnExtraction.getOtherColumnExtraction(wordID, "sentenceIDByCorrect"));
                }
            }
        }
        for (String sentenceID : sentenceIDInListByOriginal) {
            if (otherColumnExtraction.getOtherColumnExtraction(sentenceID, "original").size() != 0) {
                originalList.add(otherColumnExtraction.getOtherColumnExtraction(sentenceID, "original"));
            }
        }
        for (String sentenceID : sentenceIDInListByCorrect) {
            if (otherColumnExtraction.getOtherColumnExtraction(sentenceID, "correct").size() != 0) {
                correctList.add(otherColumnExtraction.getOtherColumnExtraction(sentenceID, "correct"));
            }
        }
    }

    /**
     * Get Original List.
     * @return originalList
     */
    List getOriginalList() {
        return originalList;
    }

    /**
     * Get Correct List.
     * @return correctList
     */
    List getCorrectList() {
        return correctList;
    }
}
