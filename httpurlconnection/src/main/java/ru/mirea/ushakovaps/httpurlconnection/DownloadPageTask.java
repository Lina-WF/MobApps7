package ru.mirea.ushakovaps.httpurlconnection;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.mirea.ushakovaps.httpurlconnection.databinding.ActivityMainBinding;

public class DownloadPageTask extends AsyncTask<String, Void, String> {
    private ActivityMainBinding binding;
    public DownloadPageTask(ActivityMainBinding binding) {
        this.binding = binding;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(String... urls) {
        try {
            return downloadIpInfo(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }
    @Override
    protected void onPostExecute(String result) {
        Log.d(MainActivity.class.getSimpleName(), result);
        try {
            JSONObject responseJson = new JSONObject(result);
            Log.d(MainActivity.class.getSimpleName(), "Response: " + responseJson);
            String city = responseJson.getString("city");
            binding.textViewCity.setText(city);
            String region = responseJson.getString("region");
            binding.textViewRegion.setText(region);
            String country = responseJson.getString("country");
            binding.textViewCountry.setText(country);

            String loc = responseJson.getString("loc");
            String latitude = loc.split(",")[0];
            String longitude = loc.split(",")[1];
            String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current_weather=true";
            WebView webView = binding.webView;
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.loadUrl(url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onPostExecute(result);
    }

    private String downloadIpInfo(String address) throws IOException {
        InputStream inputStream = null;
        String data = "";
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(100000);
            connection.setConnectTimeout(100000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                inputStream = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int read = 0;
                while ((read = inputStream.read()) != -1) {
                    bos.write(read); }
                bos.close();
                data = bos.toString();
            } else {
                data = connection.getResponseMessage()+". Error Code: " + responseCode;
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return data;
    }
}
