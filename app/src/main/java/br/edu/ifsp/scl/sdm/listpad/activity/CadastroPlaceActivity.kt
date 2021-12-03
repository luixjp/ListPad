package br.edu.ifsp.scl.sdm.listpad.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.HtmlCompat
import br.edu.ifsp.scl.sdm.listpad.data.DatabaseHelper
import br.edu.ifsp.scl.sdm.listpad.data.FirebaseStorageAPI
import br.edu.ifsp.scl.sdm.listpad.databinding.ActivityCadastroCountryBinding
import br.edu.ifsp.scl.sdm.listpad.databinding.ActivityCadastroPlaceBinding
import br.edu.ifsp.scl.sdm.listpad.interfaces.IPhotoCallBack
import br.edu.ifsp.scl.sdm.listpad.model.Continent
import br.edu.ifsp.scl.sdm.listpad.model.Country
import br.edu.ifsp.scl.sdm.listpad.model.Place
import java.io.ByteArrayOutputStream

class CadastroPlaceActivity : AppCompatActivity() {

    private val activityCadastroPlaceActivity : ActivityCadastroPlaceBinding by lazy {
        ActivityCadastroPlaceBinding.inflate(layoutInflater)
    }

    private var idCountry: Int = -1;
    private var IS_IMAGE_OK = false
    private lateinit var selecionarImagemActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityCadastroPlaceActivity.root)

        val actionBar = supportActionBar
        actionBar?.setSubtitle(HtmlCompat.fromHtml("<font color='#FFBF00'>Create a Place</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))


        activityCadastroPlaceActivity.progressIndicatorPlace.hide()


        selecionarImagemActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult> { resultado ->
                if (resultado.resultCode == RESULT_OK){
                    activityCadastroPlaceActivity.imagePhotoPlace.setImageURI(resultado.data?.data)
                    IS_IMAGE_OK = true
                }
            })

        activityCadastroPlaceActivity.imagePhotoPlace.setOnClickListener {
            val i = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            if (i.resolveActivity(packageManager) != null) {
                selecionarImagemActivityResultLauncher.launch(i)
            } else {
                Toast.makeText(
                    applicationContext,
                    "Problems accessing the Image Gallery.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        activityCadastroPlaceActivity.btnBackPlace.setOnClickListener {
            finish();
        }


        activityCadastroPlaceActivity.btnSavePlace.setOnClickListener {
            save();
        }


        idCountry= intent.getIntExtra(CountryActivity.EXTRA_ID_COUNTRY, -1);
        if (idCountry == -1) {
            Toast.makeText(this, "Error! Place's ID not valid!", Toast.LENGTH_LONG).show()
            finish()
        }
        else
        {
            var newText = activityCadastroPlaceActivity.tvIntoPlace.text.toString()

            val db = DatabaseHelper(this)
            val country = db.selectCountry(idCountry)
            newText += country.name
            activityCadastroPlaceActivity.tvIntoPlace.text = newText
        }

    }

    fun save() {

        val name = activityCadastroPlaceActivity.edtPlaceName.text.toString()
        val desc = activityCadastroPlaceActivity.edtPlaceDescription.text.toString()
        val isVisited = if(activityCadastroPlaceActivity.ckbVisited.isChecked) 1 else 0

        if(name.isNotEmpty() && name.isNotBlank()) {
            bloqueiaTela()

            //Save SQLite without image
            val db = DatabaseHelper(this)
            val id = db.insertPlace(Place(null, idCountry, name, desc, "", isVisited))

            //Save photo on Firebase (firestorage)
            if (IS_IMAGE_OK) {
                val bitmap = (activityCadastroPlaceActivity.imagePhotoPlace.getDrawable() as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
                val dataImage: ByteArray = baos.toByteArray()

                FirebaseStorageAPI.savePhotoPlace(id.toInt(), dataImage, IPhotoCallBack { urlPhoto ->

                    db.updatePlace(
                        Place(id.toInt(), idCountry, name, desc,
                        urlPhoto, isVisited )
                    )

                    Toast.makeText(this, "Place Added!", Toast.LENGTH_LONG).show()
                    finish()
                })
            }
            else {
                Toast.makeText(this, "Place Added!", Toast.LENGTH_LONG).show()
                finish()
            }
        }
        else {
            Toast.makeText(this, "You must provide a Place Name!", Toast.LENGTH_LONG).show()
            desbloqueiaTela()
        }

    }

    private fun bloqueiaTela() {

        activityCadastroPlaceActivity.btnSavePlace.setVisibility(View.GONE)
        activityCadastroPlaceActivity.btnBackPlace.setVisibility(View.GONE)
        activityCadastroPlaceActivity.imagePhotoPlace.setEnabled(false)
        activityCadastroPlaceActivity.edtPlaceName.setEnabled(false)
        activityCadastroPlaceActivity.edtPlaceDescription.setEnabled(false)
        activityCadastroPlaceActivity.ckbVisited.setEnabled(false)
    }

    private fun desbloqueiaTela() {

        activityCadastroPlaceActivity.btnSavePlace.setVisibility(View.VISIBLE)
        activityCadastroPlaceActivity.btnBackPlace.setVisibility(View.VISIBLE)
        activityCadastroPlaceActivity.imagePhotoPlace.setEnabled(true)
        activityCadastroPlaceActivity.edtPlaceName.setEnabled(true)
        activityCadastroPlaceActivity.edtPlaceDescription.setEnabled(true)
        activityCadastroPlaceActivity.ckbVisited.setEnabled(true)
    }

}