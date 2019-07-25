package br.edu.ifms.petshop.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Exemplo de uso:
 * new ImageAsyncTask(ImageView).execute(url)
 */
public class ImageAsyncTask extends AsyncTask<String,Void,Bitmap> {
    ImageView imageView;

    public ImageAsyncTask(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String ...urls) {
        Bitmap image = null;
        String url = urls[0];
        try {
            InputStream inputStream = new URL(url).openStream();
            image = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);

        //ImageView imageView = (ImageView) view.findViewById(R.id.post_image);
        //imageView.setImageBitmap(bitmap);
        //this.cancel(true);
    }
}