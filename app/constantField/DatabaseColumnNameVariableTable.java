package constantField;

/**
 * Created by roye on 2017/4/25.
 */
public class DatabaseColumnNameVariableTable {


    public static int genericColumnNameNumber = 1;
    public static int usersInformationTableNumber = 2;
    public static int classInformationTableNumber = 3;
    public static int articlesInformationTableNumber = 4;
    public static int usersSpecialExperienceTableNumber = 5;
    public static int articlesContentTableNumber=6;
    /**
        * table name list: the table name in ceate_version_2 database
        */

    public static String articlesInformationTableName="articles_information";
    public static String articlesContentTableName="articles_content";
    public static String classInformationTableName="class_information";
    public static String usersInformationTableName="users_information";
    public static String usersSpecialExperienceTableName="users_special_experience";
    public static String[] tablesNameList = {usersInformationTableName, articlesInformationTableName, classInformationTableName, usersSpecialExperienceTableName, articlesContentTableName};
    /*
        *  genneric column: It is the ID column for all table.
         */
    public static String ID = "id";
    public static String SYSTEM_TYPE = "system_type";

    public static String[] genericColumnNameList = {ID};
    /**
         *articles_information column Name List: the column name of articles_information table
         */

    public static String SUBMITTED_YEAR = "submitted_year";
    public static String submittedMonth = "submitted_month";
    public static String haveSubmitted = "have_submitted";
    public static String WRITING_LOCATION = "writting_location";
    public static String ARTICLE_STYLE = "article_style";
    public static String ARTICLE_TOPIC = "article_topic";
    public static String articleTitle = "article_title";
    public static String NUMBER_Of_WORDS = "number_of_words";
    public static String longMin = "long_min";
    public static String longSeg = "long_seg";
    public static String[] articlesInformationColumnNameList = {SUBMITTED_YEAR, submittedMonth, haveSubmitted, WRITING_LOCATION, ARTICLE_STYLE, ARTICLE_TOPIC, articleTitle, NUMBER_Of_WORDS, longMin, longSeg};
    /**
        * user_information column Name List: the column name of user_information table
         * */

    public static String idUnderYear = "id_under_year";
    public static String chineseName = "chinese_name";
    public static String spanishName = "spanish_name";
    public static String GENDER = "gender";
    public static String SCHOOL_NAME = "school_name";
    public static String studentId = "student_id";
    public static String schoolSystem = "school_system";
    public static String DEPARTMENT = "department";
    public static String grade = "grade";
    public static String group = "group_type";
    public static String LEARNING_HOURS = "learning_hours";
    public static String learningYears = "learning_years";
    public static String learningMonths = "learning_months";
    public static String wisconsinNumberOfCorrect = "wisconsin_number_of_correct";
    public static String wisconsinScore = "wisconsin_score";
    public static String dateOfAgreementSubmit = "date_of_agreement_submit";
    public static String level = "level";
    public static String collector = "collector";
    public static String[] userInformationColumnNameList = {idUnderYear, chineseName, spanishName, GENDER, SCHOOL_NAME, studentId, schoolSystem, DEPARTMENT, grade, group, LEARNING_HOURS, learningYears, learningMonths, wisconsinNumberOfCorrect, wisconsinScore, dateOfAgreementSubmit, level, collector};
    /*
        *  user_special_information column Name List: the column name of special_information table;
        * */
    public static String motherTongue = "mother_tongue";
    public static String SPECIAL_EXPERIENCE = "special_experience";
    public static String spanishRelatedResident = "spanish_related_resident";
    public static String spanishRelatedExchange = "spanish_related_exchange";
    public static String spanishDepartmentExchange = "spanish_department_exchange";
    public static String[] userSpecialInformationColumnNameList = {motherTongue, SPECIAL_EXPERIENCE,spanishRelatedResident,spanishRelatedExchange,spanishDepartmentExchange};
    /*
        *class_information column Name List: the column name of class_information table;
         *  */
    public static String className = "class_name";
    public static String classSchoolSystem = "class_school_system";
    public static String classDepartment = "class_department";
    public static String classGrade = "class_grade";
    public static String classGroutp = "class_group";
    public static String teacher = "teacher";
    public static String[] classInformationColumnNameList = {className,classSchoolSystem,classDepartment,classGrade,classGroutp,teacher};
    /**
     * articles_content column Name List: the column name of articles_content table;
     */
    public static String originalArticleText = "original_article_text";
    public static String correctedArticleText = "corrected_article_text";
    public static String xmlContent = "xml_content";
    public static String[] articleContentColumnNameList = {originalArticleText, correctedArticleText, xmlContent};
    /**
     * words_table.
     */
    public static final String LEMMA = "lemma";
    public static final String FUZZY = "fuzzy";
}
