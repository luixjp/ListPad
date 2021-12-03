package br.edu.ifsp.scl.sdm.listpad.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import br.edu.ifsp.scl.sdm.listpad.model.Country
import br.edu.ifsp.scl.sdm.listpad.model.Place


class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_NAME = "listpad.db"
        private val DATABASE_VERSION = 1
        private val TABLE_NAME_COUNTRY = "country"
        private val TABLE_NAME_PLACE = "place"
        private val ID = "_id"
        private val ID_CONTINENT = "_id_continent"
        private val ID_COUNTRY = "_id_country"
        private val NAME = "name"
        private val ABBR = "abbreviation"
        private val URGT = "urgent"
        private val VISITED = "visited"
        private val DESC = "description"
        private val URLIMG = "url_image"
    }

    override fun onCreate(sdb: SQLiteDatabase?) {

        val CREATE_TABLE_COUNTRY = "CREATE TABLE IF NOT EXISTS $TABLE_NAME_COUNTRY ($ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$ID_CONTINENT INTEGER NOT NULL, $NAME TEXT, $ABBR TEXT, $URLIMG TEXT, $URGT INTEGER DEFAULT 0)"
        sdb?.execSQL(CREATE_TABLE_COUNTRY)

        val CREATE_TABLE_PLACE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME_PLACE ($ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$ID_COUNTRY INTEGER NOT NULL, $NAME TEXT, $DESC TEXT, $URLIMG TEXT, $VISITED INTEGER DEFAULT 0)"
        sdb?.execSQL(CREATE_TABLE_PLACE)
    }

    override fun onUpgrade(sdb: SQLiteDatabase?, oldVersion1: Int, newVersion2: Int) {

        throw NotImplementedError();
        /*
        if(version1 < 2) { //migrando da 1 para 2
        }
        if(version1<3) { //migrando da 2 para 3
        }
        */
    }

    fun insertCountry(country: Country) : Long {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(ID, country.id)
        values.put(ID_CONTINENT, country.idContinent)
        values.put(NAME, country.name)
        values.put(ABBR, country.abbreviation)
        values.put(URLIMG, country.urlImage)
        values.put(URGT, country.urgent)

        val result = db.insert(TABLE_NAME_COUNTRY, null, values)
        db.close()
        return result
    }

    fun insertPlace(place: Place) : Long {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(ID, place.id)
        values.put(ID_COUNTRY, place.idCountry)
        values.put(NAME, place.name)
        values.put(DESC, place.description)
        values.put(URLIMG, place.urlImage)
        values.put(VISITED, place.visited)

        val result = db.insert(TABLE_NAME_PLACE, null, values)
        db.close()
        return result
    }

    fun updateCountry(country: Country) : Int {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(ID, country.id)
        values.put(ID_CONTINENT, country.idContinent)
        values.put(NAME, country.name)
        values.put(ABBR, country.abbreviation)
        values.put(URLIMG, country.urlImage)
        values.put(URGT, country.urgent)

        val result = db.update(TABLE_NAME_COUNTRY, values,"$ID=?", arrayOf(country.id.toString()))
        db.close()
        return result
    }

    fun updatePlace(place: Place) : Int {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(ID, place.id)
        values.put(ID_COUNTRY, place.idCountry)
        values.put(NAME, place.name)
        values.put(DESC, place.description)
        values.put(URLIMG, place.urlImage)
        values.put(VISITED, place.visited)

        val result = db.update(TABLE_NAME_PLACE, values,"$ID=?", arrayOf(place.id.toString()))
        db.close()
        return result
    }

    fun deleteCountry(country: Country) : Int {

        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME_COUNTRY, "$ID=?", arrayOf(country.id.toString()))
        db.close()
        return result
    }

    fun deleteCountryById(id: Int) : Int {

        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME_COUNTRY, "$ID=?", arrayOf(id.toString()))
        db.close()
        return result
    }

    fun deletePlace(place: Place) : Int {

        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME_PLACE, "$ID=?", arrayOf(place.id.toString()))
        db.close()
        return result
    }

    fun deletePlaceById(id: Int) : Int {

        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME_PLACE, "$ID=?", arrayOf(id.toString()))
        db.close()
        return result
    }


    fun deletePlacesByCountryId(id: Int) : Int {

        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME_PLACE, "$ID_COUNTRY=?", arrayOf(id.toString()))
        db.close()
        return result
    }

    fun selectCountry(idCountry : Int) : Country {

        lateinit var country : Country
        val params = arrayOf<String>(idCountry.toString())
        val query = "SELECT * FROM $TABLE_NAME_COUNTRY WHERE $ID = ?"
        val db = this.readableDatabase

        val cursor = db.rawQuery(query, params);

        while(cursor.moveToNext()) {
            country = Country (
                cursor.getInt(0), //ID
                cursor.getInt(1), //ID_CONTINENT
                cursor.getString(2), //NAME
                cursor.getString(3), //ABBR
                cursor.getString(4), //URLIMG
                cursor.getInt(5) //URGT
            )
        }
        cursor.close()

        return country

    }

    fun selectPlace(idPlace : Int) : Place {

        lateinit var place : Place
        val params = arrayOf<String>(idPlace.toString())
        val query = "SELECT * FROM $TABLE_NAME_PLACE WHERE $ID = ?"
        val db = this.readableDatabase

        val cursor = db.rawQuery(query, params);

        while(cursor.moveToNext()) {
            place = Place (
                cursor.getInt(0), //ID
                cursor.getInt(1), //ID_COUNTRY
                cursor.getString(2), //NAME
                cursor.getString(3), //DESC
                cursor.getString(4), //URLIMG
                cursor.getInt(5) //VISITED
            )
        }
        cursor.close()

        return place

    }

    fun listAllCountries(): ArrayList<Country> {
        val listCountries = ArrayList<Country>()

        val query = "SELECT * FROM $TABLE_NAME_COUNTRY ORDER BY $NAME"
        val db = this.readableDatabase

        val cursor = db.rawQuery(query, null)

        while(cursor.moveToNext()) {
            val c = Country (
                cursor.getInt(0), //ID
                cursor.getInt(1), //ID_CONTINENT
                cursor.getString(2), //NAME
                cursor.getString(3), //ABBR
                cursor.getString(4), //URLIMG
                cursor.getInt(5) //URGT
            )
            listCountries.add(c)
        }
        cursor.close()

        return listCountries
    }

    fun listAllCountriesByContinent(idContinent :Int): ArrayList<Country> {
        val listCountries = ArrayList<Country>()

        val params = arrayOf<String>(idContinent.toString())
        val query = "SELECT * FROM $TABLE_NAME_COUNTRY WHERE $ID_CONTINENT = ? ORDER BY $NAME"
        val db = this.readableDatabase

        val cursor = db.rawQuery(query, params);

        while(cursor.moveToNext()) {
            val c = Country (
                cursor.getInt(0), //ID
                cursor.getInt(1), //ID_CONTINENT
                cursor.getString(2), //NAME
                cursor.getString(3), //ABBR
                cursor.getString(4), //URLIMG
                cursor.getInt(5) //URGT
            )
            listCountries.add(c)
        }
        cursor.close()

        return listCountries
    }

    fun listAllPlaces(): ArrayList<Place> {
        val listPlaces = ArrayList<Place>()

        val query = "SELECT * FROM $TABLE_NAME_PLACE ORDER BY $NAME"
        val db = this.readableDatabase

        val cursor = db.rawQuery(query, null)

        while(cursor.moveToNext()) {
            val c = Place (
                cursor.getInt(0), //ID
                cursor.getInt(1), //ID_COUNTRY
                cursor.getString(2), //NAME
                cursor.getString(3), //DESC
                cursor.getString(4), //URLIMG
                cursor.getInt(5) //VISITED
            )
            listPlaces.add(c)
        }
        cursor.close()

        return listPlaces
    }

    fun listAllPlacesByCountry(idCountry :Int): ArrayList<Place> {
        val listPlaces = ArrayList<Place>()

        val params = arrayOf<String>(idCountry.toString())
        val query = "SELECT * FROM $TABLE_NAME_PLACE WHERE $ID_COUNTRY = ? ORDER BY $NAME"
        val db = this.readableDatabase

        val cursor = db.rawQuery(query, params);

        while(cursor.moveToNext()) {
            val c = Place (
                cursor.getInt(0), //ID
                cursor.getInt(1), //ID_COUNTRY
                cursor.getString(2), //NAME
                cursor.getString(3), //DESC
                cursor.getString(4), //URLIMG
                cursor.getInt(5) //VISITED
            )
            listPlaces.add(c)
        }
        cursor.close()

        return listPlaces
    }
}