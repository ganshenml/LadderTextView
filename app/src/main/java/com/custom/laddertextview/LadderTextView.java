package com.custom.laddertextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class LadderTextView extends android.support.v7.widget.AppCompatTextView {
    private static final String TAG = "LadderView";
    private Path linePath;
    private Paint paint, textPaint;
    private int width, height;
    private float strokeWidth = 2;
    private Region mRegion;
    private String textContent;
    private int lineOffset = 0;//划线的偏移量
    private int textOffset = 0;//文本的偏移量
    private float offsetScale = 1;//梯高与（梯顶与梯底）之差的比例（梯底比梯顶长）
    private boolean isLeft = true;//分为左和右两种斜角梯形模式
    private boolean isSelected = false;//是否是选定
    private int selectedColor = Color.BLACK;

    public LadderTextView(Context context) {
        super(context);
        init();
    }

    public LadderTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
        init();
    }


    public LadderTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs);
        init();
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LadderTextView);
        textContent = typedArray.getString(R.styleable.LadderTextView_textContent);
        offsetScale = typedArray.getFloat(R.styleable.LadderTextView_offsetScale, 0.5f);
        isLeft = typedArray.getBoolean(R.styleable.LadderTextView_isLeft, true);
        isSelected = typedArray.getBoolean(R.styleable.LadderTextView_isSelected, true);
        selectedColor = typedArray.getColor(R.styleable.LadderTextView_selectedColor, Color.GREEN);
        strokeWidth = typedArray.getDimension(R.styleable.LadderTextView_strokeWidth, 1);
        typedArray.recycle();
    }

    private void init() {
        Log.v(TAG, "init");
        mRegion = new Region();
        paint = new Paint();
        textPaint = new Paint();
        linePath = new Path();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(dp2px(getContext(), strokeWidth));
        paint.setColor(selectedColor);
        paint.setStyle(isSelected ? Paint.Style.FILL_AND_STROKE : Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);

        textPaint.setAntiAlias(true);
        textPaint.setTextSize(getTextSize());//传递TextSize(px)
        textPaint.setColor(isSelected ? Color.WHITE : selectedColor);
        setText("");//去除掉原有的Text内容
        lineOffset = dp2px(getContext(), strokeWidth) / 2;
        textOffset = (int) (getTextSize() / 2) + getBaseline() * 2;
        Log.v(TAG, "lineOffset textOffset ->" + lineOffset + " " + textOffset);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
        Log.v(TAG, "width height->" + width + " " + height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.v(TAG, "onDraw");
        if (isLeft) {
            linePath.moveTo(0 + lineOffset, 0 + lineOffset);
            linePath.lineTo(width, 0 + lineOffset);
            linePath.lineTo((int) (width - offsetScale * height), height - lineOffset);
            linePath.lineTo(0 + lineOffset, height - lineOffset);
            linePath.close();
            setTextAlignment(TEXT_ALIGNMENT_TEXT_START);
            canvas.drawPath(linePath, paint);
            canvas.drawText(textContent == null ? "" : textContent,
                    getPaddingStart() + lineOffset,
                    height / 2 + textOffset, textPaint);
        } else {
            linePath.moveTo(0 + lineOffset + offsetScale * height, 0 + lineOffset);
            linePath.lineTo(width - lineOffset, 0 + lineOffset);
            linePath.lineTo(width - lineOffset, height - lineOffset);
            linePath.lineTo(0, height - lineOffset);
            linePath.close();
            setTextAlignment(TEXT_ALIGNMENT_TEXT_END);
            canvas.drawPath(linePath, paint);
            canvas.drawText(textContent == null ? "" : textContent,
                    getWidth() - lineOffset - getPaddingEnd() - getDrawTextWidth(textPaint, textContent),
                    height / 2 + textOffset, textPaint);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isInRegion(event)) {//点击的点的位置不在范围内则不响应
                return false;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 判断点击的位置是否在要求的范围内
     * @param event
     * @return
     */
    public boolean isInRegion(MotionEvent event) {
        RectF rectF = new RectF();
        linePath.computeBounds(rectF, true);
        mRegion.setPath(linePath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        return mRegion.contains((int) event.getX(), (int) event.getY());
    }

    /**
     * 获取要画的字符串的宽度
     *
     * @param paint
     * @param textContent
     * @return
     */
    private int getDrawTextWidth(Paint paint, String textContent) {
        float totalWidth = 0f;
        if (textContent != null && textContent.length() > 0) {
            int len = textContent.length();
            float[] widths = new float[len];
            paint.getTextWidths(textContent, widths);
            for (int j = 0; j < len; j++) {
                totalWidth += widths[j];
            }
        }
        return (int) Math.ceil(totalWidth);
    }

    /**
     * @param dpValue （DisplayMetrics类中属性density）
     * @return
     */
    private int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
        invalidate();
    }

    public void setMSelected(boolean isSelected) {
        textPaint.setColor(isSelected ? Color.WHITE : selectedColor);
        paint.setStyle(isSelected ? Paint.Style.FILL_AND_STROKE : Paint.Style.STROKE);
        this.isSelected = isSelected;
        invalidate();
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }
}
