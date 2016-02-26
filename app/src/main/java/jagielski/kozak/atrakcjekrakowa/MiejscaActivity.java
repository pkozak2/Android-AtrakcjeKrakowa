package jagielski.kozak.atrakcjekrakowa;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class MiejscaActivity extends ListActivity {

    private DatabaseHandler db;
    public final static String ID = "jagielski.kozak.atrakcjekrakowa.ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miejsca);

        // nowy obiekt - obsługa bazy danych
        db = new DatabaseHandler(this);

        // wywołanie metody uzupełniającej listę
        fillData();
    }
    public void onResume() {
        super.onResume();
        fillData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_miejsca, menu);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // metoda uzupełnia naszą listę danymi pobranymi z bazy
    private void fillData() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);


        Cursor c = db.fetchAllPlaces(sp.getString("pref_Sortowanie", "nazwa ASC"));

        startManagingCursor(c);

        String[] from = new String[] { DatabaseHandler.KEY_NAZWA,  DatabaseHandler.KEY_DANE};
        int[] to = new int[] { R.id.tytul, R.id.opis};

        // Uzupełniamy listę wartościami
        SimpleCursorAdapter places = new SimpleCursorAdapter(this, R.layout.list_item, c, from, to);
        setListAdapter(places);

    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // wywolanie aktywoności z id
        Intent intent = new Intent(this, ShowPlaceActivity.class);
        intent.putExtra(ID, id);
        startActivity(intent);

        // informacja o danych związanych z kliknięciem (id jest związane z naszą bazą)
        //Log.i("BAZA", "Pozycja: " + position + " ID: " + id);

    }

}