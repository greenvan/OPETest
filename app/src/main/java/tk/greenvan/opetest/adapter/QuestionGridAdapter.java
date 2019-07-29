package tk.greenvan.opetest.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tk.greenvan.opetest.R;
import tk.greenvan.opetest.db.Common;
import tk.greenvan.opetest.model.Answer;

public class QuestionGridAdapter extends RecyclerView.Adapter<QuestionGridAdapter.MyViewHolder>  {

    Context context;
    List<Answer> answerList;

    public QuestionGridAdapter(Context context, List<Answer> answerList) {
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

        //TODO find this error
        holder.btn_question.setText(new StringBuilder(context.getString(R.string.question)).append(answerList.get(position).getQuestionId()));

        //Change color base and image on result
        Drawable img;
        if(answerList.get(position).getState() == Common.ANSWER_STATE.RIGHT_ANSWER){
            holder.btn_question.setBackgroundColor(Color.parseColor("#99cc00"));
            img = context.getDrawable(R.drawable.ic_check_white_24dp);
            holder.btn_question.setCompoundDrawablesWithIntrinsicBounds(null, null, null, img);
        } else  if(answerList.get(position).getState() ==Common.ANSWER_STATE.WRONG_ANSWER){
            holder.btn_question.setBackgroundColor(Color.parseColor("#cc0000"));
            img = context.getDrawable(R.drawable.ic_clear_white_24dp);
            holder.btn_question.setCompoundDrawablesWithIntrinsicBounds(null, null, null, img);
        } else { //No answer
            img = context.getDrawable(R.drawable.ic_error_outline_white_24dp);
            holder.btn_question.setCompoundDrawablesWithIntrinsicBounds(null, null, null, img);
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
            btn_question=(Button)itemView.findViewById(R.id.btn_question);
            btn_question.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //when user click to Question button, we will  show Question
                    Toast.makeText(context,"Pressed on question " + answerList.get(getAdapterPosition()).getQuestionId(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
