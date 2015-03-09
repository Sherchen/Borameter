package com.sherchen.Borameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private BorameterCursor m_Circular;
	private Random random = new Random();
	private CountDownTimer m_CountDownTimer;
	
	private BorameterScale m_Container;
	
	private Handler m_Handler = null;
	
	private Animation m_Clockwise;
	private Animation m_CounterClockwise;
	
	private Button m_BtnStart;
	private Button m_BtnStop;
	private TextView m_TvDisplay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setViews();
		setValues();
		setListeners();
	}
	
	private void setViews() {
		m_Circular = (BorameterCursor) findViewById(R.id.cvTest);
		m_Container = (BorameterScale) findViewById(R.id.ccContainer);
		m_BtnStop = (Button) findViewById(R.id.btn_stop);
		m_BtnStart = (Button) findViewById(R.id.btn_start);
		m_TvDisplay = (TextView) findViewById(R.id.tv_display_current);
	}
	
	private void setValues() {
		init();
		m_Container.addCursor(m_Circular);
		m_Container.computeLimitedSize(m_List.get(80));
	}

	private void setListeners() {
		m_BtnStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				m_BtnStart.setEnabled(false);
				m_BtnStop.setEnabled(true);
				simulateStart();
			}
		});
		
		m_BtnStop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				m_BtnStart.setEnabled(true);
				m_BtnStop.setEnabled(false);
				simulateStop();
			}
		});
		
		m_Container.setOnRedrawAnimationListener(new RedrawAnimationListener() {
			
			@Override
			public void onCounterClockwiseAnimation() {
				m_Container.startAnimation(m_CounterClockwise);
			}
			
			@Override
			public void onClockwiseAnimation() {
				m_Container.startAnimation(m_Clockwise);
			}
		});
		
		m_Circular.setOnBorameterListener(new BorameterNotify() {
			
			@Override
			public void onNotify(int value) {
				m_TvDisplay.setText("The current value is " + value);
			}
		});
	}
	
	private void simulateStart(){
		m_CountDownTimer = new CountDownTimer(8000, 500) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				Message msg = m_Handler.obtainMessage(0);
				msg.sendToTarget();
			}
			
			@Override
			public void onFinish() {
				Toast.makeText(MainActivity.this, "The countdown is finished", Toast.LENGTH_SHORT).show();
				m_BtnStart.setEnabled(true);
				m_BtnStop.setEnabled(false);
			}
		};
		m_CountDownTimer.start();
	}
	
	private void simulateStop(){
		if(m_CountDownTimer != null){
			m_CountDownTimer.cancel();
		}
	}
	
	private List<Integer> m_List;
	private List<Integer> m_mList;
	
	private void init(){
		initHandler();
		
		m_Clockwise = AnimationUtils.loadAnimation(this, R.anim.clockwise);
		m_CounterClockwise = AnimationUtils.loadAnimation(this, R.anim.counterclockwise);
		m_List = new ArrayList<Integer>();
		for(int i=0;i<160;i++){
			m_List.add(920 + i);
		}
		m_mList = new ArrayList<Integer>();
		for(int i=0;i<160;i++){
			m_mList.add(1090 + i);
		}
	}
	
	private void initHandler(){
		m_Handler = new Handler(getMainLooper()){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					m_Container.computeLimitedSize(m_List.get(random.nextInt(160)));
					break;
				
				case 1:
					m_Container.computeLimitedSize(m_mList.get(random.nextInt(160)));
					break;

				default:
					break;
				}
			}
			
		};
	}
	


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(m_CountDownTimer != null){
			m_CountDownTimer.cancel();
		}
	}

}
