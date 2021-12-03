package br.edu.ifsp.scl.sdm.listpad.activity


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import br.edu.ifsp.scl.sdm.listpad.data.DatabaseHelper
import br.edu.ifsp.scl.sdm.listpad.databinding.ActivityCadastroCountryBinding
import br.edu.ifsp.scl.sdm.listpad.model.Country
import android.provider.MediaStore
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.activity.result.ActivityResult
import br.edu.ifsp.scl.sdm.listpad.data.FirebaseStorageAPI
import br.edu.ifsp.scl.sdm.listpad.interfaces.IPhotoCallBack
import br.edu.ifsp.scl.sdm.listpad.model.Continent
import java.io.ByteArrayOutputStream
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.text.HtmlCompat


class CadastroCountryActivity : AppCompatActivity() {


    private var idContinent: Int = -1;
    private var IS_IMAGE_OK = false
    private lateinit var selecionarImagemActivityResultLauncher: ActivityResultLauncher<Intent>

    private val activityCadastroCountryBinding: ActivityCadastroCountryBinding by lazy {
        ActivityCadastroCountryBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityCadastroCountryBinding.root)

        val actionBar = supportActionBar
        actionBar?.setSubtitle(HtmlCompat.fromHtml("<font color='#FFBF00'>Create a Country</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))

        activityCadastroCountryBinding.progressIndicator.hide()

        selecionarImagemActivityResultLauncher = registerForActivityResult(StartActivityForResult(),
            ActivityResultCallback<ActivityResult> { resultado ->
                if (resultado.resultCode == RESULT_OK){
                    activityCadastroCountryBinding.imagePhoto.setImageURI(resultado.data?.data)
                    IS_IMAGE_OK = true
                }
            })

        activityCadastroCountryBinding.imagePhoto.setOnClickListener {
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

        activityCadastroCountryBinding.btnBack.setOnClickListener {
            finish();
        }


        activityCadastroCountryBinding.btnSave.setOnClickListener {
            save();
        }


        idContinent= intent.getIntExtra(MainActivity.EXTRA_ID_CONTINENT, -1);
        if (idContinent == -1) {
            Toast.makeText(this, "Error! Continent ID not valid!", Toast.LENGTH_LONG).show()
            finish()
        }
        else
        {
            var newText = activityCadastroCountryBinding.tvIntoContinent.text.toString()
            when(idContinent) {

                Continent.AFRICA.ordinal -> {
                    newText += Continent.AFRICA.countryName
                }
                Continent.AMERICAS.ordinal -> {
                    newText += Continent.AMERICAS.countryName
                }
                Continent.ASIA.ordinal -> {
                    newText += Continent.ASIA.countryName
                }
                Continent.EUROPE.ordinal -> {
                    newText += Continent.EUROPE.countryName
                }
                Continent.OCEANIA.ordinal -> {
                    newText += Continent.OCEANIA.countryName
                }
            }

            activityCadastroCountryBinding.tvIntoContinent.text = newText
        }

    }

    fun save() {

        val name = activityCadastroCountryBinding.edtCountryName.text.toString()
        val abb = activityCadastroCountryBinding.edtCountryAbbreviation.text.toString()
        val isUrgent = if(activityCadastroCountryBinding.ckbUrgent.isChecked) 1 else 0

        if(name.isNotEmpty() && name.isNotBlank()) {
            bloqueiaTela()

            //Save SQLite without image
            val db = DatabaseHelper(this)
            val id = db.insertCountry(Country(null, idContinent, name, abb, "", isUrgent))

            //Save photo on Firebase (firestorage)
            if (IS_IMAGE_OK) {
                val bitmap = (activityCadastroCountryBinding.imagePhoto.getDrawable() as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
                val dataImage: ByteArray = baos.toByteArray()

                FirebaseStorageAPI.savePhotoCountry(id.toInt(), dataImage, IPhotoCallBack { urlPhoto ->

                    db.updateCountry(Country(id.toInt(), idContinent, name, abb,
                        urlPhoto, isUrgent ))
                })

            }

            Toast.makeText(this, "Country Added!", Toast.LENGTH_LONG).show()
            finish()

        }
        else {
            Toast.makeText(this, "You must provide a Country Name!", Toast.LENGTH_LONG).show()
            desbloqueiaTela()
        }

    }

    private fun bloqueiaTela() {

        activityCadastroCountryBinding.btnSave.setVisibility(View.GONE)
        activityCadastroCountryBinding.btnBack.setVisibility(View.GONE)
        activityCadastroCountryBinding.imagePhoto.setEnabled(false)
        activityCadastroCountryBinding.edtCountryName.setEnabled(false)
        activityCadastroCountryBinding.edtCountryAbbreviation.setEnabled(false)
        activityCadastroCountryBinding.ckbUrgent.setEnabled(false)
    }

    private fun desbloqueiaTela() {

        activityCadastroCountryBinding.btnSave.setVisibility(View.VISIBLE)
        activityCadastroCountryBinding.btnBack.setVisibility(View.VISIBLE)
        activityCadastroCountryBinding.imagePhoto.setEnabled(true)
        activityCadastroCountryBinding.edtCountryName.setEnabled(true)
        activityCadastroCountryBinding.edtCountryAbbreviation.setEnabled(true)
        activityCadastroCountryBinding.ckbUrgent.setEnabled(true)
    }


}