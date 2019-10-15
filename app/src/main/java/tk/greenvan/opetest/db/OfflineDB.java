package tk.greenvan.opetest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import tk.greenvan.opetest.db.DBContract.AnswerEntry;
import tk.greenvan.opetest.model.Answer;
import tk.greenvan.opetest.model.Option;
import tk.greenvan.opetest.model.Question;
import tk.greenvan.opetest.model.Test;

import static java.nio.charset.StandardCharsets.UTF_8;


public class OfflineDB {

    public static void loadTestList(Context context) {
        Common.testList.clear();
        try {


            JSONObject root =
                    new JSONObject(loadJSONFromAsset(context, "tests.json"));
            JSONArray testArray = root.getJSONArray("tests");

            //Para cada elemento del array de tests
            for (int i = 0; i < testArray.length(); i++) {
                JSONObject testObj = testArray.getJSONObject(i);
                Test currentTest =
                        new Test(testObj.getString("id"), testObj.getString("name"));
                currentTest.setfileName(testObj.getString("file"));
                Common.testList.add(currentTest);
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

    }

    public static void loadTestList_new(Context context) {

        //Más sencillo pero no termina de funcionar

        Common.testList.clear();
        String jsonTestList = loadJSONFromAsset(context, "tests.json");
        Gson gson = new Gson();
        Type testListType = new TypeToken<ArrayList<Test>>() {
        }.getType();

        Common.testList = gson.fromJson(jsonTestList, testListType);

    }

    public static void loadQuestions(Context context, String filename, boolean emptyList) {

        Common.questionList.clear();
        if (emptyList) Common.answerList.clear();

        String jsonTest = loadJSONFromAsset(context, filename);
        Gson gson = new Gson();
        Common.selectedTest = gson.fromJson(jsonTest, Test.class);

        //Importante, porque en el json no hay filename
        Common.selectedTest.setfileName(filename);

        JsonElement root = new JsonParser().parse(jsonTest);
        JsonObject questions = root.getAsJsonObject().get("question").getAsJsonObject();

        for (Map.Entry<String, JsonElement> question : questions.entrySet()) {
            Question q = gson.fromJson(question.getValue(), Question.class);
            if (emptyList) {
                Answer a = new Answer(q.getId(), Common.ANSWER_STATE.NO_ANSWER);
                Common.answerList.put(a.getQuestionId(), a);
            }

            JsonObject options = question.getValue().getAsJsonObject().get("answer").getAsJsonObject();
            TreeMap<String, Option> optionsTreemap = new TreeMap<>();
            for (Map.Entry<String, JsonElement> option : options.entrySet()) {
                Option o = gson.fromJson(option.getValue(), Option.class);
                optionsTreemap.put(o.getId(), o);
            }

            q.setOptionList(optionsTreemap);

            Common.questionList.put(q.getId(), q);
        }

        Common.selectedTest.setQuestions(Common.questionList);

    }


    public static TreeMap<Integer, Answer> getUserAnswers(Context context, String username, String testId) {

        TreeMap<Integer, Answer> answerList = new TreeMap<>();

        DBHelper mDbHelper = new DBHelper(context);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = new String[]{
                AnswerEntry.COLUMN_QUESTION_ID,
                AnswerEntry.COLUMN_ANSWER_STATE,
                AnswerEntry.COLUMN_LAST_ACCESS,
                AnswerEntry.COLUMN_SELECTION
        };
        String selection = AnswerEntry.COLUMN_USER_NAME + " = ? AND "
                + AnswerEntry.COLUMN_TEST_ID + " = ?";

        String[] selectionArgs = {username, testId};

        Cursor cursor = db.query(
                AnswerEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                selection,                  // The columns for the WHERE clause
                selectionArgs,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                AnswerEntry.COLUMN_QUESTION_ID);                   // The sort order



        try {

            Log.i("GVSoft-OPETest", "Number of rows matching criteria: " + cursor.getCount());

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(AnswerEntry.COLUMN_QUESTION_ID);
            int answerStateColumnIndex = cursor.getColumnIndex(AnswerEntry.COLUMN_ANSWER_STATE);
            int lastAcessColumnIndex = cursor.getColumnIndex(AnswerEntry.COLUMN_LAST_ACCESS);
            int selectionColumnIndex = cursor.getColumnIndex(AnswerEntry.COLUMN_SELECTION);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                Answer a = new Answer();
                a.setQuestionId(cursor.getInt(idColumnIndex));
                int currentAnswerState = cursor.getInt(answerStateColumnIndex);
                a.setState(getAnswerStateFromInt(currentAnswerState));
                a.setLastAcess(cursor.getLong(lastAcessColumnIndex));
                a.setSelection(cursor.getString(selectionColumnIndex));

                answerList.put(a.getQuestionId(), a);
            }

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
            db.close();
            mDbHelper.close();
        }

        return answerList;

    }

    public static Common.ANSWER_STATE getAnswerStateFromInt(int answerState) {

        switch (answerState) {
            case AnswerEntry.RIGHT_ANSWER:
                return Common.ANSWER_STATE.RIGHT_ANSWER;
            case AnswerEntry.WRONG_ANSWER:
                return Common.ANSWER_STATE.WRONG_ANSWER;
            case AnswerEntry.NO_ANSWER:
            default:
                return Common.ANSWER_STATE.NO_ANSWER;
        }
    }

    public static int getAnswerStateAsInt(Common.ANSWER_STATE answerState) {

        switch (answerState) {
            case RIGHT_ANSWER:
                return AnswerEntry.RIGHT_ANSWER;
            case WRONG_ANSWER:
                return AnswerEntry.WRONG_ANSWER;
            case NO_ANSWER:
            default:
                return AnswerEntry.NO_ANSWER;
        }
    }

    public static void saveData_old(Context context, String username, String testId, TreeMap<Integer, Answer> data) {

        // Create database helper
        DBHelper mDbHelper = new DBHelper(context);

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Set<Integer> keys = data.keySet();
        for (Integer key : keys) {
            Answer answer = data.get(key);

            // Create a ContentValues object where column names are the keys,
            // and pet attributes from the editor are the values.
            ContentValues values = new ContentValues();
            values.put(AnswerEntry.COLUMN_USER_NAME, username);
            values.put(AnswerEntry.COLUMN_TEST_ID, testId);
            values.put(AnswerEntry.COLUMN_QUESTION_ID, answer.getQuestionId());
            values.put(AnswerEntry.COLUMN_ANSWER_STATE, answer.getState().toString());
            values.put(AnswerEntry.COLUMN_LAST_ACCESS, answer.getLastAcess());
            values.put(AnswerEntry.COLUMN_SELECTION, answer.getSelection());

            // Insert a new row in the database, returning the ID of that new row.
            long newRowId = db.insert(AnswerEntry.TABLE_NAME, null, values);


            // Show a toast message depending on whether or not the insertion was successful
            if (newRowId == -1) {
                // If the row ID is -1, then there was an error with insertion.
                Log.e("GVSoft-OPETest", "Error saving data from question" + answer.getQuestionId());
            }

        }
        db.close();
        mDbHelper.close();
    }

    public static void saveData(Context context, String username, String testId, TreeMap<Integer, Answer> data) {

        // Create database helper
        DBHelper mDbHelper = new DBHelper(context);

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsAdded = 0;
        int rowsUpdated = 0;

        // TRANSACTION: O añadimos todas las preguntas o no añadimos ninguna
        db.beginTransaction();

        try {

            Set<Integer> keys = data.keySet();
            for (Integer key : keys) {
                Answer answer = data.get(key);

                // Create a ContentValues object where column names are the keys,
                // and pet attributes from the editor are the values.
                ContentValues values = new ContentValues();
                values.put(AnswerEntry.COLUMN_USER_NAME, username);
                values.put(AnswerEntry.COLUMN_TEST_ID, testId);
                values.put(AnswerEntry.COLUMN_QUESTION_ID, answer.getQuestionId());
                values.put(AnswerEntry.COLUMN_ANSWER_STATE, getAnswerStateAsInt(answer.getState()));
                values.put(AnswerEntry.COLUMN_LAST_ACCESS, System.currentTimeMillis()); //Fecha actual
                values.put(AnswerEntry.COLUMN_SELECTION, answer.getSelection());

                String selection = AnswerEntry.COLUMN_USER_NAME + " = ? AND "
                        + AnswerEntry.COLUMN_TEST_ID + " = ? AND "
                        + AnswerEntry.COLUMN_QUESTION_ID + " = ?";

                String[] selectionArgs = new String[]{username, testId, String.valueOf(answer.getQuestionId())};

                //Do an update if the constraints match
                int affected = db.update(AnswerEntry.TABLE_NAME, values, selection, selectionArgs);
                if (affected > 0) rowsUpdated++;

                if (affected == 0) {
                    //Insert a new row for pet in the database, returning the ID of that new row.
                    long newRowId;
                    newRowId = db.insert(AnswerEntry.TABLE_NAME, null, values);

                    // Show a message depending on whether or not the insertion was successful
                    if (newRowId > 0) rowsAdded++;
                    else if (newRowId == -1) {
                        // If the row ID is -1, then there was an error with insertion.
                        Log.e("GVSoft-OPETest", "Error saving data from question" + answer.getQuestionId());
                    }
                }


            }

            //Commit the transaction
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("GVSoft-OPETest", e.getMessage());
        } finally {
            db.endTransaction();

            db.close();
            mDbHelper.close();

            Log.i("GVSoft-OPETest", "Updated rows: " + rowsUpdated);
            Log.i("GVSoft-OPETest", "Added rows: " + rowsAdded);
        }
    }



    public static String loadJSONFromAsset(Context context, String filename) {
        String json = "";
        try {
            InputStream is = context.getAssets().open("json/" + filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, UTF_8);

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        //Log.e("data", json);
        return json;

    }


}
