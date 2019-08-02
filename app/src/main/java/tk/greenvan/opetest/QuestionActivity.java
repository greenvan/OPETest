package tk.greenvan.opetest;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import tk.greenvan.opetest.db.Common;
import tk.greenvan.opetest.ui.main.QuestionFragment;
import tk.greenvan.opetest.ui.main.QuestionFragmentAdapter;

public class QuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        genFragmentList();

        QuestionFragmentAdapter questionFragmentAdapter = new QuestionFragmentAdapter(this, getSupportFragmentManager(),Common.fragmentList);
        viewPager.setAdapter(questionFragmentAdapter);


    }

    private void genFragmentList() {
        Common.fragmentList.clear();
        for (int i = 0; i< Common.questionList.size(); i++){
            Bundle bundle = new Bundle();
            bundle.putInt("question_id",Common.questionList.get(i).getId());
            QuestionFragment fragment = new QuestionFragment();
            fragment.setArguments(bundle);
            Common.fragmentList.add(fragment);
        }
    }
}