package sqlCommandLogic;

/**
 * Created by roye on 2017/4/26.
 */
public class SqlCommandComposer {
    public String getUserDataSqlById(int id)
    {
        String sql="select * from articles_content as a,users_information as b,articles_information as c,class_information as d,users_special_experience as e where a.id=b.id and b.id=c.id and c.id=d.id and d.id=e.id and a.id="+id;
        return sql;
    }
    public String updateUserDataById(int id)
    {
        String sql="";
        return sql;
    }
}
