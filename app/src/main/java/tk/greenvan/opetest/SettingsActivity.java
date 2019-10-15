package tk.greenvan.opetest;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // When the home button is pressed, take the user back to the MainActivity
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Preference num_questions_preference = findPreference(getString(R.string.quicktest_number_of_questions_key));
            Preference timeout_preference = findPreference(getString(R.string.quicktest_timeout_key));
            num_questions_preference.setOnPreferenceChangeListener(this);
            timeout_preference.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {


            Toast error = Toast.makeText(getContext(), getString(R.string.invalid_value), Toast.LENGTH_SHORT);

            String num_questions_key = getString(R.string.quicktest_number_of_questions_key);
            String timeout_key = getString(R.string.quicktest_timeout_key);

            String newValueString = (String) newValue;

            if (preference.getKey().equals(num_questions_key)) {
                try {
                    error.setText(R.string.invalid_num_questions);
                    int numquestions = Integer.parseInt(newValueString);
                    if (numquestions > 120 || numquestions <= 0) {
                        error.show();
                        return false;
                    }
                } catch (NumberFormatException nfe) {
                    error.show();
                    return false;
                }
            } else if (preference.getKey().equals(timeout_key)) {
                try {
                    error.setText(R.string.invalid_timeout);
                    float size = Float.parseFloat(newValueString);
                    if (size > 60 || size <= 0) {
                        error.show();
                        return false;
                    }
                } catch (NumberFormatException nfe) {
                    error.show();
                    return false;
                }
            }
            return true;
        }


    }
}