package org.crashball;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class CrashBallView extends View {

	private int mMode = READY;
	public static final int PAUSE = 0; // 一時停止中
	public static final int READY = 1; // スタート画面
	public static final int RUNNING = 2;// 実行中
	public static final int LOSE = 3; // ゲームオーバー
	public static final int CLEAR = 4; // クリア
	private static final int BRICK_ROW = 10;
	private static final int BRICK_COL = 10;
	// ステータスバーの高さ
	public static final int STATE = 50;
	private static int w = 320;
	private static int h = 480;

	// 最大リフレッシュレート
	private static final long DELAY_MILLIS = 1000 / 60;

	// 画面表示用のメッセージ
	private TextView mMessage;
	private TextView mPoint;
	private RefreshHandler mFieldHandler = new RefreshHandler();
	private Paint mPaint = new Paint();
	private Pad mPad;
	private ArrayList<Ball> mBalls = new ArrayList<Ball>();
	private Brick[][] mBricks = new Brick[BRICK_COL][BRICK_ROW];
	private int mBallsCount = 0;
	private int mStockBallCount = 0;
	private int mBricksCount = 0;

	// 一定時間待機後Updateを実行させる。 Updateは再度Sleepを呼ぶ
	class RefreshHandler extends Handler {
		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}

		@Override
		public void handleMessage(Message msg) {
			CrashBallView.this.update();
		}
	};

	// コンストラクタ
	public CrashBallView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(this.getClass().getSimpleName(), "constructor1 called");
		initialProcess();
	}

