package tk.greenvan.opetest.db;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import tk.greenvan.opetest.model.Answer;
import tk.greenvan.opetest.model.Question;
import tk.greenvan.opetest.model.Test;
import tk.greenvan.opetest.model.UserTest;

public class Common {


    public static String username = "anonymous";

    public static DatabaseReference mTestReference;
    public static DatabaseReference mUserTestReference;

    public static List<Test> testList = new ArrayList<>();
    public static Test selectedTest = new Test();

    public static List<Question> questionList = new ArrayList<>();
    public static List<Answer> answerList = new ArrayList<>();
    public static List<Question> filteredQuestionList = new ArrayList<>();
    public static List<Answer> filteredAnswerList = new ArrayList<>();
    public static Question selectedQuestion = new Question();

    public static List<UserTest> userTestList = new ArrayList<>();
    public static UserTest selectedUserTest = new UserTest();

    public static int right_answer_count = 150;
    public static int wrong_answer_count = 0;
    public static int no_answer_count = 0;


    public enum ANSWER_STATE {
        NO_ANSWER,
        WRONG_ANSWER,
        RIGHT_ANSWER
    }

}
