package com.example.bookmytiffin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class speciallistview extends ListView {

    public speciallistview(Context context) {
        super(context);
    }

    public speciallistview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public speciallistview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        @SuppressLint("Range") int height = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, height);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = height;
    }
}
