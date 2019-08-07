package tk.greenvan.opetest.db;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.TreeMap;

import tk.greenvan.opetest.model.Answer;
import tk.greenvan.opetest.model.Question;
import tk.greenvan.opetest.model.Test;
import tk.greenvan.opetest.model.UserTest;
import tk.greenvan.opetest.ui.main.QuestionFragment;

public class Common {


    public static String username = "anonymous";

    public static DatabaseReference mTestReference;
    public static DatabaseReference mUserTestReference;

    public static ArrayList<Test> testList = new ArrayList<>();
    public static Test selectedTest = new Test();

    public static TreeMap<Integer,Question> questionList = new TreeMap<Integer,Question>();
    public static TreeMap<Integer,Answer> answerList = new TreeMap<Integer,Answer>();
    public static TreeMap<Integer,Answer>  filteredAnswerList = new TreeMap<Integer,Answer> ();
    public static Question selectedQuestion = new Question();

    public static ArrayList<UserTest> userTestList = new ArrayList<>();
    public static UserTest selectedUserTest = new UserTest();

    public static int right_answer_count = 150;
    public static int wrong_answer_count = 0;
    public static int no_answer_count = 0;

    public static int selectedIndex = -1;

    public static ArrayList<QuestionFragment> fragmentList = new ArrayList<QuestionFragment>();

    public static VIEW_MODE viewMode = VIEW_MODE.VIEW;

    public enum VIEW_MODE {
        TEST,
        VIEW
    }

    public enum ANSWER_STATE {
        NO_ANSWER,
        WRONG_ANSWER,
        RIGHT_ANSWER
    }

}
