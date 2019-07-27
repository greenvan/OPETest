package tk.greenvan.opetest.db;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DBHelper {

    FirebaseDatabase firebaseDatabase;
    Context context;


    private static DBHelper instance;

    DatabaseReference reference;


    public DBHelper( Context context, FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
        this.context = context;
//        reference = this.firebaseDatabase.getReference("tests");
    }

    public static synchronized DBHelper getInstance(Context context, FirebaseDatabase firebaseDatabase){
        if (instance==null)
            instance=new DBHelper(context,firebaseDatabase);
        return instance;

    }

    public List<String> getTestList(){
        List<String> list = new ArrayList<String>();
        list.add("one - Test de preguntas generales");
        list.add("two - TEI Sistemas, preguntas específicas");
        list.add("3");
        list.add("four!");
        return list;
    }
}
