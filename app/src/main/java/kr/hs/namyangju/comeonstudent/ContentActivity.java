package kr.hs.namyangju.comeonstudent;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.SmsManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContentActivity extends AppCompatActivity {
    public static String curDate = "", curDay = "";
    public static String curText = "";

    //    private EditText mTitleText;
//    private EditText mBodyText;
    private TextView mDateText;
    private Button resetbt, savebt, startbt;
    private TextView todaytv, count_tv;

    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    //    private SimpleDateFormat dayFormat = new SimpleDateFormat("HH:mm:ss");
    private SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy.MM.dd.(E)");

    private Cursor note;
    private NotesDbAdapter mDbHelper;

    private int count = 0;
    private Long mRowId;

    LinearLayout main_ll;
    LinearLayout.LayoutParams weight_position_1, weight_position_2;

    DatabaseHandler db;
    private ArrayList<String> phonelist;
    private List<String> namelist;
    private Context mcontext;
    private int sendSucessCount;
    private final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private EditText popup_input;
    private AlertDialog dialog;
    private Intent msg;

    int ll_line_count = 10;
    LinearLayout ll_line[] = new LinearLayout[ll_line_count];
    LinearLayout ll_content[] = new LinearLayout[50];
    ToggleButton btn[] = new ToggleButton[50];
    TextView text_time[] = new TextView[50];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        getSupportActionBar().setTitle("애플리케이션 정보");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHandler(this);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        phonelist = new ArrayList();
        namelist = new ArrayList<String>();
        mcontext = this;

        calendar = new GregorianCalendar();
        curDay = dayFormat.format(calendar.getTime());


        savebt = (Button) findViewById(R.id.btn_sv);
        resetbt = (Button) findViewById(R.id.btn_rs);
        startbt = (Button) findViewById(R.id.btn_start);

        count_tv = (TextView) findViewById(R.id.count_txt);
        todaytv = (TextView) findViewById(R.id.today);

        main_ll = (LinearLayout) findViewById(R.id.ll_content_main);

        count_tv.setText(Integer.toString(count) + "명");
//        todaytv.setText(dayFormat.format(calendar.getTime()));
        todaytv.setText("" + curDay);

        savebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                saveState(); // 나오면서 저장하기
            }
        });
        resetbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startbt.setVisibility(View.VISIBLE);
                reset_content(v);
            }
        });
        startbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startbt.setVisibility(View.GONE);
                reset_content(v);

            }
        });


        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
                    : null;
        }

        populateFields();
        dynamicButton();
    }

    private void reset_content(View v) {
        if (v.getId() == R.id.btn_start) {
            calendar = new GregorianCalendar();
            curDate = dateFormat.format(calendar.getTime());

            for (int i = 0; i <= db.getContactsCount(); i++) {
                text_time[i].setText(curDate);
            }
        } else if (v.getId() == R.id.btn_rs) {
            for (int i = 0; i <= db.getContactsCount(); i++) {
                text_time[i].setText("");
            }

        }
    }

    private void dynamicButton() {

        weight_position_1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1);
        weight_position_2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 2);
        weight_position_1.setMargins(3, 3, 3, 3);
        int full_count = db.getContactsCount() + (5 - (db.getContactsCount() % 5));

        for (int i = 0; i < full_count / 5; i++) {
            ll_line[i] = new LinearLayout(this);
            ll_line[i].setId(i);
            main_ll.addView(ll_line[i]);
            ll_line[i].setLayoutParams(weight_position_1);
        }

        for (int i = 0; i < full_count; i++) {
            ll_content[i] = new LinearLayout(this);
            ll_content[i].setLayoutParams(weight_position_1);
            ll_content[i].setOrientation(LinearLayout.VERTICAL);
            ll_content[i].setBackgroundResource(R.drawable.button_toggle_white);
            btn_content(i);
            txt_content(i);
            ll_line[i / 5].addView(ll_content[i]);
        }

    }

    private void btn_content(int i) {

        btn[i] = new ToggleButton(this);
        btn[i].setText("" + (i + 1));
        btn[i].setWidth(10);
        btn[i].setTextSize(11);
        btn[i].setBackgroundResource(R.drawable.button_toggle);
        btn[i].setTextColor(getResources().getColor(R.color.selector_color));
        btn[i].setId(i);
        if (i < db.getContactsCount()) {
            btn[i].setText(db.getContact(i + 1).getName().toString());
            btn[i].setTextOff(db.getContact(i + 1).getName().toString());
            btn[i].setTextOn(db.getContact(i + 1).getName().toString());
        } else {
            btn[i].setVisibility(View.INVISIBLE);
        }
        btn[i].setLayoutParams(weight_position_1);

        btn[i].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_toggle_click(v.getId());
            }
        });
        ll_content[i].addView(btn[i]);
    }

    private void btn_toggle_click(int i) {
        calendar = new GregorianCalendar();
        curDate = dateFormat.format(calendar.getTime());
        if (btn[i].isChecked()) {
            count++;
            phonelist.add(db.getContact(i+1).getPhoneNumber().toString());
            namelist.add(db.getContact(i+1).getName().toString());
            count_tv.setText(Integer.toString(count) + "명");
            text_time[i].setVisibility(View.INVISIBLE);
        } else {
            phonelist.remove(db.getContact(i+1).getPhoneNumber().toString());
            namelist.remove(db.getContact(i+1).getName().toString());
            count--;
            count_tv.setText(Integer.toString(count) + "명");
            text_time[i].setText(curDate);
            text_time[i].setVisibility(View.VISIBLE);

        }
    }

    private void txt_content(int i) {

        text_time[i] = new TextView(this);
        text_time[i].setWidth(10);
        text_time[i].setTextSize(11);
        if (i < db.getContactsCount()) {
            text_time[i].setText(curDate);
        } else {
            text_time[i].setVisibility(View.INVISIBLE);
        }
        text_time[i].setId(i);
//        text_time[i].setBackgroundResource(R.drawable.button_round);
        text_time[i].setGravity(Gravity.CENTER);
        text_time[i].setLayoutParams(weight_position_2);

        ll_content[i].addView(text_time[i]);


    }

    private void smsDialog() {

        dialog = create_inputDialog();

        // Context 얻고, 해당 컨텍스트의 레이아웃 정보 얻기
        Context context = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

        // 레이아웃 설정
        View layout = inflater.inflate(R.layout.activity_alert, (ViewGroup) findViewById(R.id.popup_root));

        // Input 소프트 키보드 보이기
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        // AlertDialog에 레이아웃 추가
        dialog.setView(layout);
        popup_input = (EditText) layout.findViewById(R.id.toolEvent_popup_input);

        msg = new Intent(Intent.ACTION_SEND);
        msg.addCategory(Intent.CATEGORY_DEFAULT);
        msg.putExtra(Intent.EXTRA_SUBJECT, "지각 정보 " + todaytv.getText().toString() + "\n");
        msg.putExtra(Intent.EXTRA_TEXT, count_tv.getText().toString() + " " +
                namelist.toArray(new String[namelist.size()]));
//                msg.putExtra(Intent.EXTRA_TITLE, txt_name1.getText().toString());
        msg.setType("text/plain");
        dialog.show();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields(); //들어가기
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.message:
                if (phonelist.isEmpty()) {
//                    smsDialog();
                    Toast.makeText(ContentActivity.this, "학생이 선택되지 않았습니다", Toast.LENGTH_SHORT).show();
                } else {
                    smsDialog();

                }
                break;

            case R.id.menu_infomation:
                startActivity(new Intent(mcontext, InfoActivity.class));
                break;


        }

        return super.onOptionsItemSelected(item);
    }


    private void saveState() {
        String count = count_tv.getText().toString();
        String today = todaytv.getText().toString();
        if (mRowId == null) {
            long id = mDbHelper.createNote(count, today, curDay);
            if (id > 0) {
                mRowId = id;
            } else {
                Log.e("saveState", "failed to create note");
            }
        } else {
            if (!mDbHelper.updateNote(mRowId, count, today, curDay)) {
                Log.e("saveState", "failed to update note");
            }
        }
    }


    private void populateFields() {
        if (mRowId != null) {
            note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            count_tv.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            todaytv.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_DATE)));
