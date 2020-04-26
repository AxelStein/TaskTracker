package com.axel_stein.tasktracker;

import org.junit.Test;

import static com.axel_stein.tasktracker.utils.ArgsUtil.checkRules;
import static com.axel_stein.tasktracker.utils.ArgsUtil.notEmptyString;
import static com.axel_stein.tasktracker.utils.ArgsUtil.notNull;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void t() {
        Object object = null;
        checkRules(
                notNull(object),
                notEmptyString(object.toString())
        );
    }

}