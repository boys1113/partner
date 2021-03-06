package com.a3did.partner.partner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.a3did.partner.account.PartnerUserInfo;
import com.a3did.partner.account.UserManager;
import com.a3did.partner.adapterlist.AchievementListData;
import com.a3did.partner.adapterlist.AssistantListData;
import com.a3did.partner.adapterlist.RewardListData;
import com.a3did.partner.fragmentlist.AccountFragment;
import com.a3did.partner.fragmentlist.AchievementFragment;
import com.a3did.partner.fragmentlist.AssistantFragment;
import com.a3did.partner.fragmentlist.CompletedListFragment;
import com.a3did.partner.fragmentlist.DefaultFragment;
import com.a3did.partner.fragmentlist.MissedListFragment;
import com.a3did.partner.fragmentlist.RewardFragment;
import com.a3did.partner.fragmentlist.SafetyFragment;
import com.a3did.partner.partner.utils.AudioWriterPCM;
import com.a3did.partner.recosample.RecoRangingActivity;
import com.google.firebase.auth.UserInfo;
import com.naver.speech.clientapi.SpeechConfig;
import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconRegion;

import net.daum.mf.speech.api.TextToSpeechClient;
import net.daum.mf.speech.api.TextToSpeechListener;
import net.daum.mf.speech.api.TextToSpeechManager;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends RecoRangingActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AccountFragment.OnFragmentInteractionListener,
        AchievementFragment.OnFragmentInteractionListener,
        AssistantFragment.OnFragmentInteractionListener,
        CompletedListFragment.OnFragmentInteractionListener,
        DefaultFragment.OnFragmentInteractionListener,
        MissedListFragment.OnFragmentInteractionListener,
        RewardFragment.OnFragmentInteractionListener,
        SafetyFragment.OnFragmentInteractionListener, TextToSpeechListener
{



    private long prevtime = 0;
    private long currenttime = 0;
    ////////////

    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int UART_PROFILE_READY = 10;
    public static final String TAG = "nRFUART";
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int STATE_OFF = 10;

    TextView mRemoteRssiVal;
    RadioGroup mRg;
    private int mState = UART_PROFILE_DISCONNECTED;
    private UartService mService = null;
    private BluetoothDevice mDevice = null;
    private BluetoothAdapter mBtAdapter = null;
    private ListView messageListView;
    private ArrayAdapter<String> listAdapter;
    private Button btnConnectDisconnect,btnSend;
    private EditText edtMessage;

    public InteractionManager mInteractionManager;

    ////////////////

    DefaultFragment mDefaultFragment;
    AccountFragment mAccountFragemt;
    AssistantFragment mAssistantFragment;
    AchievementFragment mAchievementFragment;
    SafetyFragment mSafetyFragment;
    RewardFragment mRewardFragment;
    CompletedListFragment mCompletedListFragment;
    MissedListFragment mMissedListFragment;
    Toolbar toolbar;
    FloatingActionButton fab;
    int mFragmentID;

    public boolean isOnPause = false;

    public static final String DAUM_KEY = "3d03690d5e27935dc3be9bb14c52ee98";

    private NaverRecognizer mNaverRecognizer;
    private String mResult;
    private AudioWriterPCM writer;
    public static TextToSpeechClient ttsClient;
    public UserManager mUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(toolbar);




        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
//        service_init();


//        String deviceAddress = "DB:E7:DF:00:57:83";
//        mService.connect(deviceAddress);

        btnConnectDisconnect=(Button) findViewById(R.id.fab2);
        btnConnectDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBtAdapter.isEnabled()) {
                    Log.i(TAG, "onClick - BT not enabled yet");
//                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                }
                else {
                    if (btnConnectDisconnect.getText().equals("Connect")){

                        //Connect button pressed, open DeviceListActivity class, with popup windows that scan for devices

//                        Intent newIntent = new Intent(com.a3did.partner.partner.MainActivity.this, DeviceListActivity.class);
//                        startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);

                        String deviceAddress = "DB:E7:DF:00:57:83";
                        mService.connect(deviceAddress);
                    } else {
                        //Disconnect button pressed
                        if (mDevice!=null)
                        {
                            mService.disconnect();

                        }
                    }
                }
            }
        });

        btnConnectDisconnect.setVisibility(View.INVISIBLE);


        //User Management Instance
        mUserManager = UserManager.getInstance();
        mUserManager.setContext(this);
        //기본 정보 기입
        mUserManager.generateInformation();
        mUserManager.setCurrentUserInfo(0);




        //intent = new Intent(this, RecoBackgroundRangingService.class);
        //startService(intent);

        //Newton talk API 연동
        TextToSpeechManager.getInstance().initializeLibrary(getApplicationContext());

        if (ttsClient != null && ttsClient.isPlaying()) {
            ttsClient.stop();
            return;
        }

        String speechMode;
        speechMode = TextToSpeechClient.NEWTONE_TALK_2;
        speechMode = TextToSpeechClient.NEWTONE_TALK_1;

        String voiceType;
        voiceType = TextToSpeechClient.VOICE_MAN_READ_CALM;
        voiceType = TextToSpeechClient.VOICE_WOMAN_READ_CALM;
        voiceType = TextToSpeechClient.VOICE_MAN_DIALOG_BRIGHT;
        voiceType = TextToSpeechClient.VOICE_WOMAN_DIALOG_BRIGHT;


        double speechSpeed;
        speechSpeed = 0.5;
        speechSpeed = 2.0;
        speechSpeed = 1.0;
        ttsClient = new TextToSpeechClient.Builder()
                .setApiKey(DAUM_KEY)
                .setSpeechMode(speechMode)
                .setSpeechSpeed(speechSpeed)
                .setSpeechVoice(voiceType)
                .setListener(MainActivity.this)
                .build();
        //ttsClient.play("파트너 앱 실행합니다.");

        //Naver API 연동


        //Interaction Initialization
        mInteractionManager = InteractionManager.getInstance();
        mInteractionManager.Init(ttsClient, this);






        //Initialize fragment
        mDefaultFragment = new DefaultFragment();
        mDefaultFragment.setContext(this);
        mAccountFragemt = new AccountFragment();
        mAssistantFragment = new AssistantFragment();
        mAchievementFragment = new AchievementFragment();
        mSafetyFragment = new SafetyFragment();
        mSafetyFragment.setContext(this);
        mRewardFragment = new RewardFragment();
        mCompletedListFragment = new CompletedListFragment();
        mMissedListFragment = new MissedListFragment();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toolbar.setTitle(mDefaultFragment.mName);
                mFragmentID = R.layout.fragment_default;
                getSupportFragmentManager().beginTransaction().replace(R.id.partner_container,mDefaultFragment).commit();
            }
        });
        toolbar.setSubtitle(mUserManager.getCurrentUserName());
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                               String title = "";
                String deviceAddress = "DB:E7:DF:00:57:83";
                mService.connect(deviceAddress);
                switch (mFragmentID)
                {
                    case R.layout.fragment_default:
                        title = "Partner";


                        break;
                    case R.layout.fragment_assistant:
                        title = "Assistant";
                        ArrayList<AssistantListData> list = mUserManager.getCurrentUserInfo().mScheduleInfoList;
                        if(list.size() != 0){
                            AssistantListData data = list.get(0);
                            if(!ttsClient.isPlaying()){
                                ttsClient.play("지금, " + data.getTitle() + ",를 해야 할 시간이예요");
                                //mInteractionManager.hapticpress = 1;
                            }
                        }


                        break;
                    case R.layout.fragment_achievement:
                        title = "Achievement";
                        ArrayList<AchievementListData> list1 = mUserManager.getCurrentUserInfo().mAchievementInfoList;
                        if(list1.size() != 0){
                            AchievementListData data = list1.get(0);
                            if(!ttsClient.isPlaying()){
                                ttsClient.play("혹시, " + data.getTitle() + ",를 다 하셨나요?");
                                //mInteractionManager.hapticpress2 = 1;
                            }
                        }
                        break;
                    case R.layout.fragment_safety:
                        title = "Safety";
                        UserManager userManager = UserManager.getInstance();
                        PartnerUserInfo userInfo = userManager.getCurrentUserInfo();
                       // RewardListData data = userInfo.mRewardInfoList.get(index);

                        sendMessage(userInfo.mParentPhoneNumber,"[Partner] " + userInfo.mName +"이가 지정된 위험지역에 들어갔어요!! 빨리 연락해보세요~!");

                        break;
                    case R.layout.fragment_reward:
                        title = "Reward";
                        break;
                    case R.layout.fragment_account:
                        title = "Account";
                        break;
                    case R.layout.fragment_completed_list:
                        title = "CompletedList";
                        break;
                    case R.layout.fragment_missed_list:
                        title = "MissedList";
                        break;
                    default:

                };

                //DialogHtmlView(title);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                if(!mInteractionManager.mNaverRecognizer.getSpeechRecognizer().isRunning())
                    mInteractionManager.mNaverRecognizer.recognize();
                else
                    mInteractionManager.mNaverRecognizer.getSpeechRecognizer().stop();;
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        toolbar.setTitle(mDefaultFragment.mName);
        getSupportFragmentManager().beginTransaction().replace(R.id.partner_container,mDefaultFragment).commit();
        mFragmentID = R.layout.fragment_default;


