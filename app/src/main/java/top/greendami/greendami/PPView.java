package top.greendami.greendami;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Hau on 2017/3/24.
 */
//一个简单的View可以放在xml中使用，http://www.jianshu.com/p/f7c164288aca
public class PPView extends View {
    String TAG = "PPView";

    boolean isloading = true;
    Context mContext;

    private int mwidth = 100;
    private  int mheight = 100;

    private  int mColor;
    public Paint mPaint = new Paint();

    float time = 0;

    //小球与中间打球的最远距离
    float distance = 100;


    public PPView(Context context) {
        super(context);
        mContext = context;
    }

    public PPView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mColor =0;
        init();
    }
    private void init(){
        mPaint.setAntiAlias(true);//图像边缘相对清晰一点，锯齿痕迹不那么明显
        mPaint.setColor(mColor);
    }

    protected  void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        //进行测量单位的转换
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        //宽度至少是高度的4倍，美观？
        if (widthSpecSize < 4 * heightSpecSize) {
            widthSpecSize = 4 * heightSpecSize;
        }

        mwidth = widthSpecSize;
        mheight = heightSpecSize;

        distance = 1.2f*mheight;
        setMeasuredDimension(widthSpecSize, heightSpecSize);//自定义视图的大小


    }


    protected  void  onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(isloading){
            //计算大圆半径
            float bigR = mheight*3.2f+mheight*0.0f*Math.abs((float)Math.sin((Math.toRadians(time))));
            //计算小圆半径
            float smallR = mheight * 0.22f + mheight * 0.03f * Math.abs((float) Math.cos(Math.toRadians(time)));
            //圆心x坐标
            float bigx = (getWidth())/2;
            float smallx = getSmallcenterX();
            //绘制圆
            canvas.drawCircle(bigx,mheight/2,bigR,mPaint);
            canvas.drawCircle(smallx,mheight/2,smallR,mPaint);

            //绘制俩圆的链接,需确定贝塞尔曲线上下两面的各三个点
            //1.小球在右侧
            if(smallx>bigx){ //小球在右侧
                Path path = new Path();
                //上面
                //第一个点（x1，y1）在大圆上
                float x1 = bigx + bigR * (float) Math.cos(Math.toRadians(time));
                float y1 = mheight / 2 - bigR * (float) Math.sin(Math.toRadians(time));
                if (y1 > mheight / 2 - smallR) {
                    y1 = mheight / 2 - smallR;
                    x1 = bigx + (float) (Math.sqrt(bigR * bigR - smallR * smallR));
                }

                //上面的贝塞尔曲线的第二个点（x2，y2），两个圆心中点
                float x2 = smallx - smallR * (float) Math.cos(Math.toRadians(time));
                float y2 = mheight / 2 - smallR * (float) Math.sin(Math.toRadians(time));
                if (y2 > mheight / 2 - smallR * 0.8) {
                    y2 = mheight / 2 - smallR * 0.8f;
                    x2 = smallx - smallR * (float) (Math.sqrt(1-0.64f));
                }
                //下面的贝塞尔曲线的第三个点，在小圆身上
                float x3 = smallx - smallR * (float) Math.cos(Math.toRadians(time));
                float y3 = mheight / 2 + smallR * (float) Math.sin(Math.toRadians(time));
                if (y3 < mheight / 2 + smallR * 0.8) {
                    y3 = mheight / 2 + smallR * 0.8f;
                    x3 = smallx - smallR * (float) (Math.sqrt(1-0.64f));
                }
                //下面的贝塞尔曲线的第一个点，在大圆身上
                float x4 = bigx + bigR * (float) Math.cos(Math.toRadians(time));
                float y4 = mheight / 2 + bigR * (float) Math.sin(Math.toRadians(time));
                if (y4 < mheight / 2 + smallR) {
                    y4 = mheight / 2 + smallR;
                    x4 = bigx + (float) (Math.sqrt(bigR * bigR - smallR * smallR));
                }

                path.moveTo(x1, y1);

                path.quadTo((bigx + smallx) / 2, mheight / 2, x2, y2);
                // 绘制贝赛尔曲线（Path）

                path.lineTo(x3, y3);

                path.quadTo((bigx + smallx) / 2, mheight / 2, x4, y4);
                canvas.drawPath(path, mPaint);
            }

            //小球在左侧
            if (smallx < bigx) {
                Path path = new Path();
                float x1 = bigx + bigR * (float) Math.cos(Math.toRadians(time));
                float y1 = mheight / 2 - bigR * (float) Math.sin(Math.toRadians(time));
                if (y1 > mheight / 2 - smallR) {
                    y1 = mheight / 2 - smallR;
                    x1 = bigx - (float) (Math.sqrt(bigR * bigR - smallR * smallR));
                }

                float x2 = smallx - smallR * (float) Math.cos(Math.toRadians(time));
                float y2 = mheight / 2 - smallR * (float) Math.sin(Math.toRadians(time));
                if (y2 > mheight / 2 - smallR * 0.8) {
                    y2 = mheight / 2 - smallR * 0.8f;
                    x2 = smallx + smallR * (float) (Math.sqrt(1-0.64f));
                }

                float x3 = smallx - smallR * (float) Math.cos(Math.toRadians(time));
                float y3 = mheight / 2 + smallR * (float) Math.sin(Math.toRadians(time));
                if (y3 < mheight / 2 + smallR * 0.8) {
                    y3 = mheight / 2 + smallR * 0.8f;
                    x3 = smallx + smallR * (float) (Math.sqrt(1-0.64f));
                }
                float x4 = bigx + bigR * (float) Math.cos(Math.toRadians(time));
                float y4 = mheight / 2 + bigR * (float) Math.sin(Math.toRadians(time));
                if (y4 < mheight / 2 + smallR) {
                    y4 = mheight / 2 + smallR;
                    x4 = bigx - (float) (Math.sqrt(bigR * bigR - smallR * smallR));
                }

                path.moveTo(x1, y1);

                path.quadTo((bigx + smallx) / 2, mheight / 2, x2, y2);
                // 绘制贝赛尔曲线（Path）

                path.lineTo(x3, y3);

                path.quadTo((bigx + smallx) / 2, mheight / 2, x4, y4);
                canvas.drawPath(path, mPaint);
            }

            postInvalidate();
        }

    }








    private float getSmallcenterX() {
        time = time + 2.5f;
        return mwidth / 2 + distance * (float) Math.cos(Math.toRadians(time));
    }

}
