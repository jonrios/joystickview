package com.zerokol.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class VerticalJoystickView extends JoystickView {


	public VerticalJoystickView(Context context) {
		super(context);
	}

	public VerticalJoystickView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.JoystickView,	0, 0);

		try {
			mainCircleColor = a.getColor(R.styleable.JoystickView_mainCircleColor, Color.GRAY);
			buttonColor = a.getColor(R.styleable.JoystickView_buttonColor, Color.BLACK);
		} finally {
			a.recycle();
		}

		initJoystickView();
	}

	public VerticalJoystickView(Context context, AttributeSet attrs, int defaultStyle) {
		super(context, attrs, defaultStyle);
		initJoystickView();
	}

	protected void initJoystickView() {
		mainCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
		mainCircle.setColor(mainCircleColor);
		mainCircle.setStyle(Paint.Style.FILL_AND_STROKE);

		verticalLine = new Paint();
		verticalLine.setStrokeWidth(2);
		verticalLine.setColor(Color.BLACK);

		button = new Paint(Paint.ANTI_ALIAS_FLAG);
		button.setColor(buttonColor);
		button.setStyle(Paint.Style.FILL);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// setting the measured values to resize the view to a certain width and
		// height
		int d = Math.min(measure(widthMeasureSpec), measure(heightMeasureSpec));

		setMeasuredDimension(d, d);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		centerX = (getWidth()) / 2;
		centerY = (getHeight()) / 2;

		// painting the main circle
		canvas.drawCircle((int) centerX, (int) centerY, joystickRadius,
				mainCircle);
		// paint lines
		canvas.drawLine((float) centerX,(float) (centerY - joystickRadius),
				(float) centerX, (float) (centerY + joystickRadius),
				verticalLine);

		// painting the move button
		canvas.drawCircle(xPosition, yPosition, buttonRadius, button);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		xPosition = (int) getWidth() / 2;
		yPosition = (int) event.getY();
		double abs = Math.sqrt((xPosition - centerX) * (xPosition - centerX)
				+ (yPosition - centerY) * (yPosition - centerY));
		if (abs > joystickRadius) {
			xPosition = (int) ((xPosition - centerX) * joystickRadius / abs + centerX);
			yPosition = (int) ((yPosition - centerY) * joystickRadius / abs + centerY);
		}
		invalidate();
		if (event.getAction() == MotionEvent.ACTION_UP) {
			xPosition = (int) centerX;
			yPosition = (int) centerY;
			thread.interrupt();
			if (onJoystickMoveListener != null)
				onJoystickMoveListener.onValueChanged(getAngle(), getPower(),
						getDirection());
		}
		if (onJoystickMoveListener != null
				&& event.getAction() == MotionEvent.ACTION_DOWN) {
			if (thread != null && thread.isAlive()) {
				thread.interrupt();
			}
			thread = new Thread(this);
			thread.start();
			if (onJoystickMoveListener != null)
				onJoystickMoveListener.onValueChanged(getAngle(), getPower(),
						getDirection());
		}
		return true;
	}

	public void setOnJoystickMoveListener(OnJoystickMoveListener listener,
			long repeatInterval) {
		this.onJoystickMoveListener = listener;
		this.loopInterval = repeatInterval;
	}

}