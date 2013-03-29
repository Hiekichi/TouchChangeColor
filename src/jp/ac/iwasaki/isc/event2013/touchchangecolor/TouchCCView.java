package jp.ac.iwasaki.isc.event2013.touchchangecolor;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class TouchCCView extends SurfaceView implements SurfaceHolder.Callback {

	int mGameStatus; // ゲームの状態  （0:ゲーム開始前、1:プレイヤー0(1人目)の開始前、2:プレイヤー1(2人目)の開始前
	
	int mBan = 0; // 0:先攻  1:後攻
	int mChoiceColor; // RGBのどの1色以外を変化させるか。ランダムで選ばれる   0:R, 1:G, 2:B
	float mTargetFirstColor;  // 目標とする色の成分1つ目（赤か緑か青か）
	float mTargetSecondColor; // 目標とする色の成分2つ目（赤か緑か青か）
	int mTargetColor; // 目標とする色
	float[] mPlayerFirstColor  = new float[2]; // プレイヤーがぐりぐりして増えていく値
	float[] mPlayerSecondColor = new float[2]; // プレイヤーがぐりぐりして増えていく値
	int[] mPlayerColor = new int[2]; // プレイヤーが選んだ色
	float mTapPointY;
	
	
	private SurfaceHolder	mHolder;  // サーフェイスホルダー
	Paint mPaint = new Paint();  // ペイントインスタンス
	Random mRandom = new Random();
	
	public TouchCCView(Context context) {
		super(context);

		// サーフェイスホルダーの準備
		mHolder = this.getHolder();
		mHolder.addCallback(this);
		mHolder.setFixedSize(this.getWidth(), this.getHeight());

		// ゲーム開始
		initGame();
	}
	
	private void initGame() {
		mChoiceColor = mRandom.nextInt(3);
		mTargetFirstColor  = mRandom.nextInt(180) + 50;
		mTargetSecondColor = mRandom.nextInt(180) + 50;
		mTargetColor = makeColor( mChoiceColor, (int)mTargetFirstColor, (int)mTargetSecondColor );
		mPlayerFirstColor[0]  = mPlayerFirstColor[1]  = 255.0f;
		mPlayerSecondColor[0] = mPlayerSecondColor[1] = 255.0f;
		mPlayerColor[0] = mPlayerColor[1] = 0;
		mGameStatus = 0;
		printMessage("この色になるように画面を上下にスワイプしてください\nタッチで開始します");
	}
	
	private int makeColor(int choice, int value1, int value2) {
		int returnColor = 0;
		
		switch (choice) {
		case 0:
			returnColor = Color.rgb(0, value1, value2);
			break;
		case 1:
			returnColor = Color.rgb(value1, 0, value2);
			break;
		case 2:
			returnColor = Color.rgb(value1, value2, 0);
			break;
		}
		return returnColor;
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// ビュー作成時に行わせる処理
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder,
			                    int format, int w, int h) {
		// 画面サイズ変化時に行わせる処理

		// 再描画を指示
		this.repaint(holder);
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// ビュー終了時に行わせる処理
	}

	// 再描画を指示するときに明示的に呼び出すメソッド
	protected void repaint(SurfaceHolder holder) {
		// キャンバスをロック
		Canvas canvas = holder.lockCanvas();
		
		// 描画
		if (mGameStatus == 0) {
			mPaint.setColor( mTargetColor );
			canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);
		}
		else if (mGameStatus == 1 || mGameStatus == 3) {
			mPaint.setColor( Color.BLACK );
			canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);			
		}
		else if (mGameStatus == 2 || mGameStatus == 4) {
			mPlayerFirstColor[mBan] = (int)( 255 * (mTapPointY / canvas.getHeight()) );
			mPlayerSecondColor[mBan] = (int)( 255 * (mTapPointY / canvas.getWidth()) );
			mPlayerColor[mBan] = makeColor( mChoiceColor, (int)mPlayerFirstColor[mBan], (int)mPlayerSecondColor[mBan] );
			mPaint.setColor( mPlayerColor[mBan] );
			canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);
			//mPaint.setColor( Color.YELLOW );
			//canvas.drawText("" + mTapPointY, 100, 100, mPaint);
		}
		else if (mGameStatus == 5) {
			mPaint.setColor( mTargetColor );
			canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight() / 2, mPaint);
			mPaint.setColor( mPlayerColor[0] );
			canvas.drawRect(0, canvas.getHeight() / 2, canvas.getWidth() / 2, canvas.getHeight(), mPaint);
			mPaint.setColor( mPlayerColor[1] );
			canvas.drawRect(canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getWidth(), canvas.getHeight(), mPaint);
			//mPaint.setColor( Color.YELLOW );
			//canvas.drawText("" + mTargetColor, 300, 100, mPaint);
			//canvas.drawText("" + mPlayerColor[0], 100, 400, mPaint);
			//canvas.drawText("" + mPlayerColor[1], 400, 400, mPaint);
		}

		
		// キャンバスをアンロック
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// タッチされた時に行わせる処理
			//Toast.makeText(getContext(), "max:" + mTekazuMax + "\nima:" + mTekazuIma, Toast.LENGTH_LONG).show();
			if (mGameStatus == 0) {
				printMessage("1人目の番です");
				mGameStatus = 1;
				mBan = 0;
				repaint(getHolder()); // 再描画を指示
			}
			else if (mGameStatus == 2) {
				printMessage("2人目の番です");
				mGameStatus = 3;
				mBan = 1;
				repaint(getHolder()); // 再描画を指示
			}
			else if (mGameStatus == 4) {
				printMessage("結果発表\n1番目の人（左）と2番目の人（右）どちらが似た色でしたか？");
				mGameStatus = 5;
				repaint(getHolder()); // 再描画を指示
			}
			else if (mGameStatus == 5) {
				initGame();
				repaint(getHolder()); // 再描画を指示
			}
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (mGameStatus == 1) {
				mGameStatus = 2;
				mTapPointY = 0;
			}
			if (mGameStatus == 3) {
				mGameStatus = 4;
				mTapPointY = 0;
			}
		}
		else if(event.getAction() == MotionEvent.ACTION_MOVE) {
			if (mGameStatus == 2 || mGameStatus == 4) {
				mTapPointY = event.getY();
				repaint(getHolder()); // 再描画を指示
			}
		}

		return true;
	}

	private void printMessage(String message) {
		Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
	}
}