package tk.greenvan.opetest.db;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import tk.greenvan.opetest.MainActivity;
import tk.greenvan.opetest.model.Question;
import tk.greenvan.opetest.model.Test;

public class DBHelper {

    FirebaseDatabase mFirebaseDatabase;
    Context context;


    private static DBHelper instance;

    DatabaseReference mTestReference;
    ChildEventListener mTestChildEventListener;


    public DBHelper(final Context context, FirebaseDatabase firebaseDatabase) {
        this.mFirebaseDatabase = firebaseDatabase;
        this.context = context;
        mTestReference = this.mFirebaseDatabase.getReference().child("tests");
    }

    public static synchronized DBHelper getInstance(Context context, FirebaseDatabase firebaseDatabase){
        if (instance==null)
            instance=new DBHelper(context,firebaseDatabase);
        return instance;

    }

    public List<Test> getTestList(){

        final AlertDialog dialog = new SpotsDialog.Builder()
                .setContext(context)
                .setCancelable(false)
                .build();

        if(!dialog.isShowing())
            dialog.show();

        mTestReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot testDataSnapshot:dataSnapshot.getChildren()) {
                    String testId= testDataSnapshot.child("id").getValue().toString();
                    String testName = testDataSnapshot.child("name").getValue().toString();
                    Log.i("########3","antes de añadir " + testName);
                    Common.testList.add(new Test(testId,testName));

                }
                if (dialog.isShowing())
                    dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context,""+ databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        Log.i("########3","antes de añadir dkldk");

Common.testList.add(new Test("dkldkla"));
        return Common.testList;
    }
}
