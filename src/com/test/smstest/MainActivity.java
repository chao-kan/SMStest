package com.test.smstest;

import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsMessage;
import android.telephony.gsm.SmsManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity 
{
	private TextView sender;
	private TextView content;
	
	private IntentFilter receiveFilter;
	private MessageReceiver messageReceiver;
	
	private EditText to;
	private EditText msgInput;
	private Button send;
	
	private IntentFilter sendFilter;
	private SendStatusReceiver sendStatusReceiver;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        sender = (TextView)findViewById(R.id.sender);
        content = (TextView)findViewById(R.id.content);
        
        receiveFilter = new IntentFilter();
        receiveFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
      //���ε�ϵͳ���Ž���
//      receiveFilter.setPriority(100);
        messageReceiver = new MessageReceiver();
        registerReceiver(messageReceiver, receiveFilter);
        
        sendFilter = new IntentFilter();
        sendFilter.addAction("SEND_SMS_ACTION");
        sendStatusReceiver = new SendStatusReceiver();
        registerReceiver(sendStatusReceiver, sendFilter);  //������ע��
        
        to = (EditText)findViewById(R.id.to);
        msgInput = (EditText)findViewById(R.id.msg_input);
        send = (Button)findViewById(R.id.send);
        send.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				SmsManager smsManager = SmsManager.getDefault();
				Intent sendIntent = new Intent("SEND_SMS_ACTION");
				PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, 0, sendIntent, 0);
				smsManager.sendTextMessage(to.getText().toString(), null, msgInput.getText().toString(), pi, null);
			}
		});
    }

    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
    	unregisterReceiver(messageReceiver);
    	unregisterReceiver(sendStatusReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    class MessageReceiver extends BroadcastReceiver
    {
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Bundle bundle = intent.getExtras();
			Object[] pdus = (Object[]) bundle.get("pdus");  //��ȡ������Ϣ
			SmsMessage[] messages = new SmsMessage[pdus.length];
			for(int i = 0 ;i <messages.length; i++)
			{
				messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
			}
			String address = messages[0].getOriginatingAddress();  //��ȡ���ͺ���
			String fullMessage = "";
			for(SmsMessage message : messages)
			{
				fullMessage += message.getMessageBody();
			}
			sender.setText(address);
			content.setText(fullMessage);
//			abortBroadcast();    //��ֹ�㲥���������������ȼ���ϵͳ�Դ��Ķ��Ž������յ����ţ�Ӧ�����ã�����
		}
    }
    
    
    class SendStatusReceiver extends BroadcastReceiver
    {
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if(getResultCode()==RESULT_OK)
			{
				//���ŷ��ͳɹ�
				Toast.makeText(context, "���ŷ��ͳɹ�", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(context, "���ŷ���ʧ��", Toast.LENGTH_SHORT).show();
			}
		}
    	
    }
}
