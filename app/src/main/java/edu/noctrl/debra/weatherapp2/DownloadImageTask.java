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
            File file;
            String fileName = Uri.parse(urldisplay).getLastPathSegment();
            file = new File(ctx.getCacheDir(), fileName);
            // file = File.createTempFile(fileName, null, context.getCacheDir());
            file.createNewFile();
            FileOutputStream fs = ctx.openFileOutput(fileName,Context.MODE_PRIVATE);
            result.compress(Bitmap.CompressFormat.PNG, 100, fs);
            fs.flush();
            fs.close();

            System.out.println("File path is " + file.getAbsolutePath());
            System.out.println("File name is " + file.getName());
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
    public File getTempFile(Context context, String url, Bitmap bm) throws IOException {
        File file;
        String fileName = Uri.parse(url).getLastPathSegment();
        file = new File(context.getCacheDir(), fileName);
           // file = File.createTempFile(fileName, null, context.getCacheDir());
      //  file.createNewFile();
        FileOutputStream fs = ctx.openFileOutput(fileName,Context.MODE_PRIVATE);
        bm.compress(Bitmap.CompressFormat.PNG, 100, fs);
        fs.flush();
        fs.close();

        System.out.println("File path is " + file.getAbsolutePath());
        System.out.println("File name is " + file.getName());
        return file;

    }
}