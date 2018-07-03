package com.sc.github.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * 高仿今日头条loading flash效果
 * 100%还原
 *
 * @author shamchu
 * @date 2018/6/28 上午9:29
 */
public class FlashView extends View {

    private static final int DEFAULT_MASK_WIDTH = 160;
    private static final int DEFAULT_MASK_HEIGHT = 80;
    private static final int DEFAULT_DURATION = 1200;

    private Paint mDstPaint;
    private Paint mSrcPaint;
    private Bitmap mDstBmp;
    private RectF mDstRect;
    private int mFlashDuration;
    private Bitmap mMaskBitmap;
    private Bitmap mRenderMaskBitmap;
    private Canvas mMaskCanvas;
    private int mMaskWidth;
    private int mMaskHeight;
    private float mHorMoveDistance;
    private int[] mStartColor = {0, -1, 0};
    private float[] mEndColor = {0.4F, 0.6F, 0.8F};
    private int mStartAlpha = 130;
    private ObjectAnimator mAnimator;
    private boolean mPlaying = false;

    public FlashView(@NonNull Context context) {
        this(context, null);
    }

    public FlashView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlashView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Xfermode mode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        mDstPaint = new Paint();
        mDstPaint.setAntiAlias(true);
        mDstPaint.setDither(true);
        mDstPaint.setFilterBitmap(true);
        mDstPaint.setXfermode(mode);
        mSrcPaint = new Paint();
        mSrcPaint.setAlpha(mStartAlpha);
        mFlashDuration = DEFAULT_DURATION;
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        mMaskWidth = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_MASK_WIDTH, metrics));
        mMaskHeight = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_MASK_HEIGHT, metrics));
        mMaskBitmap = createMaskBitmap();
        mRenderMaskBitmap = createRenderMaskBitmap();
        mMaskCanvas = new Canvas(mRenderMaskBitmap);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int resultW = widthSize;
        int resultH = heightSize;
        if (widthMode == MeasureSpec.AT_MOST) {
            resultW = mDstBmp.getWidth();
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            resultH = mDstBmp.getHeight();
        }
        setMeasuredDimension(resultW, resultH);
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        mDstRect = new RectF(0, 0, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mDstBmp, 0f, 0f, mSrcPaint);
        mMaskCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        mMaskCanvas.drawBitmap(mDstBmp, null, mDstRect, null);
        mMaskCanvas.drawBitmap(mMaskBitmap, mHorMoveDistance, 0, mDstPaint);
        canvas.drawBitmap(mRenderMaskBitmap, 0, 0, null);
    }

    private void setPercent(float percent) {
        mHorMoveDistance = this.mMaskWidth * (percent - 1.5f);
        invalidate();
    }

    public void setImage(int resId) {
        mDstBmp = BitmapFactory.decodeResource(getResources(), resId);
    }

    private Bitmap createMaskBitmap() {
        int horMoveArea = ((int) (this.mMaskWidth * 2.5f));
        int verMoveArea = this.mMaskHeight;
        Shader shader = new LinearGradient(0.35f * horMoveArea, 1.0f * verMoveArea, 0.65f * horMoveArea, 0.0f * verMoveArea, this.mStartColor, this.mEndColor, Shader.TileMode.CLAMP);
        Bitmap maskBitmap = Bitmap.createBitmap(horMoveArea, verMoveArea, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(maskBitmap);
        Paint paint = new Paint();
        paint.setShader(shader);
        canvas.drawRect(0.0f, 0.0f, horMoveArea, verMoveArea, paint);
        return maskBitmap;
    }

    private Bitmap createRenderMaskBitmap() {
        return Bitmap.createBitmap(mMaskWidth, mMaskHeight, Bitmap.Config.ARGB_8888);
    }

    public void start() {
        if (mPlaying) {
            return;
        }
        mPlaying = true;
        startAnim();
    }

    public void stop() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mPlaying = false;
    }

    private void startAnim() {
        if (mAnimator == null) {
            mAnimator = ObjectAnimator.ofFloat(this, "percent", 0f, 1.5f);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.setDuration(mFlashDuration);
            mAnimator.setRepeatCount(-1);
        }
        mAnimator.start();
    }

    public void setDuration(int duration) {
        mFlashDuration = duration;
    }

    public void setSrcAlpha(int startAlpha) {
        this.mStartAlpha = startAlpha;
        if (mSrcPaint != null) {
            mSrcPaint.setAlpha(startAlpha);
        }
    }

    public boolean isPlaying() {
        return mPlaying;
    }
}
