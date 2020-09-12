package edu.singaporetech.helpla.firebaseHelper;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import androidx.annotation.NonNull;
import edu.singaporetech.helpla.utils.Utils;

public class FirebaseStorageHelper {



    private StorageReference storageReference;
    private FirebaseDatabaseHelper databaseHelper;
    private OnDownloadImage lstr;
    public FirebaseStorageHelper(OnDownloadImage lstr){
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseHelper = new FirebaseDatabaseHelper(null);
        this.lstr = lstr;
    }

    public void uploadFile(Context context, Uri ImageUri, final String key){
        if(ImageUri != null){
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + Utils.getFileExtension(ImageUri,context));
            fileReference.putFile(ImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("FirebaseStorageHelper",key);
                            HashMap<String,String> img = new HashMap<>();
                            img.put("image_url",taskSnapshot.getMetadata().getPath());
                            databaseHelper.updateData(key,img);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("FED","FAILED " + e.getMessage());

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot
//                    .getTotalByteCount());
//                    progressBar.setProgress((int) progress);
                }
            });
        }
        else {
            Toast toast = Toast.makeText(context,
                    "No file selected", Toast.LENGTH_SHORT); toast.show();

        }
    }

    public void getImage(String imageName){
        if(imageName != null){
            StorageReference storageReference =  FirebaseStorage.getInstance().getReference();;
            storageReference.child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    lstr.onCompleted(uri);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

        }

    }

    public interface OnDownloadImage{
        void onCompleted(Uri uri);
    }
}