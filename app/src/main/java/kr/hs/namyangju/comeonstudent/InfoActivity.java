
package kr.hs.namyangju.comeonstudent;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    private Context mcontext;


    private String path;//저장하는 곳의 경로
    private EditText ed1;//제목 적는 EditText
    private EditText ed2;//내용 적는 EditText
    private Button bt1;//저장하기 버튼
    private Button bt2;//불러오기 버튼
    private Button bt3;//삭제하기 버튼
    private Button bt4;//반영하기 버튼

    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    DatabaseHandler db;

    public static ArrayList<String> name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        mcontext = this;
        getSupportActionBar().setTitle("애플리케이션 정보");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DatabaseHandler(this);
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "/CLASS";

//        ed1 = (EditText) findViewById(R.id.ed1);
//        ed2 = (EditText) findViewById(R.id.ed2);
//        bt1 = (Button) findViewById(R.id.bt1);
        bt2 = (Button) findViewById(R.id.bt2);
        bt3 = (Button) findViewById(R.id.bt3);
//        bt4 = (Button) findViewById(R.id.bt4);

//        bt1.setOnClickListener(btListener);
        bt2.setOnClickListener(btListener);
        bt3.setOnClickListener(btListener);
//        bt4.setOnClickListener(btListener);


    }

    Button.OnClickListener btListener = new Button.OnClickListener() {
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
//                case R.id.bt1:
//                    String ed1text = ed1.getText().toString().trim();
//                    if (ed1text.length() > 0) {
//                        onTextWriting(ed1text, ed2.getText().toString());
//                    }
//                    break;
                case R.id.bt2:
                    if (ContextCompat.checkSelfPermission(InfoActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(InfoActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {

                            // Show an expanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.

                        } else {

                            // No explanation needed, we can request the permission.

                            ActivityCompat.requestPermissions(InfoActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    } else {
                        onTextRead();
                    }
                    break;
                case R.id.bt3:
                    deltext();
                    break;
//                case R.id.bt4:
//                    Toast.makeText(HelpActivity.this, "abcd", Toast.LENGTH_SHORT).show();
//                    break;
            }
        }
    };

    private void onTextWriting(String title, String body) {
        File file;
        file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(path + File.separator + title + ".txt");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter buw = new BufferedWriter(new OutputStreamWriter(fos, "UTF8"));
            buw.write(body);
            buw.close();
            fos.close();
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {

        }
    }

    private void onTextRead() {
//        Toast.makeText(mcontext, "35명 이하의 학생들에 대해서만 최적화 되어 있습니다. 36명 이상의 학생들은 제대로 동작하지 않을수 있습니다.", Toast.LENGTH_SHORT).show();
        final ArrayList<File> filelist = new ArrayList<File>();
        File files = new File(path);
        if (!files.exists()) {
            files.mkdirs();
        }
        if (files.listFiles().length > 0) {
            for (File file : files.listFiles(new TextFileFilter())) {
                filelist.add(file);
            }
        }
        CharSequence[] filename = new CharSequence[filelist.size()];
        for (int i = 0; i < filelist.size(); i++) {
            filename[i] = filelist.get(i).getName();
        }
        new AlertDialog.Builder(this)
                .setTitle("TEXT FILE LIST")
                .setItems(filename, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {

                                // TODO Auto-generated method stub
                                try {
                                    name = new ArrayList<String>();

                                    StringBuffer bodytext = new StringBuffer();
                                    File selecttext = filelist.get(arg1);
                                    FileInputStream fis = new FileInputStream(selecttext);
                                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis, "euc-kr"));
//                            while((body = bufferReader.readLine())!=null){
//                                bodytext.append(body);
//                            }


                                    String csvLine;

                                    while ((csvLine = bufferedReader.readLine()) != null) {
                                        String[] row = csvLine.split(",");
                                        for (int i = 0; i < row.length; i++) {
                                            name.add(row[i]);
//                                    bodytext.append(row[i]);
                                        }
                                    }
                                    for (int i = 1; i < name.size() - 1; i = i + 3) {
                                        db.addContact(new Contact(name.get(i).toString(), name.get(i + 1).toString()));
                                    }

                                    Toast.makeText(getBaseContext(), "DB가 " + name.size() / 3 + "개 추가되었습니다", Toast.LENGTH_SHORT).show();
                                    List<Contact> contacts = db.getAllContacts();

                                    for (Contact cn : contacts) {
                                        String log = "Id: " + cn.getID() + " ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber();
                                        // Writing Contacts to log
                                        Log.d("Name: ", log);
                                    }
//                                    ed1.setText(selecttext.getName());
//                                    ed2.setText(name.toString());
                                } catch (
                                        IOException ex)

                                {
                                    throw new RuntimeException("Error in reading CSV FILE : " + ex);
                                }
                            }
                        }

                ).

                setNegativeButton("취소", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        }

                ).

                show();
    }

    private void deltext() {
        db.deleteContact();
        Toast.makeText(getBaseContext(), "DB 가 삭제되었습니다", Toast.LENGTH_SHORT).show();

//        for (int i = 0; i <= db.getContactsCount(); i++) {
//            db.removeDeck(i);
//        }
        List<Contact> contacts = db.getAllContacts();

        for (Contact cn : contacts) {
            String log = "Id: " + cn.getID() + " ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber();
            // Writing Contacts to log
            Log.d("Name: ", log);

        }

//        final ArrayList<File> filelist = new ArrayList<File>();
//        File files = new File(path);
//        if (!files.exists()) {
//            files.mkdirs();
//        }
//        if (files.listFiles().length > 0) {
//            for (File file : files.listFiles(new TextFileFilter())) {
//                filelist.add(file);
//            }
//        }
//        CharSequence[] filename = new CharSequence[filelist.size()];
//        for (int i = 0; i < filelist.size(); i++) {
//            filename[i] = filelist.get(i).getName();
//        }
//        new AlertDialog.Builder(this)
//                .setTitle("TEXT FILE LIST")
//                .setItems(filename, new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        // TODO Auto-generated method stub
//                        filelist.get(arg1).delete();
//                        deltext();
//                    }
//                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
//
//            public void onClick(DialogInterface dialog, int which) {
//                // TODO Auto-generated method stub
//
//            }
//        }).show();
    }

    class TextFileFilter implements FileFilter {
        public boolean accept(File file) {
            // TODO Auto-generated method stub
            if (file.getName().endsWith(".txt") | (file.getName().endsWith(".csv")))
                return true;
            return false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(com.namgong.lsh.dbtest.R.menu.menu_main, menu);
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

        }

        return super.onOptionsItemSelected(item);
    }

}
