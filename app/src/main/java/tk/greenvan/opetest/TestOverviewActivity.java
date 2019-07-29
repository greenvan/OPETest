package tk.greenvan.opetest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import tk.greenvan.opetest.adapter.QuestionGridAdapter;
import tk.greenvan.opetest.db.Common;
import tk.greenvan.opetest.model.Answer;
import tk.greenvan.opetest.util.SpaceDecoration;

public class TestOverviewActivity extends AppCompatActivity {

    TextView tv_progress, tv_result, tv_right_answer_and_total;
    Button btn_filter_total, btn_filter_right, btn_filter_wrong, btn_filter_no_answer;
    Button btn_show_questions, btn_do_test, btn_random_test;

    RecyclerView rv_question_list_grid;
    QuestionGridAdapter questionGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_overview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tv_progress = (TextView)findViewById(R.id.tv_progress);
        tv_result = (TextView)findViewById(R.id.tv_result);
        tv_right_answer_and_total = (TextView)findViewById(R.id.tv_right_answer_and_total);

        btn_filter_total = (Button)findViewById(R.id.btn_filter_total);
        btn_filter_right = (Button)findViewById(R.id.btn_filter_right_answer);
        btn_filter_wrong = (Button)findViewById(R.id.btn_filter_wrong_answer);
        btn_filter_no_answer = (Button)findViewById(R.id.btn_filter_no_answer);

        btn_show_questions = (Button)findViewById(R.id.btn_show_questions);
        btn_do_test = (Button)findViewById(R.id.btn_do_test);
        btn_random_test = (Button)findViewById(R.id.btn_random_test);

        rv_question_list_grid = (RecyclerView)findViewById(R.id.rv_question_list_grid);
        rv_question_list_grid.setHasFixedSize(true);
        rv_question_list_grid.setLayoutManager(new GridLayoutManager(this,5));


        //TODO false data load from now
        Common.answerList.add(new Answer(1, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(2, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(3, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(4, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(15, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(16, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(17, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(18, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(21, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(31, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(41, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(51, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(1, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(2, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(3, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(4, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(15, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(16, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(17, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(18, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(21, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(31, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(41, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(51, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(1, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(2, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(3, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(4, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(15, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(16, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(17, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(18, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(21, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(31, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(41, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(51, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(1, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(2, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(3, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(4, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(15, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(16, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(17, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(18, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(21, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(31, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(41, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(51, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(1, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(2, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(3, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(4, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(15, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(16, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(17, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(18, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(21, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(31, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(41, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(51, Common.ANSWER_STATE.RIGHT_ANSWER));

        //Set Adapter
        questionGridAdapter = new QuestionGridAdapter(this, Common.answerList);
        rv_question_list_grid.addItemDecoration(new SpaceDecoration(4));
        rv_question_list_grid.setAdapter(questionGridAdapter);

    }
}
