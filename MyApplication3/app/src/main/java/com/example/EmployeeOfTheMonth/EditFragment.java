package com.example.EmployeeOfTheMonth;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final int IMAGE_GALLERY_REQUEST = 2;
    private static final int IMAGE_CAMERA_REQUEST = 3;
    private static Context context = null;
    private File filePathImageCamera;
    private Uri imagePath;
    private String imageFilePath;
    private ImageView imageView;
    private BitmapDrawable drawable;
    private Bitmap bitmap;
    private String[] stickerNames = {"glasses", "rednose", "hair_1", "hair_2", "confettie"};
    public EditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        final View RootView = inflater.inflate(R.layout.fragment_edit, container, false);

        imageView = RootView.findViewById(R.id.imageView);
        final FrameLayout canvas = (FrameLayout) RootView.findViewById(R.id.frame_container);

        // spinner
        final Spinner spin = (Spinner) RootView.findViewById(R.id.simpleSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, stickerNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);


        // buttons
        Button saveButton = (Button) RootView.findViewById(R.id.buttonSave);
        Button stickerButton = (Button) RootView.findViewById(R.id.buttonSticker);
        Button textButton = (Button) RootView.findViewById(R.id.buttonText);
        Button galleryButton = (Button) RootView.findViewById(R.id.buttonGal);


        galleryButton.setOnClickListener(new View.OnClickListener() {

            // Get a picture fromm gallery
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

        // Save the edited image
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                requestPermission();
                try {
                    saveEdit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Select and add a Sticker-image to canvas
        stickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = spin.getSelectedItem().toString();

                StickerImageView iv_sticker = new StickerImageView(EditFragment.context);

                switch (text) {
                    case "glasses":
                        iv_sticker.setImageDrawable(getResources().getDrawable(R.drawable.glasses));
                        break;
                    case "rednose":
                        iv_sticker.setImageDrawable(getResources().getDrawable(R.drawable.rednose));
                        break;
                    case "hair_1":
                        iv_sticker.setImageDrawable(getResources().getDrawable(R.drawable.hair_1));
                        break;
                    case "hair_2":
                        iv_sticker.setImageDrawable(getResources().getDrawable(R.drawable.hair_blonde));
                        break;
                    default:
                        iv_sticker.setImageDrawable(getResources().getDrawable(R.drawable.confettie));
                        break;
                }
                canvas.addView(iv_sticker);

            }
        });

        // Add a sticker-Text to canvas
        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat month_date = new SimpleDateFormat("MMMM");

                String showMonth = month_date.format(cal.getTime());

                StickerTextView tv_sticker = new StickerTextView(EditFragment.context);
                tv_sticker.setText("Novi medewerker van de Maand " + showMonth);
                canvas.addView(tv_sticker);
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

                Glide.with(EditFragment.this).load(imagePath).into(imageView);

            }
        } else if (requestCode == IMAGE_CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (filePathImageCamera != null && filePathImageCamera.exists()) {

                    imagePath = Uri.fromFile(filePathImageCamera);

                    Glide.with(EditFragment.this).load(imagePath).into(imageView);
                }

            }
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Object item = arg0.getItemAtPosition(position);
        Toast.makeText(getContext(), "Sticker: " + item, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO
    }

    //Save the edited picture
    private void saveEdit() throws IOException {

        //Get the bitmap from the view
        bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        imageView.draw(canvas);

        //Saving the bitmap
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "EDIT_" + timeStamp + "_";

        File sdCard = Environment.getExternalStorageDirectory();
        File storageDir = new File(sdCard.getAbsolutePath() + "/Images/");
        // File storageDir = new File(context.getExternalFilesDir(null), "Images");

        //Create the storage directory if it does not exist
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        //File image = null;

        File image = new File(storageDir, imageFileName+".jpg");
        //image = File.createTempFile(imageFileName, ".png", storageDir);

        FileOutputStream out = new FileOutputStream(image);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
        imageView.destroyDrawingCache();

        Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show();
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





