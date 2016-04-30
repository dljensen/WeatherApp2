package edu.noctrl.debra.weatherapp2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
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
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);

        //save File to Cache
        try {
            getTempFile(ctx, urldisplay);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /***************************************
     * Below method taken and modified from
     * http://developer.android.com/training/basics/data-storage/files.html#WriteInternalStorage
     * @param context
     * @param url
     * @return
     * @throws IOException
     ***************************************/
    public File getTempFile(Context context, String url) throws IOException {
            File file;
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        return file;

    }
}