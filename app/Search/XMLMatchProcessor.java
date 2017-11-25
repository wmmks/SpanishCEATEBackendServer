package Search;

import constantField.XMLArticleConstantTable;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Created by Dan on 2017/11/11.
 */
public class XMLMatchProcessor {

    private ArrayList<ArrayList<HashMap>> article;
    private ArrayList<HashMap> paragraph;
    private HashMap<Integer,String> sentence;

    public XMLMatchProcessor() {
        article = new ArrayList<>();
        paragraph = new ArrayList<>();
        sentence = new HashMap<>();
    }

    /**
     * XML Matching Parser.
     * @param xml xml body
     * @throws DocumentException Document Exception
     */
    public void setXMLMatchParser(String xml) throws DocumentException {
        XMLMatchProcessor xmlMatchProcessor = new XMLMatchProcessor();
        SAXReader reader = new SAXReader();
        Iterator ir = reader.read(new StringReader(xml)).getRootElement().nodeIterator();
        while (ir.hasNext()) {
            Node textNode = (Node) ir.next();

            if (textNode.getNodeTypeName().equals(XMLArticleConstantTable.xmlTextType)) {
                String text = textNode.getText();
                text = removeLineFeed(text);
                if (text.contains(". ") || text.contains("? ") || text.contains("! ")) {
                    if (!text.equals(". ")){
                        String[] words = xmlMatchProcessor.getText(text);
                        for (String w : words) {
                            if(w.contains(XMLArticleConstantTable.sentenceTag)) {
                                w = w.replace(XMLArticleConstantTable.sentenceTag,"");
                                sentence.put(0,w);
                                sentence.put(1,"");
                                sentence.put(2,"");
                                paragraph.add(new HashMap<>(sentence));
                                article.add(new ArrayList<>(paragraph));
                                sentence.clear();
                                paragraph.clear();
                                sentence.put(0,"");
                            } else {
                                sentence.put(0,w);
                            }
                        }
                    } else {
                        sentence.put(0,text);
                        sentence.put(1,"");
                        sentence.put(2,"");
                        paragraph.add(new HashMap<>(sentence));
                        article.add(new ArrayList<>(paragraph));
                        sentence.clear();
                        paragraph.clear();
                        sentence.put(0,"");
                    }
                } else {
                    sentence.put(0,text);
                }
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
                //要換下一段落了
                if (correctText.contains(". ") || correctText.contains("? ") || correctText.contains("! ")) {
                    if (!sentence.isEmpty()) {
                        //先輸出上句完準備將此correct句當新段落的開始
                        sentence.put(1,"");
                        sentence.put(2,"");
                        paragraph.add(new HashMap<>(sentence));
                        article.add(new ArrayList<>(paragraph));
                        paragraph.clear();
                        sentence.clear();
                        sentence.put(0,"");
                        sentence.put(1,correctText);
                        sentence.put(2,originalText);
                        paragraph.add(new HashMap<>(sentence));
                        sentence.clear();

                    } else {
                        sentence.put(0,"");
                        sentence.put(1,correctText);
                        sentence.put(2,originalText);
                        paragraph.add(new HashMap<>(sentence));
                        article.add(new ArrayList<>(paragraph));
                        paragraph.clear();
                        sentence.clear();
                    }
                } else {
                    if (sentence.isEmpty()){
                        sentence.put(0,"");
                    }
                    sentence.put(1,correctText);
                    sentence.put(2,originalText);
                    paragraph.add(new HashMap<>(sentence));
                    sentence.clear();
                }
            }
        }
        if(sentence.size() == 1) {
            sentence.put(1,"");
            sentence.put(2,"");
        } else {
            sentence.put(0,"");
        }
        paragraph.add(new HashMap<>(sentence));
        article.add(new ArrayList<>(paragraph));
        paragraph.clear();
        sentence.clear();
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

    String[] getText(String text) {
        String re="(\\. )|(\\? )|(! )";
        Pattern p =Pattern.compile(re);
        Matcher m = p.matcher(text);

        String[] words = p.split(text);

        if(words.length > 0)
        {
            int count = 0;
            while(count < words.length)
            {
                if(m.find())
                {
                    words[count] += m.group() + XMLArticleConstantTable.sentenceTag;
                }
                count++;
            }
        }
        return words;
    }

    public String getMatchingFormat (String sentenceID, String query) {
        HtmlTagProducer tagProducer = new HtmlTagProducer();
        StringBuilder htmlCorrect = new StringBuilder();
        StringBuilder htmlOriginal = new StringBuilder();
        int sid = 1;
        int wid = 1;
        boolean markSentence = false;
        for (ArrayList<HashMap> s: article) {
            if (String.valueOf(sid).equals(sentenceID)) {
                markSentence = true;
            }
            htmlCorrect.append(tagProducer.getSentenceTag(sid, true, markSentence));
            htmlOriginal.append(tagProducer.getSentenceTag(sid, false, markSentence));
            for (HashMap w: s) {
                if (!w.get(0).equals("")) {
                    String word;
                    if (w.get(0).toString().contains(query)) {
                        String queryTag = "<span style=\"color: red;\"><u>" + query + "</u></span>";
                        word = w.get(0).toString().replaceAll(query, queryTag);
                    } else {
                        word = w.get(0).toString();
                    }
                    htmlCorrect.append(word);
                    htmlOriginal.append(word);
                }
                if ((!w.get(1).equals("") || !w.get(2).equals(""))) {
                    htmlCorrect.append(tagProducer.getWordTag(wid, w.get(1).toString(),true, markSentence, query));
                    htmlOriginal.append(tagProducer.getWordTag(wid,w.get(2).toString(),false, markSentence, query));
                }
                wid++;
            }
            if (markSentence) {
                htmlCorrect.append("</u>");
                htmlOriginal.append("</u>");
                markSentence = false;
            }
            htmlCorrect.append("</span>");
            htmlOriginal.append("</span>");
            sid++;
        }
        return htmlCorrect.toString() + "@" + htmlOriginal.toString();
    }
}
