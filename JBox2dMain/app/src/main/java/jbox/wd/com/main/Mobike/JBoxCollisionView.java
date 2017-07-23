package jbox.wd.com.main.Mobike;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class JBoxCollisionView extends FrameLayout {
    private JBoxCollisionImpl jboxImpl;

    public JBoxCollisionView(Context context) {
        this(context, null);
    }

    public JBoxCollisionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JBoxCollisionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        jboxImpl = new JBoxCollisionImpl(this);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        jboxImpl.onSizeChanged(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        jboxImpl.onLayout(changed);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        jboxImpl.onDraw();
    }

    public void onSensorChanged(float x, float y) {
        jboxImpl.onSensorChanged(x,y);
    }

    public void onRandomChanged(){
        jboxImpl.onRandomChanged();
    }
}
