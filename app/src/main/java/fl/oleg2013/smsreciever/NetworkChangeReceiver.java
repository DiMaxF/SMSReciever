package fl.oleg2013.smsreciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import fl.oleg2013.smsreciever.utils.Utils;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (Utils.Server.isOnline(context)) {
            Log.e(context.getString(R.string.app_name), "Online Connect Intenet ");
            if(Utils.FileManager.FileExist(context.getString(R.string.unpostedSmsFileName), context)){
                try{
                    JSONObject json = Utils.FileManager.GetJson(context.getString(R.string.unpostedSmsFileName), context);
                    JSONArray array = json.getJSONArray("array");
                    for(int i = 0; i < array.length(); i++){
                        JSONObject obj = (JSONObject) array.get(i);
                        String params = obj.getString("sms");
                        JSONObject data = Utils.FileManager.GetJson(context.getString(R.string.settingsFileName), context);
                        String url = data.getString("link") + params;
                        SendJson(url);
                    }

                    Utils.FileManager.DeleteFile(context.getString(R.string.unpostedSmsFileName), context);
                }catch (Exception e){

                }
            }
        }
    }

    private void SendJson(String url){
        Thread thread = new Thread(Utils.Server.GetRequest(url));
        thread.start();
    }
}
