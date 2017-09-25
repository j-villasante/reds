package com.berry.blue.reds.main;

import com.berry.blue.reds.interfaces.ViewStandardI;

public interface ViewStartI extends ViewStandardI {
    void onWordObtained(Word word);
    void onValueError(String message);
}
