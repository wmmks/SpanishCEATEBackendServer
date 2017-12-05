package sqlCommandLogic;

import constantField.DatabaseColumnNameVariableTable;
import databaseUtil.SqlObject;
import extractContent.ExtractArticleContentColumnObject;
import json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by roye on 2017/4/26.
 */
public class SqlCommandComposer {
    public String getUserDataSqlByIdAndSystemType(int id, int systemType) {
        String sql = "select * from articles_content as a,users_information as b,articles_information as c,class_information as d,users_special_experience as e where a.id=b.id and b.id=c.id and c.id=d.id and d.id=e.id and a.id=" + id + " and a.system_type=" + systemType;
        return sql;
    }
    public String getOtherColumnSqlByText(String text) {
        String sql = "select * from words_table as a where a.text='" + text + "'";
        return sql;
    }
    public String getOtherColumnSqlByTextAndPOS(String text, String pos) {
        String sql = "select * from words_table as a where a.text='" + text + "' and a.pos='" + pos + "'";
        return sql;
    }
    public String getOriginalSqlByWordId(int id) {
        String sql = "select * from original_words_Index_table as a where a.word_id='" + id + "'";
        return sql;
    }
    public String getCorrectSqlByWordId(int id) {
        String sql = "select * from corrected_words_Index_table as a where a.word_id='" + id + "'";
        return sql;
    }
    public String getOriginalSqlByWordId(int sentenceID, int position) {
        String sql = "select * from original_words_Index_table as a where a.sentence_id='"
                + sentenceID + "'" + "and a.position='" + position + "'";
        return sql;
    }
    public String getCorrectSqlByWordId(int sentenceID, int position) {
        String sql = "select * from corrected_words_Index_table as a where a.sentence_id='"
                + sentenceID + "'" + "and a.position='" + position + "'";
        return sql;
    }
    public String getOtherColumnSqlByNextWordIDAndPOS(int nextWordID, String pos) {
        String sql = "select * from words_table as a where a.id='"
                + nextWordID + "'" + "and a.pos='" + pos + "'";
        return sql;
    }
    public String getOriginalSqlBySentenceId(int id) {
        String sql = "select * from original_sentences_content as a where a.id='" + id + "'";
        return sql;
    }
    public String getCorrectSqlBySentenceID(int id) {
        String sql = "select * from corrected_sentences_content as a where a.id='" + id + "'";
        return sql;
    }
    public String getXMLByArticleID(String id) {
        String sql = "select xml_content from articles_content where id='" + id + "'";
        return sql;
    }
    public String getCorrectSentenceIDByArticleID(String articleID) {
        String sql = "select min(a.id) from corrected_sentences_content as a where a.article_id ='" + articleID + "' UNION select min(b.id) from original_sentences_content as b where b.article_id ='" + articleID + "'";
        return sql;
    }
    public String getExistByOtherColumnCondition(Object object) {
        List<String> condition = (List<String>) object;
        String sql = "select * from users_information as a, users_special_experience as b, articles_information as c " +
                "where a.id = '" + condition.get(0) + "' and a.id = b.id and b.id = c.id and a.learning_hours BETWEEN '" +
                condition.get(1).split("~")[0] + "' and '" + condition.get(1).split("~")[1] + "' and a.gender LIKE '%" +
                condition.get(2) + "%' and a.department LIKE '%" +
                condition.get(3) + "%' and b.special_experience = '" +
                condition.get(4).split("~")[0] + "' and '" + condition.get(4).split("~")[1] + "'and c.number_of_words BETWEEN '" +
                condition.get(5).split("~")[0] + "' and '" + condition.get(5).split("~")[1] + "'and c.article_style BETWEEN '" +
                condition.get(6).split("~")[0] + "' and '" + condition.get(6).split("~")[1] + "'and c.article_topic BETWEEN '" +
                condition.get(7).split("~")[0] + "' and '" + condition.get(7).split("~")[1] + "'and c.writting_location BETWEEN '" +
                condition.get(8).split("~")[0] + "' and '" + condition.get(8).split("~")[1] + "'and c.submitted_year BETWEEN '" +
                condition.get(9).split("~")[0] + "' and '" + condition.get(9).split("~")[1] + "'";
        System.out.println(sql);
        return sql;
    }
    public UserData getUserData(JSONObject userDataJsonObject)
    {
        String sql="";
        UserData userData=new UserData();
        Set<String> columnNameSet = userDataJsonObject.keySet();
        SqlObject userInformationSqlObject=new SqlObject();
        SqlObject articleInformationSqlObject=new SqlObject();
        SqlObject classInformationSqlObject=new SqlObject();
        SqlObject userSpecialExperienceSqlObject=new SqlObject();
        SqlObject articleContentSqlObject=new SqlObject();
        ExtractArticleContentColumnObject extractArticleContentColumnObject = new ExtractArticleContentColumnObject();
        for(String columnName : columnNameSet)
        {
            if(checkTableNumber(columnName)==DatabaseColumnNameVariableTable.genericColumnNameNumber)
            {
                userInformationSqlObject.addSqlObject(columnName,userDataJsonObject.get(columnName));
                articleInformationSqlObject.addSqlObject(columnName,userDataJsonObject.get(columnName));
                classInformationSqlObject.addSqlObject(columnName,userDataJsonObject.get(columnName));
                userSpecialExperienceSqlObject.addSqlObject(columnName,userDataJsonObject.get(columnName));
                articleContentSqlObject.addSqlObject(columnName,userDataJsonObject.get(columnName));
            }
            else if(checkTableNumber(columnName)==DatabaseColumnNameVariableTable.usersInformationTableNumber)
            {
                userInformationSqlObject.addSqlObject(columnName,userDataJsonObject.get(columnName));
            }
            else if(checkTableNumber(columnName)==DatabaseColumnNameVariableTable.classInformationTableNumber)
            {
                classInformationSqlObject.addSqlObject(columnName,userDataJsonObject.get(columnName));
            }
            else if(checkTableNumber(columnName)==DatabaseColumnNameVariableTable.articlesInformationTableNumber)
            {
                articleInformationSqlObject.addSqlObject(columnName,userDataJsonObject.get(columnName));
            }
            else if(checkTableNumber(columnName)==DatabaseColumnNameVariableTable.usersSpecialExperienceTableNumber)
            {
                userSpecialExperienceSqlObject.addSqlObject(columnName,userDataJsonObject.get(columnName));
            }
            else if(checkTableNumber(columnName)==DatabaseColumnNameVariableTable.articlesContentTableNumber)
            {
                extractArticleContentColumnObject.setArticleContentColumnObject(columnName, userDataJsonObject);
                Object object = extractArticleContentColumnObject.getArticleContentColumnObject();
                articleContentSqlObject.addSqlObject(columnName, object);
            }
        }
        userData.setUserInformationSqlObject(userInformationSqlObject);
        userData.setClassInformationSqlObject(classInformationSqlObject);
        userData.setUserSpecialExperienceSqlObject(userSpecialExperienceSqlObject);
        userData.setArticleInformationSqlObject(articleInformationSqlObject);
        userData.setArticleContentSqlObject(articleContentSqlObject);
        return userData;
    }
    private int checkTableNumber(String columnName)
    {
        int tableNumber=-1;
        if(Arrays.asList(DatabaseColumnNameVariableTable.genericColumnNameList).contains(columnName))
        {
            tableNumber=DatabaseColumnNameVariableTable.genericColumnNameNumber;
        }
        else if(Arrays.asList(DatabaseColumnNameVariableTable.userInformationColumnNameList).contains(columnName))
        {
            tableNumber=DatabaseColumnNameVariableTable.usersInformationTableNumber;
        }
        else if(Arrays.asList(DatabaseColumnNameVariableTable.articlesInformationColumnNameList).contains(columnName))
        {
            tableNumber=DatabaseColumnNameVariableTable.articlesInformationTableNumber;
        }
        else if(Arrays.asList(DatabaseColumnNameVariableTable.classInformationColumnNameList).contains(columnName))
        {
            tableNumber=DatabaseColumnNameVariableTable.classInformationTableNumber;
        }
        else if(Arrays.asList(DatabaseColumnNameVariableTable.userSpecialInformationColumnNameList).contains(columnName))
        {
            tableNumber=DatabaseColumnNameVariableTable.usersSpecialExperienceTableNumber;
        }
        else if(Arrays.asList(DatabaseColumnNameVariableTable.articleContentColumnNameList).contains(columnName))
        {
            tableNumber=DatabaseColumnNameVariableTable.articlesContentTableNumber;
        }
        return tableNumber;
    }
}
