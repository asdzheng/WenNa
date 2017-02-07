package com.xiangzi.qq_list;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.List;


public class BallView extends FrameLayout {
	int GROUND_INDEX = 0 ; //表示球在第几个坐标

	public static final int UP_ZERO = 30; //向上最小速度，小于UP_ZERO表示升到最高处
	public static final int DOWN_ZERO = 60; //小于DOWN_ZERO表示小球已落地
	public static float g = 4000; //重力加速度

	int startX = 0; //表情运动前的坐标
	int startY = 0;

	int currentX= -100;//表情实时运动坐标
	int currentY= -100;

	float startVX = 0f;//表情初始速度
	float startVY = -(float)(1200);

	float currentVX = 0f;//表情实时速度
	float currentVY = 0f;

	float impactFactoryY = 0.5f;//衰减系数
	float impactFactoryX = 0.1f;

	double timeX;
	double timeY;

	List<Point> pointList;

	BallThread bt = null;
	boolean bFall = false;//用来判断是否在做下降运动

	ImageView imageView;

	public BallView(Context Activity) {
		super(Activity);
	}

	public BallView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public BallView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public void initMovables(List<Point> pointList){
		this.pointList = pointList;

		GROUND_INDEX = 0;
		startX = pointList.get(GROUND_INDEX).x;
		startY = pointList.get(GROUND_INDEX).y;
		bt = new BallThread();
		post(bt);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(imageView != null) {
			Log.w("BallView", "x = " + currentX + " | y = " + currentY);
			imageView.offsetLeftAndRight(currentX - imageView.getLeft());
			imageView.offsetTopAndBottom(currentY - imageView.getTop());
		}
		super.onDraw(canvas);
	}
	public class BallThread implements Runnable {
		boolean flag = false; //一次小球运动标志
		double current;//实时时间

		boolean isNextIndex = true;

		public BallThread(){
			timeX = System.nanoTime();
			this.flag = true;
			imageView = new ImageView(getContext());
			imageView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			imageView.setImageResource(R.drawable.huaji);
			addView(imageView);
		}

		public void run(){
			if(GROUND_INDEX < pointList.size()-1){

				if(isNextIndex) {
					initStart();
					isNextIndex = false;
				}

				if(flag){
					postInvalidate();
					current = System.nanoTime();
					double timeSpanX = (double)((current -  timeX)/1000/1000/1000);
					currentX = (int)(startX +  currentVX * timeSpanX);
					if( bFall){
						double timeSpanY = (double)((current -  timeY)/1000/1000/1000);
						currentY = (int)( startY +  startVY * timeSpanY + timeSpanY * timeSpanY * g/2);
						currentVY = (float)( startVY + g * timeSpanY);
						if( startVY < 0 && Math.abs( currentVY) <= BallView.UP_ZERO){
							timeY = System.nanoTime();
							currentVY = 0;
							startVY = 0;
							startY =  currentY;
						}

						if( currentY +  imageView.getHeight() >= pointList.get(GROUND_INDEX +1).y && currentVY > 0){
//							System.out.println("开始弹跳" );
							//到达目标坐标点，开始弹跳
							currentVX =  currentVX * (impactFactoryX);//速度衰减
							currentVY = 0 -  currentVY * (impactFactoryY);

							if(Math.abs( currentVY) < BallView.DOWN_ZERO){
								//当速度衰减到最小速度以后，小球落地，运动结束
								try{
									Thread.sleep(100);
								}catch(Exception e){
									e.printStackTrace();
								}
								this.flag = false;
								System.out.println("线程结束"+ GROUND_INDEX);
								GROUND_INDEX++;
								isNextIndex = true;
							}else{
								startX = (int) currentX;

								timeX = System.nanoTime();
								startY =  currentY;
								timeY = System.nanoTime();
								startVY =  currentVY;
							}
						}
					}else {
						if( currentVY == 0 ||  currentVY > 0){
							timeY = System.nanoTime();
							bFall = true;//bFall参数用来判断是否进行S2的自由落体运动

							System.out.println("currentVY == 0 ||  currentVY > 0" );
						}else{
							currentVY = (int)( startVY + g * timeSpanX);
							currentY = (int)( currentY +  startVY * timeSpanX + timeSpanX * timeSpanX * g/2);

							System.out.println("currentVY = "+ currentVY + " | currentY = " + currentY );
						}
					}
				}
				post(this);
			} else {
				removeCallbacks(this);
			}

		}

		private void initStart() {
			flag = true;
			startVY = -(float)(1200);
			double t1 = -startVY/g; //V=V0+g*t  V=0 ,则t=-V0/g
			double s = (startVY*t1+0.5*g*t1*t1);
			double t2 =Math.sqrt(((2*(pointList.get(GROUND_INDEX +1).y-startY+(Math.abs(s))))/g));
			double t = t1+t2;
			startVX = (float)((pointList.get(GROUND_INDEX +1).x-startX)/(t));
			currentVX = startVX;
			currentX = startX;
			currentY = startY;
		}
	}
}
