package tk.greenvan.opetest;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import dmax.dialog.SpotsDialog;
import tk.greenvan.opetest.adapter.TestGridAdapter;
import tk.greenvan.opetest.db.Common;
import tk.greenvan.opetest.db.OfflineDB;
import tk.greenvan.opetest.model.Answer;
import tk.greenvan.opetest.model.Test;
import tk.greenvan.opetest.model.UserTest;
import tk.greenvan.opetest.util.SpaceDecoration;

public class MainActivity extends AppCompatActivity {

    public static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 1;

    private String mUsername;

    TestGridAdapter testGridAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    private RecyclerView rv_test_grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = ANONYMOUS;

        if (Common.Mode == Common.MODE.ONLINE) {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();

            Common.mTestReference = this.mFirebaseDatabase.getReference().child("tests");
            //Los datos se cargan tras autentificar: loadTestList();

            //TODO cuando no hay conexión, mostrar error y no quedarse "loading..."
        }



        /* TEST GRID */
        rv_test_grid = findViewById(R.id.rv_test_list_grid);
        rv_test_grid.setHasFixedSize(true);
        rv_test_grid.setLayoutManager(new GridLayoutManager(this, 2));


        testGridAdapter = new TestGridAdapter(MainActivity.this, Common.testList);

        int spaceInPixel = 4;
        rv_test_grid.addItemDecoration(new SpaceDecoration(spaceInPixel));

        rv_test_grid.setAdapter(testGridAdapter);
        /* END OF TEST GRID */


        if (Common.Mode == Common.MODE.ONLINE) {
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        //user is signed in
                        //onSignedInItitialize(user.getDisplayName());
                        onSignedInItitialize(user.getUid());

                    } else {
                        //user is signed out
                        onSignedOutCleanup();
                        // Choose authentication providers
                        List<AuthUI.IdpConfig> providers = Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build());

                        // Create and launch sign-in intent
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setIsSmartLockEnabled(false)
                                        .setAvailableProviders(providers)
                                        .build(),
                                RC_SIGN_IN);
                    }
                }
            };
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Common.Mode == Common.MODE.ONLINE)
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        else onSignedInItitialize(mUsername);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Common.Mode == Common.MODE.ONLINE)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        else onSignedOutCleanup();
    }


    private void onSignedInItitialize(String displayName) {
        mUsername = displayName;
        Common.username = displayName;
        loadTestList();
        loadUserTestList();
        testGridAdapter.notifyDataSetChanged();
    }


    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        Common.username = ANONYMOUS;
        Common.testList.clear();
        Common.userTestList.clear();
        testGridAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                //Sign out
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadTestList(){
        if (Common.Mode == Common.MODE.ONLINE) {
            onlineLoadTestList();
        } else {
            OfflineDB.loadTestList(MainActivity.this);
            testGridAdapter.notifyDataSetChanged();
        }
    }

    public void loadUserTestList() {
        if (Common.Mode == Common.MODE.ONLINE) {
            onlineLoadUserTestList(); //Aquí es más eficiente porque los carga todos juntos
        } else {
            //OfflineDB.loadUserTestList(MainActivity.this); //Quizá no sea necesario aquí
        }

    }

    public void onlineLoadTestList() {

        final AlertDialog dialog = new SpotsDialog.Builder()
                .setContext(MainActivity.this)
                .setCancelable(false)
                .build();

        if(!dialog.isShowing())
            dialog.show();

        Common.mTestReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Common.testList.clear();
                for (DataSnapshot testDataSnapshot:dataSnapshot.getChildren()) {
                    String testId= testDataSnapshot.child("id").getValue().toString();
                    String testName = testDataSnapshot.child("name").getValue().toString();
                    Common.testList.add(new Test(testId,testName));
                }
                if (dialog.isShowing())
                    dialog.dismiss();
                testGridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,""+ databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onlineLoadUserTestList() {

        /*final AlertDialog dialog = new SpotsDialog.Builder()
                .setContext(MainActivity.this)
                .setCancelable(false)
                .build();

        if(!dialog.isShowing())
            dialog.show();*/

        Common.mUserTestReference = this.mFirebaseDatabase.getReference().child("userTests").child(mUsername);

        Common.mUserTestReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Common.userTestList.clear();
                        for (DataSnapshot userTestDataSnapshot:dataSnapshot.getChildren()) {

                            UserTest userTest = new UserTest();
                            userTest.setUsername(mUsername);
                            userTest.setTestID(userTestDataSnapshot.getKey());


                            TreeMap<Integer,Answer> answerList = new TreeMap<Integer,Answer>();

                            for (DataSnapshot answerDataSnapshot: userTestDataSnapshot.getChildren()) {
                                Answer answer = answerDataSnapshot.getValue(Answer.class);
                                answerList.put(answer.getQuestionId(),answer);
                            }

                            userTest.setAnswerList(answerList);

                            Common.userTestList.add(userTest);

                        }
                       /* if (dialog.isShowing())
                            dialog.dismiss();*/
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this,""+ databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
