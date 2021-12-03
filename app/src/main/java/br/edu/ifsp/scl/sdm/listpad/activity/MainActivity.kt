package br.edu.ifsp.scl.sdm.listpad.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import br.edu.ifsp.scl.sdm.listpad.databinding.ActivityMainBinding
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.text.HtmlCompat
import br.edu.ifsp.scl.sdm.listpad.R
import br.edu.ifsp.scl.sdm.listpad.model.Continent
import java.util.*


class MainActivity : AppCompatActivity() {

    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    companion object Extras {
        const val EXTRA_ID_CONTINENT = "br.edu.ifsp.scl.sdm.listpad.activity.MainActivity.EXTRA_ID_CONTINENT"
    }

    private var idContinent: Int = -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        val actionBar = supportActionBar
        actionBar?.setSubtitle(HtmlCompat.fromHtml("<font color='#FFBF00'>Select a Continent or World</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))


        val continentsNames = arrayOf(
            Continent.WORLD.countryName,
            Continent.AFRICA.countryName,
            Continent.AMERICAS.countryName,
            Continent.ASIA.countryName,
            Continent.EUROPE.countryName,
            Continent.OCEANIA.countryName
        )

        val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, continentsNames
        )

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        activityMainBinding.spinContinents.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

                val countrySelected = parent.selectedItem as String
                when(Continent.valueOf(countrySelected.uppercase(Locale.getDefault()))) {
                    Continent.WORLD -> {
                        activityMainBinding.ivContinent.setImageResource(R.mipmap.ic_world_map)
                        idContinent = Continent.WORLD.ordinal
                    }
                    Continent.AFRICA -> {
                        activityMainBinding.ivContinent.setImageResource(R.mipmap.ic_africa_map)
                        idContinent = Continent.AFRICA.ordinal
                    }
                    Continent.AMERICAS -> {
                        activityMainBinding.ivContinent.setImageResource(R.mipmap.ic_americas_map)
                        idContinent = Continent.AMERICAS.ordinal
                    }
                    Continent.ASIA -> {
                        activityMainBinding.ivContinent.setImageResource(R.mipmap.ic_asia_map)
                        idContinent = Continent.ASIA.ordinal
                    }
                    Continent.EUROPE -> {
                        activityMainBinding.ivContinent.setImageResource(R.mipmap.ic_europe_map)
                        idContinent = Continent.EUROPE.ordinal
                    }
                    Continent.OCEANIA -> {
                        activityMainBinding.ivContinent.setImageResource(R.mipmap.ic_oceania_map)
                        idContinent = Continent.OCEANIA.ordinal
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        activityMainBinding.spinContinents.setAdapter(spinnerArrayAdapter)

        activityMainBinding.btnTravel.setOnClickListener {
            //Redirecionar para a tela de paises (countries)
            val intentCountry = Intent(this, CountryActivity::class.java)
            intentCountry.putExtra(EXTRA_ID_CONTINENT, idContinent)
            startActivity(intentCountry)
        }

    }


}
