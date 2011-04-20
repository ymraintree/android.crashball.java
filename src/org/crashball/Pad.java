package org.crashball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

public class Pad implements ActiveObject {
	private int mScreenWide = 100;

	// Pad Size
	public static final int HEIGHT = 10;
	public static final int WIDE = 80;
	public static final int WIDE_BLOCK = WIDE / 5;
	public static final int HALF_WIDE = WIDE / 2;

	// Pad related variables.
	public float x;
	public float y;

	private float mTouchX = 100;

	public float getlx() {
		return (x + WIDE);
	}

	public float getly() {
		return (y + HEIGHT);
	}

	public int colArea(float pointX) {
		if (pointX < x) {
			return 0;
		} else {
			return (int) (pointX - x) / WIDE_BLOCK + 1;
		}
	}

	public void onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			mTouchX = event.getX();
		}
	}

	public Pad(int w, int h) {
		mScreenWide = w;
		y = 400;
	}

	public void update() {
		x = mTouchX - HALF_WIDE;
		if (x < 0) {
			x = 0;
		} else if (getlx() > mScreenWide) {
			x = mScreenWide - WIDE;
		}
	}

	public void setSize(int w, int h) {
		mScreenWide = w;
	}

	public void draw(Canvas canvas, Paint paint) {
		paint.setColor(Color.YELLOW);
		canvas.drawRect(x, y, x + WIDE, y + HEIGHT, paint);
	}

	@Override
	public Rect getRect() {
		return (new Rect((int)x, (int)y, (int)x + WIDE, (int)y + HEIGHT));
	}

}
