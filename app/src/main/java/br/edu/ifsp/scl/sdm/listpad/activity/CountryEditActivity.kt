package br.edu.ifsp.scl.sdm.listpad.activity

import android.R
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
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import br.edu.ifsp.scl.sdm.listpad.data.DatabaseHelper
import br.edu.ifsp.scl.sdm.listpad.data.FirebaseStorageAPI
import br.edu.ifsp.scl.sdm.listpad.databinding.ActivityCountryEditBinding
import br.edu.ifsp.scl.sdm.listpad.interfaces.IPhotoCallBack
import java.io.ByteArrayOutputStream
import com.squareup.picasso.Picasso

class CountryEditActivity : AppCompatActivity() {


    private val activityCountryEditBinding: ActivityCountryEditBinding by lazy {
        ActivityCountryEditBinding.inflate(layoutInflater)
    }

    private var idCountry: Int = -1;
    private var IS_IMAGE_OK = false
    private lateinit var selecionarImagemActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityCountryEditBinding.root)

        val actionBar = supportActionBar
        actionBar?.setSubtitle(HtmlCompat.fromHtml("<font color='#FFBF00'>Review a Country</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))


        activityCountryEditBinding.progressIndicatorEdit.hide()

        idCountry = intent.getIntExtra(CountryActivity.EXTRA_ID_COUNTRY, -1);
        if(idCountry < 0) {
            Toast.makeText(this, "Error! Country ID not valid!", Toast.LENGTH_LONG).show()
            finish()
        }
        else {
            loadData()
        }

        selecionarImagemActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult> { resultado ->
                if (resultado.resultCode == RESULT_OK){
                    activityCountryEditBinding.imagePhotoEdit.setImageURI(resultado.data?.data)
                    IS_IMAGE_OK = true
                }
            })

        activityCountryEditBinding.imagePhotoEdit.setOnClickListener {
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

        activityCountryEditBinding.btnBack.setOnClickListener {
            finish();
        }


        activityCountryEditBinding.btnDelete.setOnClickListener {
            delete();
        }


        activityCountryEditBinding.btnUpdate.setOnClickListener {
            update();
        }
    }

    fun loadData() {
        val db = DatabaseHelper(this)
        val country = db.selectCountry(idCountry);


        //Carregar imagem na tela
        if (country.urlImage != null && country.urlImage != "") {
            Picasso.get().load(country.urlImage).into(activityCountryEditBinding.imagePhotoEdit)
        }

        //name
        activityCountryEditBinding.edtCountryNameEdit.setText(country.name)

        //abb
        activityCountryEditBinding.edtCountryAbbreviationEdit.setText(country.abbreviation)

        //urgent
        activityCountryEditBinding.ckbUrgentEdit.isChecked = country.urgent!! > 0

    }

    fun update() {

        val name = activityCountryEditBinding.edtCountryNameEdit.text.toString()
        val abb = activityCountryEditBinding.edtCountryAbbreviationEdit.text.toString()
        val isUrgent = if(activityCountryEditBinding.ckbUrgentEdit.isChecked) 1 else 0

        if(name.isNotEmpty() && name.isNotBlank()) {
            bloqueiaTela()

            val db = DatabaseHelper(this)
            val country = db.selectCountry(idCountry);
            country.name = name
            country.abbreviation = abb
            country.urgent = isUrgent

            //Save photo on Firebase (firestorage)
            if (IS_IMAGE_OK) {

                val bitmap =
                    (activityCountryEditBinding.imagePhotoEdit.getDrawable() as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
                val dataImage: ByteArray = baos.toByteArray()

                if (country.urlImage.isNotEmpty()) {
                    //delete old image
                    FirebaseStorageAPI.deletePhotoCountry(country.id!!)
                }

                FirebaseStorageAPI.savePhotoCountry(country.id!!, dataImage, IPhotoCallBack { urlPhoto ->
                    country.urlImage = urlPhoto
                    //Save data into SQLite
                    db.updateCountry(country)
                })

            }
            else {
                //Save data into SQLite
                db.updateCountry(country)
            }

            Toast.makeText(this, "Country Updated!", Toast.LENGTH_LONG).show()
            finish()
        }
        else {
            Toast.makeText(this, "You must provide a Country Name!", Toast.LENGTH_LONG).show()
            desbloqueiaTela()
        }

    }

    private fun bloqueiaTela() {

        activityCountryEditBinding.btnUpdate.setVisibility(View.GONE)
        activityCountryEditBinding.btnDelete.setVisibility(View.GONE)
        activityCountryEditBinding.btnBack.setVisibility(View.GONE)
        activityCountryEditBinding.imagePhotoEdit.setEnabled(false)
        activityCountryEditBinding.edtCountryNameEdit.setEnabled(false)
        activityCountryEditBinding.edtCountryAbbreviationEdit.setEnabled(false)
        activityCountryEditBinding.ckbUrgentEdit.setEnabled(false)
    }

    private fun desbloqueiaTela() {

        activityCountryEditBinding.btnUpdate.setVisibility(View.VISIBLE)
        activityCountryEditBinding.btnDelete.setVisibility(View.VISIBLE)
        activityCountryEditBinding.btnBack.setVisibility(View.VISIBLE)
        activityCountryEditBinding.imagePhotoEdit.setEnabled(true)
        activityCountryEditBinding.edtCountryNameEdit.setEnabled(true)
        activityCountryEditBinding.edtCountryAbbreviationEdit.setEnabled(true)
        activityCountryEditBinding.ckbUrgentEdit.setEnabled(true)
    }

    private fun delete() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete the Country")
        builder.setMessage("Do you want to delete the Country?")
        builder.setIcon(R.drawable.ic_dialog_alert)
        builder.setPositiveButton(
                "Yes"){ dialog, which ->


            //TODO: Como fazer uma transaction no Android/Kotlin??
            //{
            val db = DatabaseHelper(this)

            //excluir lugares desse pais
            db.deletePlacesByCountryId(idCountry)

            //TODO: criar uma função para excluir as imagens dos lugares que pertecem a um pais

            //excluir pais
            db.deleteCountryById(idCountry)

            //excluir image do pais
            FirebaseStorageAPI.deletePhotoCountry(idCountry)


            //}
            Toast.makeText(this, "Country was deleted!", Toast.LENGTH_LONG).show()
            finish()

            }
        builder.setNegativeButton("No", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}