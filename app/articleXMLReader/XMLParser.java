package articleXMLReader;

import constantField.XMLArticleConstantTable;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;


import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * XML Parser.
 *
 * @version 1.0 2017年9月13日
 * @author Alex
 *
 */
public class XMLParser {
    /**
     * original Article word and corrected Article word.
     */
    private Article originalArticle, correctedArticle;

    /**
     * Constructor.
     */
    public XMLParser() {
        originalArticle = new Article();
        correctedArticle = new Article();
    }

    /**
     * XML Parser.
     * @param xml xml body
     * @throws DocumentException Document Exception
     */
    public void setXMLParser(String xml) throws DocumentException {
        Pattern p = Pattern.compile("[^\\u0009\\u000A\\u000D\u0020-\\uD7FF\\uE000-\\uFFFD\\u10000-\\u10FFF]+");
        xml = p.matcher(xml).replaceAll("");
        Iterator ir = DocumentHelper.parseText(xml).getRootElement().nodeIterator();
        while (ir.hasNext()) {
            Node textNode = (Node) ir.next();
            if (textNode.getNodeTypeName().equals(XMLArticleConstantTable.xmlTextType)) {
                String text = textNode.getText();
                text = removeLineFeed(text);
                originalArticle.addWord(text);
                correctedArticle.addWord(text);
            } else if (textNode.getNodeTypeName().equals(XMLArticleConstantTable.xmlElementType)) {
                Element textElement = (Element) textNode;
                String correctText = textElement.attributeValue(
                        XMLArticleConstantTable.xmlCorrectedTextTag);
                String originalText = textElement.getText();
                if (correctText != null) {
                    if (correctText.equals(XMLArticleConstantTable.xmlIgnoredTag)) {
                        correctText = "";
                    }
                } else {
                    correctText = textElement.getText();
                }
                correctText = removeLineFeed(correctText);
                originalText = removeLineFeed(originalText);
                originalArticle.addWord(removeLineFeed(originalText));
                correctedArticle.addWord(removeLineFeed(correctText));
            }
        }
    }

    /**
     * Remove line.
     * @param sentence text
     * @return text article Content
     */
    private String removeLineFeed(String sentence) {
        String text = sentence;
        while (text.contains("\n")) {
            text = text.replaceAll("\n", " ");
        }
        return text;
    }

    /**
     * Get Original Article.
     * @return originalArticle original article
     */
    public Article getOriginalArticle() {
        return originalArticle;
    }

    /**
     * Get Corrected Article.
     * @return correctedArticle corrected article
     */
    public Article getCorrectedArticle() {
        return correctedArticle;
    }
}
