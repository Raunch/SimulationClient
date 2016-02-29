package com.raunch.simulationcharge;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import cn.cmgame.billing.api.BillingResult;
import cn.cmgame.billing.api.GameInterface;
import cn.cmgame.billing.api.PropsType;


public class MainActivity extends ListActivity {

    public static final String LOG = "test";

    static final String[] BUTTONS = new String[]{
            "测试机费点1:001",
            "测试机费点2:002",
            "测试机费点3:003",
            "测试机费点4:004",
            "测试机费点5:005",
            "测试机费点6:006",
            "测试机费点7:007",
            "测试机费点8:008",
            "测试机费点9:009",
            "测试机费点10:010",
            "测试机费点11:011",
            "测试机费点12:012",
            "测试机费点13:013",
            "测试机费点14:014",
            "测试机费点15:015",
            "测试机费点16:016",
            "测试机费点17:017",
            "测试机费点18:018",
            "测试机费点19:019",
            "测试机费点20:020",
            "测试机费点21:021",
            "测试机费点22:022",
            "测试机费点23:023",
            "测试机费点24:024",
            "测试机费点25:025",
            "测试机费点26:026",
            "测试机费点27:027",
            "测试机费点28:028",
            "测试机费点29:029",
            "测试机费点30:030",
            "测试机费点31:031",
            "测试机费点32:032",
            "测试机费点33:033",
            "测试机费点34:034",
            "测试机费点35:035",
            "测试机费点36:036",
            "测试机费点37:037",
            "测试机费点38:038",
            "测试机费点39:039",
            "测试机费点40:040"
    };

    final GameInterface.IPayCallback payCallback = new GameInterface.IPayCallback() {
        @Override
        public void onResult(int resultCode, String billingIndex, Object obj) {
            String result = "";
            switch (resultCode) {
                case BillingResult.SUCCESS:
                    result = "购买道具：[" + billingIndex + "] 成功！";
                    break;
                case BillingResult.FAILED:
                    result = "购买道具：[" + billingIndex + "] 失败！";
                    break;
                default:
                    result = "购买道具：[" + billingIndex + "] 取消！";
                    break;
            }
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    };

    Handler mDelaydedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i(LOG,"The service time is " + System.currentTimeMillis());
            startService(new Intent(MainActivity.this, NotificationService.class));
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("com.njck.payment".equals(action)) {
                String payindex = intent.getStringExtra("index");
                Log.i(LOG, "Go here do payment and index is " + payindex);
                GameInterface.doBilling(MainActivity.this, true, true, payindex,
                        null, payCallback);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //不显示程序的标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //不显示系统的标题栏
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        GameInterface.initializeApp(this);
        // 计费结果的监听处理，合作方通常需要在收到SDK返回的onResult时，告知用户的购买结果


        setListAdapter(new ArrayAdapter<String>(this, R.layout.main_menu_item, BUTTONS));
        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String index = getBillingIndex(position);
                if (isRightCode(index)) {
                    boolean test= GameInterface.getActivateFlag(index);
                    if (test) {
                        Toast.makeText(MainActivity.this, "Get flag ture", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Get flag false", Toast.LENGTH_SHORT).show();
                    }
                    GameInterface.doBilling(MainActivity.this, GameInterface.UiType.COMPACT,
                            PropsType.RIGHTS, index, null,
                            payCallback);
                } else if(isForceCode(index)) {
                    if(GameInterface.getActivateFlag(index)){
                        Toast.makeText(MainActivity.this, "超级达人 已购买过", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    GameInterface.doBilling(MainActivity.this, true, false, index, null, payCallback);
                }  else {
                    GameInterface.doBilling(MainActivity.this, true, true, index,
                            null, payCallback);
                }
            }
        });

        Message message = new Message();
        mDelaydedHandler.sendMessageDelayed(message, 5000);
        Log.i(LOG,"The time is " + System.currentTimeMillis());
        IntentFilter myFilter = new IntentFilter();
        myFilter.addAction("com.njck.payment");
        registerReceiver(mReceiver, myFilter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private String getBillingIndex(int i) {
        String info = BUTTONS[i];
        String[] indexs = info.split(":");
        Log.i("IAP", "The index is " + indexs[1]);
        return indexs[1];
    }

    private boolean isForceCode(String billingIndex) {
        if (billingIndex.equals("047")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isRightCode(String billingIndex) {
        if (billingIndex.equals("040") || billingIndex.equals("041") || billingIndex.equals("042")) {
            return true;
        } else {
            return false;
        }
    }
}
