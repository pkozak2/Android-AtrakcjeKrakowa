package jagielski.kozak.atrakcjekrakowa;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.net.URL;


public class ShowPlaceActivity extends FragmentActivity implements OnMapReadyCallback {
    private DatabaseHandler db;
    private int id;
    public final static String ID = "jagielski.kozak.atrakcjekrakowa.ID";
    Miejsce miejsce;
    Button przycisk;
    ConnectionDetector connectiondetector;
    double gpsh;
    double gpsl;
    MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_place);


        Intent intent = getIntent();
        id = (int) intent.getLongExtra(FavActivity.ID, 1);

        db = new DatabaseHandler(this);

        miejsce =  db.getPlace(id);

         mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gpsh = Double.parseDouble(miejsce.getGpsh());
        gpsl = Double.parseDouble(miejsce.getGpsl());
        //Log.i("int", "l"+gpsh +"l ");
       filldata();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_place, menu);
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
    public void onMapReady(GoogleMap map) {
        LatLng place = new LatLng(gpsh, gpsl);
        map.addMarker(new MarkerOptions()
                .position(place)
                .title(miejsce.getNazwa()));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 13));
        map.getUiSettings().setScrollGesturesEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(false);
    }



    public void filldata() {
        connectiondetector = new ConnectionDetector(ShowPlaceActivity.this);

        getActionBar().setTitle(miejsce.getNazwa());


        TextView Text;// = (TextView) findViewById(R.id.nazwa);
       // Text.setText(miejsce.getNazwa());

        Text = (TextView) findViewById(R.id.opis);
        Text.setText(miejsce.getOpis());
        przycisk = (Button) findViewById(R.id.button4);

        if(connectiondetector.isConnectingToInternet()) {
            new DownloadImageTask((ImageView) findViewById(R.id.foto)).execute(miejsce.getZdjecie());
        } else {
            ImageView obrazek = (ImageView) findViewById(R.id.foto);
            obrazek.setImageResource(R.drawable.logo);
        }
        if (db.isFavourite(miejsce)){

            przycisk.setText(getString(R.string.FavouritesDelButton));
            przycisk.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    delFav();
                }
            });
        } else {
            przycisk.setText(getString(R.string.FavouritesButton));
            przycisk.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    addFav();
                }
            });
        }

        TextView gpsh = (TextView) findViewById(R.id.GPSHDANE);
        gpsh.setText(miejsce.getGpsh());

        TextView gpsl = (TextView) findViewById(R.id.GPSLDANE);
        gpsl.setText(miejsce.getGpsl());


    }

    public void addFav() {
        db.addFav(miejsce);
        przycisk.setText(getString(R.string.FavouritesDelButton));
    }
    public void delFav(){
        db.deleteFromFav(miejsce);
        przycisk.setText(getString(R.string.FavouritesButton));
    }

    public void naviguj(View view) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+gpsh+", "+gpsl);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
