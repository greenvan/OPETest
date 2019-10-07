package tk.greenvan.opetest.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        DBHelper mDbHelper = new DBHelper(context);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM user_answer"
        // to get a Cursor that contains all rows from the table where testId = id.
        String sql = "SELECT * FROM " + AnswerEntry.TABLE_NAME + " WHERE "
                + AnswerEntry.COLUMN_TEST_ID + " = '" + testId + "' AND "
                + AnswerEntry.COLUMN_USER_NAME + " = '" + username
                + "' ORDER BY " + AnswerEntry.COLUMN_QUESTION_ID + "; ";

        Cursor cursor = db.rawQuery(sql, null);

        TreeMap<Integer, Answer> answerList = new TreeMap<>();
        try {

            Log.i("######", "Number of rows in database table: " + cursor.getCount());
            //TODO hacemos un for para cada entrada de la tabla
            // Debemos actualizar la lista de test del usuario ??? o lo hacemos cada vez que selecciona un test???
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }

        return answerList;

    }

    public static void saveEmptyAnswerList() {
        //TODO implement
    }

    public static void saveData() {

        //TODO implement

        // Guardar los datos de filteredList en answerList y la base de datos
     /*   Set<Integer> keys = Common.filteredAnswerList.keySet();
        for (Integer key : keys) {
            Answer answer = Common.filteredAnswerList.get(key);
            Common.mUserTestReference.child(Common.selectedTest.getId()).child(String.valueOf(answer.getQuestionId())).setValue(answer);
        }*/
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

    //TODO delete this method, just for debugging
    public static void printjson() {
        Gson gson = new Gson();
        String jsonString = gson.toJson(Common.questionList);

        //   Log.i("###########", Common.questionList.size() + " ");
        Log.i("#########", "Imprimo json");
        Log.i("#########", jsonString);

        Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJsonString = prettyGson.toJson(Common.questionList);
        Log.i("#########", "Imprimo json bonito");
        Log.i("#########", prettyJsonString);
    }


}
