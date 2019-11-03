package koock.koock.autobitly;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Patterns;
import android.webkit.URLUtil;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class ClipboardService extends Service {

    private ClipboardManager clipboardManager;

    String shorten_url = "null"; // default url
    String clipboardString = "null"; // default url
    SharedPref sharedPref;


    public ClipboardService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        sharedPref = SharedPref.getInstance(getApplicationContext());

        clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                ClipData clipboardData = clipboardManager.getPrimaryClip();
                if(clipboardManager.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN)) {
                    clipboardString = clipboardData.getItemAt(0).coerceToText(getApplicationContext()).toString();
                }
                if(Patterns.WEB_URL.matcher(clipboardString).matches()){

                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            // All your networking logic
                            // should be here
                            try {
                                shorten_url = requestShorten(clipboardString);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            clipboardManager.setPrimaryClip(ClipData.newPlainText("shortenUrl",shorten_url));
                        }
                    });
                }
            }
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        clipboardManager =null;
    }

    public String requestShorten(String url) throws IOException, JSONException {

        String access_token = sharedPref.getToken(); //generate access token in bitly dashboard
        URL bitlyEndpoint = new URL("https://api-ssl.bitly.com/v3/shorten?access_token="+ access_token +"&longUrl=" + url);
        HttpsURLConnection bitlyConnection = (HttpsURLConnection)bitlyEndpoint.openConnection();
        bitlyConnection.setRequestMethod("GET");
        bitlyConnection.setRequestProperty("Content-Type","application/json");
        bitlyConnection.setConnectTimeout(10000);
        bitlyConnection.setReadTimeout(10000);

        bitlyConnection.connect();


        int code = bitlyConnection.getResponseCode();

        if (code == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(bitlyConnection.getInputStream(),"UTF-8"));

            String json = readAll(in);

            JSONObject jsonObject = new JSONObject(json);

            return jsonObject.getJSONObject("data").getString("url");

        } else {
            throw new IOException();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
