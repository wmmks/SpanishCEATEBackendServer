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
class XMLMatchProcessor {

    private ArrayList<ArrayList<HashMap>> article;
    private ArrayList<HashMap> sentence;
    private HashMap<Integer,String> wordMap;

    XMLMatchProcessor() {
        article = new ArrayList<>();
        sentence = new ArrayList<>();
        wordMap = new HashMap<>();
    }

    /**
     * XML Matching Parser.
     * @param xml xml body
     * @throws DocumentException Document Exception
     */
    void setXMLMatchParser(String xml, String articleSource) throws DocumentException {
        SAXReader reader = new SAXReader();
        Iterator ir = reader.read(new StringReader(xml)).getRootElement().nodeIterator();
        boolean whitespaceFlag = false;
        String elementText;
        while (ir.hasNext()) {
            Node textNode = (Node) ir.next();
            if (textNode.getNodeTypeName().equals(XMLArticleConstantTable.xmlTextType)) {
                String text = textNode.getText();
                if (isChinese(text)) {
                    text = "";
                }
                text = removeLineFeed(text);
                //上個詞剛好是(.)(?)(!)結束
                if (whitespaceFlag) {
                    //當前一般詞為空白開頭
                    if((text.contains(" ") && text.indexOf(" ") == 0)) {
                        //表示上個詞剛好是(.)(?)(!)結束來自correct
                        if (!wordMap.containsKey(0)) {
                            article.add(new ArrayList<>(sentence));
                            sentence.clear();
                        } else {
                            //當前一般詞不為一個空白，且上個一般詞存在
                            if (!text.equals(" ")) {
                                setWordMap(" ","","");
                                sentence.add(new HashMap<>(wordMap));
                                saveSentence();
                                StringBuilder sb = new StringBuilder(text);
                                text = sb.deleteCharAt(0).toString();
                            } else if (text.equals(" ")) {
                                setWordMap(" ","","");
                                sentence.add(new HashMap<>(wordMap));
                                saveSentence();
                                text = "";
                            }
                        }
                    }
                    whitespaceFlag = false;
                }
                if (text.contains(". ") || text.contains("? ") || text.contains("! ")) {
                    if (!text.equals(". ") && !text.equals("? ") && !text.equals("! ")) {
                        String[] words = getPauseMarkText(text);
                        for (String w : words) {
                            if(w.contains(XMLArticleConstantTable.sentenceTag)) {
                                w = w.replace(XMLArticleConstantTable.sentenceTag,"");
                                if (wordMap.containsKey(0)) {
                                    w = wordMap.get(0) + w;
                                }
                                setWordMap(w,"","");
                                sentence.add(new HashMap<>(wordMap));
                                saveSentence();
                                wordMap.put(0,"");
                            } else {
                                if(IsPauseMarkEnd(w)) {
                                    whitespaceFlag = true;
                                    setWordMap(w,"","");
                                    sentence.add(new HashMap<>(wordMap));
                                    wordMap.clear();
                                } else {
                                    wordMap.put(0,w);
                                }
                            }
                        }
                    } else {
                        setWordMap(text,"","");
                        sentence.add(new HashMap<>(wordMap));
                        saveSentence();
                        wordMap.put(0,"");
                    }
                } else if(IsPauseMarkEnd(text)) {
                    whitespaceFlag = true;
                    setWordMap(text,"","");
                    sentence.add(new HashMap<>(wordMap));
                    wordMap.clear();
                } else {
                    if (wordMap.containsKey(0)) {
                        wordMap.put(0, wordMap.get(0) + text);
                    } else {
                        wordMap.put(0,text);
                    }
                }
            } else if (textNode.getNodeTypeName().equals(XMLArticleConstantTable.xmlElementType)) {
                Element textElement = (Element) textNode;
                String correctText = textElement.attributeValue(XMLArticleConstantTable.xmlCorrectedTextTag);
                String originalText = textElement.getText();
                if (correctText != null) {
                    if (correctText.equals(XMLArticleConstantTable.xmlIgnoredTag)) {
                        correctText = "";
                    }
                } else {
                    correctText = textElement.getText();
                }
                if (articleSource.equals("original")) {
                    elementText = originalText;
                } else {
                    elementText = correctText;
                }
                //上一個詞若以(.)(?)(!)結束
                if (whitespaceFlag && !elementText.equals("")) {
                    if (elementText.contains(" ") && elementText.indexOf(" ") == 0) {
                        saveSentence();
                        setWordMap("", correctText, originalText);
                        sentence.add(new HashMap<>(wordMap));
                        wordMap.clear();
                        correctText = "";
                        originalText = "";
                    }
                    whitespaceFlag = false;
                }
                //要換下一段落了
                if (elementText.contains(".") || elementText.contains("?") || elementText.contains("!")) {
                    if (wordMap.containsKey(0)) {
                        //先輸出上句完準備將此correct句當新段落的開始
                        // if correct "." 在最後，直接輸出成一段
                        if (IsPauseMarkEnd(elementText)) {
                            wordMap.put(1,correctText);
                            wordMap.put(2,originalText);
                            sentence.add(new HashMap<>(wordMap));
                            wordMap.clear();
                            whitespaceFlag = true;
                        } else {
                            if (elementText.contains(". ") || elementText.contains("? ") || elementText.contains("! ")) {
                                wordMap.put(1,"");
                                wordMap.put(2,"");
                                sentence.add(new HashMap<>(wordMap));
                                saveSentence();
                                setWordMap("", correctText, originalText);
                                sentence.add(new HashMap<>(wordMap));
                                wordMap.clear();
                            } else {
                                wordMap.put(1,correctText);
                                wordMap.put(2,originalText);
                                sentence.add(new HashMap<>(wordMap));
                                wordMap.clear();
                            }
                        }
                    } else {
                        if (IsPauseMarkEnd(elementText)) {
                            setWordMap("", correctText, originalText);
                            sentence.add(new HashMap<>(wordMap));
                            wordMap.clear();
                            whitespaceFlag = true;
                        } else {
                            if (elementText.contains(". ") || elementText.contains("? ") || elementText.contains("! ")) {
                                setWordMap("","","");
                                sentence.add(new HashMap<>(wordMap));
                                saveSentence();
                                setWordMap("", correctText, originalText);
                                sentence.add(new HashMap<>(wordMap));
                                wordMap.clear();
                            } else {
                                setWordMap("", correctText, originalText);
                                sentence.add(new HashMap<>(wordMap));
                                wordMap.clear();
                            }
                        }
                    }
                } else {
                    if (!wordMap.containsKey(0)){
                        wordMap.put(0,"");
                    }
                    wordMap.put(1,correctText);
                    wordMap.put(2,originalText);
                    sentence.add(new HashMap<>(wordMap));
                    wordMap.clear();
                }
            }
        }
        if(wordMap.containsKey(0)) {
            wordMap.put(1,"");
            wordMap.put(2,"");
        } else {
            wordMap.put(0,"");
        }
        sentence.add(new HashMap<>(wordMap));
        saveSentence();
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
     * 判斷句尾是否為可能斷句符號.
     * @param text 判斷的字串
     * @return true or false
     */
    private boolean IsPauseMarkEnd(String text) {
        return (text.contains(".") && text.lastIndexOf(".") == (text.length() - 1)) ||
                (text.contains("?") && text.lastIndexOf("?") == (text.length() - 1)) ||
                (text.contains("!") && text.lastIndexOf("!") == (text.length() - 1));
    }

    /**
     * Save a sentence in article general condition.
     */
    private void saveSentence() {
        article.add(new ArrayList<>(sentence));
        sentence.clear();
        wordMap.clear();
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

    private void setWordMap(String text, String correctText, String originalText) {
        wordMap.put(0,text);
        wordMap.put(1,correctText);
        wordMap.put(2,originalText);
    }

    private String[] getPauseMarkText(String text) {
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
    /**
     * Output the matching format of the correct article and original article.
     */
    String getMatchingFormat (String sentenceID, String query) {
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