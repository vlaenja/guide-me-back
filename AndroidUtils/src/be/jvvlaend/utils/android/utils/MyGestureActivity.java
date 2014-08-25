package be.jvvlaend.utils.android.utils;

import android.view.MotionEvent;

public abstract class MyGestureActivity extends MyActivity {
	private float x1, x2, y1, y2;

	@Override
	public boolean onTouchEvent(MotionEvent touchevent) {
		switch (touchevent.getAction())
		{
		// when user first touches the screen we get x and y coordinate
		case MotionEvent.ACTION_DOWN: {
			x1 = touchevent.getX();
			y1 = touchevent.getY();
			break;
		}
		case MotionEvent.ACTION_UP: {
			x2 = touchevent.getX();
			y2 = touchevent.getY();
			boolean horizontalSwipe = (Math.abs(x1 - x2) > Math.abs(y1 - y2));

			// if left to right sweep event on screen
			if (x1 < x2)
			{
				if (horizontalSwipe) {
					swipe(Swipe.RIGHT);
					return true;
				}
			}

			// if right to left sweep event on screen
			if (x1 > x2)
			{
				if (horizontalSwipe) {
					swipe(Swipe.LEFT);
					return true;
				}
			}

			// if UP to Down sweep event on screen
			if (y1 < y2)
			{
				if (!horizontalSwipe) {
					swipe(Swipe.DOWN);
					return true;
				}
			}

			// if Down to UP sweep event on screen
			if (y1 > y2)
			{
				if (!horizontalSwipe) {
					swipe(Swipe.UP);
					return true;
				}
			}
			break;
		}
		}
		return super.onTouchEvent(touchevent);
	}

	public abstract void swipe(Swipe direction);
}
