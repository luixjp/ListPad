package br.edu.ifsp.scl.sdm.listpad.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.text.HtmlCompat
import br.edu.ifsp.scl.sdm.listpad.data.DatabaseHelper
import br.edu.ifsp.scl.sdm.listpad.databinding.ActivityPlaceViewBinding
import com.squareup.picasso.Picasso

class PlaceViewActivity : AppCompatActivity() {

    private var idPlace: Int = -1;

    private val activityPlaceViewBinding: ActivityPlaceViewBinding by lazy {
        ActivityPlaceViewBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityPlaceViewBinding.root)

        val actionBar = supportActionBar
        actionBar?.setSubtitle(HtmlCompat.fromHtml("<font color='#FFBF00'>View a Place</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))


        idPlace = intent.getIntExtra(PlaceActivity.EXTRA_ID_PLACE, -1);
        if(idPlace < 0) {
            Toast.makeText(this, "Error! Place's ID not valid!", Toast.LENGTH_LONG).show()
            finish()
        }
        else {
            loadData()
        }

        activityPlaceViewBinding.btnBackPlace.setOnClickListener {
            finish();
        }
    }

    fun loadData() {
        val db = DatabaseHelper(this)
        val place = db.selectPlace(idPlace);


        //Carregar imagem na tela
        if (place.urlImage != null && place.urlImage != "") {
            Picasso.get().load(place.urlImage).into(activityPlaceViewBinding.imagePhotoPlace)
        }
        activityPlaceViewBinding.imagePhotoPlace.isEnabled = false

        //name
        activityPlaceViewBinding.tvPlaceName.setText(place.name)

        //desc
        activityPlaceViewBinding.tvPlaceDescription.setText(place.description)

        //visited
        val visitedText = if(place.visited!! > 0) "Yes" else "No"
        activityPlaceViewBinding.tvVisited.setText(visitedText);

    }
}