//	public CrashBallView(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//		Log.d(this.getClass().getSimpleName(), "constructor2 called");
//		initialProcess();
//	}

	private void initialProcess() {
		Log.d(this.getClass().getSimpleName(), "initialProcess called");
		setFocusable(true);
//		mMessage = (TextView) findViewById(R.id.message);
		Log.d(this.getClass().getSimpleName(), "w=" + w + " h=" + h);
	}

	// 新ゲームの作成
	private void newGame() {

		mBalls.clear();
		mBricksCount = 0;
		mStockBallCount = 5;
		mBallsCount = 0;
		for (int i = 0; i < BRICK_COL; i++) {
			for (int j = 0; j < BRICK_ROW; j++) {
				mBricksCount++;
				mBricks[i][j] = new BrickNormal(i, j);
			}
		}
		CrashBallView.this.invalidate();
	}

	private MotionEvent mTouchEvent;

	public boolean onTouchEvent(MotionEvent event) {
		mTouchEvent = event;
		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			switch (mMode) {
			case READY:
				setMode(RUNNING);
				addBall();
				break;
			case LOSE:
				setMode(READY);
				break;
			case CLEAR:
				setMode(READY);
				break;
			}
		}
		return true;
	}

	public void setMode(int newMode) {
		int oldMode = mMode;
		mMode = newMode;

		if (newMode == RUNNING) {
			if (oldMode == READY) {
				newGame();

				Resources resource = getContext().getResources();
				CharSequence newMessage = resource
						.getText(R.string.new_ball_help);
				mMessage.setText(newMessage);
				mMessage.setVisibility(View.VISIBLE);
			} else if (oldMode == PAUSE) {
				if (mBallsCount == 0) {
					Resources resource = getContext().getResources();
					CharSequence newMessage = resource
							.getText(R.string.new_ball_help);
					mMessage.setText(newMessage);
				} else {
					mMessage.setVisibility(View.INVISIBLE);
				}
			}
			if (oldMode != RUNNING) {
				update();
			}
			return;
		}

		CharSequence newMessage = "";
		Resources resource = getContext().getResources();
		switch (newMode) {
		case PAUSE:
			newMessage = resource.getText(R.string.pause_message);
			break;
		case READY:
			newMessage = resource.getText(R.string.ready_message);
			break;
		case LOSE:
			newMessage = resource.getText(R.string.game_over_message);
			break;
		case CLEAR:
			newMessage = resource.getText(R.string.game_clear_message);
			break;
		}
		Log.d(this.getClass().getSimpleName(), "newMessage=" + newMessage);
		mMessage.setText(newMessage);
		mMessage.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.d(this.getClass().getSimpleName(), "onSizeChanged called w=" + w + " h=" + h);
		mPad = new Pad(w, h);
		setMode(READY);
	}

	private boolean isBricksCrash(int xIndex, int yIndex) {
		if (yIndex >= BRICK_ROW || xIndex >= BRICK_COL) {
			return false;
		}
		if (mBricks[xIndex][yIndex] != null) {
			return true;
		}
		return false;
	}

	private void crashBrick(int xIndex, int yIndex) {
		if (yIndex >= BRICK_ROW || xIndex >= BRICK_COL
				|| mBricks[xIndex][yIndex] == null) {
			return;
		}
		CrashBallView.this.invalidate(mBricks[xIndex][yIndex].getRect());
		mBricks[xIndex][yIndex] = null;
		mBricksCount--;
		if (mBricksCount <= 0) {
			setMode(CLEAR);
		}

	}

	public void update() {
		if (mMode == RUNNING) {

			if (mTouchEvent != null) {
				CrashBallView.this.invalidate(mPad.getRect());
				mPad.onTouchEvent(mTouchEvent);
				mPad.update();
				CrashBallView.this.invalidate(mPad.getRect());
			}

			int xCrash;
			int yCrash;
			for (int i = 0; i < this.mBallsCount; i++) {
				xCrash = 0;
				yCrash = 0;
				Ball ball = this.mBalls.get(i);

				CrashBallView.this.invalidate(ball.getRect());
				ball.update();
				CrashBallView.this.invalidate(ball.getRect());

				int xIndex = (int) (ball.x / Brick.WIDE);
				int yIndex = (int) ((ball.y - STATE) / Brick.HEIGHT);
				int lxIndex = (int) (ball.getlx() / Brick.WIDE);
				int lyIndex = (int) ((ball.getly() - STATE) / Brick.HEIGHT);

				if (isBricksCrash(xIndex, yIndex)) {
					xCrash++;
					yCrash++;
				}
				if (isBricksCrash(lxIndex, yIndex)) {
					xCrash--;
					yCrash++;
				}
				if (isBricksCrash(xIndex, lyIndex)) {
					xCrash++;
					yCrash--;
				}
				if (isBricksCrash(lxIndex, lyIndex)) {
					xCrash--;
					yCrash--;
				}
				crashBrick(xIndex, yIndex);
				crashBrick(xIndex, lyIndex);
				crashBrick(lxIndex, yIndex);
				crashBrick(lxIndex, lyIndex);

				if (yCrash > 0) {
					ball.topCrash(yIndex);
				} else if (yCrash < 0) {
					ball.downCrash(lyIndex);
				}
				if (xCrash > 0) {
					ball.leftCrash(xIndex);
				} else if (xCrash < 0) {
					ball.rightCrash(lxIndex);
				}

				if (mPad.y <= ball.getly() && mPad.getly() >= ball.y
						&& mPad.x <= ball.getlx() && mPad.getlx() >= ball.x) {
					float newXSpeed;
					float newYSpeed;

					// newXSpeed = ball.xSpeed +(ball.getcx() - mPad.getcx())/5;
					// newYSpeed = - (ball.ySpeed - Math.abs(ball.getcx() -
					// mPad.getcx()) * 1.2f);
					newXSpeed = ball.xSpeed + (ball.getcx() - mPad.getlx()) / 5;
					newYSpeed = -(ball.ySpeed - Math.abs(ball.getcx()
							- mPad.getlx()) * 1.2f);
					if (newYSpeed > -10) {
						newYSpeed = -10;
					}
					ball.setXSpeed(newXSpeed);
					ball.setYSpeed(newYSpeed);

					if (ball.maxYSpeed < 15) {
						ball.maxYSpeed += 0.1f;
					}
				} else if (ball.y > h) {
					this.mBalls.remove(i);
					mBallsCount--;
					if (mBallsCount == 0) {

						if (mStockBallCount > 0) {
							Resources resource = getContext().getResources();
							CharSequence newMessage = resource
									.getText(R.string.new_ball_help);
							mMessage.setText(newMessage);
							mMessage.setVisibility(View.VISIBLE);

						} else {
							setMode(LOSE);
							return;
						}
					}
				}

			}
			mFieldHandler.sleep(DELAY_MILLIS);
		} else {
			CrashBallView.this.invalidate();
		}
	}

	@Override
	public void onDraw(Canvas canvas) {

		canvas.drawColor(Color.rgb(120, 140, 160));
		mPaint.setColor(Color.BLACK);
		Log.d(this.getClass().getSimpleName(), "onDraw w=" + w + " h=" + h);
		canvas.drawRect(0, STATE, w, h, mPaint);

		mPad.draw(canvas, mPaint);

		for (int i = 0; i < this.mBallsCount; i++) {
			this.mBalls.get(i).draw(canvas, mPaint);
		}

		for (int i = 0; i < BRICK_COL; i++) {
			for (int j = 0; j < BRICK_ROW; j++) {
				if (mBricks[i][j] != null) {
					mBricks[i][j].draw(canvas, mPaint);
				}
			}

		}

	}

	public void setTextView(TextView message) {
		this.mMessage = message;
	}

	public void setPointTextView(TextView point) {
		this.mPoint = point;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			if (mMode == READY) {
				setMode(RUNNING);
				update();
				return (true);
			}
			if (mMode == RUNNING) {
				setMode(PAUSE);
				update();
				return (true);
			}
			if (mMode == PAUSE) {
				setMode(RUNNING);
				update();
				return (true);
			}
			return (true);
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
				|| keyCode == KeyEvent.KEYCODE_SPACE) {
			addBall();
		}

		return super.onKeyDown(keyCode, event);

	}

	private void addBall() {
		Log.d(this.getClass().getSimpleName(), "addBall called");
		if (mMode == RUNNING && mStockBallCount > 0) {
			if (mBallsCount == 0) {
				mMessage.setVisibility(View.INVISIBLE);
			}
			mBalls.add(new Ball(w / 2, 300, -0.2f, -5, w, h));
			mBallsCount++;
			mStockBallCount--;
			Resources resource = getContext().getResources();
			CharSequence newMessage = resource
					.getText(R.string.stock_ball_count);
			mPoint.setText(newMessage + Integer.toString(mStockBallCount));
		}
	}

	// State load
	public void restoreState(Bundle icicle) {
		setMode(PAUSE);
		mMode = icicle.getInt("mode");
		mBalls = flaotsToBalls(icicle.getFloatArray("balls"));
	}

	private ArrayList<Ball> flaotsToBalls(float[] rawArray) {
		ArrayList<Ball> balls = new ArrayList<Ball>();

		int coordCount = rawArray.length;
		for (int index = 0; index < coordCount; index += 4) {
			Ball ball = new Ball(rawArray[index], rawArray[index + 1],
					rawArray[index + 2], rawArray[index + 3], w, h);
			balls.add(ball);
		}
		return balls;
	}

	// State save
	public Bundle saveState(Bundle icicle) {
		icicle.putInt("mode", mMode);
		icicle.putFloatArray("balls", ballsToFloats(mBalls));
		return icicle;

	}

	private float[] ballsToFloats(ArrayList<Ball> cvec) {
		int count = cvec.size();
		float[] rawArray = new float[count * 4];
		for (int index = 0; index < count; index++) {
			Ball setBall = (Ball) cvec.get(index);
			rawArray[4 * index] = setBall.x;
			rawArray[4 * index + 1] = setBall.y;
			rawArray[4 * index + 2] = setBall.xSpeed;
			rawArray[4 * index + 3] = setBall.ySpeed;
		}
		return rawArray;
	}
	

}
