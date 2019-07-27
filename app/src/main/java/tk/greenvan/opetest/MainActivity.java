package tk.greenvan.opetest;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tk.greenvan.opetest.adapter.TestGridAdapter;
import tk.greenvan.opetest.db.DBHelper;
import tk.greenvan.opetest.util.SpaceDecoration;

public class MainActivity extends AppCompatActivity {

    //TODO: Linkar con Firebase
    public static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 1;

    private String mUsername;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;

    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //TODO: Dos nuevas actividades: QuestionActivity, ScoreActivity (Mostrar mis calificaciones)
    //TODO: Una clase Test, Question, UserTests, etc.

    private RecyclerView rv_test_grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = ANONYMOUS;
        mFirebaseDatabase = FirebaseDatabase.getInstance();


        rv_test_grid = (RecyclerView) findViewById(R.id.rv_test_list_grid);

        rv_test_grid.setHasFixedSize(true);
        rv_test_grid.setLayoutManager(new GridLayoutManager(this,2));

        //Get Screen height
        //DisplayMetrics displayMetrics = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //int height = displayMetrics.heightPixels /8; //Max size of item in Quiz


        TestGridAdapter adapter = new TestGridAdapter(MainActivity.this, DBHelper.getInstance(this,mFirebaseDatabase).getTestList());
        int spaceInPixel = 4;
        rv_test_grid.addItemDecoration(new SpaceDecoration(spaceInPixel));
        rv_test_grid.setAdapter(adapter);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

}
