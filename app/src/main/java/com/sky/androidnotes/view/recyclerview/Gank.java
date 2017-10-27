package com.sky.androidnotes.view.recyclerview;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tonycheng on 2017/8/3.
 */

public class Gank implements Serializable{

    private boolean error;
    private List<Result> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
