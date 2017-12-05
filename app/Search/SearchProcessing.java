package Search;

import com.fasterxml.jackson.databind.JsonNode;
import constantField.ConstantField;
import constantField.DatabaseColumnNameVariableTable;
import json.JSONObject;
import play.libs.Json;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
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
     * @return result
     * @throws SQLException SQL Exception
     */
    public JsonNode setSearchProcessing(JSONObject userDataJsonObject)  throws SQLException {
        String wordText = userDataJsonObject.getString(ConstantField.WORD_TEXT);
        String wordPOS = userDataJsonObject.getString(ConstantField.WORD_POS);
        String nextWordPOS = userDataJsonObject.getString(ConstantField.NEXT_WORD_POS);
        String originalOrCorrect = userDataJsonObject.getString(ConstantField.ORIGINAL_OR_CORRECT);
        JSONObject resultJsonObject = new JSONObject();
        List<List<Map<String, String>>> originalListList;
        List<List<Map<String, String>>> correctListList;
        SearchType palabra = new SearchType();
        palabra.setPalabraSentence(wordText, wordPOS, nextWordPOS);
        originalListList = palabra.getOriginalList();
        correctListList = palabra.getCorrectList();
        int count = 0;
        if (originalOrCorrect.equals("1")) {
            String original_sentence = "", original_sentence_id = "", original_article_id = "";
            for (List<Map<String, String>> originalList : originalListList) {
                for (Map<String, String> original : originalList) {
                    count++;
                    for (String m : original.keySet()) {
                        if (m.equals(ConstantField.ORIGINAL_ARTICLE_ID)) {
                            original_article_id = original.get(m);
                            if(!palabra.judgeExists(original_article_id, userDataJsonObject)){
                                break;
                            } else {
                                resultJsonObject.put(count + ""
                                        ,"<a href = \"/cate/showArticle.php?" + "&articleID=" + original_article_id
                                                + "&sentenceID=" + original_sentence_id + "&query=" + wordText + "&source=original"
                                                + "\">" + original_sentence + "</a>");
                            }
                        } else if (m.equals(ConstantField.ORIGINAL_SENTENCE)) {
                            original_sentence = Pattern.compile("(" + wordText + ")", Pattern.CASE_INSENSITIVE).
                                    matcher(original.get(m)).replaceAll("<span style=\"color:#FF0000;\">$1</span>");
                        } else if (m.equals(ConstantField.ORIGINAL_SENTENCE_ID)){
                            original_sentence_id = original.get(m);
                        }
                    }
                }
            }
        } else if(originalOrCorrect.equals("2")) {
            String correct_sentence = "", correct_sentence_id = "", correct_article_id = "";
            for (List<Map<String, String>> correctList : correctListList) {
                for (Map<String, String> correct : correctList) {
                    count++;
                    for (String m : correct.keySet()) {
                        if (m.equals(ConstantField.CORRECT_ARTICLE_ID)) {
                            correct_article_id = correct.get(m);
                            if(!palabra.judgeExists(correct_article_id, userDataJsonObject)){
                                break;
                            } else {
                                resultJsonObject.put(count + ""
                                        ,"<a href = \"/cate/showArticle.php?" + "&articleID=" + correct_article_id
                                                + "&sentenceID=" + correct_sentence_id + "&query=" + wordText + "&source=correct"
                                                + "\">" + correct_sentence + "</a>");
                            }
                        } else if (m.equals(ConstantField.CORRECT_SENTENCE)) {
                            correct_sentence = Pattern.compile("(" + wordText + ")", Pattern.CASE_INSENSITIVE).
                                    matcher(correct.get(m)).replaceAll("<span style=\"color:#FF0000;\">$1</span>");
                        } else if (m.equals(ConstantField.CORRECT_SENTENCE_ID)){
                            correct_sentence_id = correct.get(m);
                        }
                    }
                }
            }
        }
        return Json.parse(resultJsonObject.toString());
    }
}
