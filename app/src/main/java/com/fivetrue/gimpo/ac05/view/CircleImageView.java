package com.fivetrue.gimpo.ac05.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.fivetrue.gimpo.ac05.image.ImageLoadManager;
import com.fivetrue.gimpo.ac05.utils.Log;

/**
 * Created by kwonojin on 16. 6. 14..
 */
public class CircleImageView extends ImageView {

    private static final String TAG = "CircleImageView";
    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
    }

    public void setImageUrl(final String url){
        Bitmap bm = ImageLoadManager.getInstance().getBitmapFromCache(url + ":circle");
        if(bm != null && !bm.isRecycled()){
            setImageBitmap(bm);
        }else{
            ImageLoadManager.getInstance().loadImageUrl(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if(response != null && response.getBitmap() != null && !response.getBitmap().isRecycled()){
                        Bitmap output = Bitmap.createBitmap(response.getBitmap().getWidth(),
                                response.getBitmap().getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(output);

                        final Paint paint = new Paint();
                        final Rect rect = new Rect(0, 0, response.getBitmap().getWidth(),
                                response.getBitmap().getHeight());

                        paint.setAntiAlias(true);
                        canvas.drawARGB(0, 0, 0, 0);
                        canvas.drawCircle(response.getBitmap().getWidth() / 2,
                                response.getBitmap().getHeight() / 2, response.getBitmap().getWidth() / 2, paint);
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                        canvas.drawBitmap(response.getBitmap(), rect, rect, paint);
                        response.getBitmap().recycle();
                        paint.reset();
                        paint.setAntiAlias(true);
                        paint.setStrokeWidth(1);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setColor(Color.BLACK);
                        canvas.drawCircle(response.getBitmap().getWidth() / 2,
                                response.getBitmap().getHeight() / 2, response.getBitmap().getWidth() / 2, paint);
                        ImageLoadManager.getInstance().putBitmapToCache(url + ":circle", output);
                        setImageBitmap(output);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG, "onErrorResponse: " + error.toString());
                }
            });
        }
    }

}
