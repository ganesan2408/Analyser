/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.Scroller;

import com.yhh.analyser.R;

public class SwitchButton extends CompoundButton {
    private static final int TOUCH_MODE_IDLE = 0;
    private static final int TOUCH_MODE_DOWN = 1;
    private static final int TOUCH_MODE_DRAGGING = 2;
    private int buttonLeft;  
    private int buttonTop;  
    private int tempSlideX = 0; 
    private int tempMinSlideX = 0; 
    private int tempMaxSlideX = 0; 
    private int tempTotalSlideDistance;  
    private int duration = 20; 
    private int touchMode;
    private int touchSlop;
    private int withTextInterval = 16;  
    private float touchX;   
    private float minChangeDistanceScale = 0.2f;   
    private Paint paint;   
    private RectF buttonRectF;  
    private Drawable frameDrawable; 
    private Drawable stateDrawable;   
    private Drawable stateMaskDrawable;    
    private Drawable sliderDrawable;   
    private SwitchScroller switchScroller; 
    private PorterDuffXfermode porterDuffXfermode;

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        setGravity(Gravity.CENTER_VERTICAL);
        paint = new Paint();
        paint.setColor(Color.RED);
        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        switchScroller = new SwitchScroller(getContext(), new AccelerateDecelerateInterpolator());
        buttonRectF = new RectF();

