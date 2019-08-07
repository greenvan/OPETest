package tk.greenvan.opetest;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Set;

import tk.greenvan.opetest.db.Common;
import tk.greenvan.opetest.model.Option;
import tk.greenvan.opetest.model.Question;
import tk.greenvan.opetest.ui.main.QuestionFragment;
import tk.greenvan.opetest.ui.main.QuestionFragmentAdapter;

public class QuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        final TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(Common.selectedTest.getName());


        final ViewPager viewPager = findViewById(R.id.vp_question_view_pager);
        final TabLayout tabs = findViewById(R.id.tl_question_tabs);
        tabs.setupWithViewPager(viewPager);



        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TabLayout.Tab currentTab = tabs.getTabAt(tabs.getSelectedTabPosition());
                Question q = Common.questionList.get(new Integer(currentTab.getText().toString()));

                String clue = q.getClue();

                if(clue.isEmpty()){
                    Set<String> keys = q.getOptionList().keySet();
                    for (String key: keys
                    ) {
                        Option o = q.getOptionList().get(key);
                        if (o.isCorrect())
                            clue = o.getText();
                    }
                }

                Snackbar.make(view, clue, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        genFragmentList();


        QuestionFragmentAdapter questionFragmentAdapter = new QuestionFragmentAdapter(this, getSupportFragmentManager(),Common.fragmentList);
        viewPager.setAdapter(questionFragmentAdapter);


        //Hay que activar como seleccionado el fragment que coincide con selected question.
        viewPager.setCurrentItem(Common.selectedIndex);


    }

    private void genFragmentList() {
        Common.fragmentList.clear();
        //Set<Integer> keys = Common.questionList.keySet();
        Set<Integer> keys = Common.filteredAnswerList.keySet();
        for (Integer key: keys){
            Bundle bundle = new Bundle();
            bundle.putInt("question_id",key);
            QuestionFragment fragment = new QuestionFragment();
            fragment.setArguments(bundle);
            Common.fragmentList.add(fragment);
        }
    }

}