//            curText = note.getString(
//                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));

        }
    }


    private AlertDialog create_inputDialog() {
        AlertDialog dialogBox = new AlertDialog.Builder(this)
                .setTitle("메시지 전송")
                .setMessage("전송할 내용을 입력하세요")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 예 버튼 눌렀을때 액션 구현
                        if (ContextCompat.checkSelfPermission(ContentActivity.this,
                                Manifest.permission.SEND_SMS)
                                != PackageManager.PERMISSION_GRANTED) {

                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(ContentActivity.this,
                                    Manifest.permission.SEND_SMS)) {

                                // Show an expanation to the user *asynchronously* -- don't block
                                // this thread waiting for the user's response! After the user
                                // sees the explanation, try again to request the permission.

                            } else {

                                // No explanation needed, we can request the permission.

                                ActivityCompat.requestPermissions(ContentActivity.this,
                                        new String[]{Manifest.permission.SEND_SMS},
                                        MY_PERMISSIONS_REQUEST_SEND_SMS);

                                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                                // app-defined int constant. The callback method gets the
                                // result of the request.
                            }
                        } else {
                            sendSMS(phonelist, popup_input.getText().toString());

                        }


                    }
                })
                .setNeutralButton("아니오", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 아니오 버튼 눌렀을때 액션 구현
                    }
                }).create();
        return dialogBox;
    }

    public void sendSMS(final ArrayList<String> smsNumbers, String smsText) {
        sendSucessCount = 0;
        PendingIntent sentIntent = PendingIntent.getBroadcast(mcontext, 0, new Intent("SMS_SENT_ACTION"), 0);

        mcontext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        sendSucessCount++;

                        if (sendSucessCount == smsNumbers.size()) {
                            Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show();
                            sendSucessCount = 0;
                            context.unregisterReceiver(this);
                        }
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "전송 실패", Toast.LENGTH_SHORT).show();
                        context.unregisterReceiver(this);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
                        context.unregisterReceiver(this);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
                        context.unregisterReceiver(this);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
                        context.unregisterReceiver(this);
                        break;
                }
            }
        }, new IntentFilter("SMS_SENT_ACTION"));

        SmsManager mSmsManager = SmsManager.getDefault();

        for (int i = 0; i < smsNumbers.size(); i++) { //문자 목록 log
            String log = "phone: " + smsNumbers.get(i);
            // Writing Contacts to log
            Log.d("Name: ", log);

            mSmsManager.sendTextMessage(smsNumbers.get(i), null, smsText, sentIntent, null);//문자보내기

        }

    }
}
