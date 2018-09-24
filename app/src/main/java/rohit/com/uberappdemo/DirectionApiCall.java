package rohit.com.uberappdemo;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DirectionApiCall {

    public static void getDirectionDataFromAPI(String url, final IGetDataCallBack iGetDataCallBack) {

        class DownloadTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... urls) {

                String data = "";

                try {
                    InputStream iStream = null;
                    HttpURLConnection urlConnection = null;
                    try {
                        URL url = new URL(urls[0]);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.connect();

                        iStream = urlConnection.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                        StringBuffer sb = new StringBuffer();

                        String line = "";
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                        data = sb.toString();
                        br.close();
                    } catch (Exception e) {
                        Log.d("Exception", e.toString());
                    } finally {
                        if (iStream != null) {
                            iStream.close();
                        }
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }
                    return data;
                } catch (Exception e) {
                    Log.d("Background Task", e.toString());
                }
                return data;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                iGetDataCallBack.onSuccess(result);
            }
        }

        new DownloadTask().execute(url);

    }


}
