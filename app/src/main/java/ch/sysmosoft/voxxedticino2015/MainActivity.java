package ch.sysmosoft.voxxedticino2015;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends ActionBarActivity {
    private DownloadImageAsyncTask downloadImageAsyncTask;
    private ImageButton downloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_voxxed);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(getSupportActionBar() != null) {
                getSupportActionBar().setElevation(0);
            }

            getWindow().setNavigationBarColor(getResources().getColor(R.color.primary_dark));
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Fade());
            getWindow().setAllowEnterTransitionOverlap(true);
        }

        // HTTP image download
        downloadButton = (ImageButton) findViewById(R.id.download_image);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage("https://voxxeddays.com/wp-content/uploads/2014/11/Ticino-300x93.jpg");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isDeviceRooted()) {
            downloadButton.setVisibility(View.VISIBLE);
        } else {
            downloadButton.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, getString(R.string.device_rooted), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isDeviceRooted() {
        /*
         * Logic to decide whether the device is rooted or not.
         *
         * FIXME: THIS IS A DEMO, IT IS NOT PRODUCTION READY.
         */
        String buildTags = android.os.Build.TAGS;

        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }
        return false;
    }

    private void downloadImage(String url) {
        downloadImageAsyncTask = new DownloadImageAsyncTask();
        downloadImageAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    // FIXME: do not use AsyncTasks in production code.
    class DownloadImageAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... fileUrls) {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(fileUrls[0]).openConnection();

                /*
                 * TODO: Business logic...
                 */

                return fileUrls[0];
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if(!"".equals(result)) {
                Toast.makeText(MainActivity.this, getString(R.string.download_complete), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.download_error), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
