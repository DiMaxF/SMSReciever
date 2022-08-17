package fl.oleg2013.smsreciever.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {

    public static class Data{

        private static final String dataFileName = "data.json";

        public static void SaveJson(String json, Context context) {
            try{
                File dir = new File(String.valueOf(context.getFilesDir()));
                dir.mkdirs();
                File file = new File(context.getFilesDir(), dataFileName);

                if (file.exists()) {
                    file.delete();
                    SaveJson(json, context);
                    return;
                }

                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(json);
                bufferedWriter.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }


        public static JSONObject GetJson(Context context) {
            try{
                File file = new File(context.getFilesDir(), dataFileName);
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null){
                    stringBuilder.append(line).append("\n");
                    line = bufferedReader.readLine();
                }
                bufferedReader.close();
                return new JSONObject(stringBuilder.toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        public static boolean DataExist(Context context){
            return new File(context.getFilesDir(), dataFileName).exists();
        }
    }

    public static class Server{

        public static void PostRequest(String content, Context context){
            try{
                URL url = new URL (Utils.Data.GetJson(context).getString("link"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject(content);
                try(OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonParam.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG" , conn.getResponseMessage());
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        public static void GetRequest(String params, Context context){
            try{
                String url = Utils.Data.GetJson(context).getString("link") + params;
                URL link = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) link.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                urlConnection.disconnect();
                Log.d("CODE" , String.valueOf(urlConnection.getResponseCode()));
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
