package extractContent;

import constantField.ConstantField;
import constantField.DatabaseColumnNameVariableTable;
import databaseUtil.DatabaseController;
import sqlCommandLogic.SqlCommandComposer;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List getOtherColumnExtraction(Object object, String flag) throws SQLException {
        switch (flag) {
            // 會出現 0~多
            case ConstantField.WORD_ID:
                List<String> wordIDList = new ArrayList<>();
                resultSet = databaseController.execSelect(sqlCommandComposer.getOtherColumnSqlByText(object.toString()));
                while (resultSet.next()) {
                    wordIDList.add(resultSet.getObject(1).toString());
                }
                return wordIDList;
            // 只會出現一個或沒有
            case ConstantField.TEXT_AND_POS_WORD_ID :
                List<String> textAndPosWordIDList = new ArrayList<>();
                String[] textAndPosWordID = object.toString().split(":");
                resultSet = databaseController.execSelect(sqlCommandComposer.getOtherColumnSqlByTextAndPOS
                        (textAndPosWordID[0], textAndPosWordID[1]));
                if (resultSet.next()) {
                    textAndPosWordIDList.add(resultSet.getObject(1).toString());
                }
                return textAndPosWordIDList;
            // 會出現 0~多
            case ConstantField.SENTENCE_ID_BY_ORIGINAL :
                ArrayList<String> sentenceIDByOriginal = new ArrayList<>();
                resultSet = databaseController.execSelect(sqlCommandComposer.getOriginalSqlByWordId(Integer.parseInt(object.toString())));
                while (resultSet.next()) {
                    sentenceIDByOriginal.add(resultSet.getObject(2).toString());
                }
                return sentenceIDByOriginal;
            // 會出現 0~多
            case ConstantField.SENTENCE_ID_BY_CORRECT :
                ArrayList<String> sentenceIDByCorrect = new ArrayList<>();
                resultSet = databaseController.execSelect(sqlCommandComposer.getCorrectSqlByWordId(Integer.parseInt(object.toString())));
                while (resultSet.next()) {
                    sentenceIDByCorrect.add(resultSet.getObject(2).toString());
                }
                return sentenceIDByCorrect;
            // 會出現 0~多
            case ConstantField.SENTENCE_ID_AND_POSITION_BY_ORIGINAL:
                ArrayList<String> sentenceIDAndPositionByOriginal = new ArrayList<>();
                resultSet = databaseController.execSelect(sqlCommandComposer.getOriginalSqlByWordId(Integer.parseInt(object.toString())));
                while (resultSet.next()) {
                    sentenceIDAndPositionByOriginal.add(resultSet.getObject(2).toString()
                    + ":" + resultSet.getObject(4).toString());
                }
                return sentenceIDAndPositionByOriginal;
            // 會出現 0~多
            case ConstantField.SENTENCE_ID_AND_POSITION_BY_CORRECT:
                ArrayList<String> sentenceIDAndPositionByCorrect = new ArrayList<>();
                resultSet = databaseController.execSelect(sqlCommandComposer.getCorrectSqlByWordId(Integer.parseInt(object.toString())));
                while (resultSet.next()) {
                    sentenceIDAndPositionByCorrect.add(resultSet.getObject(2).toString()
                            + ":" + resultSet.getObject(4).toString());
                }
                return sentenceIDAndPositionByCorrect;
            // 只會有一個或沒有
            case ConstantField.NEXT_WORD_ID_BY_ORIGINAL :
                ArrayList<String> nextWordIDByOriginal = new ArrayList<>();
                resultSet = databaseController.execSelect(sqlCommandComposer.getOriginalSqlByWordId(
                        Integer.parseInt(object.toString().split(":")[0])
                        , Integer.parseInt(object.toString().split(":")[1]) + 1));
                if (resultSet.next()) {
                    nextWordIDByOriginal.add(resultSet.getObject(3).toString());
                }
                return nextWordIDByOriginal;
            // 只會有一個或沒有
            case ConstantField.NEXT_WORD_ID_BY_CORRECT :
                ArrayList<String> nextWordIDByCorrect = new ArrayList<>();
                resultSet = databaseController.execSelect(sqlCommandComposer.getCorrectSqlByWordId(
                        Integer.parseInt(object.toString().split(":")[0])
                        , Integer.parseInt(object.toString().split(":")[1]) + 1));
                if (resultSet.next()) {
                    nextWordIDByCorrect.add(resultSet.getObject(3).toString());
                }
                return nextWordIDByCorrect;
            // 只會有一個或沒有
            case ConstantField.NEXT_WORD_ID_AND_POS_WORD_ID :
                ArrayList<String> nextWordIDAndPosWordIDList = new ArrayList<>();
                String[] nextWordIDAndPosWordID = object.toString().split(":");
                resultSet = databaseController.execSelect(sqlCommandComposer.getOtherColumnSqlByNextWordIDAndPOS(
                        Integer.parseInt(nextWordIDAndPosWordID[0]), nextWordIDAndPosWordID[1]));
                if (resultSet.next()) {
                    nextWordIDAndPosWordIDList.add(resultSet.getObject(1).toString());
                }
                return nextWordIDAndPosWordIDList;
            case ConstantField.ORIGINAL :
                List<Map<String ,String>> original = new ArrayList<>();
                Map<String, String> originalMap = new HashMap<>();
                resultSet = databaseController.execSelect(sqlCommandComposer.getOriginalSqlBySentenceId(Integer.parseInt(object.toString())));
                if (resultSet.next()) {
                    originalMap.put(ConstantField.ORIGINAL_SENTENCE_ID, resultSet.getObject(1).toString());
                    originalMap.put(ConstantField.ORIGINAL_ARTICLE_ID, resultSet.getObject(2).toString());
                    originalMap.put(ConstantField.ORIGINAL_SENTENCE, resultSet.getObject(3).toString());
                    original.add(originalMap);
                }
                return original;
            case ConstantField.CORRECT :
                List<Map<String ,String>> correct = new ArrayList<>();
                Map<String, String> correctMap = new HashMap<>();
                resultSet = databaseController.execSelect(sqlCommandComposer.getCorrectSqlBySentenceID(Integer.parseInt(object.toString())));
                if (resultSet.next()) {
                    correctMap.put(ConstantField.CORRECT_SENTENCE_ID, resultSet.getObject(1).toString());
                    correctMap.put(ConstantField.CORRECT_ARTICLE_ID, resultSet.getObject(2).toString());
                    correctMap.put(ConstantField.CORRECT_SENTENCE, resultSet.getObject(3).toString());
                    correct.add(correctMap);
                }
                return correct;
            case ConstantField.JUDGE_OTHER_CONDITION :
                List<Boolean> booleanList = new ArrayList<>();
                resultSet = databaseController.execSelect(sqlCommandComposer.getExistByOtherColumnCondition(object));
                if (resultSet.next()) {
                    booleanList.add(true);
                } else {
                    booleanList.add(false);
                }
                return booleanList;
            case DatabaseColumnNameVariableTable.LEMMA :
                List<String> lemmaList = new ArrayList<>();
                resultSet = databaseController.execSelect(sqlCommandComposer.getTextOfLemma(object.toString()));
                while (resultSet.next()) {
                    lemmaList.add(resultSet.getObject(1).toString().toLowerCase());
                }
                return lemmaList;
            default:
                return new ArrayList<>();
        }
    }
}
