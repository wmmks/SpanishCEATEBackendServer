package databaseUtil;

import articleXMLReader.XMLParser;
import constantField.DatabaseColumnNameVariableTable;

import java.util.ArrayList;

/**
 * Created by roye on 2017/4/26.
 */
public class SqlObject {
    private int size;
    private ArrayList<String> columnName;
    private ArrayList<Object> columnValue;
    private XMLParser xmlParser;
    public int size()
    {
        return size;
    }
    public SqlObject()
    {
        columnName=new ArrayList<>();
        columnValue=new ArrayList<>();
        xmlParser = new XMLParser();
        size=0;
    }
    public void addSqlObject(String column,Object value)
    {
        if(columnName.contains(column))
        {
            columnValue.add(columnName.indexOf(column),value);
            columnValue.remove(columnName.indexOf(column)+1);
        }
        else
        {
            columnName.add(column);
            columnValue.add(value);
            size=columnName.size();
        }

    }
    public int getSize()
    {
        return size;
    }
    public String getColumnNameIndexOf(int i)
    {
        return columnName.get(i);
    }
    public Object getColumnValueIndexOf(int i)
    {
        if(columnName.size()>=i)
        {
            return columnValue.get(i);
        }

            return null;
    }
    public int getColumnNameIndex(String column)
    {
        if(columnName.contains(column))
        {
            return columnName.lastIndexOf(column);
        }
        else
        {
             return -1;
        }
    }
    public String getColumnNameString()
    {
        String columnString="";
        if(size!=0)
        {
            columnString+=columnName.get(0);
            for(int i=1;i<size;i++)
            {
                columnString+=",";
                columnString+=columnName.get(i);
            }
        }
        return columnString;
    }
    public String getColumnValueString()
    {
        String columnPreparedStatementString="";
        if(size!=0)
        {
            columnPreparedStatementString+=toSqlValue(columnValue.get(0));
            for(int i=1;i<size;i++)
            {
                columnPreparedStatementString+=",";
                columnPreparedStatementString+=toSqlValue(columnValue.get(i));
            }
        }
        return  columnPreparedStatementString;
    }

    public String getColumnNameValuePairString() {
        String columnPreparedStatementString = "";
        if (size != 0) {
            if (columnName.get(0).equals(DatabaseColumnNameVariableTable.xmlContent)) {
                try {
                    xmlParser.setXMLParser(columnValue.get(0).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            columnPreparedStatementString += columnName.get(0) + "=" + toSqlValue(columnValue.get(0));
            for (int i = 1; i < size; i++) {
                columnPreparedStatementString += ",";
                if (columnName.get(i).equals(DatabaseColumnNameVariableTable.originalArticleText)) {
                    columnPreparedStatementString += columnName.get(i) + "='";
                    for (int j = 0; j < xmlParser.getOriginalArticle().getArticleList().size(); j++) {
                        if (j != 0) {
                            columnPreparedStatementString += xmlParser.getOriginalArticle().getArticleList().get(j);
                        } else {
                            if (xmlParser.getOriginalArticle().getArticleList().get(j).contains("\t")) {
                                String[] x = xmlParser.getOriginalArticle().getArticleList().get(j).split("\t");
                                columnPreparedStatementString += "      " + x[x.length - 1];
                            } else if (xmlParser.getOriginalArticle().getArticleList().get(j).contains(" ")) {
                                String[] x = xmlParser.getOriginalArticle().getArticleList().get(j).split(" ");
                                columnPreparedStatementString += "      " + x[x.length - 1];
                            }
                        }
                    }
                    columnPreparedStatementString += "'";
                } else if (columnName.get(i).equals(DatabaseColumnNameVariableTable.correctedArticleText)) {
                    columnPreparedStatementString += columnName.get(i) + "='";
                    for (int j = 0; j < xmlParser.getCorrectedArticle().getArticleList().size(); j++) {
                        if (j != 0) {
                            columnPreparedStatementString += xmlParser.getCorrectedArticle().getArticleList().get(j);
                        } else {
                            if (xmlParser.getCorrectedArticle().getArticleList().get(j).contains("\t")) {
                                String[] x = xmlParser.getCorrectedArticle().getArticleList().get(j).split("\t");
                                columnPreparedStatementString += "      " + x[x.length - 1];
                            } else if (xmlParser.getCorrectedArticle().getArticleList().get(j).contains(" ")) {
                                String[] x = xmlParser.getCorrectedArticle().getArticleList().get(j).split(" ");
                                columnPreparedStatementString += "      " + x[x.length - 1];
                            }
                        }
                    }
                    columnPreparedStatementString += "'";
                } else {
                    columnPreparedStatementString += columnName.get(i) + "=" + toSqlValue(columnValue.get(i));
                }
            }
        }
        return  columnPreparedStatementString;
    }

    private String toSqlValue(Object obj)
    {

        if(obj.getClass()==Integer.class)
        {
           return obj.toString();
        }
        else if(obj.getClass()==String.class)
        {
            return "'"+obj.toString()+"'";
        }
        else return null;
    }
}
