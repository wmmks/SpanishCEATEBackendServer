package xml_check;

import articleXMLReader.XMLParser;
import com.fasterxml.jackson.databind.JsonNode;
import json.JSONObject;
import org.dom4j.DocumentException;
import play.libs.Json;

/**
 * XML Processing.
 * @version 1.0 2018年09月07日
 * @author Alex
 *
 */
public class XMLProcessing {
    /**
     * XML Processing About Web Expression.
     * @param userDataJsonObject user data Json Object
     * @return result original or correct sentence link
     */
    public JsonNode setXMLResult(JSONObject userDataJsonObject) throws DocumentException {
        String xml = userDataJsonObject.getString("xml");
        XMLParser xmlParser = new XMLParser();
        xmlParser.setXMLParser(xml);
        JSONObject resultJsonObject = new JSONObject();
        resultJsonObject.put("original_article", xmlParser.getOriginalArticle().toString());
        resultJsonObject.put("correct_article", xmlParser.getCorrectedArticle().toString());
        return Json.parse(resultJsonObject.toString());
    }
}
