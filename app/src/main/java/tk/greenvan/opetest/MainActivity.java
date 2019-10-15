package tk.greenvan.opetest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.auth.AuthUI;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Duration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import tk.greenvan.opetest.adapter.TestGridAdapter;
import tk.greenvan.opetest.db.Common;
import tk.greenvan.opetest.db.OfflineDB;
import tk.greenvan.opetest.model.Answer;
import tk.greenvan.opetest.model.Test;
import tk.greenvan.opetest.model.UserTest;
import tk.greenvan.opetest.util.SpaceDecoration;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

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

        //Cargar preferencias del usuario
        setUpSharedPreferences();




        /* TEST GRID */
        rv_test_grid = findViewById(R.id.rv_test_list_grid);
        rv_test_grid.setHasFixedSize(true);
        rv_test_grid.setLayoutManager(new GridLayoutManager(this, 2));


        testGridAdapter = new TestGridAdapter(MainActivity.this, Common.testList);

        int spaceInPixel = 4;
        rv_test_grid.addItemDecoration(new SpaceDecoration(spaceInPixel));

        rv_test_grid.setAdapter(testGridAdapter);
        /* END OF TEST GRID */


        //TODO
        // - Incluir opción para descargar últimos tests


    }

    @Override
    protected void onStart() {
        super.onStart();

        String str_mode = (Common.Mode == Common.MODE.OFFLINE) ? getString(R.string.offline_mode_summary) : getString(R.string.online_mode_summary);

        getSupportActionBar().setTitle(getString(R.string.app_name) + " - [" + str_mode + "]");

        if (Common.Mode == Common.MODE.ONLINE) {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();

            Common.mTestReference = this.mFirebaseDatabase.getReference().child("tests");
            //Los datos se cargan tras autentificar: loadTestList();

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
                                new AuthUI.IdpConfig.EmailBuilder().build()
                                //TODO hacer funcionar la configuración de Google Sign in
                                //,new AuthUI.IdpConfig.GoogleBuilder().build()
                        );

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

    private void setUpSharedPreferences() {

        SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(this);


        Common.Mode =
                sharedPreferences.getBoolean(getString(R.string.online_mode_key), false) ? Common.MODE.ONLINE : Common.MODE.OFFLINE;

        Common.onRightMoveToNext =
                sharedPreferences.getBoolean(getString(R.string.on_right_move_to_next_key), false);

        Common.NUM_QUESTIONS_QUICK_TEST =
                loadNumQuestionsFromSharedPreferences(sharedPreferences);

        Common.TOTAL_TIME_QUICK_TEST =
                loadTimeoutFromSharedPreferences(sharedPreferences);


        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private int loadTimeoutFromSharedPreferences(SharedPreferences sharedPreferences) {
        return Integer.parseInt(
                sharedPreferences.getString(
                        getString(R.string.quicktest_timeout_key),
                        "6"
                )
        ) * 60 * 1000;
    }

    private int loadNumQuestionsFromSharedPreferences(SharedPreferences sharedPreferences) {


        return Integer.parseInt(
                sharedPreferences.getString(
                        getString(R.string.quicktest_number_of_questions_key),
                        "30"
                )
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Common.Mode == Common.MODE.ONLINE) {
            //TODO Casca si no hay listener a pesar de poner el modo online
            //if (mAuthStateListener != null)
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
            //else this.onRestart();
        }
        else onSignedInItitialize(mUsername);
    }

    @Override
    protected void onPause() {

        super.onPause();
        if (Common.Mode == Common.MODE.ONLINE)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        else onSignedOutCleanup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onSignedOutCleanup();
        //Common.testList.clear();
        //Common.userTestList.clear();

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void onSignedInItitialize(String displayName) {
        mUsername = displayName;
        Common.username = displayName;
        //if (Common.testList.size() == 0)
        loadTestList();
        // if (Common.userTestList.size()==0)
        loadUserTestList();
        testGridAdapter.notifyDataSetChanged();
    }


    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        Common.username = ANONYMOUS;
        // TODO Test if deleting this increases performance
        Common.testList.clear();
        Common.userTestList.clear();
        testGridAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.signed_in, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.sign_in_cancelled, Toast.LENGTH_SHORT).show();
                //En lugar de terminar con la actividad, ponemos el modo offline
                //finish();
                if (Common.Mode == Common.MODE.ONLINE)
                    mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
                activateOfflineMode();
            }
        }
    }


    public void reload() {

        this.onRestart(); //TODO check a better way
        this.onResume();
        this.testGridAdapter.notifyDataSetChanged();
    }

    private void activateOfflineMode() {

        //Editar las preferencias y ahí el listener se ocupará de restart

        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(this).edit();

        editor.putBoolean(getString(R.string.online_mode_key), false);
        editor.commit();

        reload();

    }

    private void activateOnlineMode() {

        //Editar las preferencias y ahí el listener se ocupará de restart

        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(this).edit();

        editor.putBoolean(getString(R.string.online_mode_key), true);
        editor.commit();

        reload();

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
            case R.id.signing_menu:
                //Sign out or sign in
                //Si estamos en modo online hacemos un sign-out
                if (Common.Mode == Common.MODE.ONLINE) { //También en modo mixto
                    AuthUI.getInstance().signOut(this);
                } else { //Si estamos en modo offline activamos el modo online
                    activateOnlineMode();
                }
                return true;
            case R.id.settings_menu:
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.about_menu:
                //Show About page
                View about_page_layout = LayoutInflater.from(this)
                        .inflate(R.layout.about_page, null);
                new MaterialStyledDialog.Builder(MainActivity.this)
                        .setCancelable(true)
                        .withDialogAnimation(true, Duration.SLOW)
                        .setIcon(R.mipmap.ic_launcher)
                        .setHeaderColor(R.color.fui_transparent)
                        .setCustomView(about_page_layout)
                        .setPositiveText(getString(R.string.ok))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    }
                        ).show();

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
            //Si no existe lista de tests o está vacía la
            if (Common.userTestList.size() == 0)
                onlineLoadUserTestList(); //Aquí es más eficiente porque los carga todos juntos
        } else {
            //OfflineDB.loadUserTestList(MainActivity.this); //Quizá no sea necesario aquí
        }

    }

    public void onlineLoadTestList() {


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View progres_dialog_view = getLayoutInflater().from(this)
                .inflate(R.layout.progress_dialog, null);

        builder.setView(progres_dialog_view);
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.fui_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, getString(R.string.user_cancel_load), Toast.LENGTH_LONG).show();
            }
        });

        final Dialog dialog = builder.create();

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
                    //dialog.dismiss();
                    dialog.cancel();
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


        if (key.equals(getString(R.string.online_mode_key))) {
            Common.Mode = sharedPreferences.getBoolean(key, false) ? Common.MODE.ONLINE : Common.MODE.OFFLINE;
        } else if (key.equals(getString(R.string.on_right_move_to_next_key))) {
            Common.onRightMoveToNext = sharedPreferences.getBoolean(key, false);
        } else if (key.equals(getString(R.string.quicktest_number_of_questions_key))) {
            Common.NUM_QUESTIONS_QUICK_TEST = loadNumQuestionsFromSharedPreferences(sharedPreferences);
        } else if (key.equals(getString(R.string.quicktest_timeout_key))) {
            Common.TOTAL_TIME_QUICK_TEST = loadTimeoutFromSharedPreferences(sharedPreferences);
        }


    }
}
