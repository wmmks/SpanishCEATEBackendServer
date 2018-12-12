package search;
/**
 * Created by Dan on 2017/11/11.
 */
public class HtmlTagProducer {

    public String getSentenceTag(Integer ID, Boolean correct,Boolean markSentence) {
        String sentenceID;
        String originalSentenceID = "ori" + ID;
        String correctSentenceID = "cor" + ID;
        if (correct) {
            sentenceID = doubleQuotation(correctSentenceID);

        } else {
            sentenceID = doubleQuotation(originalSentenceID);
        }

        String tag;

        if (!markSentence) {
            tag = "<span onmouseover=\"changeColor(" + singleQuotation(originalSentenceID) + "," +
                    singleQuotation(correctSentenceID) + ", '#AAAAAA', '#AAAAAA', false)\" " +
                    "onmouseout=\"changeColor(" + singleQuotation(originalSentenceID) + "," +
                    singleQuotation(correctSentenceID) + ", '#000000', '#000000', false)\" " +
                    "ID=" + sentenceID + " style=\"color: rgb(0, 0, 0);\">";
        } else {
            tag = "<span style=\"color: blue;\"><u>";
        }


        return tag;
    }

    public  String getWordTag (Integer ID, String word, Boolean correct,Boolean markSentence, String query) {
        String wordID;
        String color;
        String tag;
        String originalWordID = "om" + ID;
        String correctWordID = "cm" + ID;
        if (correct) {
            wordID = doubleQuotation(correctWordID);
            color = "color: rgb(0, 0, 0);";
        } else {
            wordID = doubleQuotation(originalWordID);
            color = "color: rgb(228, 102, 80);";

        }

        if (markSentence) {
            if (word.contains(query)) {
                String queryTag = "<span style=\"color: red;\"><u>" + query + "</u></span>";
                String regexEx = "\\b" + query + "\\b";
                word = word.replaceAll(regexEx, queryTag);
            }
        }
        if (!markSentence) {
            tag = "<span style=\"color:#000000; cursor:pointer;\">" +
                    "<span onmouseover=\"changeColor(" + singleQuotation(originalWordID) + "," +
                    singleQuotation(correctWordID) + ", '#AA0000', '#66AA00', true)\" " +
                    "onmouseout=\"changeColor(" + singleQuotation(originalWordID) + "," +
                    singleQuotation(correctWordID) + ", '#E46650', '#000000', false)\" " +
                    "ID=" + wordID + " style=" + doubleQuotation(color) + ">" + word + "</span></span>";
        } else {
            tag = word;
        }
        return tag;
    }

    private String singleQuotation (String word) {
        return "'" + word + "'";
    }

    private String doubleQuotation (String word) {
        return "\"" + word + "\"";
    }
}
