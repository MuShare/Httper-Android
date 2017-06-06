package org.mushare.httper.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.TextView;

import org.mushare.httper.R;
import org.mushare.httper.RequestSettingType;

/**
 * Created by dklap on 6/4/2017.
 */

public class MyStickyHeader extends ConstraintLayout {
    TextView title;
    ImageButton button;

    RequestSettingType type = RequestSettingType.header;

    public MyStickyHeader(Context context) {
        super(context);
        init();
    }

    public MyStickyHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyStickyHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        inflate(getContext(), R.layout.http_setting_title, this);
        title = (TextView) findViewById(R.id.textViewTitle);
        button = (ImageButton) findViewById(R.id.imageButtonAdd);
    }

    public void setButtonOnClickListener(OnClickListener onClickListener) {
        button.setOnClickListener(onClickListener);
    }

    public RequestSettingType getType() {
        return type;
    }

    public void setType(RequestSettingType type) {
        if (this.type == type || type == null) return;
        this.type = type;
        switch (type) {
            case header:
                title.setText(R.string.main_view_headers);
                button.setVisibility(VISIBLE);
                break;
            case parameter:
                title.setText(R.string.main_view_params);
                button.setVisibility(VISIBLE);
                break;
            case body:
                title.setText(R.string.main_view_body);
                button.setVisibility(INVISIBLE);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
