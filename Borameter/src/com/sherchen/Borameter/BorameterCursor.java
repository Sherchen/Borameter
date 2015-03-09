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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class BorameterCursor extends View {
	private static final int CURSOR_PINCH_LENGHT = 80;
	private static final int CURSOR_ARROW_LENGHT = 5;
	
	private static final int CURSOR_PINCH_COLOR = 0xFFFF0000;
	private static final int CURSOR_PINCH_SIZE = 2;
	
	private int m_Angle;
	private int m_Width;
	private int m_Height;
	
	private Paint m_CursorPinchPaint;
	
	private boolean m_NeedRedraw;
	
	private Path m_CursorPath;
	
	private int m_CursorVertexX;
	
	public BorameterCursor(Context context, AttributeSet attrs) {
		super(context, attrs);
		m_CursorPinchPaint = new Paint();
		m_CursorPinchPaint.setAntiAlias(true);
		m_CursorPinchPaint.setStyle(Paint.Style.STROKE);
		m_CursorPinchPaint.setColor(CURSOR_PINCH_COLOR);
		m_CursorPinchPaint.setStrokeWidth(CURSOR_PINCH_SIZE);
		
		
		m_CursorPath = new Path();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		m_Width = getWidth();
		m_Height = getHeight();
		m_CursorVertexX = m_Width / 2 - CURSOR_PINCH_LENGHT;
	}
	
	public void setAngle(int angle, int value){
		m_NeedRedraw = true;
		m_Angle = angle;
		if(m_Notify != null){
			m_Notify.onNotify(value);
		}
		invalidate();
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		if(m_NeedRedraw){
			BorameterUtil.debug("onDraw method is called");
			drawCursor(canvas);
		}
	}

	private void drawCursor(Canvas canvas){
		canvas.save();
		float x = m_Width/2;
		float y = m_Height/2;
		canvas.rotate(m_Angle, x, y);
		float[] lines = new float[]{
				m_CursorVertexX,                y,              x,                      y,
				m_CursorVertexX + CURSOR_ARROW_LENGHT, y-CURSOR_ARROW_LENGHT, m_CursorVertexX,                y,
				m_CursorVertexX,                y,              m_CursorVertexX + CURSOR_ARROW_LENGHT, y + CURSOR_ARROW_LENGHT
		};
		canvas.drawLines(lines, m_CursorPinchPaint);
		
//		m_CursorPath.reset();
//		m_CursorPath.moveTo(START_X, y);
//		m_CursorPath.lineTo(x, y);
//		m_CursorPath.moveTo(START_X + ARROW_LENGHT, y-ARROW_LENGHT);
//		m_CursorPath.lineTo(START_X, y);
//		m_CursorPath.moveTo(START_X, y);
//		m_CursorPath.lineTo(START_X + ARROW_LENGHT, y + ARROW_LENGHT);
//		canvas.drawPath(m_CursorPath, m_PinchPaint);
		
		canvas.restore();
	}
	 
	
	private BorameterNotify m_Notify;
	
	public void setOnBorameterListener(BorameterNotify notify){
		m_Notify = notify;
	}
}
