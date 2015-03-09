The preview image is like that
![image](https://github.com/Sherchen/Borameter/blob/master/borameter.gif)

I hope you guy will like it, you will find the ui is not perfect, because all of ui is drawn by canvas,

<code>
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
</code>

If you want to make the ui much more good-looking, you can use your image instead.

If you have any questions, please send my email. Thank you very much.
