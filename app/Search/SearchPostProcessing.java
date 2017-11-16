package Search;

import com.fasterxml.jackson.databind.JsonNode;
import constantField.ConstantField;
import json.JSONObject;
import play.libs.Json;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Search Post Processing.
 * 將資料進行後處理以方便前端介面呈現。
 * @version 1.0 2017年11月16日
 * @author Alex
 *
 */
public class SearchPostProcessing {
    /**
     * Search Post Processing About Web Expression.
     * @param wordText query
     * @param wordPOS query pos
     * @param nextWordPOS next word pos
     * @return result
     * @throws SQLException SQL Exception
     */
    public JsonNode setSearchPostProcessing(String wordText, String wordPOS, String nextWordPOS)  throws SQLException {
        JSONObject resultJsonObject = new JSONObject();
        List<List<Map<String, String>>> originalListList;
        List<List<Map<String, String>>> correctListList;
        SearchType palabra = new SearchType();
        palabra.setPalabraSentence(wordText, wordPOS, nextWordPOS);
        originalListList = palabra.getOriginalList();
        correctListList = palabra.getCorrectList();
        int count = 0;
        String original_sentence = "", original_sentence_id = "", original_article_id = "";
        for (List<Map<String, String>> originalList : originalListList) {
            for (Map<String, String> original : originalList) {
                count++;
                for (String m : original.keySet()) {
                    if (m.equals(ConstantField.ORIGINAL_SENTENCE)) {
                        original_sentence = original.get(m).replace(wordText,
                                "<span style=\"color:#FF0000;\">" + wordText + "</span>");
                    } else if (m.equals(ConstantField.ORIGINAL_SENTENCE_ID)){
                        original_sentence_id = original.get(m);
                    } else if (m.equals(ConstantField.ORIGINAL_ARTICLE_ID)) {
                        original_article_id = original.get(m);
                    }
                    resultJsonObject.put(count + ""
                            ,"<a href = \"/cate/showarticle.php?" + "&articleID=" + original_article_id
                                    + "&sentenceID=" + original_sentence_id + "&query=" + wordText
                                    + "\">" + original_sentence + "</a>");
                }
            }
        }
        String correct_sentence = "", correct_sentence_id = "", correct_article_id = "";
        for (List<Map<String, String>> correctList : correctListList) {
            for (Map<String, String> correct : correctList) {
                count++;
                for (String m : correct.keySet()) {
                    if (m.equals(ConstantField.CORRECT_SENTENCE)) {
                        correct_sentence = correct.get(m).replace(wordText,
                                "<span style=\"color:#FF0000;\">" + wordText + "</span>");
                    } else if (m.equals(ConstantField.CORRECT_SENTENCE_ID)){
                        correct_sentence_id = correct.get(m);
                    } else if (m.equals(ConstantField.CORRECT_ARTICLE_ID)) {
                        correct_article_id = correct.get(m);
                    }
                    resultJsonObject.put(count + ""
                            ,"<a href = \"/cate/showarticle.php?" + "&articleID=" + correct_article_id
                                    + "&sentenceID=" + correct_sentence_id + "&query=" + wordText
                                    + "\">" + correct_sentence + "</a>");
                }
            }
        }
        return Json.parse(resultJsonObject.toString());
    }
}
