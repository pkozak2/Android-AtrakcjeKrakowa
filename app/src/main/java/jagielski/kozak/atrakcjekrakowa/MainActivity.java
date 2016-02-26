package jagielski.kozak.atrakcjekrakowa;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {

    private DatabaseHandler db;

    private String url = "http://pepe67.pl/atrakcje.xml";

    private final String KEY_ATRAKCJA = "atrakcja";

    private final String KEY_IDMIEJSCA = "idmiejsca";
    private final String KEY_TYTUL = "tytul";
    private final String KEY_OPIS = "opis";
    private final String KEY_ZDJECIE = "zdjecie";
    private final String KEY_GPSL = "gpsl";
    private final String KEY_GPSH = "gpsh";
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHandler(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void settings(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void fav(View view){
        Intent intent = new Intent(this, FavActivity.class);
        startActivity(intent);
    }

    public void list(View view){
        Intent intent = new Intent(this, MiejscaActivity.class);
        startActivity(intent);
    }

    public void refresh(){

       new ReadXMLTask(MainActivity.this).execute(url);
    }



private class ReadXMLTask extends AsyncTask<String, Void, String> {

    private Context mcontext;
    ConnectionDetector connectiondetector;

    public ReadXMLTask(Context context)
    {
        mcontext= context;
        connectiondetector = new ConnectionDetector(mcontext);
    }



    @Override
    // metoda wywoływana na początku
    protected void onPreExecute() {
        // wyświetlamy okno z informacją o pobieraniu przez aplikację danych

            pd = ProgressDialog.show(MainActivity.this, "Aktualizacja", "Pobieram dane...");


    }

    @Override
    // główna metoda (osobny wątek), obsługuje zmienną liczbę parametrów, wykorzystujemy tylko jeden - adres strony
    protected String doInBackground(String... urls) {

        // zmienna przechowująca zawartośc pobranej strony
        String response = "";

        try {
            // tworzymy nowy obiekt URL do konstruktora przekazujemy pierwszy parametr urls[0] czyli adres strony
            URL url = new URL(urls[0]);

            // otwieramy połączenie
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // łączymy się ze stroną
            conn.connect();
            // pobieramy strumień danych wejściowych
            InputStream is = conn.getInputStream();
            // tworzymy strumień buforowany powiązany ze strumieniem danych wejściowych
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            // zmienna pomocnicza
            String s = "";

            // dotąd dopóki są dane w strumieniu pobieramy je i dopisujemy do zmiennej response
            while ((s = br.readLine()) != null) {
                response += s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    // metoda wywoływana po zakończeniu metody doInBackground() - parametrem jest wynik jej działania.
    protected void onPostExecute(String result) {
        if(connectiondetector.isConnectingToInternet()){
        // tworzymy nowy obiekt klasy XMLParser
        XMLParser parser = new XMLParser();

        // zmieniamy strukturę XML na DOM
        Document doc = parser.getDomElement(result);
        // tworzymy listę elementów do pokazania na ekranie (lista przechowuje tablice hashujące)
        ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();

        // pobieramy elementy typu KEY_ATRAKCJA
        NodeList nl = doc.getElementsByTagName(KEY_ATRAKCJA);

        if (db.getPlacesCount() >= 1){
            db.deleteAllPlaces();
        }

        // dla wszystkich wystąpień elementu KEY_ATRAKCJA
        for (int i = 0; i < nl.getLength(); i++) {
            // pobieramy element
            Element e = (Element) nl.item(i);

            Miejsce place = new Miejsce(parser.getValue(e, KEY_IDMIEJSCA),parser.getValue(e, KEY_TYTUL), parser.getValue(e, KEY_OPIS), parser.getValue(e, KEY_ZDJECIE), parser.getValue(e, KEY_GPSL), parser.getValue(e, KEY_GPSH));

                db.addPlace(place);
            }

        }
        else {
            Context context = getApplicationContext();
            CharSequence text = "Brak połączenia z internetem!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }
        // zamykamy okno z informacją o pobieraniu danych
        pd.dismiss();
    }

}
}