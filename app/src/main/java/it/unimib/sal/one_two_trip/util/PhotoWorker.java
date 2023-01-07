package it.unimib.sal.one_two_trip.util;

import static it.unimib.sal.one_two_trip.util.Constants.FONT_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.IMAGE_MIME;
import static it.unimib.sal.one_two_trip.util.Constants.KEY_COMPLETED;
import static it.unimib.sal.one_two_trip.util.Constants.KEY_LOCATION;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.source.PhotoCallback;
import it.unimib.sal.one_two_trip.data.source.PhotoRemoteDataSource;

public class PhotoWorker extends Worker implements PhotoCallback {

    private final Context context;
    private final PhotoRemoteDataSource photoRemoteDataSource;

    private String photo;
    private Exception exception;

    public PhotoWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.photoRemoteDataSource = ServiceLocator.getInstance().getPhotoRemoteDataSource();
        this.photoRemoteDataSource.setPhotoCallback(this);
    }

    private void drawTextOnBitmap(Bitmap bmp, String text, float textSize, boolean textItalic,
                                  float x, float y) {
        Canvas canvas = new Canvas(bmp);

        // TEXT
        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        Typeface typeface = Typeface.create(FONT_NAME, textItalic ?
                Typeface.BOLD_ITALIC : Typeface.BOLD);
        paintText.setColor(Color.WHITE);
        paintText.setFakeBoldText(true);
        paintText.setTextSize(textSize);
        paintText.setTypeface(typeface);

        float w = paintText.measureText(text, 0, text.length());

        // RECTANGLE BEHIND
        Paint paintRect = new Paint();
        paintRect.setColor(Color.BLACK);
        paintRect.setAlpha(80);

        // DRAW
        canvas.drawRoundRect((x - 10), (float) (y - (1.5 * textSize)), (x + w + 10), (y + textSize),
                10, 10, paintRect);
        canvas.drawText(text, x, y, paintText);
    }

    private void drawLogoOnBitmap(@NonNull Bitmap bmp, String text) {
        float textSize = (float) (bmp.getWidth() * 0.04);
        float x = (float) (0.7 * bmp.getWidth());
        float y = (float) (0.9 * bmp.getHeight());
        this.drawTextOnBitmap(bmp, text, textSize, true, x, y);
    }

    private void drawLocationOnBitmap(@NonNull Bitmap bmp, String text) {
        float textSize = (float) (bmp.getWidth() * 0.05);
        float x = (float) (0.1 * bmp.getWidth());
        float y = (float) (0.1 * bmp.getHeight());
        this.drawTextOnBitmap(bmp, text, textSize, false, x, y);
    }

    private Uri getImageUri(Bitmap inImage) {
        if (inImage == null) return null;

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(this.context.getContentResolver(),
                inImage, "shared_img_" + System.currentTimeMillis(), null);

        if (path == null) {
            return null;
        }
        return Uri.parse(path);
    }

    @NonNull
    private Boolean generateSharePhoto(String location, Boolean isCompleted) {
        // PRELIMINARY CHECKS
        if (location == null || location.isEmpty()) {
            return false;
        }

        try {
            this.photoRemoteDataSource.getPhoto(location);
        } catch (IOException e) {
            e.printStackTrace();
            this.exception = e;
        }

        if (this.photo == null || this.photo.isEmpty() || this.exception != null) {
            return false;
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
            e.printStackTrace();
            this.exception = e;
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

        if (bitmap == null || this.exception != null) {
            return false;
        }

        // COPY THE BITMAP TO MAKE IT MUTABLE
        Bitmap mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        this.drawLogoOnBitmap(mutable, context.getString(R.string.app_name));
        this.drawLocationOnBitmap(mutable, location);

        // GET LOCAL URI OF THE IMAGE
        Uri imgBitmapUri = this.getImageUri(mutable);

        if (imgBitmapUri == null) {
            return false;
        }

        String shareText = isCompleted ? context.getString(R.string.share_trip_text_past) :
                context.getString(R.string.share_trip_text_coming);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imgBitmapUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.setType(IMAGE_MIME);
        Intent chooserIntent = Intent.createChooser(shareIntent,
                context.getString(R.string.share_trip_using));
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        this.context.startActivity(chooserIntent);
        return true;
    }

    @NonNull
    @Override
    public Result doWork() {
        String location = getInputData().getString(KEY_LOCATION);
        boolean isCompleted = getInputData().getBoolean(KEY_COMPLETED, false);

        boolean result = this.generateSharePhoto(location, isCompleted);
        if (result) {
            return Result.success();
        } else {
            return Result.failure();
        }
    }

    @Override
    public void onSuccess(String photoUrl) {
        if (photoUrl == null || photoUrl.isEmpty()) {
            photo = null;
            return;
        }

        photo = photoUrl;
    }

    @Override
    public void onFailure(Exception exception) {
        this.exception = exception;
    }
}
