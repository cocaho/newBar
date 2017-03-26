package top.greendami.greendami;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.hau.newbar.R;
/**
 * Created by Hau on 2017/3/24.
 */

public class PPRefreshView extends ViewGroup {
    Context context;
    AbsListView mListView;
    PPView mPPView;
    View header;
    TextView title;
    ImageView mImage;//箭头
    int listTop = 0;

    float headerHeight = 40 + 80 + 40;//header的高度，上留白 + 文字（PPVIew）高度 + 下留白
    float headerpadding = 40;//上留白,下留白
    private int mYDown, mLastY;

    //最短滑动距离
    float a = 0;

    RotateAnimation mRotateAnimation;
    int state = 0; //0,正常;1,下拉;2,松开

    //预留监听事件
    PPRefreshViewListener mPPRefreshViewListener;
    public  void setPPRefreshViewListener(PPRefreshViewListener mPPRefreshViewListener){
        this.mPPRefreshViewListener = mPPRefreshViewListener;
    }

    public PPRefreshView(Context context){
        super(context);
        this.context = context;
    }

    public  PPRefreshView(Context context,AttributeSet attributeSet){
        super(context,attributeSet);
        this.context = context;

        //px转dp
        a = UnitUtil.convertPixelsToDp(ViewConfiguration.get(context).getScaledDoubleTapSlop(),context);
        //TypeArray管理attrs.xml文件
        TypedArray b = context.obtainStyledAttributes( attributeSet,R.styleable.PPRefreshView_header);
        headerHeight = b.getDimension(R.styleable.PPRefreshView_header_header_height, 200);
        headerpadding = b.getDimension(R.styleable.PPRefreshView_header_header_padding, 40);
        initAnima();
    }
    //实现箭头动画
    private  void initAnima(){
        mRotateAnimation = new RotateAnimation(0,180, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        mRotateAnimation.setFillAfter(true);
        mRotateAnimation.setDuration(200);
    }
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        if(mPPView!=null){
            mPPView.measure(widthMeasureSpec,(int) (headerHeight- 2 * headerpadding));
        }
        if(header!=null){
            header.measure(widthMeasureSpec,(int) (headerHeight- 2 * headerpadding));
        }
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

    }
    /*
    1）参数changed表示view有新的尺寸或位置；
    2）参数l表示相对于父view的Left位置；
    3）参数t表示相对于父view的Top位置；
    4）参数r表示相对于父view的Right位置；
    5）参数b表示相对于父view的Bottom位置。
    * */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(mListView==null&&getChildCount()==1){
            mListView = (ListView)getChildAt(0);
            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                    if(isBottom()){
                        //添加外部回调
                        if(mPPRefreshViewListener != null){
                            mPPRefreshViewListener.LoadMore();
                        }
                    }

                }
            });
        }
        if(mListView!=null){
            mListView.layout(1,listTop,r,b);
        }
        if(mPPView!=null){
            mPPView.layout(1,(int)(listTop - headerHeight + headerpadding), r, listTop);
        }
        if(header!=null){
            header.layout(1,(int)(listTop - headerHeight + headerpadding), r, listTop);
        }
    }
    protected  void dispatchDraw(Canvas canvas){
        super.dispatchDraw(canvas);
        //松开手指，list回到顶部
        if(state==2){
            listTop = listTop-15;
            if(listTop<headerHeight){
                listTop=(int)headerHeight;
            }
            requestLayout();
        }
        //刷新完毕，关闭header
        if (state == 0 && listTop > 0) {
            listTop = listTop - 15;
            if (listTop < 0) {
                listTop = 0;
            }
            requestLayout();
        }

    }
    public boolean dispatchTouchEvent(MotionEvent event){
        final  int action = event.getAction();

        switch (action){
            case MotionEvent.ACTION_DOWN:
                mYDown = (int)event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                mLastY = (int)event.getRawY();
                if(isTop()&&mLastY>mYDown&&(mLastY-mYDown)>a){
                    state = 1;
                    listTop = mLastY-mYDown;
                    //去除上次留下的
                    if(mPPView!=null){
                        removeView(mPPView);
                        mPPView = null;
                    }
                    if(header==null){
                        header = LayoutInflater.from(context).inflate(R.layout.header_layout, null, false);
                        title = ((TextView) header.findViewById(R.id.text));
                        mImage = ((ImageView) header.findViewById(R.id.icon));
                        addView(header);
                    }
                    if(title!=null&&(mLastY-mYDown)>a*2f){
                        title.setText("松开刷新");
                        mImage.setAnimation(mRotateAnimation);
                    }
                    requestLayout();
                }
                break;
            case MotionEvent.ACTION_UP:
                if(header!=null){
                    removeView(header);
                    header = null;
                }
                if(mPPView==null&&state==1){
                    if(mPPRefreshViewListener!=null){
                        mPPRefreshViewListener.onRefresh();
                    }
                    mPPView = new PPView(context);
                    addView(mPPView);
                    mYDown = 0;
                    mLastY = 0;
                    state = 2;
                    requestLayout();
                }
                break;
            default:
                break;

        }


        return super.dispatchTouchEvent(event);
    }

    private boolean isBottom() {

        if (mListView != null && mListView.getAdapter() != null && mListView.getAdapter().getCount() >= 5) {
            return mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
        }
        return false;
    }

    private boolean isTop() {

        if (mListView != null && mListView.getAdapter() != null) {
            return mListView.getFirstVisiblePosition() == 0;
        }
        return false;
    }

    /**
     * 收起下拉刷新的header，刷新结束
     */
    public void RefreshOver() {
        if (mPPView != null) {
            removeView(mPPView);
            mPPView = null;
        }
        if (header != null) {
            removeView(header);
            header = null;
            title = null;
            mImage = null;
        }
        state = 0;
    }

    public interface PPRefreshViewListener{
        public void onRefresh();
        public void LoadMore();
    }


}
