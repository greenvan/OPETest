package tk.greenvan.opetest.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.TreeMap;

import tk.greenvan.opetest.QuestionActivity;
import tk.greenvan.opetest.R;
import tk.greenvan.opetest.common.Common;
import tk.greenvan.opetest.model.Answer;

public class QuestionGridAdapter extends RecyclerView.Adapter<QuestionGridAdapter.MyViewHolder>  {

    Context context;
    TreeMap<Integer,Answer> answerList;

    public QuestionGridAdapter(Context context, TreeMap<Integer,Answer> answerList) {
        this.context = context;
        this.answerList = answerList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_question_grid,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Answer answer = (new ArrayList<Answer>(answerList.values())).get(position);
        //String questionLabel = context.getString(R.string.question) + " " + answer.getQuestionId();
        String questionLabel = String.valueOf(answer.getQuestionId());
        holder.btn_question.setText(questionLabel);

        //Change color base and image on result
        Drawable img;
        if(answer.getState() == Common.ANSWER_STATE.RIGHT_ANSWER){
            img = context.getDrawable(R.drawable.ic_check_white_24dp);
            holder.btn_question.setCompoundDrawablesWithIntrinsicBounds(null, null, null, img);
            holder.btn_question.setBackgroundColor(Color.parseColor("#99cc00"));
        } else  if(answer.getState() ==Common.ANSWER_STATE.WRONG_ANSWER){
            img = context.getDrawable(R.drawable.ic_clear_white_24dp);
            holder.btn_question.setCompoundDrawablesWithIntrinsicBounds(null, null, null, img);
            holder.btn_question.setBackgroundColor(Color.parseColor("#cc0000"));
        } else { //No answer
            img = context.getDrawable(R.drawable.ic_error_outline_white_24dp);
            holder.btn_question.setCompoundDrawablesWithIntrinsicBounds(null, null, null, img);
            holder.btn_question.setBackgroundColor(Color.parseColor("#00ddff"));
        }
    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        Button btn_question;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            btn_question = itemView.findViewById(R.id.btn_question);
            btn_question.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //when user click to QuestionActivity button, we will  show QuestionActivity
                    Answer answer = (new ArrayList<Answer>(answerList.values())).get(getAdapterPosition());
                    //Toast.makeText(context,"Pressed on question " + answer.getQuestionId(),Toast.LENGTH_SHORT).show();

                    Common.selectedQuestion = Common.questionList.get(answer.getQuestionId());  //Assign current question
                    Common.selectedIndex = getAdapterPosition();
                    Common.viewMode = Common.VIEW_MODE.VIEW;
                    Intent intent = new Intent(context, QuestionActivity.class);
                    context.startActivity(intent);
                }
            });
        }
    }
}
