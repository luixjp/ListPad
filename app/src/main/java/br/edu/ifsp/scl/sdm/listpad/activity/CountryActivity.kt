package br.edu.ifsp.scl.sdm.listpad.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.scl.sdm.listpad.R
import br.edu.ifsp.scl.sdm.listpad.data.CountryAdapter
import br.edu.ifsp.scl.sdm.listpad.data.DatabaseHelper
import br.edu.ifsp.scl.sdm.listpad.databinding.ActivityCountryBinding
import br.edu.ifsp.scl.sdm.listpad.model.Country
import com.google.android.material.floatingactionbutton.FloatingActionButton


class CountryActivity : AppCompatActivity() {

    private val activityCountryBinding: ActivityCountryBinding by lazy {
        ActivityCountryBinding.inflate(layoutInflater)
    }

    companion object Extras {
        const val EXTRA_ID_COUNTRY = "br.edu.ifsp.scl.sdm.listpad.activity.CountryActivity.EXTRA_ID_COUNTRY"
    }

    val db = DatabaseHelper(this)
    var countryList = ArrayList<Country>()
    lateinit var countryAdapter: CountryAdapter
    private var idContinent: Int = -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_country)
        setContentView(activityCountryBinding.root)

        val actionBar = supportActionBar
        actionBar?.setSubtitle(HtmlCompat.fromHtml("<font color='#FFBF00'>List of Countries</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))


        //Recuperando o Continent enviado da Tela Principal (MainActivity)
        idContinent= intent.getIntExtra(MainActivity.EXTRA_ID_CONTINENT, -1);
        if (idContinent == -1) {
            Toast.makeText(this, "Error! Continent ID not valid!", Toast.LENGTH_LONG).show()
            finish();
        }
        else
        {
            val fab = findViewById<FloatingActionButton>(R.id.fab)
            fab.setOnClickListener {

                if(idContinent == 0) {
                    //World
                    Toast.makeText(applicationContext, "You must select a Continent to add a Country. Please, back to the main screen!", Toast.LENGTH_LONG).show()
                }
                else {
                    val intent = Intent(this, CadastroCountryActivity::class.java)
                    intent.putExtra(MainActivity.EXTRA_ID_CONTINENT, idContinent)
                    startActivity(intent)
                }
            }
            updateUI()
        }
    }

    fun updateUI() {

        if(idContinent == 0) {
            countryList = db.listAllCountries()
        }
        else {
            countryList = db.listAllCountriesByContinent(idContinent)
        }

        if(countryList.size > 0) {

            activityCountryBinding.tvNoCountries.visibility = View.GONE
            activityCountryBinding.recyclerView.visibility = View.VISIBLE

            countryAdapter = CountryAdapter(countryList)

            activityCountryBinding.recyclerView.layoutManager = LinearLayoutManager(this)
            activityCountryBinding.recyclerView.adapter = countryAdapter

            var listener = object : CountryAdapter.CountryListener {
                override fun onItemClick(pos: Int) {
                    val intent = Intent(applicationContext, PlaceActivity::class.java)
                    val c = countryAdapter.countryList[pos]
                    intent.putExtra(EXTRA_ID_COUNTRY, c.id)
                    startActivity(intent)
                }
            }
            countryAdapter.setClickListener(listener)

            var longListener = object : CountryAdapter.LongCountryListener {
                override fun onItemLongClick(pos: Int) {
                    val intent = Intent(applicationContext, CountryEditActivity::class.java)
                    val c = countryAdapter.countryList[pos]
                    intent.putExtra(EXTRA_ID_COUNTRY, c.id)
                    startActivity(intent)
                }
            }
            countryAdapter.setLongClickListener(longListener)
        }
        else {
            activityCountryBinding.tvNoCountries.visibility = View.VISIBLE
            activityCountryBinding.recyclerView.visibility = View.GONE
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_search, menu)

        val item = menu?.findItem(R.id.action_search)
        val searchView = item?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(criterio: String?): Boolean {
                countryAdapter?.filter?.filter(criterio)
                return true
            }

            override fun onQueryTextChange(criterio: String?): Boolean {
                countryAdapter?.filter?.filter(criterio)
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