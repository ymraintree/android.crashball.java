package org.crashball;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.TextView;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	private static String ICICLE_KEY = "CRASH_BALL";
	private CrashBallView mView;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main);
        Display display = getWindowManager().getDefaultDisplay();
        Log.d(this.getClass().getSimpleName(), "onCreate width=" + display.getWidth() + " height=" + display.getHeight());
		mView = (CrashBallView) findViewById(R.id.ball);
		mView.setTextView((TextView) findViewById(R.id.message));
		mView.setPointTextView((TextView) findViewById(R.id.stock_balls));

		if (icicle == null) {
			mView.setMode(CrashBallView.READY);
		} else {
			Bundle map = icicle.getBundle(ICICLE_KEY);
			if (null != map) {
				mView.restoreState(map);
			} else {
				mView.setMode(CrashBallView.READY);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mView.setMode(CrashBallView.PAUSE);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		return (mView.onTouchEvent(event));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Store the game state
		Bundle icicle = new Bundle();
		outState.putBundle(ICICLE_KEY, mView.saveState(icicle));
	}
}