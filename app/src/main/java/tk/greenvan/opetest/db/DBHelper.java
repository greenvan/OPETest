package tk.greenvan.opetest.db;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import tk.greenvan.opetest.model.Test;

public class DBHelper {

    FirebaseDatabase mFirebaseDatabase;
    Context context;


    private static DBHelper instance;

    DatabaseReference mTestReference;


    public DBHelper( Context context, FirebaseDatabase firebaseDatabase) {
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
        List<Test> list = new ArrayList<Test>();
        list.add(new Test("one"," - Test de preguntas generales"));
        list.add(new Test("two","two - TEI Sistemas, preguntas específicas"));
        list.add(new Test("3"));
        list.add(new Test("four!"));
        return list;
    }
}
