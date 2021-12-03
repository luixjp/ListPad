package br.edu.ifsp.scl.sdm.listpad.data;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import br.edu.ifsp.scl.sdm.listpad.interfaces.IPhotoCallBack;

public class FirebaseStorageAPI {


    public static final String TAG_Imagens = "Imagens";
    public static final String TAG_Countries = "Countries";
    public static final String TAG_Places = "Places";
    private static StorageReference referenciaStorage;

    public FirebaseStorageAPI() {
        getFirebaseStorage();
    }

    public static StorageReference getFirebaseStorage() {
        if (referenciaStorage == null) {
            referenciaStorage = FirebaseStorage.getInstance().getReference();
        }
        return referenciaStorage;
    }


    public static void deletePhotoCountry(int id) {

        final StorageReference imagemRef = getFirebaseStorage()
                .child(TAG_Imagens)
                .child(TAG_Countries)
                .child(id + "_country.jpg");;

        imagemRef.delete();
    }

    public static void deletePhotoPlace(int id) {
        final StorageReference imagemRef = getFirebaseStorage()
                .child(TAG_Imagens)
                .child(TAG_Places)
                .child(id + "_place.jpg");

                imagemRef.delete();
    }

    public static void savePhotoCountry(int id, final byte[] data, final IPhotoCallBack callBack ) {

            final StorageReference imagemRef = getFirebaseStorage()
                    .child(TAG_Imagens)
                    .child(TAG_Countries)
                    .child(id + "_country.jpg");

            UploadTask uploadTask = imagemRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callBack.onCallBackPhoto(null);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imagemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String sURI = uri.toString();
                            callBack.onCallBackPhoto(sURI);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d("TAG",e.getMessage());
                        }
                    });;
                }
            });
    }


    public static void savePhotoPlace(int id, final byte[] data, final IPhotoCallBack callBack ) {

        final StorageReference imagemRef = getFirebaseStorage()
                .child(TAG_Imagens)
                .child(TAG_Places)
                .child(id + "_place.jpg");

        UploadTask uploadTask = imagemRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callBack.onCallBackPhoto(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String sURI = uri.toString();
                        callBack.onCallBackPhoto(sURI);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("TAG",e.getMessage());
                    }
                });;
            }
        });
    }


}
