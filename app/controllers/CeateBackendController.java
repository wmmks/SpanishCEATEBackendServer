package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import constantField.ConstantField;
import constantField.DatabaseColumnNameVariableTable;
import databaseUtil.DatabaseController;
import json.JSONObject;
import play.libs.Json;
import play.mvc.*;

import sqlCommandLogic.SqlCommandComposer;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by roye on 2017/4/24.
 */
public class CeateBackendController extends Controller{

    @Inject
    SqlCommandComposer sqlCommandComposer;
    @Inject
    DatabaseController databaseController;
    public Result HelloWorld()
    {

        return ok("HelloWorld");
    }
    public Result getUserInformation()
    {
        JsonNode request = request().body().asJson();
        int id=Integer.parseInt(request.findPath(ConstantField.userAndArticleId).toString());
        JsonNode result = Json.newObject();
        ResultSet resultSet=databaseController.execSelect(sqlCommandComposer.getUserDataSqlById(id));
        try {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            JSONObject resultJsonObject=new JSONObject();
            if(resultSet.next())
            {
                for(int i=1;i<=resultSetMetaData.getColumnCount();i++)
                {
                    Object columnValue=resultSet.getObject(i);
                    if(resultSetMetaData.getColumnTypeName(i).equals(ConstantField.databaseStringType))
                    {
                        resultJsonObject.put(resultSetMetaData.getColumnName(i),columnValue.toString());
                    }
                    else if(resultSetMetaData.getColumnTypeName(i).equals(ConstantField.databaseIntType))
                    {
                        resultJsonObject.put(resultSetMetaData.getColumnName(i),Integer.parseInt(columnValue.toString()));
                    }
                }
            }
            result=Json.parse(resultJsonObject.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ok(result);
    }
    public Result updateUserInformation()
    {
        JsonNode request = request().body().asJson();
        JSONObject userInformationJsonObject=new JSONObject(request.toString());
        Set<String> columnNameSet= userInformationJsonObject.keySet();
        for(String columnName:columnNameSet)
        {
            Arrays.asList(DatabaseColumnNameVariableTable.tablesNameList).contains("");

        }

        return ok(request.toString());
    }
}
