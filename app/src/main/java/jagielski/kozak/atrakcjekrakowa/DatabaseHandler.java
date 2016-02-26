package jagielski.kozak.atrakcjekrakowa;

/**
 * Created by Piotr on 2015-05-18.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

    // stałe do bazy
    // wersja bazy
    private static final int DATABASE_VERSION = 1;

    // nazwa bazy
    private static final String DATABASE_NAME = "bazaDanych.db";

    // tabela z miejscami
    private static final String TABLE_PLACES = "Miejsca";

    // nazwy kolumn tabeli z miejscami
    public static final String KEY_ID = "_id";
    public static final String KEY_IDMIEJSCA = "idmiejsca";
    public static final String KEY_NAZWA = "nazwa";
    public static final String KEY_DANE = "opis";
    public static final String KEY_FOTO = "zdjecie";
    public static final String KEY_GPSL = "gpsl";
    public static final String KEY_GPSH = "gpsh";

    // tabela ulubionych
    // tabela z miejscami
    private static final String TABLE_FAV = "Ulubione";

    public static final String FAV_KEY_ID = "_id";
    public static final String FAV_KEY_IDMIEJSCA = "idmiejsca";

    // referencja do bazy
    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    // tworzenie tabeli
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PLACES_TABLE = "CREATE TABLE " + TABLE_PLACES + "(" + KEY_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_IDMIEJSCA + " TEXT," +  KEY_NAZWA + " TEXT," + KEY_DANE + " TEXT," + KEY_FOTO + " TEXT," + KEY_GPSL + " TEXT," + KEY_GPSH + " TEXT" + ")";
        String CREATE_FAV_TABLE = "CREATE TABLE " + TABLE_FAV + "(" + FAV_KEY_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT," + FAV_KEY_IDMIEJSCA + " TEXT" + ")";

        db.execSQL(CREATE_PLACES_TABLE);
        db.execSQL(CREATE_FAV_TABLE);
    }

    // aktualizacja bazy
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // usunięcie tabeli
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV);

        // ponowne utworzenie tabeli
        onCreate(db);
    }

    // dodanie miejsca
    public void addPlace(Miejsce miejsce) {

        ContentValues values = new ContentValues();

        values.put(KEY_IDMIEJSCA, miejsce.getIdMiejsca());
        values.put(KEY_NAZWA, miejsce.getNazwa());
        values.put(KEY_DANE, miejsce.getOpis());
        values.put(KEY_FOTO, miejsce.getZdjecie());
        values.put(KEY_GPSL, miejsce.getGpsl());
        values.put(KEY_GPSH, miejsce.getGpsh());

        // wstawienie notatki do bazy
        db.insert(TABLE_PLACES, null, values);

    }

    // dodanie ulubionego
    public void addFav(Miejsce miejsce) {
        ContentValues values = new ContentValues();

        values.put(FAV_KEY_IDMIEJSCA, miejsce.getIdMiejsca());

        db.insert(TABLE_FAV, null, values);
    }

    // pobranie pojedynczej notatki
    public Miejsce getPlace(int id) {

        // zamiast new String[] { KEY_ID, KEY_TITLE, KEY_BODY }
        // możemy użyć null (wszystkie kolumny)
        Cursor cursor = db.query(TABLE_PLACES, new String[] { KEY_ID, KEY_IDMIEJSCA, KEY_NAZWA, KEY_DANE, KEY_FOTO, KEY_GPSL, KEY_GPSH },
                KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Miejsce miejsce = new Miejsce(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));

        // zwracamy notatkę
        return miejsce;
    }

    // pobranie wszystkich notatek
    public List<Miejsce> getAllPlaces() {

        List<Miejsce> placeList = new ArrayList<Miejsce>();

        // zapytanie SQL
        String selectQuery = "SELECT  * FROM " + TABLE_PLACES;

        // inny sposób wywołania zapytania
        Cursor cursor = db.rawQuery(selectQuery, null);

        // pętla przez wszystkie elementy z dodzwaniem ich do listy
        if (cursor.moveToFirst()) {
            do {
                Miejsce miejsce = new Miejsce();
                miejsce.setId(Integer.parseInt(cursor.getString(0)));
                miejsce.setIdMiejsca(cursor.getString(1));
                miejsce.setNazwa(cursor.getString(2));
                miejsce.setOpis(cursor.getString(3));
                miejsce.setZdjecie(cursor.getString(4));
                miejsce.setGpsl(cursor.getString(5));
                miejsce.setGpsh(cursor.getString(6));

                // Adding contact to list
                placeList.add(miejsce);

            } while (cursor.moveToNext());
        }

        // return contact list
        return placeList;
    }

    public Cursor fetchAllPlaces(String sort) {
        // wszystkie miejsca w formie obiektu klasy Cursor
        return db.query(TABLE_PLACES, new String[]{KEY_ID, KEY_IDMIEJSCA, KEY_NAZWA, KEY_DANE, KEY_FOTO, KEY_GPSL, KEY_GPSH}, null, null, null, null, null);
    }

    public Cursor fetchAllFavPlaces(String sort) {
        return db.rawQuery("SELECT * FROM " + TABLE_PLACES + ", " + TABLE_FAV +
                " WHERE " + TABLE_PLACES + "." + KEY_IDMIEJSCA + " = " + TABLE_FAV + "." + FAV_KEY_IDMIEJSCA + " GROUP BY " + TABLE_FAV + "." + FAV_KEY_IDMIEJSCA + "", null);
    }

    public boolean isFavourite(Miejsce miejsce){
        List<String> favourites = new ArrayList<>();

        // zapytanie SQL
        String selectQuery = "SELECT  "+ FAV_KEY_IDMIEJSCA +" FROM " + TABLE_FAV;

        // inny sposób wywołania zapytania
        Cursor cursor = db.rawQuery(selectQuery, null);

        // pętla przez wszystkie elementy z dodzwaniem ich do listy
        if (cursor.moveToFirst()) {
            do {
                // Adding contact to list
                favourites.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        Log.i("fav", favourites.toString());
        if (favourites.contains(miejsce.getIdMiejsca())) {
            return true;
        } else {
            return false;

        }
    }

    // pobranie liczby miejsc w bazie
    public int getPlacesCount() {

        String countQuery = "SELECT  * FROM " + TABLE_PLACES;
        Cursor cursor = db.rawQuery(countQuery, null);

        // zwracamy liczbę wierszy
        return cursor.getCount();
    }

    public Long getPlaceID(long id){
        String s = String.valueOf(id);

        Cursor cursor = db.rawQuery("SELECT _id FROM Miejsca WHERE idmiejsca IN (SELECT idmiejsca FROM Ulubione WHERE _id = "+ s + ")", null);
        cursor.moveToFirst();
        return Long.valueOf(cursor.getString(0)).longValue();
    }

    // aktualizacja miejsca
    public int updatePlace(Miejsce miejsce, String idMiejsca, String nazwa, String opis, String zdjecie, String gpsl, String gpsh) {

        ContentValues values = new ContentValues();
        values.put(KEY_IDMIEJSCA, idMiejsca);
        values.put(KEY_NAZWA, nazwa);
        values.put(KEY_DANE, opis);
        values.put(KEY_FOTO, zdjecie);
        values.put(KEY_GPSL, gpsl);
        values.put(KEY_GPSH, gpsh);


        // aktualizacja wiersza
        return db.update(TABLE_PLACES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(miejsce.getId()) });
    }

    // usunięcie
    public void deleteAllPlaces() {

        db.execSQL("DELETE FROM " + TABLE_PLACES);

    }

    public void deleteFromFav(Miejsce miejsce){
        db.execSQL("DELETE FROM " + TABLE_FAV + " WHERE " + FAV_KEY_IDMIEJSCA + " = " + miejsce.getIdMiejsca());
    }


}