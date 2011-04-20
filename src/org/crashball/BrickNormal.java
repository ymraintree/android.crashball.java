package org.crashball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class BrickNormal extends Brick {
	public BrickNormal(int x, int y) {
		super(x, y);
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		paint.setColor(Color.RED);
		super.draw(canvas, paint);
	}

}
