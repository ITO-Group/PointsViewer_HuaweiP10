package com.example.zhoujianyu.oldpointsviewer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
//class Position{
//    int left;int right;int top;int bottom;
//    public Position(int left,int right,int top,int bottom){
//        this.left = left;this.right = right;this.top = top;this.bottom=bottom;
//    }
//}
public class MyView extends View {
    short mdata[];
    int mDiffData[];
    int left_padding = 100;
    int top_padding = 100;
    int frame_width = 120;
    int frame_length = 150;
    int interval_length = 30;
    int screenHeight = 0;
    int screenWidth = 0;
    int capaWidth = 0;
    int capaHeight= 0;
    int ROW_NUM  = 28;
    int COL_NUM = 16;
    int frame_row_num = 8;
    int frame_col_num = 6;
    Paint redPaint = new Paint();
    Paint yellowPaint = new Paint();
    Paint whitePaint = new Paint();
    Paint blackPaint = new Paint();
    Rect points[];
    //    Rect frames[];
    Rect frame_locations[][]=new Rect[frame_row_num][frame_col_num];

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mdata = new short[ROW_NUM+COL_NUM-1];
        mDiffData = new int[ROW_NUM+COL_NUM-1];
        redPaint.setColor(Color.RED); redPaint.setStyle(Paint.Style.FILL);
        yellowPaint.setColor(Color.YELLOW); yellowPaint.setStyle(Paint.Style.STROKE);
        blackPaint.setColor(Color.BLACK); blackPaint.setStyle(Paint.Style.STROKE); blackPaint.setTextSize(30);
        whitePaint.setColor(Color.WHITE); whitePaint.setTextSize(30);
        points = new Rect[ROW_NUM+COL_NUM-1];
//        frames = new Rect[ROW_NUM+COL_NUM-1];
    }
    public void init(){
        this.capaWidth = screenWidth/COL_NUM;
        this.capaHeight= screenHeight/ROW_NUM;
        points = new Rect[ROW_NUM+COL_NUM-1];

        // initialize points positions;
        for (int i = 0;i<ROW_NUM;i++){
            int left = 0;
            int right = left+this.capaWidth;
            int top = (i-1)*this.capaHeight;
            int bottom = top+this.capaHeight;
            Rect rect  = new Rect(left,top,right,bottom);
            points[i] = rect;
        }
        for(int i = ROW_NUM;i<ROW_NUM+COL_NUM-1;i++){
            int left = (i-ROW_NUM+1)*this.capaWidth;
            int right = left+this.capaWidth;
            int top = (ROW_NUM-2)*this.capaHeight;
            int bottom = top+this.capaHeight;
            Rect rect = new Rect(left,top,right,bottom);
            points[i] = rect;
        }
        // initialize frame positions
        for(int i = 0;i<ROW_NUM+COL_NUM-1;i++){
            int row_id = i/frame_col_num;
            int col_id = i%frame_col_num;
            int left;int right;int top;int bottom;
            if(col_id>0){
                left = frame_locations[row_id][col_id-1].right+interval_length;
            }
            else left = left_padding;
            if(row_id>0){
                top = frame_locations[row_id-1][col_id].bottom+interval_length;
            }
            else top = top_padding;
            right = left+frame_width;
            bottom = top+frame_length;
            frame_locations[row_id][col_id] = new Rect(left,top,right,bottom);
        }
    }
    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void updateData(short[] data){
        for(int i = 0;i<ROW_NUM;i++){
            mDiffData[i] = data[i*COL_NUM]-mdata[i]==0?mDiffData[i]:data[i*COL_NUM]-mdata[i];
            mdata[i] = data[i*COL_NUM];
        }
        for(int i = ROW_NUM;i<ROW_NUM+COL_NUM-1;i++){
            mDiffData[i] = data[(ROW_NUM-1)*COL_NUM+i-ROW_NUM+1]-mdata[i]==0?mDiffData[i]:data[(ROW_NUM-1)*COL_NUM+i-ROW_NUM+1]-mdata[i];
            mdata[i] = data[(ROW_NUM-1)*COL_NUM+i-ROW_NUM+1];
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(int i = 0;i<ROW_NUM+COL_NUM-1;i++){
            int val = mdata[i];
            int left = points[i].left;
            int top = points[i].top;
            canvas.drawRect(points[i],redPaint);
            canvas.drawRect(points[i],yellowPaint);
            canvas.drawText(Integer.toString(i),left+20,top+40,whitePaint);
            canvas.drawRect(frame_locations[i/frame_col_num][i%frame_col_num],blackPaint);
            canvas.drawText(Integer.toString(i)+":\n"+Integer.toString(val),frame_locations[i/frame_col_num][i%frame_col_num].left+10,frame_locations[i/frame_col_num][i%frame_col_num].top+70,blackPaint);
        }
    }
}
