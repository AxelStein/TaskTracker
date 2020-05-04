package com.axel_stein.tasktracker.ui;

public class BaseViewState<T> {
    public static final int STATE_LOADING = 0;
    public static final int STATE_SUCCESS = 1;
    public static final int STATE_ERROR = 2;

    protected int mState;
    protected T mData;
    protected Throwable mError;

    public BaseViewState(int state, T data, Throwable error) {
        mState = state;
        mData = data;
        mError = error;
    }

    public T getData() {
        return mData;
    }

    public void setData(T data) {
        this.mData = data;
    }

    public Throwable getError() {
        return mError;
    }

    public void setError(Throwable error) {
        this.mError = error;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        this.mState = state;
    }

}
