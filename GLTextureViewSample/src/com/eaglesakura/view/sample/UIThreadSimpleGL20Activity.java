package com.eaglesakura.view.sample;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.eaglesakura.view.GLTextureView;
import com.eaglesakura.view.GLTextureView.GLESVersion;
import com.eaglesakura.view.GLTextureView.Renderer;
import com.eaglesakura.view.GLTextureView.RenderingThreadType;

public class UIThreadSimpleGL20Activity extends Activity implements Renderer {
    static final String TAG = UIThreadSimpleGL20Activity.class.getSimpleName();

    GLTextureView glTextureView;

    Handler uiHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glTextureView = new GLTextureView(this);

        // Setup GLTextureView
        {
            glTextureView.setVersion(GLESVersion.OpenGLES20); // set OpenGL Version
            //            glTextureView.setSurfaceSpec(SurfaceColorSpec.RGBA8, true, false); // Default RGBA8 depth(true) stencil(false)
            glTextureView.setRenderingThreadType(RenderingThreadType.RequestThread); // Default BackgroundThread
            glTextureView.setRenderer(this);
        }
        setContentView(glTextureView);
    }

    @Override
    protected void onPause() {
        glTextureView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glTextureView.onResume();

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated");

        // rendering loop on UI thread
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                glTextureView.requestRender();
                uiHandler.postDelayed(this, 100);
            }
        });
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, String.format("onSurfaceChanged(%d x %d)", width, height));
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0, (float) Math.random(), (float) Math.random(), 1.0f);
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void onSurfaceDestroyed(GL10 gl) {
        Log.d(TAG, String.format("onSurfaceDestroyed"));
    }

}
