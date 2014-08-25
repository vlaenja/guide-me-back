package be.jvvlaend.utils.android.utils;

import android.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class MyGestureFragment extends Fragment implements OnTouchListener {
	private float x1, x2, y1, y2;

	@Override
	public boolean onTouch(View view, MotionEvent touchevent) {
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
					swipe(view, Swipe.RIGHT);
				}
			}

			// if right to left sweep event on screen
			if (x1 > x2)
			{
				if (horizontalSwipe) {
					swipe(view, Swipe.LEFT);
				}
			}

			// if UP to Down sweep event on screen
			if (y1 < y2)
			{
				if (!horizontalSwipe) {
					swipe(view, Swipe.DOWN);
				}
			}

			// if Down to UP sweep event on screen
			if (y1 > y2)
			{
				if (!horizontalSwipe) {
					swipe(view, Swipe.UP);
				}
			}
			break;
		}
		}
		return false;
	}

	public abstract void swipe(View view, Swipe direction);
}
