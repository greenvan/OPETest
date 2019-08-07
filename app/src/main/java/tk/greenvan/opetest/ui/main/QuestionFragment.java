package tk.greenvan.opetest.ui.main;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.TreeMap;

import tk.greenvan.opetest.R;
import tk.greenvan.opetest.db.Common;
import tk.greenvan.opetest.model.Option;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "question_id";
    private PageViewModel pageViewModel;

    private TreeMap<String, Option> options = new TreeMap<String, Option>();



    public static QuestionFragment newInstance(int index) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_question, container, false);

        final TextView textView = rootView.findViewById(R.id.tv_question_text);
        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        final TextView tv_A = rootView.findViewById(R.id.tv_option_a);
        pageViewModel.getOptionA().observe(this, new Observer<Option>() {
            @Override
            public void onChanged(@Nullable Option o) {
                if(o!=null) {

                    tv_A.setText(o.getId() + ". " + o.getText());
                    tv_A.setVisibility(View.VISIBLE);

                    if(Common.viewMode== Common.VIEW_MODE.VIEW) {
                        if (o.isCorrect())
                            tv_A.setBackgroundColor(Color.parseColor("#99cc00"));
                        else //Por si acaso volvemos a poner en modo no_answer
                            tv_A.setBackgroundColor(Color.parseColor("#00ddff"));
                    } else {
                        //TODO change color of red and set click listeners etc.
                    }


                }else {
                    tv_A.setVisibility(View.GONE);
                }
            }
        });

        final TextView tv_B = rootView.findViewById(R.id.tv_option_b);
        pageViewModel.getOptionB().observe(this, new Observer<Option>() {
            @Override
            public void onChanged(@Nullable Option o) {
                if(o!=null) {
                    tv_B.setText(o.getId() + ". " + o.getText());
                    tv_B.setVisibility(View.VISIBLE);

                    if(Common.viewMode== Common.VIEW_MODE.VIEW) {
                        if (o.isCorrect())
                            tv_B.setBackgroundColor(Color.parseColor("#99cc00"));
                        else //Por si acaso volvemos a poner en modo no_answer
                            tv_B.setBackgroundColor(Color.parseColor("#00ddff"));
                    } else {
                        //TODO change color of red and set click listeners etc.
                    }


                }else {
                    tv_B.setVisibility(View.GONE);
                }
            }
        });

        final TextView tv_C = rootView.findViewById(R.id.tv_option_c);
        pageViewModel.getOptionC().observe(this, new Observer<Option>() {
            @Override
            public void onChanged(@Nullable Option o) {
                if(o!=null) {
                    tv_C.setText(o.getId() + ". " + o.getText());
                    tv_C.setVisibility(View.VISIBLE);

                    if(Common.viewMode== Common.VIEW_MODE.VIEW) {
                        if (o.isCorrect())
                            tv_C.setBackgroundColor(Color.parseColor("#99cc00"));
                        else //Por si acaso volvemos a poner en modo no_answer
                            tv_C.setBackgroundColor(Color.parseColor("#00ddff"));
                    } else {
                        //TODO change color of red and set click listeners etc.
                    }


                }else {
                    tv_C.setVisibility(View.GONE);
                }
            }
        });


        final TextView tv_D = rootView.findViewById(R.id.tv_option_d);
        pageViewModel.getOptionD().observe(this, new Observer<Option>() {
            @Override
            public void onChanged(@Nullable Option o) {
                if(o!=null) {
                    tv_D.setText(o.getId() + ". " + o.getText());
                    tv_D.setVisibility(View.VISIBLE);

                    if(Common.viewMode== Common.VIEW_MODE.VIEW) {
                        if (o.isCorrect())
                            tv_D.setBackgroundColor(Color.parseColor("#99cc00"));
                        else //Por si acaso volvemos a poner en modo no_answer
                            tv_D.setBackgroundColor(Color.parseColor("#00ddff"));
                    } else {
                        //TODO change color of red and set click listeners etc.
                    }


                }else {
                    tv_D.setVisibility(View.GONE);
                }
            }
        });


        final TextView tv_E = rootView.findViewById(R.id.tv_option_e);
        pageViewModel.getOptionE().observe(this, new Observer<Option>() {
            @Override
            public void onChanged(@Nullable Option o) {
                if(o!=null) {
                    tv_E.setText(o.getId() + ". " + o.getText());
                    tv_E.setVisibility(View.VISIBLE);

                    if(Common.viewMode== Common.VIEW_MODE.VIEW) {
                        if (o.isCorrect())
                            tv_E.setBackgroundColor(Color.parseColor("#99cc00"));
                        else //Por si acaso volvemos a poner en modo no_answer
                            tv_E.setBackgroundColor(Color.parseColor("#00ddff"));
                    } else {
                        //TODO change color of red and set click listeners etc.
                    }


                }else {
                    tv_E.setVisibility(View.GONE);
                }
            }
        });


        return rootView;
    }
}