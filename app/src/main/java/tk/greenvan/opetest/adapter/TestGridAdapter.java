package tk.greenvan.opetest.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tk.greenvan.opetest.R;
import tk.greenvan.opetest.model.Test;

public class TestGridAdapter extends RecyclerView.Adapter<TestGridAdapter.MyViewHolder>  {

    Context context;
    List<Test> testList;

    public TestGridAdapter(Context context, List<Test> testList) {
        this.context = context;
        this.testList = testList;
    }

    @NonNull
    @Override
    public TestGridAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_test_grid,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TestGridAdapter.MyViewHolder holder, int position) {
        holder.tv_test_name.setText(testList.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return testList.size();
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder{
        CardView card_test;
        TextView tv_test_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_test = itemView.findViewById(R.id.card_test);
            tv_test_name = itemView.findViewById(R.id.tv_test_name);

            card_test.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"Click at test "+testList.get(getAdapterPosition()).toString(), Toast.LENGTH_SHORT).show();
                    /*Common.selectedCategory = categories.get(getAdapterPosition());  //Assign current category
                    Intent intent = new Intent(context, QuestionActivity.class);
                    context.startActivity(intent);*/

                }
            });
        }
    }
}
