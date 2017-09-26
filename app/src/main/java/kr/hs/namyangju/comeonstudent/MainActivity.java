package kr.hs.namyangju.comeonstudent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    ListView listview;
    ListViewAdapter adapter;
    Button addbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        addbutton = (Button) findViewById(R.id.addbutton);

        adapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.lstv_main);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, ContentActivity.class);
                startActivityForResult(i, 0);
                startActivity(i);
            }
        });
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.addItem("aa","bb","cc");
//                Intent i = new Intent(MainActivity.this, ContentActivity.class);
//                startActivityForResult(i, 0);
//                startActivity(i);
            }
        });



//        Toast.makeText(this, i.getStringExtra("ss"),Toast.LENGTH_SHORT).show();

        // 첫 번째 아이템 추가.
//        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_account_box_black_36dp),
//                "Box", "Account Box Black 36dp") ;
//         두 번째 아이템 추가.
//        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_account_circle_black_36dp),
//                "Circle", "Account Circle Black 36dp") ;
//         세 번째 아이템 추가.
//        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_assignment_ind_black_36dp),
//                "Ind", "Assignment Ind Black 36dp") ;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
//                    Intent i = new Intent();
//                    Intent i = getIntent();
                    Toast.makeText(MainActivity.this, data.getStringExtra("ss"), Toast.LENGTH_LONG).show();
                    adapter.addItem(data.getStringExtra("today"), data.getStringExtra("name"), data.getStringExtra("count"));
                    adapter.notifyDataSetChanged();
                }
                break;
            case 2:
                if (resultCode == Activity.RESULT_OK) { /* * 내용 */ }
                break;
        }
    }
}


