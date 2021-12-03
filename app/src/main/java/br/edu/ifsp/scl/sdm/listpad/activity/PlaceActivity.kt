package br.edu.ifsp.scl.sdm.listpad.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.edu.ifsp.scl.sdm.listpad.R
import android.content.Intent
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.scl.sdm.listpad.data.DatabaseHelper
import br.edu.ifsp.scl.sdm.listpad.data.PlaceAdapter
import br.edu.ifsp.scl.sdm.listpad.databinding.ActivityPlaceBinding
import br.edu.ifsp.scl.sdm.listpad.model.Place
import com.google.android.material.floatingactionbutton.FloatingActionButton


class PlaceActivity : AppCompatActivity() {

    private val activityPlaceBinding: ActivityPlaceBinding by lazy {
        ActivityPlaceBinding.inflate(layoutInflater)
    }

    companion object Extras {
        const val EXTRA_ID_PLACE = "br.edu.ifsp.scl.sdm.listpad.activity.CountryActivity.EXTRA_ID_PLACE"
    }

    val db = DatabaseHelper(this)
    var placeList = ArrayList<Place>()
    lateinit var placeAdapter: PlaceAdapter

    private var idCountry: Int = -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityPlaceBinding.root)

        val actionBar = supportActionBar
        actionBar?.setSubtitle(HtmlCompat.fromHtml("<font color='#FFBF00'>List of Places</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))


        //Recuperando o Country enviado da Country (CountryActivity)
        idCountry= intent.getIntExtra(CountryActivity.EXTRA_ID_COUNTRY, -1);
        if (idCountry == -1) {
            Toast.makeText(this, "Error! Country ID not valid!", Toast.LENGTH_LONG).show()
            finish();
        }
        else {
            val fab = findViewById<FloatingActionButton>(R.id.fabPlace)
            fab.setOnClickListener {

                val intent = Intent(this, CadastroPlaceActivity::class.java)
                intent.putExtra(CountryActivity.EXTRA_ID_COUNTRY, idCountry)
                startActivity(intent)

            }
            updateUI()
        }
    }

    fun updateUI() {

        placeList = db.listAllPlacesByCountry(idCountry)

        if(placeList.size > 0) {

            activityPlaceBinding.tvNoPlaces.visibility = View.GONE
            activityPlaceBinding.recyclerViewPlaces.visibility = View.VISIBLE

            placeAdapter = PlaceAdapter(placeList, this)

            activityPlaceBinding.recyclerViewPlaces.layoutManager = LinearLayoutManager(this)
            activityPlaceBinding.recyclerViewPlaces.adapter = placeAdapter

            var listener = object : PlaceAdapter.PlaceListener {
                override fun onItemClick(pos: Int) {
                    val intent = Intent(applicationContext, PlaceViewActivity::class.java)
                    val p = placeAdapter.placeList[pos]
                    intent.putExtra(EXTRA_ID_PLACE, p.id)
                    startActivity(intent)
                }
            }
            placeAdapter.setClickListener(listener)

            var longListener = object : PlaceAdapter.LongPlaceListener {
                override fun onItemLongClick(pos: Int) {
                    val intent = Intent(applicationContext, PlaceEditActivity::class.java)
                    val p = placeAdapter.placeList[pos]
                    intent.putExtra(EXTRA_ID_PLACE, p.id)
                    startActivity(intent)
                }
            }
            placeAdapter.setLongClickListener(longListener)
        }
        else {
            activityPlaceBinding.tvNoPlaces.visibility = View.VISIBLE
            activityPlaceBinding.recyclerViewPlaces.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_search, menu)

        val item = menu?.findItem(R.id.action_search)
        val searchView = item?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(criterio: String?): Boolean {
                placeAdapter?.filter?.filter(criterio)
                return true
            }

            override fun onQueryTextChange(criterio: String?): Boolean {
                placeAdapter?.filter?.filter(criterio)
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        updateUI()
        super.onResume()
    }
}