//        TextView titleTextView = (TextView) findViewById(R.id.default_star_num) ;
//        titleTextView.setText(mUserManager.getCurrentUserInfo().mStarNumber);



    }
    public void sendMessage(String number, String text){

        if (number.length()>0 && text.length()>0) {
            sendSMS(number, text);
        }
    }
    public void sendSMS(String smsNumber, String smsText){
        PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT_ACTION"), 0);
        PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED_ACTION"), 0);



        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        // 도착 완료
                        Log.d("Partner", "도착 완료");
                        //Toast.makeText(mContext, "SMS 도착 완료", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:

                        // 도착 안됨
                        Log.d("Partner", "도착 안됨");
                        //Toast.makeText(mContext, "SMS 도착 실패", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_DELIVERED_ACTION"));

        SmsManager mSmsManager = SmsManager.getDefault();
        mSmsManager.sendTextMessage(smsNumber, null, smsText, sentIntent, deliveredIntent);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    ////////////////////////////////////////////////////////////////





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    public void transitionFragment(int id)
    {
        switch (id)
        {
            /*case R.id.nav_main:
                getSupportFragmentManager().beginTransaction().replace(R.id.partner_container,mDefaultFragment).commit();
                toolbar.setTitle(mDefaultFragment.mName);
                break;*/
            case R.id.nav_assistant:
                getSupportFragmentManager().beginTransaction().replace(R.id.partner_container,mAssistantFragment).commit();
                toolbar.setTitle(mAssistantFragment.mName);
                mFragmentID = R.layout.fragment_assistant;
                mInteractionManager.mSystemMode = InteractionManager.MenuType.ASSISTANT;
                break;
            case R.id.nav_achievement:
                getSupportFragmentManager().beginTransaction().replace(R.id.partner_container,mAchievementFragment).commit();
                toolbar.setTitle(mAchievementFragment.mName);
                mFragmentID = R.layout.fragment_achievement;
                mInteractionManager.mSystemMode = InteractionManager.MenuType.ACHIEVEMENT;
                break;
            case R.id.nav_safety:
                getSupportFragmentManager().beginTransaction().replace(R.id.partner_container,mSafetyFragment).commit();
                toolbar.setTitle(mSafetyFragment.mName);
                mFragmentID = R.layout.fragment_safety;
                mInteractionManager.mSystemMode = InteractionManager.MenuType.SAFETY;
                break;
            case R.id.nav_reward:
                getSupportFragmentManager().beginTransaction().replace(R.id.partner_container,mRewardFragment).commit();
                toolbar.setTitle(mRewardFragment.mName);
                mFragmentID = R.layout.fragment_reward;
                mInteractionManager.mSystemMode = InteractionManager.MenuType.REWARD;
                break;
            case R.id.nav_account:
                getSupportFragmentManager().beginTransaction().replace(R.id.partner_container,mAccountFragemt).commit();
                toolbar.setTitle(mAccountFragemt.mName);
                mFragmentID = R.layout.fragment_account;
                mInteractionManager.mSystemMode = InteractionManager.MenuType.ACCOUNT;
                break;
            case R.id.nav_completed_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.partner_container,mCompletedListFragment).commit();
                toolbar.setTitle(mCompletedListFragment.mName);
                mFragmentID = R.layout.fragment_completed_list;
                mInteractionManager.mSystemMode = InteractionManager.MenuType.COMPLETED;
                break;
            case R.id.nav_missed_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.partner_container,mMissedListFragment).commit();
                toolbar.setTitle(mMissedListFragment.mName);
                mFragmentID = R.layout.fragment_missed_list;
                mInteractionManager.mSystemMode = InteractionManager.MenuType.MISSED;
                break;

            default:
                mInteractionManager.mSystemMode = InteractionManager.MenuType.DEFAULT;
        };



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        transitionFragment(id);


        return true;
    }
    private void DialogHtmlView(String title) {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("Dialog Test");
        ab.setTitle(title);
        ab.setPositiveButton("ok", null);
        ab.show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    // Declare handler for handling SpeechRecognizer thread's Messages.
    static class RecognitionHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        RecognitionHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null && !activity.isOnPause) {

                activity.mInteractionManager.handleVoiceMessage(msg);
            }
        }
    }
