package extractContent;

import articleXMLReader.Article;
import articleXMLReader.XMLParser;
import constantField.DatabaseColumnNameVariableTable;
import constantField.XMLArticleConstantTable;
import json.JSONObject;

import java.util.regex.Pattern;

/**
 * XML Parser.
 *
 * @version 1.0 2017年9月18日
 * @author Alex
 *
 */
public class ExtractArticleContentColumnObject {
    /**
     * Extract Original Content and Correct Content.
     */
    private XMLParser xmlParser;

    /**
     * Get Article Content Column Object.
     */
    private Object object;

    /**
     * Constructor.
     */
    public ExtractArticleContentColumnObject() {
        xmlParser = new XMLParser();
        object = new Object();
    }

    /**
     * Parse XML.
     * @param columnName column Name ex: xml content or original content or correct content
     * @param userDataJsonObject userDataJsonObject content object
     */
    public void setArticleContentColumnObject(String columnName, JSONObject userDataJsonObject) {
        if (columnName.equals(DatabaseColumnNameVariableTable.xmlContent)) {
            try {
                xmlParser.setXMLParser(userDataJsonObject.getString(columnName));
            } catch (Exception e) {
                XMLArticleConstantTable.xmlErrorFlag = true;
                e.printStackTrace();
            }
            object = userDataJsonObject.get(columnName);
        } else if (columnName.equals(DatabaseColumnNameVariableTable.originalArticleText)) {
            if (isChinese(xmlParser.getOriginalArticle().toString())) {
                object = reviseTextContent(xmlParser.getOriginalArticle());
            } else {
                object = xmlParser.getOriginalArticle();
            }
        } else if (columnName.equals(DatabaseColumnNameVariableTable.correctedArticleText)) {
            if (isChinese(xmlParser.getCorrectedArticle().toString())) {
                object = reviseTextContent(xmlParser.getCorrectedArticle());
            } else {
                object = xmlParser.getCorrectedArticle();
            }
        } else {
            object = userDataJsonObject.get(columnName);
        }
        object = object.toString().replace("\'", "\\'");
    }

    /**
     * Revise Extract Content.
     * @param article article
     * @return content
     */
    private String reviseTextContent(Article article) {
        String content = "";
        for (int j = 0; j < article.getArticleList().size(); j++) {
            if (j != 0) {
                content += article.getArticleList().get(j);
            } else {
                if (article.getArticleList().get(j).contains("\t")) {
                    String[] x = article.getArticleList().get(j).split("\t");
                    content += "      " + x[x.length - 1];
                } else if (article.getArticleList().get(j).contains(" ")) {
                    String[] x = article.getArticleList().get(j).split(" ");
                    content += "      " + x[x.length - 1];
                }
            }
        }
        return content;
    }

    /**
     * 只能判断部分CJK字符（CJK统一汉字）.
     * @param str 判斷中文字的字串
     * @return true or false
     */
    private static boolean isChinese(String str) {
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\\u4E00-\\u9FBF]+");
        return pattern.matcher(str.trim()).find();
    }

    /**
     * Get Article Content Column Object.
     * @return object
     */
    public Object getArticleContentColumnObject() {
        return object;
    }
}


