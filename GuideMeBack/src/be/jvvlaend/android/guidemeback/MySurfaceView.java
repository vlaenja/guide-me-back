package be.jvvlaend.android.guidemeback;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MySurfaceView extends GLSurfaceView {

	public MySurfaceView(Context context) {
		super(context);
		setRenderer(new MyRenderer());
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

}
