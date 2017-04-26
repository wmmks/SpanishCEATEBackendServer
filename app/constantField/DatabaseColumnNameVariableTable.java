package constantField;

/**
 * Created by roye on 2017/4/25.
 */
public class DatabaseColumnNameVariableTable {

    /**
        * table name list: the table name in ceate_version_2 database
        */

    public static String articlesInformationTableName="articles_information";
    public static String articlesContentTableName="articles_content";
    public static String classInformationTableName="class_information";
    public static String usersInformationTableName="users_information";
    public static String usersSpecialExperienceTableName="users_special_experience";
    /*
        *  genneric column: It is the id column for all table.
         */
    public static String id="id";

    /**
         *articles_information column Name List: the column name of articles_information table
         */

    public static String submittedYear="submitted_year";
    public static String submittedMonth="submitted_month";
    public static String haveSubmitted="have_submitted";
    public static String writtingLocation="writting_location";
    public static String articleStyle="article_style";
    public static String articleTopic="article_topic";
    public static String articleTitle="article_title";
    public static String numberOfWords="number_of_words";
    /**
        * user_information column Name List: the column name of user_information table
         * */

    public static String idUnderYear="id_under_year";
    public static String chineseName="chinese_name";
    public static String spanishName="spanish_name";
    public static String gender="gender";
    public static String schoolName="school_name";
    public static String studentId="student_id";
    public static String schoolStstem="school_system";
    public static String department="department";
    public static String grade="grade";
    public static String group="group_type";
    public static String learningHours="learning_hours";
    public static String learningYears="learning_years";
    public static String learningMonths="learning_months";
    public static String wisconsinNumberOfCorrect="wisconsin_number_of_correct";
    public static String wisconsinScore="wisconsin_score";
    public static String dateOfAgreementSubmit="date_of_agreement_submit";
    /*
        *  user_special_information column Name List: the column name of special_information table;
        * */
    public static String motherTongue="mother_tongue";
    public static String specialExperience="special_exeperience";
    public static String spanishRelatedResident="spanish_related_resident";
    public static String spanishRelatedExchange="spanish_related_exchange";
    public static String spanishDepartmentExchange="spanish_department_exchange";
    /*
        *class_information column Name List: the column name of class_information table;
         *  */
    public static String className="class_name";
    public static String classSchoolSystem="class_school_system";
    public static String classDepartment="class_department";
    public static String classGrade="class_grade";
    public static String classGroutp="class_group";
    public static String teacher="teacher";

    /**
        *articles_content column Name List: the column name of articles_content table;
        */
    public static String originalArticleText="original_article_text";
    public static String correctedArticleText="corrected_article_text";

}