//     Declare handler for handling SpeechRecognizer thread's Messages.
    static class HapticHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        HapticHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null && !activity.isOnPause) {

                activity.mInteractionManager.handlehapticMessage(msg);
            }
        }
    }


    //////////////////////////////Newton talk Listener//////
    @Override
    public void onFinished() {
        int intSentSize = ttsClient.getSentDataSize();
        int intRecvSize = ttsClient.getReceivedDataSize();

        final String strInacctiveText = "onFinished() SentSize : " + intSentSize + " RecvSize : " + intRecvSize;

        Log.i("Partner", strInacctiveText);
        /*if (!isRunning) {
            // Start button is pushed when SpeechRecognizer's state is inactive.
            // Run SpeechRecongizer by calling recognize().
            mNaverRecognizer.recognize();
            isRunning = true;
        }*/
        //ttsClient = null;
    }

    @Override
    public void onError(int code, String s) {
        handleError(code);

        //ttsClient = null;
    }

    private void handleError(int errorCode) {
        String errorText;
        switch (errorCode) {
            case TextToSpeechClient.ERROR_NETWORK:
                errorText = "네트워크 오류";
                break;
            case TextToSpeechClient.ERROR_NETWORK_TIMEOUT:
                errorText = "네트워크 지연";
                break;
            case TextToSpeechClient.ERROR_CLIENT_INETRNAL:
                errorText = "음성합성 클라이언트 내부 오류";
                break;
            case TextToSpeechClient.ERROR_SERVER_INTERNAL:
                errorText = "음성합성 서버 내부 오류";
                break;
            case TextToSpeechClient.ERROR_SERVER_TIMEOUT:
                errorText = "음성합성 서버 최대 접속시간 초과";
                break;
            case TextToSpeechClient.ERROR_SERVER_AUTHENTICATION:
                errorText = "음성합성 인증 실패";
                break;
            case TextToSpeechClient.ERROR_SERVER_SPEECH_TEXT_BAD:
                errorText = "음성합성 텍스트 오류";
                break;
            case TextToSpeechClient.ERROR_SERVER_SPEECH_TEXT_EXCESS:
                errorText = "음성합성 텍스트 허용 길이 초과";
                break;
            case TextToSpeechClient.ERROR_SERVER_UNSUPPORTED_SERVICE:
                errorText = "음성합성 서비스 모드 오류";
                break;
            case TextToSpeechClient.ERROR_SERVER_ALLOWED_REQUESTS_EXCESS:
                errorText = "허용 횟수 초과";
                break;
            default:
                errorText = "정의하지 않은 오류";
                break;
        }

        final String statusMessage = errorText + " (" + errorCode + ")";

        Log.i("Newton talk error", statusMessage);
    }
    ///////////////////////////////////////////////////////

     public static boolean login = false;
     @Override
     public void didRangeBeaconsInRegion(Collection<RECOBeacon> recoBeacons, RECOBeaconRegion recoRegion) {
         Log.i("RECORangingActivity", "didRangeBeaconsInRegion() region: " + recoRegion.getUniqueIdentifier() + ", number of beacons ranged: " + recoBeacons.size());



         if(recoBeacons.size() != 0)
         {
             mInteractionManager.sendBeaconData(recoBeacons);
             RECOBeacon beco = recoBeacons.iterator().next();
             Log.d("test", beco.getProximityUuid());
             PartnerUserInfo info = mUserManager.getCurrentUserInfo();
             /*if(info.mBeaconInfo.equals(beco.getProximityUuid()))
             {
                 if(!ttsClient.isPlaying())
                    ttsClient.play("안녕하세요!");
             }*/
             if(!login) {
                 if(!ttsClient.isPlaying())
                     login = true;
                 ttsClient.play("안녕하세요!");
             }
         }
         else
         {
             /*if(login) {
                 if(!ttsClient.isPlaying())
                     login = false;
                 ttsClient.play("안녕히가세요.");

             }*/
         }
         //Write the code when the beacons in the region is received
     }




    /* ******************************************************************************
     * UART service connected/disconnected
     * ****************************************************************************** */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((UartService.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + mService);
            if (!mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }

        }

        public void onServiceDisconnected(ComponentName classname) {
            ////     mService.disconnect(mDevice);
            mService = null;
        }
    };


    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;
            //*********************//
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_CONNECT_MSG");
                        btnConnectDisconnect.setText("Disconnect");
                        mState = UART_PROFILE_CONNECTED;
                    }
                });
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_DISCONNECT_MSG");

                        mState = UART_PROFILE_DISCONNECTED;
                        mService.close();
                        //setUiState();

                    }
                });
            }

