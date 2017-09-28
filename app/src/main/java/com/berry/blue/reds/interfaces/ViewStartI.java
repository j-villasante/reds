package com.berry.blue.reds.interfaces;

public interface ViewStartI extends ViewStandardI {
    void onWordObtained(String word);
    void startSuccessAnimation();
    void startErrorAnimation();
    void setIsWordLoading(boolean value);
    void onValueError(String message);
}
