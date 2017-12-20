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
import java.util.regex.Pattern;

/**
 * Search Post Processing.
 * 將資料進行後處理以方便前端介面呈現。
 * @version 1.0 2017年11月16日
 * @author Alex
 *
 */
public class SearchProcessing {
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
        SearchType palabra = new SearchType();
        palabra.setSentenceOfPalabra(wordText, wordPOS, nextWordPOS);
        originalListList = palabra.getOriginalList();
        correctListList = palabra.getCorrectList();
        if (originalOrCorrect.equals("1")) {
            String original_sentence = "", original_sentence_id = "", original_article_id = "";
            for (List<Map<String, String>> originalList : originalListList) {
                for (Map<String, String> original : originalList) {
                    for (String m : original.keySet()) {
                        switch (m) {
                            case ConstantField.ORIGINAL_ARTICLE_ID :
                                original_article_id = original.get(m);
                                break;
                            case ConstantField.ORIGINAL_SENTENCE :
                                original_sentence = Pattern.compile("(" + wordText + ")", Pattern.CASE_INSENSITIVE).
                                        matcher(original.get(m)).replaceAll("<span style=\"color:#FF0000;\">$1</span>");
                                break;
                            case ConstantField.ORIGINAL_SENTENCE_ID :
                                original_sentence_id = original.get(m);
                                break;
                            default:
                                break;
                        }
                    }
                    if(!palabra.judgeExists(original_article_id, userDataJsonObject)) {
                        break;
                    } else {
                        // Avoid DB Select Similar Term Problem!
                        if (original_sentence.contains("span")) {
                            resultJsonObject.put(original_sentence_id + ""
                                    ,"<a href = \"/cate_searchpage/showArticle.php?" + "&articleID=" + original_article_id
                                            + "&sentenceID=" + original_sentence_id + "&query=" + wordText + "&source="
                                            + ConstantField.ORIGINAL + "\">" + original_sentence + "</a>");
                        }
                    }
                }
            }
        } else if(originalOrCorrect.equals("2")) {
            String correct_sentence = "", correct_sentence_id = "", correct_article_id = "";
            for (List<Map<String, String>> correctList : correctListList) {
                for (Map<String, String> correct : correctList) {
                    for (String m : correct.keySet()) {
                        switch (m) {
                            case ConstantField.CORRECT_ARTICLE_ID :
                                correct_article_id = correct.get(m);
                                break;
                            case ConstantField.CORRECT_SENTENCE :
                                correct_sentence = Pattern.compile("(" + wordText + ")", Pattern.CASE_INSENSITIVE).
                                        matcher(correct.get(m)).replaceAll("<span style=\"color:#FF0000;\">$1</span>");
                                break;
                            case ConstantField.CORRECT_SENTENCE_ID :
                                correct_sentence_id = correct.get(m);
                                break;
                            default:
                                break;
                        }
                    }
                    if(!palabra.judgeExists(correct_article_id, userDataJsonObject)) {
                        break;
                    } else {
                        // Avoid DB Select Similar Term Problem!
                        if (correct_sentence.contains("span")) {
                            resultJsonObject.put(correct_sentence_id + ""
                                    ,"<a href = \"/cate_searchpage/showArticle.php?" + "&articleID=" + correct_article_id
                                            + "&sentenceID=" + correct_sentence_id + "&query=" + wordText + "&source="
                                            + ConstantField.CORRECT + "\">" + correct_sentence + "</a>");
                        }
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
    public JsonNode setSearchProcessingOfXML(JSONObject userDataJsonObject) throws SQLException {
        DatabaseController databaseController = new DatabaseController();
        String articleID = userDataJsonObject.getString(ConstantField.ARTICLE_ID);
        String sentenceID = userDataJsonObject.getString(ConstantField.SENTENCE_ID);
        String query = userDataJsonObject.getString(ConstantField.WORD_TEXT);
        String source = userDataJsonObject.getString(ConstantField.SOURCE);
        XMLMatchProcessor xmlMatchProcessor = new XMLMatchProcessor();
        SqlCommandComposer sqlCommandComposer = new SqlCommandComposer();
        ResultSet resultSet = databaseController.execSelect(sqlCommandComposer.getXMLByArticleID(articleID));
        ResultSet minResultSet = null;
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
        return Json.parse(resultJsonObject.toString());
    }
}
