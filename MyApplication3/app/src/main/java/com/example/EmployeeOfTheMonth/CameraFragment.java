package com.example.EmployeeOfTheMonth;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.example.EmployeeOfTheMonth.BuildConfig.APPLICATION_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment {

    private File filePathImageCamera;
    private Uri imagePath;
    private static final int IMAGE_GALLERY_REQUEST = 2;
    private static final int IMAGE_CAMERA_REQUEST = 3;
    private String imageFilePath;
    private ImageView imageView;
    private static Context context = null;


    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        View RootView = inflater.inflate(R.layout.fragment_camera, container, false);
        this.imageView = (ImageView) RootView.findViewById(R.id.imageView);

        Button photoButton = (Button) RootView.findViewById(R.id.buttonCam);

        requestPermission();
            photoButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri uri = null;

                    try {
                        filePathImageCamera = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File

                    }


                    if (pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                            //Create a file to store the image

                            if (filePathImageCamera != null) {
                                Uri photoURI = FileProvider.getUriForFile(CameraFragment.context, APPLICATION_ID + ".provider", filePathImageCamera);
                                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        photoURI);
                                startActivityForResult(pictureIntent,
                                        IMAGE_CAMERA_REQUEST);
                            }


                        } else {


                            if (filePathImageCamera != null) {
                                uri = Uri.fromFile(filePathImageCamera);

                                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                startActivityForResult(pictureIntent, IMAGE_CAMERA_REQUEST);
                            }


                        }
                    }
                }
            });
            return RootView;
        }

    private File createImageFile() throws IOException {

            File sdCard = Environment.getExternalStorageDirectory();
            File storageDir = new File(sdCard.getAbsolutePath() + "/Images/");


                //File storageDir = new File(context.getExternalFilesDir(null), "Images");

        // Create the storage directory if it does not exist
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Log.d("error", "failed to create directory");
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";


        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        imageFilePath = image.getAbsolutePath();

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(storageDir));
        context.sendBroadcast(intent);

        return image;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {

                imagePath = data.getData();


                Glide.with(CameraFragment.this).load(imagePath).into(imageView);


            }
        } else if (requestCode == IMAGE_CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (filePathImageCamera != null && filePathImageCamera.exists()) {

                    imagePath = Uri.fromFile(filePathImageCamera);


                    Glide.with(CameraFragment.this).load(imagePath).into(imageView);
                }
            }
        }
    }
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat
                    .requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}