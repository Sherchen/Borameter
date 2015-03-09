
/*******************************************************************************
 * Copyright 2015,2016 Sherchen Wu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.sherchen.Borameter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 
 * @author Sherchen
 *
 */
public class BorameterScale extends View {
	public static final boolean DEBUG = true;
	public static final String TAG = "BorameterScale";
    private static final int START_ANGLE = -70;
	
	private static final int ANGLE_MAX = 320;
	private static final int ANGLE_ONE_SCALE = 2;
	private static final int ANGLE_SCALE_NUMBERS = ANGLE_MAX / ANGLE_ONE_SCALE;
	
	public static final int SCALE_BIG_LENGHT = 16;
	public static final int SCALE_SHORT_LENGHT = 8;
	public static final int SCALE_AND_TEXT_DISTANCE = 8;
	
	public static final int CENTER_RING_OUTTER_RADIUS = 10;
	public static final int CENTER_RING_INNER_RADIUS = 5;
	
	public static final float PAINT_BIG_STROKE_WIDTH = 4;
	public static final float PAINT_SMALL_STROKE_WIDTH = 1.1f;
	
	private static final int MODEL_MIDDLE = 0;
	private static final int MODEL_FIRST = 1;
	private static final int MODEL_END = 2;
	
	private int m_CurrentModel = MODEL_MIDDLE;
	
	public static final int SCALE_CIRCULAR_COLOR = 0xFFFFB90F;
	
	private int m_HalfViewWidth;
	private int m_HafViewHeight;
	private Paint m_BigPaint;
	private Paint m_SmallPaint;
	private BorameterUtil m_CircularUtil;
	private int m_Min;
	private int m_Max;
	private boolean m_NeedRedraw;
	
	private List<BorameterCursor> m_CursorList;
	
	private RedrawAnimationListener m_AnimationListener;
	
	public BorameterScale(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}
	
	private void initPaint(){
		int m_CircleColor = SCALE_CIRCULAR_COLOR;
		m_BigPaint = new Paint();
		m_BigPaint.setAntiAlias(true);
		m_BigPaint.setStyle(Paint.Style.STROKE);
		m_BigPaint.setColor(m_CircleColor);
		m_BigPaint.setStrokeWidth(PAINT_BIG_STROKE_WIDTH);
		
		m_SmallPaint = new Paint();
		m_SmallPaint.setAntiAlias(true);
		m_SmallPaint.setStyle(Paint.Style.STROKE);
		m_SmallPaint.setColor(m_CircleColor);
		m_SmallPaint.setStrokeWidth(PAINT_SMALL_STROKE_WIDTH);
	}
	
	
	public void addCursor(BorameterCursor pinch){
		if(m_CursorList == null){
			m_CursorList = new ArrayList<BorameterCursor>();
		}
		m_CursorList.add(pinch);
	}
	
	public void setOnRedrawAnimationListener(RedrawAnimationListener listener){
		m_AnimationListener = listener;
	}
	
