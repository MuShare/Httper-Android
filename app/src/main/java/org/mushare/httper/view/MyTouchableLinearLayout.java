package org.mushare.httper.view;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by dklap on 5/5/2017.
 */

public class MyTouchableLinearLayout extends LinearLayout {
    private boolean touchable = true;

    public MyTouchableLinearLayout(Context context) {
        super(context);
    }

    public MyTouchableLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTouchableLinearLayout(Context context, @Nullable AttributeSet attrs, int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void touchable(boolean touchable) {
        this.touchable = touchable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !touchable || super.onInterceptTouchEvent(ev);
    }
}
