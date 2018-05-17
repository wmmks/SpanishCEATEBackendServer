package Search;

import com.fasterxml.jackson.databind.JsonNode;
import constantField.ConstantField;
import constantField.DatabaseColumnNameVariableTable;
import databaseUtil.DatabaseController;
import json.JSONObject;
import org.dom4j.DocumentException;
import play.libs.Json;
import sqlCommandLogic.SqlCommandComposer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Search Post Processing.
 * 將資料進行後處理以方便前端介面呈現。
 * @version 1.0 2017年11月16日
 * @author Alex
 *
 */
public class SearchPreProcessing {
    /**
     * Fuzzy String.
     */
    private String fuzzy = "";

    /**
     * Fuzzy Result JsonObject.
     */
    private JSONObject fuzzyResultJsonObject;

    /**
     * Search Type.
     */
    private SearchType palabra = new SearchType();

    /**
     * Search Post Processing About Web Expression.
     * @param userDataJsonObject user data Json Object
     * @return result original or correct sentence link
     * @throws SQLException SQL Exception
     */
    public JsonNode setSearchProcessingOfPalabra(JSONObject userDataJsonObject) throws SQLException {
        String wordText;
        String wordPOS;
        if (fuzzy.equals("")) {
            wordText = userDataJsonObject.getString(ConstantField.WORD_TEXT);
            wordPOS = userDataJsonObject.getString(ConstantField.WORD_POS);
        } else {
            wordText = fuzzy.split(":")[0];
            // @ 用來辨識是否為 fuzzy 搜尋的字符
            wordPOS = fuzzy.split(":")[1] + "@";
        }
        String nextWordPOS = userDataJsonObject.getString(ConstantField.NEXT_WORD_POS);
        String originalOrCorrect = userDataJsonObject.getString(ConstantField.ORIGINAL_OR_CORRECT);
        JSONObject resultJsonObject = new JSONObject();
        List<List<Map<String, String>>> originalListList;
        List<List<Map<String, String>>> correctListList;
        List<Integer> originalPositionList;
        List<Integer> correctPositionList;
        if (originalOrCorrect.equals("1")) {
            palabra.setSentenceOfPalabra(wordText, wordPOS, nextWordPOS, ConstantField.ORIGINAL);
            originalListList = palabra.getOriginalList();
            originalPositionList = palabra.getOriginalPositionList();
            resultJsonObject = htmlString(userDataJsonObject, ConstantField.ORIGINAL,
                    originalListList, originalPositionList);
        } else if(originalOrCorrect.equals("2")) {
            palabra.setSentenceOfPalabra(wordText, wordPOS, nextWordPOS, ConstantField.CORRECT);
            correctListList = palabra.getCorrectList();
            correctPositionList = palabra.getCorrectPositionList();
            resultJsonObject = htmlString(userDataJsonObject, ConstantField.CORRECT,
                    correctListList, correctPositionList);
        }
        if (!fuzzy.equals("")) {
            for (String s : resultJsonObject.keySet()) {
                fuzzyResultJsonObject.put(s, resultJsonObject.get(s));
            }
        }
        return Json.parse(resultJsonObject.toString());
    }

