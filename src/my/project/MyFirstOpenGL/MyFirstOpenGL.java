package my.project.MyFirstOpenGL;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class MyFirstOpenGL extends Activity 
{
	//private FrameLayout mainLayout;
	//private OpenGLRenderer gl3DView = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        FrameLayout mainLayout = (FrameLayout) findViewById(R.id.frameLayout1);
        //
        // Create OpenGL surface and render
        // 
        OpenGLRenderer gl3DView = new OpenGLRenderer(this, mainLayout);
    }
}