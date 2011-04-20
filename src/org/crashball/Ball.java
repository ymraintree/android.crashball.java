package org.crashball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Ball implements ActiveObject {
	private int mScreenWide = 100;

	// Ball Size
	private static final int SIZE = 16;
	private static final int HALF_SIZE = SIZE / 2;
	private int mState = 50;

	// Ball Point
	public static final int LEFT_TOP = 0;
	public static final int RIGHT_TOP = 1;
	public static final int LEFT_DOWN = 2;
	public static final int RIGHT_DOWN = 3;

	// Ball Speed
	public float xSpeed;
	public float ySpeed;
	public float maxYSpeed = 8f;
	public final float maxXSpeed = 3f;
	// Ball related variables.
	public float x;
	public float y;

	public float g = 0.15f;

	public float getlx() {
		return x + SIZE;
	}

	public float getcx() {
		return x + HALF_SIZE;
	}

	public float getly() {
		return y + SIZE;
	}

	public Rect getRect() {
		return new Rect((int) this.x - 1, (int) this.y - 1,
				(int) this.getlx() + 1, (int) this.getly() + 1);

	}

	public Ball(float x, float y, float xSpeed, float ySpeed, int w, int h) {
		this.x = x;
		this.y = y;
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
		this.mScreenWide = w;
		this.mState = CrashBallView.STATE;
	}

	public void setXSpeed(float speed) {
		this.xSpeed = getNewSpeed(speed, maxXSpeed);
	}

	public void setYSpeed(float speed) {
		this.ySpeed = getNewSpeed(speed, maxYSpeed);
	}

	private float getNewSpeed(float newSpeed, float maxSpeed) {
		if (maxSpeed < Math.abs(newSpeed)) {
			if (newSpeed > 0) {
				return maxSpeed;
			} else {
				return -maxSpeed;
			}
		}
		return newSpeed;
	}

	private int getAfterCrashPoint(float speed, float position, int wall) {
		int intPosition = (int) position;
		int intSpeed = (int) speed;
		int newPoint = ((wall * 2) - intPosition - intSpeed);
		// 壁のめりこみ対策
		if ((speed > 0 && newPoint > wall) || (speed < 0 && newPoint < wall)) {
			// newPoint = wall;
		}
		return (newPoint);
	}

	public void update() {
		ySpeed += g;
		if (x + xSpeed <= 0) {
			x = getAfterCrashPoint(xSpeed, x, 0);
			xSpeed *= -1.01f;
			xSpeed = getNewSpeed(xSpeed, maxXSpeed);
		} else {
			float lx = getlx();
			if (lx + xSpeed >= mScreenWide) {
				x = getAfterCrashPoint(xSpeed, lx, mScreenWide) - SIZE;
				xSpeed *= -1.01f;
				xSpeed = getNewSpeed(xSpeed, maxXSpeed);
			} else {
				x += xSpeed;
			}
		}
		if (y + ySpeed <= mState) {
			y = getAfterCrashPoint(ySpeed, y, mState);

			ySpeed *= -0.9f;
			ySpeed = getNewSpeed(ySpeed, maxYSpeed);
		} else {
			y += ySpeed;
		}
	}

	public void draw(Canvas canvas, Paint paint) {
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		canvas.drawCircle(x + HALF_SIZE, y + HALF_SIZE, HALF_SIZE, paint);
	}

	public void topCrash(int index) {
		y += ySpeed;
		ySpeed = Math.abs(ySpeed);
	}

	public void downCrash(int index) {
		y += ySpeed;
		ySpeed = -Math.abs(ySpeed);
	}

	public void leftCrash(int index) {
		x += xSpeed;
		xSpeed = Math.abs(xSpeed);
	}

	public void rightCrash(int index) {
		x += xSpeed;
		xSpeed = -Math.abs(xSpeed);
	}
}