    /**
     * Html Format.
     */
    public JSONObject htmlString(JSONObject userDataJsonObject, String type, List<List<Map<String,
            String>>> listList, List<Integer> positionList) throws SQLException {
        // Sentence Temperate
        String sentenceTem;
        String wordText;
        JSONObject resultJsonObject = new JSONObject();
        if(fuzzy.equals("")) {
            wordText = userDataJsonObject.getString(ConstantField.WORD_TEXT);
        } else {
            wordText = fuzzy.split(":")[0];
        }
        String priorResult = "";
        String htmlSentence = "", sentence = "", sentence_id = "", article_id = "";
        for (int i = 0; i < listList.size(); i++) {
            for (String m : listList.get(i).get(0).keySet()) {
                if (m.equals(type + ConstantField._ARTICLE_ID)) {
                    article_id = listList.get(i).get(0).get(m);
                } else if(m.equals(type + ConstantField._SENTENCE)) {
                    int flag = 0;
                    // 上一篇為重複的文章及句子但不同 position !
                    if (priorResult.equals("")) {
                        sentence = listList.get(i).get(0).get(m);
                        sentenceTem = sentence;
                    } else {
                        sentenceTem = priorResult;
                        flag--;
                    }
                    String[] ss = sentenceTem.split(" ");
                    htmlSentence = "";
                    for (int j = 0; j < ss.length ; j++) {
                        if (ss[j].equals(wordText) && j == positionList.get(i) - flag) {
                            htmlSentence += "<span style=\"color:#FF0000;\">" + wordText + "</span> ";
                        } else if(ss[j].equals(wordText + ".") && j == positionList.get(i) - flag) {
                            htmlSentence += "<span style=\"color:#FF0000;\">" + wordText + "</span>.";
                        } else if(ss[j].equals(wordText + ",") && j == positionList.get(i) - flag) {
                            htmlSentence += "<span style=\"color:#FF0000;\">" + wordText + "</span>, ";
                            flag++;
                        } else if(ss[j].equals(wordText + "!") && j == positionList.get(i) - flag) {
                            htmlSentence += "<span style=\"color:#FF0000;\">" + wordText + "</span>!";
                        } else if(ss[j].equals(wordText + "?") && j == positionList.get(i) - flag) {
                            htmlSentence += "<span style=\"color:#FF0000;\">" + wordText + "</span>?";
                        } else if (j != ss.length - 1) {
                            htmlSentence += ss[j] + " ";
                            if (ss[j].contains(",")) {
                                flag++;
                            }
                        } else {
                            htmlSentence += ss[j];
                        }
                    }
                } else if(m.equals(type + ConstantField._SENTENCE_ID)) {
                    sentence_id = listList.get(i).get(0).get(m);
                }
            }
            // 判斷下一句是否是同一篇文章的同一句
            if (i < listList.size() - 1) {
                if (listList.get(i + 1).get(0).get(type + ConstantField._SENTENCE_ID).equals(sentence_id)
                        && listList.get(i + 1).get(0).get(type + ConstantField._ARTICLE_ID).equals(article_id)) {
                    priorResult = htmlSentence;
                    continue;
                } else {
                    priorResult = "";
                }
            }
            if(palabra.judgeExists(article_id, userDataJsonObject)) {
                // Avoid DB Select Similar Term Problem!
                if (htmlSentence.contains("span")) {
                    resultJsonObject.put(sentence_id + ""
                            ,"<a href = \'/cate_searchpage/showArticle.php?" + "&articleID=" + article_id
                                    + "&sentenceID=" + sentence_id + "&query=" + wordText + "&source="
                                    + type + "&sentence=" + sentence + "\'>"
                                    + htmlSentence + "</a>");
                }
            }
        }
        return resultJsonObject;
    }

    /**
     * Search Processing About Fuzzy.
     * @param userDataJsonObject user data Json Object
     * @return result text list of lemma link
     * @throws SQLException SQL Exception
     */
    public JsonNode setSearchProcessingOfFuzzy(JSONObject userDataJsonObject) throws SQLException {
        fuzzyResultJsonObject = new JSONObject();
        palabra.setFuzzyOfPalabra(userDataJsonObject.getString(DatabaseColumnNameVariableTable.FUZZY));
        List<String> fuzzyList = palabra.getFuzzyList();
        for(String f : fuzzyList) {
            fuzzy = f;
            setSearchProcessingOfPalabra(userDataJsonObject);
            fuzzy = "";
        }
        return Json.parse(fuzzyResultJsonObject.toString());
    }

