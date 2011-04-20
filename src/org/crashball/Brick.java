package org.crashball;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

abstract class Brick implements ActiveObject {

	public int x;
	public int y;
	public int lx;
	public int ly;

	public Brick(int x, int y) {
		this.x = x * WIDE;
		this.y = y * HEIGHT + CrashBallView.STATE;
		this.ly = this.y + HEIGHT;
		this.lx = this.x + WIDE;
		Log.d(this.getClass().getSimpleName(), "Brick(" + x + ", " + y + ") x=" + this.x + " y=" + this.y + " lx=" + lx + " ly=" + ly);
	}

	// Pad Size
	public static final int HEIGHT = 20;
	public static final int WIDE = 32;

	public Rect getRect() {
		return (new Rect(x, y, lx, ly));
	}

	public void update() {

	}

	public void draw(Canvas canvas, Paint paint) {
		canvas.drawRect(x, y, lx - 1, ly - 1, paint);
	}

	public boolean crash(Ball ball, int crashPoint) {
		return true;
	}

}
