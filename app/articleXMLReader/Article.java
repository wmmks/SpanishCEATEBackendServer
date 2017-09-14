package articleXMLReader;

import java.util.ArrayList;

/**
 * Article object.
 *
 * @version 1.0 2017年9月13日
 * @author Alex
 *
 */
public class Article {
    /**
     * Word list about Article.
     */
    private ArrayList<String> articleList;

    /**
     * Constructor.
     */
    Article() {
        articleList = new ArrayList<>();
    }

    /**
     * Add Article Word.
     * @param word article word
     */
    void addWord(String word) {
        articleList.add(word);
    }

    /**
     * Get Article List.
     * @return articleList article list
     */
    public ArrayList<String> getArticleList() {
        return articleList;
    }

    /**
     * Convert Article List To String.
     * @return articleContent to
     */
    public String toString() {
        String articleContent = "";
        for (String word : articleList) {
            articleContent += word;
        }
        while (articleContent.contains("  ")) {
            articleContent = articleContent.replaceAll("  ", " ");
        }
        return articleContent;
    }
}
