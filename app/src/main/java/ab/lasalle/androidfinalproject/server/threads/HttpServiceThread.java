package ab.lasalle.androidfinalproject.server.threads;


import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ab.lasalle.androidfinalproject.server.callbacks.ApiResponseCallback;
import lombok.Data;
import lombok.Setter;

@Data
public class HttpServiceThread extends Thread {

    private String apiName;

    private String serviceResponse;

    //private String requestBody;
    private String requestBody;

    private ApiResponseCallback serverResponseNotifier;


    private HttpURLConnection connection;

    public HttpServiceThread(String apiName, String requestBody, ApiResponseCallback serverResponseNotifier) {
        this.apiName = apiName;
        this.requestBody = requestBody;
        this.serverResponseNotifier = serverResponseNotifier;
    }

    @Override
    public void run() {
        initiateServerCommnunication();
    }

    private void postMessage(String webResponse) {
        try {
            JSONObject responseObject = new JSONObject(webResponse);

            serverResponseNotifier.onApiResponseRecieved(responseObject);
        } catch (Exception e) {
            serverResponseNotifier.onApiResponseRecieved(emergencyResponse("998", "Problem in Server response conversion"));

        }


    }


    private void initiateServerCommnunication() {

        try {

            System.setProperty("http.keepAlive", "false");
            URL url = new URL("http://192.168.0.144:8080/" + apiName);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(60000);
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "application/json");
            // connection.setRequestProperty("auth_token", Token);
            // conn.setRequestProperty("Content-Type",
            // "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = null;

            out = connection.getOutputStream();

            out.write(requestBody.getBytes());

            out.close();

            connection.connect();

            InputStream instream = null;
int requestCode = connection.getResponseCode();
            instream = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    instream));
            String line;

            StringBuilder builder = new StringBuilder();


            while ((line = reader.readLine()) != null) {
                builder.append(line);
                serviceResponse = builder.toString();
            }

            postMessage(serviceResponse);


        } catch (Exception e) {

            serverResponseNotifier.onApiResponseRecieved(emergencyResponse("999", "Problem in Server Communication"));
        }


    }


    private JSONObject emergencyResponse(String status, String message) {

        JSONObject obj = new JSONObject();

        try {
            obj.put("status", status);
            obj.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return obj;
    }


}



