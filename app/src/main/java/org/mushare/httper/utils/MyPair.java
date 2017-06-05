package org.mushare.httper.utils;

import java.io.Serializable;

/**
 * Created by dklap on 6/5/2017.
 */

public class MyPair implements Serializable {
    private String first, second;

    MyPair() {
    }

    public MyPair(String first, String second) {
        this.first = first;
        this.second = second;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }
}
