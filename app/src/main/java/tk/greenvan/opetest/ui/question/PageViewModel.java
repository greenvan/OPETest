package tk.greenvan.opetest.ui.question;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.TreeMap;

import tk.greenvan.opetest.db.Common;
import tk.greenvan.opetest.model.Option;

public class PageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            return Common.questionList.get(input).getText();
            //return "Hello world from section: " + input;
        }
    });

    private LiveData<String> mClue = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            return Common.questionList.get(input).getClue();
        }
    });

    //TODO Future line, all this mOption fields will be deleted after implementing a recyclerview
    private LiveData<Option> mOptionA = Transformations.map(mIndex, new Function<Integer, Option>() {
        @Override
        public Option apply(Integer input) {
            return Common.questionList.get(input).getOptionList().get("A");
        }
    });

    private LiveData<Option> mOptionB = Transformations.map(mIndex, new Function<Integer, Option>() {
        @Override
        public Option apply(Integer input) {
            return Common.questionList.get(input).getOptionList().get("B");
        }
    });

    private LiveData<Option> mOptionC = Transformations.map(mIndex, new Function<Integer, Option>() {
        @Override
        public Option apply(Integer input) {
            return Common.questionList.get(input).getOptionList().get("C");
        }
    });

    private LiveData<Option> mOptionD = Transformations.map(mIndex, new Function<Integer, Option>() {
        @Override
        public Option apply(Integer input) {
            return Common.questionList.get(input).getOptionList().get("D");
        }
    });

    private LiveData<Option> mOptionE = Transformations.map(mIndex, new Function<Integer, Option>() {
        @Override
        public Option apply(Integer input) {
            return Common.questionList.get(input).getOptionList().get("E");
        }
    });


    private LiveData<TreeMap> mOptions = Transformations.map(mIndex, new Function<Integer, TreeMap>() {
        @Override
        public TreeMap<String, Option> apply(Integer input) {
            return Common.questionList.get(input).getOptionList();
        }
    });

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<String> getClue() {
        return mClue;
    }
    public LiveData<TreeMap> getOptions() {
        return mOptions;
    }

    public MutableLiveData<Integer> getIndex() {
        return mIndex;
    }

    public LiveData<Option> getOptionA() {
        return mOptionA;
    }
    public LiveData<Option> getOptionB() {
        return mOptionB;
    }
    public LiveData<Option> getOptionC() {
        return mOptionC;
    }
    public LiveData<Option> getOptionD() {
        return mOptionD;
    }
    public LiveData<Option> getOptionE() {
        return mOptionE;
    }

}