    /**
     * Search Post Processing About Lemma Web Expression.
     * @param userDataJsonObject user data Json Object
     * @return result text list of lemma link
     * @throws SQLException SQL Exception
     */
    public JsonNode setSearchProcessingOfLemma(JSONObject userDataJsonObject) throws SQLException {
        Set<String> resultTextOfLemma = new HashSet<>();
        JSONObject textResult = new JSONObject();
        palabra.setLemmaOfPalabra(userDataJsonObject.getString(DatabaseColumnNameVariableTable.LEMMA));
        List<String> lemmaList = palabra.getLemmaList();
        resultTextOfLemma.addAll(lemmaList);
        Iterator iterator = resultTextOfLemma.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            textResult.put(count + "", iterator.next());
            count++;
        }
        return Json.parse(textResult.toString());
    }

    /**
     * Search Post Processing About XML Web Expression.
     * @param userDataJsonObject user data Json Object
     * @return result original and correct article
     * @throws SQLException SQL Exception
     */
    public JsonNode setSearchProcessingOfXMLAndAuthorInfo(JSONObject userDataJsonObject) throws SQLException {
        DatabaseController databaseController = new DatabaseController();
        String articleID = userDataJsonObject.getString(ConstantField.ARTICLE_ID);
        String sentenceID = userDataJsonObject.getString(ConstantField.SENTENCE_ID);
        String query = userDataJsonObject.getString(ConstantField.WORD_TEXT);
        String source = userDataJsonObject.getString(ConstantField.SOURCE);
        XMLMatchProcessor xmlMatchProcessor = new XMLMatchProcessor();
        SqlCommandComposer sqlCommandComposer = new SqlCommandComposer();
        ResultSet resultSet = databaseController.execSelect(sqlCommandComposer.getXMLByArticleID(articleID));
        ResultSet minResultSet = null;
        ResultSet articleAuthorInfoResult = databaseController.execSelect(sqlCommandComposer.getAuthorInformation(articleID));
        int minID = 0;
        String xml = "";
        if (source.equals(ConstantField.ORIGINAL)) {
            minResultSet = databaseController.execSelect(sqlCommandComposer.getOriginalSentenceIDByArticleID(articleID));
        } else if (source.equals(ConstantField.CORRECT)) {
            minResultSet = databaseController.execSelect(sqlCommandComposer.getCorrectSentenceIDByArticleID(articleID));
        }
        if (minResultSet != null) {
            try {
                if (minResultSet.next()) {
                    minID = minResultSet.getInt(1);
                }
                if (resultSet.next()) {
                    xml = resultSet.getString(1);
                }
            } catch (SQLException e) {
                e.getErrorCode();
            }
            try {
                xmlMatchProcessor.setXMLMatchParser(xml, source);
            } catch (DocumentException e) {
                e.getMessage();
            }
        }
        JSONObject resultJsonObject = new JSONObject();
        resultJsonObject.put(ConstantField.ORIGINAL_ARTICLE, xmlMatchProcessor.getMatchingFormat((Integer.parseInt(sentenceID) - minID + 1) + "", query).split("@")[1]);
        resultJsonObject.put(ConstantField.CORRECT_ARTICLE, xmlMatchProcessor.getMatchingFormat((Integer.parseInt(sentenceID) - minID + 1) + "", query).split("@")[0]);
        if (articleAuthorInfoResult.next()) {
            resultJsonObject.put(DatabaseColumnNameVariableTable.ID, articleAuthorInfoResult.getString(1));
            resultJsonObject.put(DatabaseColumnNameVariableTable.GENDER, articleAuthorInfoResult.getString(2));
            resultJsonObject.put(DatabaseColumnNameVariableTable.SCHOOL_NAME, articleAuthorInfoResult.getString(3));
            resultJsonObject.put(DatabaseColumnNameVariableTable.DEPARTMENT, articleAuthorInfoResult.getString(4));
            resultJsonObject.put(DatabaseColumnNameVariableTable.SUBMITTED_YEAR, articleAuthorInfoResult.getString(5));
            resultJsonObject.put(DatabaseColumnNameVariableTable.LEARNING_HOURS, articleAuthorInfoResult.getString(6));
            if (articleAuthorInfoResult.getString(7).equals("1")) {
                resultJsonObject.put(DatabaseColumnNameVariableTable.SPECIAL_EXPERIENCE, "無");
            } else {
                resultJsonObject.put(DatabaseColumnNameVariableTable.SPECIAL_EXPERIENCE, "有");
            }
        }
        return Json.parse(resultJsonObject.toString());
    }
}
