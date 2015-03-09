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

import android.graphics.Paint;
import android.graphics.Rect;

public class BorameterUtil {
	private static final boolean DEBUG = BorameterScale.DEBUG;
	private static final String TAG = BorameterScale.TAG;
	private int m_HalfWidth;
	private int m_HalfHeight;
	private Paint m_CirclePaint;
	private int m_LongScaleRadius;
	
	public BorameterUtil(int halfWidth, int halfHeight, Paint circlePaint){
		m_HalfWidth = halfWidth;
		m_HalfHeight = halfHeight;
		m_CirclePaint = circlePaint;
		m_LongScaleRadius = m_HalfWidth - BorameterScale.SCALE_BIG_LENGHT - BorameterScale.SCALE_AND_TEXT_DISTANCE;
	}
	
	
	public void getXY(int angle ,int[] outXY, String text){
		if(outXY == null) return;
		if(outXY.length != 2) return;
		Rect rect = new Rect();
		getTextSize(text, m_CirclePaint, rect);
		int textWidth = rect.width();
		int textHeight = rect.height();
		int x = 0, y = 0;
		int fixedX = getXCoordination(angle);
		int fixedY = getYCoordination(angle);
//		if(angle < 90){
//			x = fixedX;
//			y = fixedY + textHeight / 2;
//		}else if(angle == 90){
//			x = fixedX - textWidth / 2;
//			y = fixedY + textHeight;
//		}else if(angle <= 180){
//			x = fixedX - textWidth;
//			y = fixedY + textHeight / 2;
//		}else if(angle < 270){
//			x = fixedX - textWidth;
//			y = fixedY - textHeight / 2;
//		}else if(angle == 270){
//			x = fixedX - textWidth / 2;
//			y = fixedY - textHeight;
//		}else {
//			x = fixedX;
//			y = fixedY - textHeight / 2;
//		}
		
		if(angle == 310 || angle == 350){
			x = fixedX;
			y = fixedY - textHeight / 2;
		}else if(angle == 30){
			x = fixedX;
			y = fixedY + textHeight / 2;
		}else if(angle == 70){
			x = fixedX - textWidth / 2;
			y = fixedY + textHeight ;
		}else if(angle == 110){
			x = fixedX - textWidth / 2;
			y = fixedY + textHeight;
		}else if(angle == 150){
			x = fixedX - textWidth;
			y = fixedY + textHeight / 2;
		}else if(angle == 190 || angle == 230){
			x = fixedX - textWidth;
			y = fixedY - textHeight / 2;
		}
		
		outXY[0] = x;
		outXY[1] = y;
	}
	
//	10-01 22:44:16.281: V/CircularContainer(3388): drawScaleText--angle:310
//	10-01 22:44:16.291: V/CircularContainer(3388): drawScaleText--angle:350
//	10-01 22:44:16.291: V/CircularContainer(3388): drawScaleText--angle:30
//	10-01 22:44:16.301: V/CircularContainer(3388): drawScaleText--angle:70
//	10-01 22:44:16.301: V/CircularContainer(3388): drawScaleText--angle:110
//	10-01 22:44:16.311: V/CircularContainer(3388): drawScaleText--angle:150
//	10-01 22:44:16.311: V/CircularContainer(3388): drawScaleText--angle:190
//	10-01 22:44:16.311: V/CircularContainer(3388): drawScaleText--angle:230

	
	private int getXCoordination(int angle){
		if(angle <= 90){
			return getXCoordinationBelow90(angle);
		}else if(angle <= 180){
			return 2 * m_HalfWidth - getXCoordinationBelow90(180 - angle);
		}else if(angle <= 270){
			return 2 * m_HalfWidth - getXCoordinationBelow90(angle - 180);
		}else {
			return getXCoordinationBelow90(360 - angle);
		}
	}
	
	private int getYCoordination(int angle){
		if(angle <= 90){
			return getYCoordinationBelow90(angle);
		}else if(angle <= 180){
			return getYCoordinationBelow90(180 - angle);
		}else if(angle <= 270){
			return 2 * m_HalfHeight - getYCoordinationBelow90(angle - 180);
		}else {
			return 2 * m_HalfHeight - getYCoordinationBelow90(360 - angle);
		}
	}
	
	private int getXCoordinationBelow90(int angle){
		return m_HalfWidth - getCos(angle, m_LongScaleRadius);
	}
	
	private int getYCoordinationBelow90(int angle){
		return  m_HalfHeight - getSin(angle, m_LongScaleRadius);
	}
	
	
	private int getSin(int angle, int radius){
		return (int) (Math.sin(Math.PI * angle / 180) * radius);
	}
	
	private int getCos(int angle, int radius){
		return (int) (Math.cos(Math.PI * angle / 180) * radius);
	}
	
	public void getTextSize(String text, Paint m_CirclePaint, Rect rect){
		m_CirclePaint.getTextBounds(text, 0, text.length(), rect);
	}
	
	public static void debug(String msg){
		if(DEBUG) android.util.Log.v(TAG, msg);
	}
	
}
