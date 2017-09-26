package kr.hs.namyangju.comeonstudent;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class NoteList extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

    private static final int DELETE_ID = Menu.FIRST;
    private int mNoteNumber = 1;

    private NotesDbAdapter mDbHelper;

    private ListView m_ListView;
//    private ArrayAdapter<String>    m_Adapter;

    ListViewAdapter m_Adapter;
    Button addbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
//		registerForContextMenu(getListView());

        addbutton = (Button) findViewById(R.id.addbutton);
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NoteList.this, ContentActivity.class);
                createNote();
            }
        });

        m_Adapter = new ListViewAdapter();

        m_ListView = (ListView) findViewById(R.id.lstv_main);
        m_ListView.setAdapter(m_Adapter);

        m_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(NoteList.this, ContentActivity.class);
                i.putExtra(NotesDbAdapter.KEY_ROWID, id);
                startActivityForResult(i, ACTIVITY_EDIT);

            }
        });
        m_ListView.setOnItemLongClickListener(this);

        fillData();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notelist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
//
//                AlertDialog.Builder dialog = new AlertDialog.Builder(NoteList.this);
//                dialog.setTitle("About");
//                dialog.setMessage("Hello! I'm Heng, the creator of this application. This application is created based on learning." +
//                        " Used it on trading or any others activity that is related to business is strictly forbidden."
//                        + "If there is any bug is found please freely e-mail me. " +
//                        "\n\tedisonthk@gmail.com"
//                );
//                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//
//                    }
//                });
//                dialog.show();
                startActivity(new Intent(NoteList.this, InfoActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNote() {
        Intent i = new Intent(this, ContentActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//        Intent i = new Intent(this, NoteEdit.class);
//        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
//        startActivityForResult(i, ACTIVITY_EDIT);
//    }

    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchAllNotes();
        startManagingCursor(notesCursor);


        String[] from = new String[]{NotesDbAdapter.KEY_TITLE, NotesDbAdapter.KEY_DATE};
//        int[] to = new int[]{R.id.text1, R.id.date_row};
        int[] to = new int[]{R.id.txt_count1, R.id.txt_date};
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.listview_item, notesCursor, from, to);
//        setListAdapter(notes);
        m_ListView.setAdapter(notes);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(NoteList.this, "cc", Toast.LENGTH_SHORT).show();
        return false;
    }
}
