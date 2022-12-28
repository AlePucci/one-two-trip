package it.unimib.sal.one_two_trip.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.source.PhotoCallback;
import it.unimib.sal.one_two_trip.data.source.PhotoRemoteDataSource;

/**
 * This class is used to generate a shareable image from a location name.
 * It uses Unsplash API to get an image from the location name and then
 * it adds the location name to the image and the app logo.
 * The image is then saved to the device and a share intent is launched.
 * Launch .execute() to start the process on an instance of this class.
 * **BETA feature**
 */
public class SharePhotoGenerator extends AsyncTask<String, Void, Bitmap> implements PhotoCallback {
    private final Context context;
    private final PhotoRemoteDataSource photoRemoteDataSource;
    private final boolean isCompleted;  //about the trip we are generating photos

    private String photo;
    private Exception exception;

    /**
     * @param application the application
     * @param isCompleted true if the trip is completed, false otherwise
     */
    public SharePhotoGenerator(Application application, boolean isCompleted) {
        this.context = application.getApplicationContext();
        this.isCompleted = isCompleted;
        this.photoRemoteDataSource = ServiceLocator.getInstance().getPhotoRemoteDataSource();
        this.photoRemoteDataSource.setPhotoCallback(this);
    }

    private void drawTextOnBitmap(Bitmap bmp, String text, int textColor, float textSize,
                                  boolean textBold, float x, float y) {
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Typeface typeface = Typeface.create("sans-serif-condensed-medium",
                Typeface.BOLD_ITALIC);
        paint.setColor(textColor);
        paint.setFakeBoldText(textBold);
        paint.setTextSize(textSize);
        paint.setTypeface(typeface);
        canvas.drawText(text, x, y, paint);
    }

    private void drawLogoOnBitmap(Bitmap bmp, String text) {
        float textSize = (float) (bmp.getWidth() * 0.04);
        float x = (float) (0.7 * bmp.getWidth());
        float y = (float) (0.9 * bmp.getHeight());
        drawTextOnBitmap(bmp, text, Color.WHITE, textSize, true, x, y);
    }

    private void drawLocationOnBitmap(Bitmap bmp, String text) {
        float textSize = (float) (bmp.getWidth() * 0.05);
        float x = (float) (0.1 * bmp.getWidth());
        float y = (float) (0.1 * bmp.getHeight());
        drawTextOnBitmap(bmp, text, Color.WHITE, textSize, true, x, y);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, "shared_img_" + System.currentTimeMillis(), null);

        if (path == null) {
            return null;
        }
        return Uri.parse(path);
    }

    @Override
    public void onSuccess(String photoUrl) {
        if (photoUrl == null || photoUrl.isEmpty()) {
            return;
        }

        photo = photoUrl;
    }

    @Override
    public void onFailure(Exception exception) {
    }


    @Override
    protected Bitmap doInBackground(String... location) {
        if (location == null || location.length == 0) {
            return null;
        }

        try {
            this.photoRemoteDataSource.getPhoto(location[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // DOWNLOAD THE IMAGE FROM THE FETCHED URL
        Bitmap bitmap;
        InputStream input = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(photo);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            this.exception = e;
            e.printStackTrace();
            bitmap = null;
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (bitmap == null) {
            return null;
        }

        // COPY THE BITMAP TO MAKE IT MUTABLE
        Bitmap tmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        drawLogoOnBitmap(tmp, context.getString(R.string.app_name));
        drawLocationOnBitmap(tmp, location[0]);

        return tmp;
    }

    protected void onPostExecute(Bitmap result) {
        if (result == null || exception != null) {
            return;
        }

        // GET LOCAL URI OF THE IMAGE
        Uri imgBitmapUri = getImageUri(context, result);

        if (imgBitmapUri == null) {
            return;
        }

        String shareText = this.isCompleted ?
                context.getString(R.string.share_trip_text_past) :
                context.getString(R.string.share_trip_text_coming);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imgBitmapUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(shareIntent,
                context.getString(R.string.share_trip_using));
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(chooserIntent);
    }
}
