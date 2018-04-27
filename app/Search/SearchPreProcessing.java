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
     * Search Post Processing About Web Expression.
     * @param userDataJsonObject user data Json Object
     * @return result original or correct sentence link
     * @throws SQLException SQL Exception
     */
    public JsonNode setSearchProcessingOfPalabra(JSONObject userDataJsonObject)  throws SQLException {
        String wordText = userDataJsonObject.getString(ConstantField.WORD_TEXT);
        String wordPOS = userDataJsonObject.getString(ConstantField.WORD_POS);
        String nextWordPOS = userDataJsonObject.getString(ConstantField.NEXT_WORD_POS);
        String originalOrCorrect = userDataJsonObject.getString(ConstantField.ORIGINAL_OR_CORRECT);
        JSONObject resultJsonObject = new JSONObject();
        List<List<Map<String, String>>> originalListList;
        List<List<Map<String, String>>> correctListList;
        List<Integer> originalPositionList;
        List<Integer> correctPositionList;
        SearchType palabra = new SearchType();
        palabra.setSentenceOfPalabra(wordText, wordPOS, nextWordPOS);
        originalListList = palabra.getOriginalList();
        correctListList = palabra.getCorrectList();
        originalPositionList = palabra.getOriginalPositionList();
        correctPositionList = palabra.getCorrectPositionList();
        String originalPriorResult = "";
        String correctPriorResult = "";
        if (originalOrCorrect.equals("1")) {
            String original_htmlSentence = "", original_sentence = "", original_sentence_id = "", original_article_id = "";
            for (int i = 0; i < originalListList.size(); i++) {
                for (String m : originalListList.get(i).get(0).keySet()) {
                    switch (m) {
                        case ConstantField.ORIGINAL_ARTICLE_ID :
                            original_article_id = originalListList.get(i).get(0).get(m);
                            break;
                        case ConstantField.ORIGINAL_SENTENCE :
                            int flag = 0;
                            // 上一篇為重複的文章及句子但不同 position !
                            if (originalPriorResult.equals("")) {
                                original_sentence = originalListList.get(i).get(0).get(m);
                            } else {
                                original_sentence = originalPriorResult;
                                flag--;
                            }
                            String[] ss = original_sentence.split(" ");
                            original_htmlSentence = "";
                            for (int j = 0; j < ss.length ; j++) {
                                if (ss[j].equals(wordText) && j == originalPositionList.get(i) - flag) {
                                    original_htmlSentence += "<span style=\"color:#FF0000;\">" + wordText + "</span> ";
                                } else if(ss[j].equals(wordText + ".") && j == originalPositionList.get(i) - flag) {
                                    original_htmlSentence += "<span style=\"color:#FF0000;\">" + wordText + "</span>.";
                                } else if(ss[j].equals(wordText + ",") && j == originalPositionList.get(i) - flag) {
                                    original_htmlSentence += "<span style=\"color:#FF0000;\">" + wordText + "</span>, ";
                                    flag++;
                                } else if(ss[j].equals(wordText + "!") && j == originalPositionList.get(i) - flag) {
                                    original_htmlSentence += "<span style=\"color:#FF0000;\">" + wordText + "</span>!";
                                } else if(ss[j].equals(wordText + "?") && j == originalPositionList.get(i) - flag) {
                                    original_htmlSentence += "<span style=\"color:#FF0000;\">" + wordText + "</span>?";
                                } else if (j != ss.length - 1) {
                                    original_htmlSentence += ss[j] + " ";
                                    if (ss[j].contains(",")) {
                                        flag++;
                                    }
                                } else {
                                    original_htmlSentence += ss[j];
                                }
                            }
                            break;
                        case ConstantField.ORIGINAL_SENTENCE_ID :
                            original_sentence_id = originalListList.get(i).get(0).get(m);
                            break;
                        default:
                            break;
                        }
                }
                // 判斷下一句是否是同一篇文章的同一句
                if (i < originalListList.size() - 1) {
                    if (originalListList.get(i + 1).get(0).get(ConstantField.ORIGINAL_SENTENCE_ID).equals(original_sentence_id)
                            && originalListList.get(i + 1).get(0).get(ConstantField.ORIGINAL_ARTICLE_ID).equals(original_article_id)) {
                        originalPriorResult = original_htmlSentence;
                        continue;
                    } else {
                        originalPriorResult = "";
                    }
                }
                if(palabra.judgeExists(original_article_id, userDataJsonObject)) {
                    // Avoid DB Select Similar Term Problem!
                    if (original_htmlSentence.contains("span")) {
                        resultJsonObject.put(original_sentence_id + ""
                                ,"<a href = \'/cate_searchpage/showArticle.php?" + "&articleID=" + original_article_id
                                        + "&sentenceID=" + original_sentence_id + "&query=" + wordText + "&source="
                                        + ConstantField.ORIGINAL  + "&sentence=" + original_sentence + "\'>"
                                        + original_htmlSentence + "</a>");
                    }
                }
            }
        } else if(originalOrCorrect.equals("2")) {
            String correct_htmlSentence = "", correct_sentence = "", correct_sentence_id = "", correct_article_id = "";
            for (int i = 0; i < correctListList.size(); i++) {
                for (String m : correctListList.get(i).get(0).keySet()) {
                    switch (m) {
                        case ConstantField.CORRECT_ARTICLE_ID :
                            correct_article_id = correctListList.get(i).get(0).get(m);
                            break;
                        case ConstantField.CORRECT_SENTENCE :
                            int flag = 0;
                            // 上一篇為重複的文章及句子但不同 position !
                            if (correctPriorResult.equals("")) {
                                correct_sentence = correctListList.get(i).get(0).get(m);
                            } else {
                                correct_sentence = correctPriorResult;
                                flag--;
                            }
                            String[] ss = correct_sentence.split(" ");
                            correct_htmlSentence = "";
                            for (int j = 0; j < ss.length ; j++) {
                                if (ss[j].equals(wordText) && j == correctPositionList.get(i) - flag) {
                                    correct_htmlSentence += "<span style=\"color:#FF0000;\">" + wordText + "</span> ";
                                } else if(ss[j].equals(wordText + ".") && j == correctPositionList.get(i) - flag) {
                                    correct_htmlSentence += "<span style=\"color:#FF0000;\">" + wordText + "</span>.";
                                } else if(ss[j].equals(wordText + ",") && j == correctPositionList.get(i) - flag) {
                                    correct_htmlSentence += "<span style=\"color:#FF0000;\">" + wordText + "</span>, ";
                                    flag++;
                                } else if(ss[j].equals(wordText + "!") && j == correctPositionList.get(i) - flag) {
                                    correct_htmlSentence += "<span style=\"color:#FF0000;\">" + wordText + "</span>!";
                                } else if(ss[j].equals(wordText + "?") && j == correctPositionList.get(i) - flag) {
                                    correct_htmlSentence += "<span style=\"color:#FF0000;\">" + wordText + "</span>?";
                                } else if (j != ss.length - 1) {
                                    correct_htmlSentence += ss[j] + " ";
                                    if (ss[j].contains(",")) {
                                        flag++;
                                    }
                                } else {
                                    correct_htmlSentence += ss[j];
                                }
                            }
                            break;
                        case ConstantField.CORRECT_SENTENCE_ID :
                            correct_sentence_id = correctListList.get(i).get(0).get(m);
                            break;
                        default:
                            break;
                    }
                }
                // 判斷下一句是否是同一篇文章的同一句
                if (i < correctListList.size() - 1) {
                    if (correctListList.get(i + 1).get(0).get(ConstantField.CORRECT_SENTENCE_ID).equals(correct_sentence_id)
                            && correctListList.get(i + 1).get(0).get(ConstantField.CORRECT_ARTICLE_ID).equals(correct_article_id)) {
                        correctPriorResult = correct_htmlSentence;
                        continue;
                    } else {
                        correctPriorResult = "";
                    }
                }
                if(palabra.judgeExists(correct_article_id, userDataJsonObject)) {
                    // Avoid DB Select Similar Term Problem!
                    if (correct_htmlSentence.contains("span")) {
                        resultJsonObject.put(correct_sentence_id + ""
                                ,"<a href = \'/cate_searchpage/showArticle.php?" + "&articleID=" + correct_article_id
                                        + "&sentenceID=" + correct_sentence_id + "&query=" + wordText + "&source="
                                        + ConstantField.CORRECT + "&sentence=" + correct_sentence + "\'>"
                                        + correct_htmlSentence + "</a>");
                    }
                }
            }
        }
        return Json.parse(resultJsonObject.toString());
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
        SearchType palabra = new SearchType();
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
