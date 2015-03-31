package ch.sysmosoft.voxxedticino2015;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends ActionBarActivity {
    private DownloadImageAsyncTask downloadImageAsyncTask;

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
        ((Button) findViewById(R.id.download_image)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isDeviceRooted()) {
                    downloadImage("http://hotel-icastelli.com/files/2013/08/piazza-castello.jpg");
                } else {
                    Toast.makeText(MainActivity.this, "Download complete!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isDeviceRooted() {
        /*
         * Logic to decide whether the device is rooted or not.
         */

        return true;
    }

    private void downloadImage(String url) {
        downloadImageAsyncTask = new DownloadImageAsyncTask();
        downloadImageAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    class DownloadImageAsyncTask extends AsyncTask<String, Void, String> {
        private File tempFile;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... fileUrls) {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(fileUrls[0]).openConnection();

                tempFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "HTTP_openConnection.jpg");

                if(tempFile.exists()) {
                    tempFile.delete();
                }
                tempFile.createNewFile();
                FileOutputStream out = new FileOutputStream(tempFile.getAbsolutePath());

                // Download the file
                InputStream in = conn.getInputStream();
                byte[] buffer = new byte[1024 * 4];
                int n = 0;
                while (-1 != (n = in.read(buffer))) {
                    if (!isCancelled()) {
                        out.write(buffer, 0, n);
                    }
                }
                in.close();
                out.close();
                return tempFile.getName();
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

        @Override
        protected void onCancelled(String result) {
            if (result != null) {
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            }
        }
    }
}
