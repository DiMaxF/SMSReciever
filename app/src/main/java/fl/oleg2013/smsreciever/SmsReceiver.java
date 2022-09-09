package fl.oleg2013.smsreciever;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fl.oleg2013.smsreciever.utils.Utils;

public class SmsReceiver extends BroadcastReceiver {

    public static final String pdu_type = "pdus";

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        String strMessage = "";
        String format = bundle.getString("format");
        Object[] pdus = (Object[]) bundle.get(pdu_type);
        if (pdus != null) {
            boolean isVersionM = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                if (isVersionM) msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                 else msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                strMessage += "SMS from " + msgs[i].getOriginatingAddress();
                strMessage += " :" + msgs[i].getMessageBody() + "\n";
                Log.d(context.getString(R.string.app_name), "onReceive: " + strMessage);
                Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
                String params = "?sms=" + msgs[i].getMessageBody() + "&tel=" + msgs[i].getOriginatingAddress();

                if(Utils.Server.isOnline(context)) SendToServer(context, params);
                else SaveLocal(context, params);
            }
        }
    }

    private void SendToServer(Context context, String params){
        JSONObject json = Utils.FileManager.GetJson(context.getString(R.string.settingsFileName), context);
        try {
            String url = json.getString("link") + params;
            Thread thread = new Thread(Utils.Server.GetRequest(url));
            thread.start();
        } catch (JSONException e) {
            e.printStackTrace();
            SaveLocal(context, params);
        }
    }

    private void SaveLocal(Context context, String params){
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();

        if(Utils.FileManager.FileExist(context.getString(R.string.unpostedSmsFileName), context)){
            json = Utils.FileManager.GetJson(context.getString(R.string.unpostedSmsFileName), context);
            try {
                array = json.getJSONArray("array");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            String unpostedSms = "{\"sms\":\"" + params + "\"}";
            JSONObject jsonSms = new JSONObject(unpostedSms);
            array.put(jsonSms);
            json.put("array", array);
            Utils.FileManager.SaveJson(json.toString(), context.getString(R.string.unpostedSmsFileName), context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
