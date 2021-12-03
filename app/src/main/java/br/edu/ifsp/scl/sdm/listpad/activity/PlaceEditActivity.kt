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
import br.edu.ifsp.scl.sdm.listpad.databinding.ActivityPlaceEditBinding
import br.edu.ifsp.scl.sdm.listpad.interfaces.IPhotoCallBack
import br.edu.ifsp.scl.sdm.listpad.model.Place
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream


class PlaceEditActivity : AppCompatActivity() {

    private val activityPlaceEditBinding: ActivityPlaceEditBinding by lazy {
        ActivityPlaceEditBinding.inflate(layoutInflater)
    }

    private var idPlace: Int = -1;
    private var IS_IMAGE_OK = false
    private lateinit var selecionarImagemActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityPlaceEditBinding.root)

        val actionBar = supportActionBar
        actionBar?.setSubtitle(HtmlCompat.fromHtml("<font color='#FFBF00'>Review a Place</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))


        activityPlaceEditBinding.progressIndicatorPlace.hide()

        idPlace = intent.getIntExtra(PlaceActivity.EXTRA_ID_PLACE, -1);
        if(idPlace < 0) {
            Toast.makeText(this, "Error! Place's ID not valid!", Toast.LENGTH_LONG).show()
            finish()
        }
        else {
            loadData()
        }

        selecionarImagemActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult> { resultado ->
                if (resultado.resultCode == RESULT_OK){
                    activityPlaceEditBinding.imagePhotoPlace.setImageURI(resultado.data?.data)
                    IS_IMAGE_OK = true
                }
            })

        activityPlaceEditBinding.imagePhotoPlace.setOnClickListener {
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

        activityPlaceEditBinding.btnBackPlace.setOnClickListener {
            finish();
        }

        activityPlaceEditBinding.btnDeletePlace.setOnClickListener {
            delete();
        }

        activityPlaceEditBinding.btnUpdatePlace.setOnClickListener {
            update();
        }
    }

    fun loadData() {
        val db = DatabaseHelper(this)
        val place = db.selectPlace(idPlace);


        //Carregar imagem na tela
        if (place.urlImage != null && place.urlImage != "") {
            Picasso.get().load(place.urlImage).into(activityPlaceEditBinding.imagePhotoPlace)
        }

        //name
        activityPlaceEditBinding.edtPlaceName.setText(place.name)

        //desc
        activityPlaceEditBinding.edtPlaceDescription.setText(place.description)

        //visited
        activityPlaceEditBinding.ckbVisited.isChecked = place.visited!! > 0

    }

    fun update() {

        val name = activityPlaceEditBinding.edtPlaceName.text.toString()
        val desc = activityPlaceEditBinding.edtPlaceDescription.text.toString()
        val isVisited = if(activityPlaceEditBinding.ckbVisited.isChecked) 1 else 0

        if(name.isNotEmpty() && name.isNotBlank()) {
            bloqueiaTela()

            //Save SQLite without image
            val db = DatabaseHelper(this)
            val place = db.selectPlace(idPlace)
            place.name = name
            place.description = desc
            place.visited = isVisited


            //Save photo on Firebase (firestorage)
            if (IS_IMAGE_OK) {
                val bitmap = (activityPlaceEditBinding.imagePhotoPlace.getDrawable() as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
                val dataImage: ByteArray = baos.toByteArray()

                if (place.urlImage.isNotEmpty()) {
                    //delete old image
                    FirebaseStorageAPI.deletePhotoPlace(place.id!!)
                }

                FirebaseStorageAPI.savePhotoPlace(place.id!!, dataImage, IPhotoCallBack { urlPhoto ->
                    place.urlImage = urlPhoto
                    //Save data into SQLite
                    db.updatePlace(place)
                })

            }
            else {
                //Save data into SQLite
                db.updatePlace(place)
            }

            Toast.makeText(this, "Place Added!", Toast.LENGTH_LONG).show()
            finish()

        }
        else {
            Toast.makeText(this, "You must provide a Place Name!", Toast.LENGTH_LONG).show()
            desbloqueiaTela()
        }

    }

    private fun delete() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete the Place")
        builder.setMessage("Do you want to delete the Place?")
        builder.setIcon(R.drawable.ic_dialog_alert)
        builder.setPositiveButton(
            "Yes"){ dialog, which ->
            val db = DatabaseHelper(this)

            //TODO: Como fazer uma transaction no Android/Kotlin??
            //{

            //excluir lugar
            db.deletePlaceById(idPlace)

            //excluir imagem do lugar
            FirebaseStorageAPI.deletePhotoPlace(idPlace)
            //}

            Toast.makeText(this, "Place was deleted!", Toast.LENGTH_LONG).show()
            finish()

        }
        builder.setNegativeButton("No", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun bloqueiaTela() {

        activityPlaceEditBinding.btnUpdatePlace.setVisibility(View.GONE)
        activityPlaceEditBinding.btnDeletePlace.setVisibility(View.GONE)
        activityPlaceEditBinding.btnBackPlace.setVisibility(View.GONE)
        activityPlaceEditBinding.imagePhotoPlace.setEnabled(false)
        activityPlaceEditBinding.edtPlaceName.setEnabled(false)
        activityPlaceEditBinding.edtPlaceDescription.setEnabled(false)
        activityPlaceEditBinding.ckbVisited.setEnabled(false)
    }

    private fun desbloqueiaTela() {

        activityPlaceEditBinding.btnUpdatePlace.setVisibility(View.VISIBLE)
        activityPlaceEditBinding.btnDeletePlace.setVisibility(View.VISIBLE)
        activityPlaceEditBinding.btnBackPlace.setVisibility(View.VISIBLE)
        activityPlaceEditBinding.imagePhotoPlace.setEnabled(true)
        activityPlaceEditBinding.edtPlaceName.setEnabled(true)
        activityPlaceEditBinding.edtPlaceDescription.setEnabled(true)
        activityPlaceEditBinding.ckbVisited.setEnabled(true)
    }

}