	public boolean computeLimitedSize(int current){
		boolean isSideOfLimited = insideOfLimited(current);
		if(!isSideOfLimited){
			final int oldMin = m_Min;
			if(m_CurrentModel == MODEL_FIRST){
				m_Min = current - 10;
				m_Max = current + ANGLE_SCALE_NUMBERS - 10;
			}else if(m_CurrentModel == MODEL_MIDDLE){
				m_Min = current - ANGLE_SCALE_NUMBERS / 2;
				m_Max = current + ANGLE_SCALE_NUMBERS / 2;
			}else if(m_CurrentModel == MODEL_END){
				m_Min = current - ANGLE_SCALE_NUMBERS + 10;
				m_Max = current + 10;
			}else{
				throw new RuntimeException("No such model");
			}
			doActionAfterComputeLimitedSize();
			if(m_AnimationListener != null){
				if(oldMin < m_Min){
					m_AnimationListener.onClockwiseAnimation();
				}else{
					m_AnimationListener.onCounterClockwiseAnimation();
				}
			}
		}
		int angle = START_ANGLE + (current - m_Min) * ANGLE_ONE_SCALE;
		togglePinch(angle, current);
		return isSideOfLimited;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		int m_Width = getWidth();
		int m_Height = getHeight();
		m_HalfViewWidth = m_Width / 2;
		m_HafViewHeight = m_Height / 2;
		if(m_HalfViewWidth != 0 && m_HafViewHeight != 0 && m_BigPaint != null){
			m_CircularUtil = new BorameterUtil(m_HalfViewWidth, m_HafViewHeight, m_BigPaint);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(m_NeedRedraw){
			if(DEBUG)Log.v(TAG,"onDraw--real");
			drawCircle(canvas);
			drawScale(canvas);
			drawCenter(canvas);
//			m_NeedDraw = false;
		}
	}
	
	private void drawCircle(Canvas canvas) {
		canvas.save();
		canvas.drawCircle(m_HalfViewWidth, m_HafViewHeight, m_HalfViewWidth, m_BigPaint);
		canvas.restore();
	}

	private void drawScale(Canvas canvas) {
		boolean drawText;
		boolean drawLongScale;
		for(int i=0;i<ANGLE_SCALE_NUMBERS/2;i++){
			drawText = false;
			drawLongScale = false;
			if(i%5==0){
				drawLongScale = true;
				if(i/5%2 == 1){
					drawText = true;
				}
			}
			drawScaleByAngle(canvas, START_ANGLE + i * ANGLE_ONE_SCALE * 2, m_Min + i * 2, drawText, drawLongScale);
		}
	}
	
	private void drawScaleByAngle(Canvas canvas, final int angle, int value, boolean drawText, boolean drawLongScale){
		canvas.save();
		float[] lines;
		int drawAngle = angle;
		if(angle < 0){
			drawAngle += 360;
		}
		Paint paint;
		if(drawLongScale){
			lines = new float[]{0, m_HafViewHeight, SCALE_BIG_LENGHT, m_HafViewHeight};
			paint = m_BigPaint;
		}else{
			lines = new float[]{0, m_HafViewHeight, SCALE_SHORT_LENGHT, m_HafViewHeight};
			paint = m_SmallPaint;
		}
		if(drawText){
			drawScaleText(canvas, drawAngle, String.valueOf(value));
		}
		canvas.rotate(drawAngle, m_HalfViewWidth, m_HafViewHeight);
		canvas.drawLines(lines, paint);
		canvas.restore();
	}
	
	private void drawCenter(Canvas canvas){
		canvas.save();
		canvas.drawCircle(m_HalfViewWidth, m_HafViewHeight, CENTER_RING_INNER_RADIUS, m_SmallPaint);
		canvas.drawCircle(m_HalfViewWidth, m_HafViewHeight, CENTER_RING_OUTTER_RADIUS, m_SmallPaint);
		canvas.restore();
	}

	private void drawScaleText(Canvas canvas, int angle ,String text){
		if(DEBUG)Log.v(TAG,"drawScaleText--angle:"+angle);
		canvas.save();
		int[] output = new int[2];
		m_CircularUtil.getXY(angle, output, text);
		canvas.drawText(text, output[0], output[1], m_SmallPaint);
		canvas.restore();
	}
	
	private boolean insideOfLimited(int current){
		if(DEBUG)Log.v(TAG,"current:"+current+" m_Min:"+m_Min+" m_Max:"+m_Max);
		return current >= m_Min && current <= m_Max;
	}
	
	private void doActionAfterComputeLimitedSize(){
//		Log.v(TAG, "doActionAfterComputeLimitedSize");
		m_NeedRedraw = true;
		invalidate();
	}
	
	private void togglePinch(int angle, int value){
		for(BorameterCursor circularView : m_CursorList){
			circularView.setAngle(angle, value);
		}
	}
}
