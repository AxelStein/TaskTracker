package com.axel_stein.tasktracker.ui;

public class BaseViewAction {
    public static final int ACTION_FINISH = 0;
    public static final int ACTION_SHOW_MESSAGE = 1;

    private int mAction;
    private int mExtraInt;

    private BaseViewAction(int action) {
        mAction = action;
    }

    public BaseViewAction(int action, int extraInt) {
        mAction = action;
        mExtraInt = extraInt;
    }

    public int getId() {
        return mAction;
    }

    public int getIntExtra() {
        return mExtraInt;
    }

    public static BaseViewAction finish() {
        return new BaseViewAction(ACTION_FINISH);
    }

    public static BaseViewAction showMessage(int msg) {
        return new BaseViewAction(ACTION_SHOW_MESSAGE, msg);
    }

}
