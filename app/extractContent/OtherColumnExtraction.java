package extractContent;

import databaseUtil.DatabaseController;
import sqlCommandLogic.SqlCommandComposer;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Other Column Extraction.
 *
 * @version 1.0 2017年9月18日
 * @author Alex
 *
 */
public class OtherColumnExtraction {

    @Inject
    private SqlCommandComposer sqlCommandComposer = new SqlCommandComposer();

    @Inject
    private ResultSet resultSet;

    @Inject
    private DatabaseController databaseController = new DatabaseController();

    /**
     * Get Other Column Content.
     */
    public ArrayList<String> getOtherColumnExtraction(Object object, String flag) throws SQLException {
        switch (flag) {
            // 會出現 0~多
            case "wordID" :
                ArrayList<String> wordIDList = new ArrayList<>();
                resultSet = databaseController.execSelect(sqlCommandComposer.getOtherColumnSqlByText(object.toString()));
                while (resultSet.next()) {
                    wordIDList.add(resultSet.getObject(1).toString());
                }
                return wordIDList;
            // 只會出現一個或沒有
            case "textAndPos_WordID" :
                ArrayList<String> textAndPosWordIDList = new ArrayList<>();
                String[] textAndPosWordID = object.toString().split(":");
                resultSet = databaseController.execSelect(sqlCommandComposer.getOtherColumnSqlByTextAndPOS
                        (textAndPosWordID[0], textAndPosWordID[1]));
                if (resultSet.next()) {
                    textAndPosWordIDList.add(resultSet.getObject(1).toString());
                }
                return textAndPosWordIDList;
            // 會出現 0~多
            case "sentenceID" :
                ArrayList<String> sentenceID = new ArrayList<>();
                resultSet = databaseController.execSelect(sqlCommandComposer.getOtherColumnSqlByWordId(Integer.parseInt(object.toString())));
                while (resultSet.next()) {
                    sentenceID.add(resultSet.getObject(2).toString());
                }
                return sentenceID;
            // 會出現 0~多
            case "sentenceIDAndPosition" :
                ArrayList<String> sentenceIDAndPosition = new ArrayList<>();
                resultSet = databaseController.execSelect(sqlCommandComposer.getOtherColumnSqlByWordId(Integer.parseInt(object.toString())));
                while (resultSet.next()) {
                    sentenceIDAndPosition.add(resultSet.getObject(2).toString()
                    + ":" + resultSet.getObject(4).toString());
                }
                return sentenceIDAndPosition;
            // 只會有一個或沒有
            case "nextWordID" :
                ArrayList<String> nextWordIDList = new ArrayList<>();
                String[] nextWordID = object.toString().split(":");
                resultSet = databaseController.execSelect(sqlCommandComposer.getOtherColumnSqlByWordId(
                        Integer.parseInt(nextWordID[0]), Integer.parseInt(nextWordID[1]) + 1));
                if (resultSet.next()) {
                    nextWordIDList.add(resultSet.getObject(3).toString());
                }
                return nextWordIDList;
            // 只會有一個或沒有
            case "nextWordIDAndPos_WordID" :
                ArrayList<String> nextWordIDAndPosWordIDList = new ArrayList<>();
                String[] nextWordIDAndPosWordID = object.toString().split(":");
                resultSet = databaseController.execSelect(sqlCommandComposer.getOtherColumnSqlByNextWordIDAndPOS(
                        Integer.parseInt(nextWordIDAndPosWordID[0]), nextWordIDAndPosWordID[1]));
                if (resultSet.next()) {
                    nextWordIDAndPosWordIDList.add(resultSet.getObject(1).toString());
                }
                return nextWordIDAndPosWordIDList;
            case "original" :
                ArrayList<String> original = new ArrayList<>();
                resultSet = databaseController.execSelect(sqlCommandComposer.getOriginalSqlBySentenceId(Integer.parseInt(object.toString())));
                if (resultSet.next()) {
                    original.add(resultSet.getObject(3).toString());
                }
                return original;
            case "correct" :
                ArrayList<String> correct = new ArrayList<>();
                resultSet = databaseController.execSelect(sqlCommandComposer.getCorrectSqlBySentenceID(Integer.parseInt(object.toString())));
                if (resultSet.next()) {
                    correct.add(resultSet.getObject(3).toString());
                }
                return correct;
            default:
                return new ArrayList<>();
        }
    }
}
