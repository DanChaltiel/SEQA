
package com.dan.seqa.outils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;



public class GIFView extends View {

private Movie mMovie;
private long movieStart;

public GIFView(Context context, int gifID) {
    super(context);
    initializeView(gifID);
}

public GIFView(Context context, AttributeSet attrs, int gifID) {
    super(context, attrs);
    initializeView(gifID);
}

public GIFView(Context context, AttributeSet attrs, int defStyle, int gifID) {
    super(context, attrs, defStyle);
    initializeView(gifID);
}

private void initializeView(int gifID) {
    InputStream is = getContext().getResources().openRawResource(gifID);
    mMovie = Movie.decodeStream(is);
}

@Override
protected void onDraw(Canvas canvas) {
    canvas.drawColor(Color.TRANSPARENT);
    super.onDraw(canvas);
    long now = android.os.SystemClock.uptimeMillis();

    if (movieStart == 0) {
        movieStart = (int) now;
    }
    if (mMovie != null) {
        int relTime = (int) ((now - movieStart) % mMovie.duration());
        mMovie.setTime(relTime);
        mMovie.draw(canvas, getWidth() - mMovie.width(), getHeight()
                - mMovie.height());
        this.invalidate();
    }
}}