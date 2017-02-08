package com.xiangzi.qq_list;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.List;

/**
 * Created by 郑加波 on 2017/2/7.
 *
 * @description :
 */

public class BallGroup extends FrameLayout {


    public BallGroup(Context Activity) {
        super(Activity);
    }

    public BallGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BallGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initMovables(List<Point> pointList) {
        BallView view = new BallView(getContext());
        view.setLayoutParams(new LayoutParams(80, 80));
        view.setImageResource(R.drawable.huaji);
        addView(view);

        view.initMovables(pointList);
    }

}