//            currCount = System.currentTimeMillis();
//            if(currCount - prevCount < 2000 ) {
//                enableHaptic = false;
//            }
//            else{
//                enableHaptic = true;
//            }
//
//            if(enableHaptic){
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        try {
//                            Log.d("part", "jh : " + txValue +"");
//                            String text = new String(txValue, "UTF-8");
//                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
//                        } catch (Exception e) {
//                            Log.e(TAG, e.toString());
//                        }
//                    }
//                });
//            }
//
//            prevCount = currCount;
            //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
            }
            //*********************//
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);

//                currenttime = System.currentTimeMillis();
//                if (currenttime - prevtime > 20000 ) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                Log.d("part", "jh : " + txValue + "");

                                Message msg = mInteractionManager.getHaptic().obtainMessage();

                                String text = new String(txValue, "UTF-8");
                                msg.what = 1;
                                msg.obj = txValue;
                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                v.vibrate(300);
                               // mInteractionManager.getHaptic().sendMessage(msg);
                                //mInteractionManager.getHaptic().sendMessage(msg);
                                mInteractionManager.handlehapticMessage(msg);
                                Log.d("TIMEERROR", "");
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                    });

//                    prevtime = currenttime;
//
//                    Log.d("time", "prevtime:"+ prevtime);
//                }
            }
            //*********************//
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)){
                //showMessage("Device doesn't support UART. Disconnecting");
                mService.disconnect();
            }


        }
    };



    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    /* ******************************************************************************
     * ****************************************************************************** */

    @Override
    public void onStart() {
        super.onStart();
        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
        if(mInteractionManager.mNaverRecognizer != null && mInteractionManager.mNaverRecognizer.getSpeechRecognizer() != null) {
            Log.d(TAG, "onStart");
            mInteractionManager.mNaverRecognizer.getSpeechRecognizer().initialize();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        TextToSpeechManager.getInstance().finalizeLibrary();
        login = false;
        //stopService(intent);

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        unbindService(mServiceConnection);
        mService.stopSelf();
        mService= null;

    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        if(mInteractionManager.mNaverRecognizer != null && mInteractionManager.mNaverRecognizer.getSpeechRecognizer() != null) {
            Log.d(TAG, "onStop1");
            mInteractionManager.mNaverRecognizer.getSpeechRecognizer().release();
        }
    }

    @Override
    protected void onPause() {



        Log.d(TAG, "onPause");
        super.onPause();
        isOnPause = true;
        if(mInteractionManager.mNaverRecognizer != null && mInteractionManager.mNaverRecognizer.getSpeechRecognizer() != null) {
            Log.d(TAG, "onPause1");
            mInteractionManager.mNaverRecognizer.getSpeechRecognizer().stopImmediately();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onResume() {
        super.onResume();
        isOnPause = false;

        Log.d(TAG, "onResume");
        if (!mBtAdapter.isEnabled()) {
            Log.i(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);

        }

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }




}
