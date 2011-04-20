package org.crashball;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public interface ActiveObject {
	void update();

	void draw(Canvas canvas, Paint paint);

	Rect getRect();
}
