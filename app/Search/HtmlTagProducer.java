package Search;
/**
 * Created by Dan on 2017/11/11.
 */
public class HtmlTagProducer {

    public String getParagraphTag(Integer ID, Boolean correct) {
        String paragraphID;
        String originalParagraphID = "ori" + ID;
        String correctParagraphID = "cor" + ID;
        if (correct) {
            paragraphID = doubleQuotation(correctParagraphID);

        } else {
            paragraphID = doubleQuotation(originalParagraphID);
        }

        String tag = "<span onmouseover=\"changeColor(" + singleQuotation(originalParagraphID) + "," +
                singleQuotation(correctParagraphID) + ", '#AAAAAA', '#AAAAAA', false)\" " +
                "onmouseout=\"changeColor(" + singleQuotation(originalParagraphID) + "," +
                singleQuotation(correctParagraphID) + ", '#000000', '#000000', false)\" " +
                "id=" + paragraphID + " style=\"color: rgb(0, 0, 0);\">";
        return tag;
    }

    public  String getWordTag (Integer ID, String word, Boolean correct) {
        String wordID;
        String color;
        String originalWordID = "om" + ID;
        String correctWordID = "cm" + ID;
        if (correct) {
            wordID = doubleQuotation(correctWordID);
            color = "color: rgb(0, 0, 0);";

        } else {
            wordID = doubleQuotation(originalWordID);
            color = "color: rgb(228, 102, 80);";

        }

        if (word.equals("")) {
            word = " ";
        }

        String tag = "<span style=\"color:#000000; cursor:pointer;\">" +
                "<span onmouseover=\"changeColor(" + singleQuotation(originalWordID) + "," +
                singleQuotation(correctWordID) + ", '#AA0000', '#66AA00', true)\" " +
                "onmouseout=\"changeColor(" + singleQuotation(originalWordID) + "," +
                singleQuotation(correctWordID) + ", '#E46650', '#000000', false)\" " +
                "id=" + wordID + " style=" + doubleQuotation(color) + ">" + word + "</span></span>";
        return tag;
    }

    private String singleQuotation (String word) {
        return "'" + word + "'";
    }

    private String doubleQuotation (String word) {
        return "\"" + word + "\"";
    }
}
