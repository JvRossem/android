package com.example.EmployeeOfTheMonth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShareFragment extends Fragment {

    public ShareFragment() {
        // Required empty public constructor
    }

    private Uri imagePath;
    private File filePathImageCamera;
    private String imageFilePath;
    private static final int IMAGE_GALLERY_REQUEST = 2;
    private static final int IMAGE_CAMERA_REQUEST = 3;
    private ImageView imageView;
    private static Context context = null;
    Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        final View RootView = inflater.inflate(R.layout.fragment_share, container, false);
        this.imageView = RootView.findViewById(R.id.imageView);

        Button shareButton = (Button) RootView.findViewById(R.id.sharebutton);
        Button galleryButton = (Button) RootView.findViewById(R.id.buttonGal);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        galleryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                photoGalleryIntent();
            }

            private void photoGalleryIntent() {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Get Image From"), IMAGE_GALLERY_REQUEST);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                Uri screenshotUri = Uri.parse(String.valueOf(imagePath));

                sharingIntent.setType("image/png");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                startActivity(Intent.createChooser(sharingIntent, "Share image using"));

            }
        });
        return RootView;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {

                imagePath = data.getData();


                Glide.with(ShareFragment.this).load(imagePath).into(imageView);


            }
        } else if (requestCode == IMAGE_CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (filePathImageCamera != null && filePathImageCamera.exists()) {

                    imagePath = Uri.fromFile(filePathImageCamera);

                    Glide.with(ShareFragment.this).load(imagePath).into(imageView);
                }

            }
        }

    }
}