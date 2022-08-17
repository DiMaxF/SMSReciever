package fl.oleg2013.smsreciever.utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

        public static Runnable GetRequest(String params, Context context){
            return () -> {
                try{
                    String url = Data.GetJson(context).getString("link") + params;
                    URL link = new URL(url);
                    HttpURLConnection urlConnection = (HttpURLConnection) link.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    urlConnection.disconnect();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            };
        }
    }

}


