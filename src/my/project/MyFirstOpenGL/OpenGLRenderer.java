package my.project.MyFirstOpenGL;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

public class OpenGLRenderer implements Renderer
{
	private Context context;
	private float orientation = 0.0f;;
	
	final int MAX_TEXTURE = 20;
	private int[] textureID = new int[MAX_TEXTURE];
	

	//     UV Coordinates             
	//          _________ 
	//  v0(0,0) |         | v3(1,0) 
	//          |         |
	//          |         |
	//  v1(0,1) |_________| v2(1,1)
	//
	//
	//   plane vertices
	//     _________ 
	//  p0 |         | p3 
	//     |         |
	//     |         |
	//  p1 |_________| p2
	//

	float squVtx[] = {   
			-1.0f,  1.0f,  0.0f, //p0 Left-Top corner
            -1.0f, -1.0f,  0.0f, //p1 Left-bottom corner 
             1.0f, -1.0f,  0.0f, //p2 Right-bottom corner 
             1.0f,  1.0f,  0.0f };//p3 Right-top corner 

	// USE GL_TRIANGLE_FAN
	short squInx[] = { 	0, 1, 2, 3}; //0-1-2 first triangle
									 //0-2-3 second triangle	
	float squTex[] ={ 	0.0f, 0.0f,  //v0 Left-Top corner
            			0.0f, 1.0f,  //v1 Left-bottom corner 
            			1.0f, 1.0f,  //v2 Right-top corner
            			1.0f, 0.0f };//v3 Right-bottom corner 
	
	// Our UV texture buffer.
	private FloatBuffer mTextureBuffer;	
	private FloatBuffer mVerticesBuffer;
	private ShortBuffer mIndexBuffer;
	
	public OpenGLRenderer(Context context, FrameLayout mainLayout)
	{  
        this.context = context;
 		//
		// Create GLSurfaceView and set this class as the renderer.
		//
        GLSurfaceView glView = new GLSurfaceView(context);
        glView.setRenderer(this);  
 		
        //put to Main layout
        mainLayout.addView(glView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}
	
	@Override
	public void onDrawFrame(GL10 gl) 
	{
		// Clear the whole screen and depth.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		// Setting the GL_MODELVIEW matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);	
		// Load the Identity matrix
		gl.glLoadIdentity();
		
		// Move to Z = -10
		gl.glTranslatef(0, 0, -10f);
		// Rotate y-asix
		gl.glRotatef(orientation, 0, 1.0f, 0);
		
		//every time plus 1 defgee.
		//orientation += 1.0f;
		//if ( orientation >= 360 )
		//	orientation = 0.0f;
		orientation = 270.0f;
		
		// Draw the scene
		DrawTheScene(gl);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) 
	{
		// Set OpenGL viewport
		gl.glViewport(0, 0, width, height);
		
		// Setting the GL_PROJECTION matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// Load the Identity matrix
		gl.glLoadIdentity();
		
		float ratio = (float) width / height;
		// Set the fovy to 45 degree. near depth is 0.1f and far depth is 100.f.
		// And maintain the screen ratio.
		GLU.gluPerspective(gl, 45, ratio, 0.1f, 100.f);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig arg1) 
	{
		// Set the background to black
		gl.glClearColor(0, 0, 0, 0);
		
		//Load the texture
	    LoadTexture(gl, context);  
	}

	private void LoadTexture(GL10 gl, Context cont)
	{
		 gl.glTexParameterf(GL10.GL_TEXTURE_2D, 
		                       GL10.GL_TEXTURE_MAG_FILTER, 
		                       GL10.GL_LINEAR);
		 InputStream istream = cont.getResources().openRawResource(R.drawable.guam);
		 
		 Bitmap bitmap;
		 try{
			 bitmap = BitmapFactory.decodeStream(istream);
		 }
		 finally{
			 try{
				 istream.close();
			 }
			 catch(IOException e){}
		 } 
		 gl.glGenTextures(MAX_TEXTURE, textureID, 0);
		 gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID[0]);
		 GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, bitmap, 0 ); 
		 gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		 
		 bitmap.recycle();
		 
		 mTextureBuffer = getNativeFloatBuffer(squTex);
		 mVerticesBuffer = getNativeFloatBuffer(squVtx);
		 mIndexBuffer = getNativeShortBuffer(squInx);
	}	
	//
	// Draw the scene
	//
	private void DrawTheScene(GL10 gl)
	{ 
		gl.glEnable( GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);  
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glVertexPointer( 3, GL10.GL_FLOAT, 0, mVerticesBuffer);
		gl.glTexCoordPointer( 2, GL10.GL_FLOAT, 0, mTextureBuffer);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID[0]); 
		
		gl.glDrawElements(	GL10.GL_TRIANGLE_FAN,
							squInx.length, 
		                    GL10.GL_UNSIGNED_SHORT, 
		                    mIndexBuffer);
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}	
	
	private FloatBuffer getNativeFloatBuffer(float[] buffer)
	{
		ByteBuffer ibb = ByteBuffer.allocateDirect(buffer.length*4);
		ibb.order(ByteOrder.nativeOrder());
		FloatBuffer fbf = ibb.asFloatBuffer();

		fbf.put(buffer);
		fbf.position(0);
		return fbf;
	}	
	
	private ShortBuffer getNativeShortBuffer(short[] buffer)
	{
		ByteBuffer ibb = ByteBuffer.allocateDirect(buffer.length*2);
		ibb.order(ByteOrder.nativeOrder());
		ShortBuffer sbf = ibb.asShortBuffer();

		sbf.put(buffer);
		sbf.position(0);
		return sbf;
	}
	
}
