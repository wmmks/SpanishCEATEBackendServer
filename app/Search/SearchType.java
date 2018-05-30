package Search;

import constantField.ConstantField;
import constantField.DatabaseColumnNameVariableTable;
import extractContent.OtherColumnExtraction;
import json.JSONObject;

import java.sql.SQLException;
import java.util.*;

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
     * Original Position List.
     */
    private List<Integer> originalPositionList;

    /**
     * Correct Position List.
     */
    private List<Integer> correctPositionList;

    /**
     * Lemma List.
     */
    private List lemmaList;

    /**
     * Fuzzy List.
     */
    private List fuzzyList;

    /**
     * Extract Other Column.
     */
    private OtherColumnExtraction otherColumnExtraction = new OtherColumnExtraction();

    /**
     * Produce Query Relation for 3 type.
     * @param wordText query
     * @param wordPOS word pos
     * @param nextWordPOS next word pos
     * @throws SQLException SQL Exception
     */
    void setSentenceOfPalabra(String wordText, String wordPOS, String nextWordPOS, String type) throws SQLException {
        List<List<String>> wordIDListInList = new ArrayList<>();
        List<String> sentenceIDInListByOriginal = new ArrayList<>();
        List<String> sentenceIDInListByCorrect = new ArrayList<>();
        List<String> sentenceIDByOriginal, sentenceIDByCorrect;
        String nextWordID;
        originalList = new ArrayList<>();
        correctList = new ArrayList<>();
        originalPositionList = new ArrayList<>();
        correctPositionList = new ArrayList<>();
        // Palabra POS POS
        if (!nextWordPOS.equals("") && !wordPOS.equals("") && !wordText.equals("")) {
            List<String> wordIDList;
            // Fuzzy Search
            if (wordPOS.contains("@")) {
                wordPOS = wordPOS.replace("@", "");
                wordPOS = wordPOS.split("=")[1];
                wordIDList = otherColumnExtraction.getOtherColumnExtraction(wordText + ":" + wordPOS, ConstantField.TEXT_AND_POS_WORD_ID_FUZZY);
            } else { // Normal Search
                wordIDList = otherColumnExtraction.getOtherColumnExtraction(wordText + ":" + wordPOS, ConstantField.TEXT_AND_POS_WORD_ID);
            }
            //List<String> wordIDList = otherColumnExtraction.getOtherColumnExtraction(wordText + ":" + wordPOS, ConstantField.TEXT_AND_POS_WORD_ID);
            if (type.equals(ConstantField.ORIGINAL)) {
                for (String wordId : wordIDList) {
                    sentenceIDByOriginal = otherColumnExtraction.getOtherColumnExtraction(wordId, ConstantField.SENTENCE_ID_AND_POSITION_BY_ORIGINAL);
                    for (String sentenceID : sentenceIDByOriginal) {
                        List list = otherColumnExtraction.getOtherColumnExtraction(sentenceID, ConstantField.NEXT_WORD_ID_BY_ORIGINAL);
                        if (!list.isEmpty()) {
                            nextWordID = list.get(0).toString();
                            if (!otherColumnExtraction.getOtherColumnExtraction(nextWordID + ":" + nextWordPOS, ConstantField.NEXT_WORD_ID_AND_POS_WORD_ID).isEmpty()) {
                                sentenceIDInListByOriginal.add(sentenceID);
                            }
                        }
                    }
                }
            } else if (type.equals(ConstantField.CORRECT)) {
                for (String wordId : wordIDList) {
                    sentenceIDByCorrect = otherColumnExtraction.getOtherColumnExtraction(wordId, ConstantField.SENTENCE_ID_AND_POSITION_BY_CORRECT);
                    for (String sentenceID : sentenceIDByCorrect) {
                        List list = otherColumnExtraction.getOtherColumnExtraction(sentenceID, ConstantField.NEXT_WORD_ID_BY_CORRECT);
                        if (!list.isEmpty()) {
                            nextWordID = list.get(0).toString();
                            if (!otherColumnExtraction.getOtherColumnExtraction(nextWordID + ":" + nextWordPOS, ConstantField.NEXT_WORD_ID_AND_POS_WORD_ID).isEmpty()) {
                                sentenceIDInListByCorrect.add(sentenceID);
                            }
                        }
                    }
                }
            }
        } else {
            // Palabra POS
            if (!wordPOS.equals("") && !wordText.equals("")) {
                // Fuzzy Search
                if (wordPOS.contains("@")) {
                    wordPOS = wordPOS.replace("@", "");
                    wordPOS = wordPOS.split("=")[1];
                    wordIDListInList.add(otherColumnExtraction.getOtherColumnExtraction(wordText + ":" + wordPOS, ConstantField.TEXT_AND_POS_WORD_ID_FUZZY));
                } else { // Normal Search
                    wordIDListInList.add(otherColumnExtraction.getOtherColumnExtraction(wordText + ":" + wordPOS, ConstantField.TEXT_AND_POS_WORD_ID));
                }
            }
            // Palabra
            else {
                wordIDListInList.add(otherColumnExtraction.getOtherColumnExtraction(wordText, ConstantField.WORD_ID));
            }
            if(type.equals(ConstantField.ORIGINAL)) {
                for (List<String> wordIDList : wordIDListInList) {
                    for (String wordID : wordIDList) {
                        sentenceIDInListByOriginal.addAll(otherColumnExtraction.getOtherColumnExtraction(wordID, ConstantField.SENTENCE_ID_BY_ORIGINAL));
                    }
                }
            } else if(type.equals(ConstantField.CORRECT)) {
                for (List<String> wordIDList : wordIDListInList) {
                    for (String wordID : wordIDList) {
                        sentenceIDInListByCorrect.addAll(otherColumnExtraction.getOtherColumnExtraction(wordID, ConstantField.SENTENCE_ID_BY_CORRECT));
                    }
                }
            }
        }
        // 必須要有順序，SearchPreProcessing htmlString 才可以正常運作!
        if (type.equals(ConstantField.ORIGINAL)) {
            Collections.sort(sentenceIDInListByOriginal);
            for (String sentenceID : sentenceIDInListByOriginal) {
                List list = otherColumnExtraction.getOtherColumnExtraction(sentenceID.split(":")[0], ConstantField.ORIGINAL);
                if (list.size() != 0) {
                    originalList.add(list);
                    originalPositionList.add(Integer.parseInt(sentenceID.split(":")[1]));
                }
            }
        } else if(type.equals(ConstantField.CORRECT)) {
            Collections.sort(sentenceIDInListByCorrect);
            for (String sentenceID : sentenceIDInListByCorrect) {
                List list = otherColumnExtraction.getOtherColumnExtraction(sentenceID.split(":")[0], ConstantField.CORRECT);
                if (list.size() != 0) {
                    correctList.add(list);
                    correctPositionList.add(Integer.parseInt(sentenceID.split(":")[1]));
                }
            }
        }
    }

    /**
     * Produce Fuzzy Query Term.
     * @param fuzzy lemma
     * @throws SQLException SQL Exception
     */
    void setFuzzyOfPalabra(String fuzzy) throws SQLException {
        fuzzyList = otherColumnExtraction.getOtherColumnExtraction(fuzzy, DatabaseColumnNameVariableTable.FUZZY);
    }

    /**
     * Produce Query Relation for 1 type.
     * @param lemma lemma
     * @throws SQLException SQL Exception
     */
    void setLemmaOfPalabra(String lemma) throws SQLException {
        lemmaList = otherColumnExtraction.getOtherColumnExtraction(lemma, DatabaseColumnNameVariableTable.LEMMA);
    }

    /**
     * Other Column Condition judge Article Exists.
     */
    boolean judgeExists(String articleID, JSONObject userDataJsonObject) throws SQLException {
        List<String> otherCondition = new ArrayList<>();
        String learningHours = userDataJsonObject.getString(DatabaseColumnNameVariableTable.LEARNING_HOURS);
        String gender = userDataJsonObject.getString(DatabaseColumnNameVariableTable.GENDER);
        String department = userDataJsonObject.getString(DatabaseColumnNameVariableTable.DEPARTMENT);
        String specialExperience = userDataJsonObject.getString(DatabaseColumnNameVariableTable.SPECIAL_EXPERIENCE);
        String numberOfWords = userDataJsonObject.getString(DatabaseColumnNameVariableTable.NUMBER_Of_WORDS);
        String articleStyle = userDataJsonObject.getString(DatabaseColumnNameVariableTable.ARTICLE_STYLE);
        String articleTopic = userDataJsonObject.getString(DatabaseColumnNameVariableTable.ARTICLE_TOPIC);
        String writingLocation = userDataJsonObject.getString(DatabaseColumnNameVariableTable.WRITING_LOCATION);
        String submittedYear = userDataJsonObject.getString(DatabaseColumnNameVariableTable.SUBMITTED_YEAR);
        otherCondition.add(articleID);
        otherCondition.add(learningHours);
        otherCondition.add(gender);
        otherCondition.add(department);
        otherCondition.add(specialExperience);
        otherCondition.add(numberOfWords);
        otherCondition.add(articleStyle);
        otherCondition.add(articleTopic);
        otherCondition.add(writingLocation);
        otherCondition.add(submittedYear);
        List booleanList = otherColumnExtraction.getOtherColumnExtraction(otherCondition, ConstantField.JUDGE_OTHER_CONDITION);
        return booleanList.get(0).equals(true);
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

    /**
     * Get Original Position List.
     * @return originalPositionList
     */
    List getOriginalPositionList() {
        return originalPositionList;
    }

    /**
     * Get Correct Position List.
     * @return correctPositionList
     */
    List getCorrectPositionList() {
        return correctPositionList;
    }

    /**
     * Get Lemma List.
     * @return lemmaList
     */
    List getLemmaList() {
        return lemmaList;
    }

    /**
     * Get Fuzzy List.
     * @return fuzzyList
     */
    List getFuzzyList() {
        return fuzzyList;
    }
}
