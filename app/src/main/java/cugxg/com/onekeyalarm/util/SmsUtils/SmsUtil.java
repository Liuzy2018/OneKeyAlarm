package cugxg.com.onekeyalarm.util.SmsUtils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.telephony.SmsManager;

import java.util.List;

import cugxg.com.onekeyalarm.util.ToastUtils;

public class SmsUtil {
    public static void sendSMS(String phoneNumber, String message, final Context context){
        // 获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager
                .getDefault();

        String SENT_SMS_ACTION = "SENT_SMS_ACTION";
        Intent sentIntent = new Intent(SENT_SMS_ACTION);
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, sentIntent, 0);
// register the Broadcast Receivers
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        ToastUtils.show(context,"短信发送成功");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        ToastUtils.show(context,"普通错误。是否需要加区号？");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        ToastUtils.show(context,"无线广播被明确地关闭");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        ToastUtils.show(context,"没有提供pdu");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        ToastUtils.show(context,"服务当前不可用");
                        break;
                }
            }
        }, new IntentFilter(SENT_SMS_ACTION));

        String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
        Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
        PendingIntent deliverPI = PendingIntent.getBroadcast(context, 0, deliverIntent, 0);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
                switch (getResultCode()){

                }
            }
        }, new IntentFilter(DELIVERED_SMS_ACTION));

        // 拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {

            smsManager.sendTextMessage(phoneNumber, null, text,sentPI,deliverPI);
        }
    }

    //彩信发送函数
    public static Intent sendMMS(String name,List<String> url,String number,String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        for (String s:url){
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(s));// uri为你的附件的uri
        }
        
        intent.putExtra("subject", name); //彩信的主题
        intent.putExtra("address", number); //彩信发送目的号码
        intent.putExtra("sms_body", content); //彩信中文字内容
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.setType("image/*");// 彩信附件类型
        intent.setClassName("com.android.mms","com.android.mms.ui.ComposeMessageActivity");

        return intent;
    }
}
