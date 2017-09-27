package com.berry.blue.reds.interfaces;

import com.berry.blue.reds.fires.Beans;

public interface ViewStartI extends ViewStandardI {
    void onWordObtained(Beans.Word word);
    void onValueError(String message);
}
