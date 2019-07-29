package tk.greenvan.opetest.db;

import java.util.ArrayList;
import java.util.List;

import tk.greenvan.opetest.model.Answer;
import tk.greenvan.opetest.model.Question;
import tk.greenvan.opetest.model.Test;

public class Common {

    public static List<Test> testList = new ArrayList<>();
    public static Test selectedTest = new Test();

    public static List<Question> questionList = new ArrayList<>();
    public static List<Answer> answerList = new ArrayList<>();
    public static List<Answer> filteredAnswerList = new ArrayList<>();
    public static Question selectedQuestion = new Question();


    public enum ANSWER_STATE {
        NO_ANSWER,
        WRONG_ANSWER,
        RIGHT_ANSWER
    }

}
