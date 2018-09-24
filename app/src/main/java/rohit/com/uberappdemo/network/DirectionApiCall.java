package rohit.com.uberappdemo.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import rohit.com.uberappdemo.interfaces.IGetDataCallBack;

public class DirectionApiCall {

    /*
    *   This function is using HttpURLConnection to fetch the data from Google Direction API using AsyncTask.
    * */

    public static void getDirectionDataFromAPI(String url, final IGetDataCallBack<String> iGetDataCallBack) {

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
                        StringBuilder sb = new StringBuilder();

                        String line = "";
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                        data = sb.toString();
                        br.close();
                    } catch (Exception e) {
                        e.printStackTrace();
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
                    e.printStackTrace();
                }
                return data;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                iGetDataCallBack.onSuccess(result);
            }
        }

        //AsyncTask execution trigger.
        new DownloadTask().execute(url);

    }


}
