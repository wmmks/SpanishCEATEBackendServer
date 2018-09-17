package controllers;

import org.dom4j.DocumentException;
import search.SearchPreProcessing;
import com.fasterxml.jackson.databind.JsonNode;
import constantField.ConstantField;
import constantField.DatabaseColumnNameVariableTable;
import constantField.XMLArticleConstantTable;
import databaseUtil.DatabaseController;
import json.JSONObject;
import play.libs.Json;
import play.mvc.*;

import sqlCommandLogic.SqlCommandComposer;
import sqlCommandLogic.UserData;
import xml_check.XMLProcessing;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by roye on 2017/4/24.
 */
public class CeateBackendController extends Controller {

    @Inject
    SqlCommandComposer sqlCommandComposer;

    public Result HelloWorld()
    {
        return ok("HelloWorld");
    }

    /**
     * Check Page, Get User Data Function.
     */
    public Result getUserData() {
        DatabaseController databaseController = new DatabaseController();
        JsonNode request = request().body().asJson();
        int id = Integer.parseInt(request.findPath(ConstantField.userAndArticleID).toString());
        int systemType = Integer.parseInt(request.findPath(ConstantField.userAndArticleSystemType).textValue());
        JsonNode result = Json.newObject();
        ResultSet resultSet = databaseController.execSelect(sqlCommandComposer.getUserDataSqlByIdAndSystemType(id, systemType));
        try {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            JSONObject resultJsonObject = new JSONObject();
            if (resultSet.next())
            {
                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++)
                {
                    Object columnValue = resultSet.getObject(i);
                    if (resultSetMetaData.getColumnTypeName(i).equals(ConstantField.databaseStringType))
                    {
                        resultJsonObject.put(resultSetMetaData.getColumnName(i), columnValue.toString());
                    }
                    else if (resultSetMetaData.getColumnTypeName(i).equals(ConstantField.databaseIntType))
                    {
                        resultJsonObject.put(resultSetMetaData.getColumnName(i), Integer.parseInt(columnValue.toString()));
                    }
                }
            }
            result = Json.parse(resultJsonObject.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ok(result);
    }

    /**
     * Check Page, Update User Data Function.
     */
    public Result updateUserData() {
        DatabaseController databaseController = new DatabaseController();
        JsonNode request = request().body().asJson();
        JsonNode result;
        JSONObject userDataJsonObject = new JSONObject(request.toString());
        UserData userData = sqlCommandComposer.getUserData(userDataJsonObject);
        int id = userDataJsonObject.getInt(DatabaseColumnNameVariableTable.ID);
        int systemType = userDataJsonObject.getInt(DatabaseColumnNameVariableTable.SYSTEM_TYPE);
        String updateCondition = "where " + DatabaseColumnNameVariableTable.ID + "=" + id + " and " + DatabaseColumnNameVariableTable.SYSTEM_TYPE + "=" + systemType;
        databaseController.execUpdate(DatabaseColumnNameVariableTable.usersInformationTableName, userData.getUserInformationSqlObject(), updateCondition);
        databaseController.execUpdate(DatabaseColumnNameVariableTable.articlesInformationTableName, userData.getArticleInformationSqlObject(), updateCondition);
        databaseController.execUpdate(DatabaseColumnNameVariableTable.classInformationTableName, userData.getClassInformationSqlObject(), updateCondition);
        databaseController.execUpdate(DatabaseColumnNameVariableTable.usersSpecialExperienceTableName, userData.getUserSpecialExperienceSqlObject(), updateCondition);
        if (!XMLArticleConstantTable.xmlErrorFlag) {
            databaseController.execUpdate(DatabaseColumnNameVariableTable.articlesContentTableName, userData.getArticleContentSqlObject(), updateCondition);
            result = Json.parse(new JSONObject().put("message", "User ID=" + id + " data update finish").toString());
        } else {
            result = Json.parse(new JSONObject().put("error_message", "User ID=" + id + " xml format is incorrect, please fix it and re-click update.").toString());
        }
        XMLArticleConstantTable.xmlErrorFlag = false;
        return ok(result);
    }

    /**
     * Search Page, Extract Sentence Link List Function.
     */
    public Result getSearchData() {
        JsonNode request = request().body().asJson();
        JsonNode result = Json.newObject();
        SearchPreProcessing searchPreProcessing = new SearchPreProcessing();
        try {
            result = searchPreProcessing.setSearchProcessingOfPalabra(new JSONObject(request.toString()));
        } catch (SQLException e) {
            e.getErrorCode();
        }
        return ok(result);
    }

    /**
     * Search Page, Extract Lemma Link List Function.
     */
    public Result getSearchLemmaData() {
        JsonNode request = request().body().asJson();
        JsonNode result = Json.newObject();
        SearchPreProcessing searchPostProcessing = new SearchPreProcessing();
        try {
            result = searchPostProcessing.setSearchProcessingOfLemma(new JSONObject(request.toString()));
        } catch (SQLException e) {
            e.getErrorCode();
        }
        return ok(result);
    }

    /**
     * Search Page, Fuzzy Function.
     */
    public Result getFuzzyData() {
        JsonNode request = request().body().asJson();
        JsonNode result = Json.newObject();
        SearchPreProcessing searchPostProcessing = new SearchPreProcessing();
        try {
            result = searchPostProcessing.setSearchProcessingOfFuzzy(new JSONObject(request.toString()));
        } catch (SQLException e) {
            e.getErrorCode();
        }
        return ok(result);
    }

    /**
     * Search Page, Extract Original Article and Correct Article And Author Information Function.
     */
    public Result getSearchXMLAndAuthorInfoResult() {
        JsonNode request = request().body().asJson();
        JsonNode result = Json.newObject();
        SearchPreProcessing searchPostProcessing = new SearchPreProcessing();
        try {
            result = searchPostProcessing.setSearchProcessingOfXMLAndAuthorInfo(new JSONObject(request.toString()));
        } catch (SQLException e) {
            e.getErrorCode();
        }
        return ok(result);
    }

    /**
     * XML Checker, Extract Original Article and Correct Article Function.
     */
    public Result getXMLResult() {
        JsonNode request = request().body().asJson();
        XMLProcessing xmlProcessing = new XMLProcessing();
        JsonNode result;
        try {
            result = xmlProcessing.setXMLResult(new JSONObject(request.toString()));
        } catch (DocumentException e) {
            System.out.print(e.getMessage());
            result = Json.parse(new JSONObject().put("error_message", "XML format is incorrect, please fix it and re-click checking.").toString());
        }
        return ok(result);
    }
}