        if(attrs != null && getContext() != null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SwitchButton);
            if(typedArray != null){
                withTextInterval = (int) typedArray.getDimension(R.styleable.SwitchButton_withTextInterval, 0.0f);
                setDrawables(
                    typedArray.getDrawable(R.styleable.SwitchButton_frameDrawable),
                    typedArray.getDrawable(R.styleable.SwitchButton_stateDrawable),
                    typedArray.getDrawable(R.styleable.SwitchButton_stateMaskDrawable),
                    typedArray.getDrawable(R.styleable.SwitchButton_sliderDrawable)
                );
                typedArray.recycle();
            }
        }

        ViewConfiguration config = ViewConfiguration.get(getContext());
        touchSlop = config.getScaledTouchSlop();
        setChecked(isChecked());
        setClickable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth;
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.AT_MOST:
                measureWidth = getCompoundPaddingLeft() + getCompoundPaddingRight();
                break;
            case MeasureSpec.EXACTLY:
                measureWidth = MeasureSpec.getSize(widthMeasureSpec);
                break;
            case MeasureSpec.UNSPECIFIED:
                measureWidth = getCompoundPaddingLeft() + getCompoundPaddingRight();
                break;
            default:
                measureWidth = getCompoundPaddingLeft() + getCompoundPaddingRight();
                break;
        }

        int measureHeight;
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.AT_MOST:
                measureHeight = (frameDrawable != null? frameDrawable.getIntrinsicHeight():0) + getCompoundPaddingTop() + getCompoundPaddingBottom();
                break;
            case MeasureSpec.EXACTLY:
                measureHeight = MeasureSpec.getSize(heightMeasureSpec);
                break;
            case MeasureSpec.UNSPECIFIED:
                measureHeight = (frameDrawable != null? frameDrawable.getIntrinsicHeight():0) + getCompoundPaddingTop() + getCompoundPaddingBottom();
                break;
            default:
                measureHeight = (frameDrawable != null? frameDrawable.getIntrinsicHeight():0) + getCompoundPaddingTop() + getCompoundPaddingBottom();
                break;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(measureWidth < getMeasuredWidth()){
            measureWidth = getMeasuredWidth();
        }

        if(measureHeight < getMeasuredHeight()){
            measureHeight = getMeasuredHeight();
        }

        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Drawable[] drawables = getCompoundDrawables();
        int drawableRightWidth = 0;
        int drawableTopHeight = 0;
        int drawableBottomHeight = 0;
        if(drawables != null){
            if(drawables.length > 1 && drawables[1] != null){
                drawableTopHeight = drawables[1].getIntrinsicHeight() + getCompoundDrawablePadding();
            }
            if(drawables.length > 2 && drawables[2] != null){
                drawableRightWidth = drawables[2].getIntrinsicWidth() + getCompoundDrawablePadding();
            }
            if(drawables.length > 3 && drawables[3] != null){
                drawableBottomHeight = drawables[3].getIntrinsicHeight() + getCompoundDrawablePadding();
            }
        }

        buttonLeft = (getWidth() - (frameDrawable!=null?frameDrawable.getIntrinsicWidth():0) - getPaddingRight() - drawableRightWidth);
        buttonTop = (getHeight() - (frameDrawable!=null?frameDrawable.getIntrinsicHeight():0) + drawableTopHeight - drawableBottomHeight) / 2;
        buttonRectF.set(buttonLeft, buttonTop, buttonLeft + (frameDrawable != null ? frameDrawable.getIntrinsicWidth() : 0), buttonTop + (frameDrawable != null ? frameDrawable.getIntrinsicHeight() : 0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(buttonLeft, buttonTop);

        if(stateDrawable != null && stateMaskDrawable != null){
            Bitmap stateBitmap = getBitmapFromDrawable(stateDrawable);
            if(stateMaskDrawable != null && stateBitmap != null && !stateBitmap.isRecycled()){
                int src = canvas.saveLayer(0, 0, getWidth(), getHeight(), paint, Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
                stateMaskDrawable.draw(canvas);
                paint.setXfermode(porterDuffXfermode);
                canvas.drawBitmap(stateBitmap, tempSlideX, 0, paint);
                paint.setXfermode(null);
                canvas.restoreToCount(src);
            }
        }

        if(frameDrawable != null){
            frameDrawable.draw(canvas);
        }

        if(sliderDrawable != null){
            Bitmap sliderBitmap = getBitmapFromDrawable(sliderDrawable);
            if(sliderBitmap != null && !sliderBitmap.isRecycled()){
                canvas.drawBitmap(sliderBitmap, tempSlideX, 0, paint);
            }
        }

        // �ں�ͼ��
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN : {
                if(isEnabled() && buttonRectF.contains(event.getX(), event.getY())){
                    touchMode = TOUCH_MODE_DOWN;
                    tempTotalSlideDistance = 0; 
                    touchX = event.getX(); 
                    setClickable(false);    
                }
                break;
            }

            case MotionEvent.ACTION_MOVE : {
                switch(touchMode){
                    case TOUCH_MODE_IDLE : {
                        break;
                    }
                    case TOUCH_MODE_DOWN : {
                        final float x = event.getX();
                        if (Math.abs(x - touchX) > touchSlop) {
                            touchMode = TOUCH_MODE_DRAGGING;
                            if(getParent() != null){
                                getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            touchX = x;
                            return true;
                        }
                        break;
                    }
                    case TOUCH_MODE_DRAGGING : {
                        float newTouchX = event.getX();
                        tempTotalSlideDistance += setSlideX(tempSlideX + ((int) (newTouchX - touchX)));   
                        touchX = newTouchX;
                        invalidate();
                        return true;
                    }
                }
                break;
            }

            case MotionEvent.ACTION_UP :{
                setClickable(true);

                if(touchMode == TOUCH_MODE_DRAGGING){
                    touchMode = TOUCH_MODE_IDLE;
                    if(Math.abs(tempTotalSlideDistance) >= Math.abs(frameDrawable.getIntrinsicWidth() * minChangeDistanceScale)){
                        toggle();  
                     }else{
                        switchScroller.startScroll(isChecked());
                    }
                }else if(touchMode == TOUCH_MODE_DOWN){
                    touchMode = TOUCH_MODE_IDLE;
                    toggle();
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL :
            case MotionEvent.ACTION_OUTSIDE : {
                setClickable(true);
                if (touchMode == TOUCH_MODE_DRAGGING) {
                    touchMode = TOUCH_MODE_IDLE;
                    switchScroller.startScroll(isChecked()); 
                }else{
                    touchMode = TOUCH_MODE_IDLE;
                }
                break;
            }
        }

        super.onTouchEvent(event);
        return isEnabled();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int[] drawableState = getDrawableState();
        if(frameDrawable != null) frameDrawable.setState(drawableState); 
        if(stateDrawable != null) stateDrawable.setState(drawableState); 
        if(stateMaskDrawable != null) stateMaskDrawable.setState(drawableState); 
        if(sliderDrawable != null) sliderDrawable.setState(drawableState); 
        invalidate();
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == frameDrawable || who == stateDrawable || who == stateMaskDrawable || who == sliderDrawable;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
    public void jumpDrawablesToCurrentState() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            super.jumpDrawablesToCurrentState();
            if(frameDrawable != null) frameDrawable.jumpToCurrentState();
            if(stateDrawable != null) stateDrawable.jumpToCurrentState();
            if(stateMaskDrawable != null) stateMaskDrawable.jumpToCurrentState();
            if(sliderDrawable != null) sliderDrawable.jumpToCurrentState();
        }
    }

    @Override
     public void setChecked(boolean checked) {
        boolean changed = checked != isChecked();
        super.setChecked(checked);
        if(changed){
            if(getWidth() > 0 && switchScroller != null){   
                switchScroller.startScroll(checked);
            }else{
                setSlideX(isChecked() ? tempMinSlideX : tempMaxSlideX);  
            }
        }
    }

    @Override
    public int getCompoundPaddingRight() {
        int padding = super.getCompoundPaddingRight() + (frameDrawable!=null?frameDrawable.getIntrinsicWidth():0);
        if (!TextUtils.isEmpty(getText())) {
            padding += withTextInterval;
        }
        return padding;
    }

    public void setDrawables(Drawable frameBitmap, Drawable stateDrawable, Drawable stateMaskDrawable, Drawable sliderDrawable){
        if(frameBitmap == null || stateDrawable == null || stateMaskDrawable == null || sliderDrawable == null){
            throw new IllegalArgumentException("ALL NULL");
        }

        this.frameDrawable = frameBitmap;
        this.stateDrawable = stateDrawable;
        this.stateMaskDrawable = stateMaskDrawable;
        this.sliderDrawable = sliderDrawable;

        this.frameDrawable.setBounds(0, 0, this.frameDrawable.getIntrinsicWidth(), this.frameDrawable.getIntrinsicHeight());
        this.frameDrawable.setCallback(this);
        this.stateDrawable.setBounds(0, 0, this.stateDrawable.getIntrinsicWidth(), this.stateDrawable.getIntrinsicHeight());
        this.stateDrawable.setCallback(this);
        this.stateMaskDrawable.setBounds(0, 0, this.stateMaskDrawable.getIntrinsicWidth(), this.stateMaskDrawable.getIntrinsicHeight());
        this.stateMaskDrawable.setCallback(this);
        this.sliderDrawable.setBounds(0, 0, this.sliderDrawable.getIntrinsicWidth(), this.sliderDrawable.getIntrinsicHeight());
        this.sliderDrawable.setCallback(this);

        this.tempMinSlideX = (-1 * (stateDrawable.getIntrinsicWidth() - frameBitmap.getIntrinsicWidth()));  //��ʼ��X����Сֵ
        setSlideX(isChecked() ? tempMinSlideX : tempMaxSlideX); 

        requestLayout();
    }

    public void setDrawableResIds(int frameDrawableResId, int stateDrawableResId, int stateMaskDrawableResId, int sliderDrawableResId){
        if(getResources() != null){
            setDrawables(
                getResources().getDrawable(frameDrawableResId),
                getResources().getDrawable(stateDrawableResId),
                getResources().getDrawable(stateMaskDrawableResId),
                getResources().getDrawable(sliderDrawableResId)
            );
        }
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setMinChangeDistanceScale(float minChangeDistanceScale) {
        this.minChangeDistanceScale = minChangeDistanceScale;
    }

    public void setWithTextInterval(int withTextInterval) {
        this.withTextInterval = withTextInterval;
        requestLayout();
    }

    private int setSlideX(int newSlideX) {
        if(newSlideX < tempMinSlideX) newSlideX = tempMinSlideX;
        if(newSlideX > tempMaxSlideX) newSlideX = tempMaxSlideX;
        int addDistance = newSlideX - tempSlideX;
        this.tempSlideX = newSlideX;
        return addDistance;
    }

    private static Bitmap getBitmapFromDrawable(Drawable drawable){
        if(drawable == null){
            return null;
        }

        if(drawable instanceof DrawableContainer){
            return getBitmapFromDrawable(drawable.getCurrent());
        }else if(drawable instanceof BitmapDrawable){
            return ((BitmapDrawable) drawable).getBitmap();
        }else{
            return null;
        }
    }

    private class SwitchScroller implements Runnable {
        private Scroller scroller;

        public SwitchScroller(Context context, android.view.animation.Interpolator interpolator) {
            this.scroller = new Scroller(context, interpolator);
        }

        public void startScroll(boolean checked){
            scroller.startScroll(tempSlideX, 0, (checked? tempMinSlideX : tempMaxSlideX) - tempSlideX, 0, duration);
            post(this);
        }

        @Override
        public void run() {
            if(scroller.computeScrollOffset()){
                setSlideX(scroller.getCurrX());
                invalidate();
                post(this);
            }
        }
    }
}

