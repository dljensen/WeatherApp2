package edu.noctrl.debra.weatherapp2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    Context ctx;
    String urldisplay;

    public DownloadImageTask(ImageView bmImage, Context ctx) {
        this.bmImage = bmImage;
        this.ctx = ctx;
    }

    protected Bitmap doInBackground(String... urls) {
        urldisplay = urls[0];
        Bitmap mIcon11 = null;
        String fileName = Uri.parse(urldisplay).getLastPathSegment(); //added this 5/3
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
            File cacheImage = new File(ctx.getCacheDir(), fileName); //create file
            FileOutputStream out = new FileOutputStream(cacheImage); //add to output stream
            mIcon11.compress(Bitmap.CompressFormat.PNG, 100, out); //compress the image
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);

    }

}
