package com.example.EmployeeOfTheMonth;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static java.lang.System.out;


/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public GalleryFragment() {
        // Required empty public constructor
    }

    private File filePathImageCamera;
    private Uri imagePath;
    private static final int IMAGE_GALLERY_REQUEST = 2;
    private static final int IMAGE_CAMERA_REQUEST = 3;
    private String imageFilePath;
    private ImageView imageView;
    private static Context context = null;
    private BitmapDrawable drawable;
    private Bitmap bitmap;
    private String[] stickerNames = {"glasses", "rednose", "hair_1", "hair_2", "confettie"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        final View RootView = inflater.inflate(R.layout.fragment_gallery, container, false);

        imageView = RootView.findViewById(R.id.imageView);
        final ConstraintLayout canvas = (ConstraintLayout) RootView.findViewById(R.id.frameLayout2);

        final Spinner spin = (Spinner) RootView.findViewById(R.id.simpleSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, stickerNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);

        Button saveButton = (Button) RootView.findViewById(R.id.buttonSave);
        Button stickerButton = (Button) RootView.findViewById(R.id.buttonSticker);
        Button textButton = (Button) RootView.findViewById(R.id.buttonText);
        Button galleryButton = (Button) RootView.findViewById(R.id.buttonGal);


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

        // Save the edited image
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                makeBitmap();
                saveEdit();
            }
        });

        // Select and add a Sticker image to canvas
        stickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = spin.getSelectedItem().toString();

                StickerImageView iv_sticker = new StickerImageView(GalleryFragment.context);

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
        // add a sticker Text to canvas
        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat month_date = new SimpleDateFormat("MMMM");

                String showMonth = month_date.format(cal.getTime());

                StickerTextView tv_sticker = new StickerTextView(GalleryFragment.context);
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


                Glide.with(GalleryFragment.this).load(imagePath).into(imageView);


            }
        } else if (requestCode == IMAGE_CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (filePathImageCamera != null && filePathImageCamera.exists()) {

                    imagePath = Uri.fromFile(filePathImageCamera);

                    Glide.with(GalleryFragment.this).load(imagePath).into(imageView);
                }

            }
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
       // Toast.makeText(getContext(), "Sticker: " + stickerNames[position], Toast.LENGTH_SHORT).show();

        Object item = arg0.getItemAtPosition(position);
        Toast.makeText(getContext(), "Sticker: " + item, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO - Custom Code
    }

    //Save edited view
    private void saveEdit(){
        FileOutputStream outputStream = null;


        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "EDIT" + timeStamp + "_";
        File sdCard = Environment.getExternalStorageDirectory();
        File storageDir = new File(sdCard.getAbsolutePath() + "/Images/");

        // Create the storage directory if it does not exist
        assert storageDir != null;
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Log.d("error", "failed to create directory");
        }

        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageFilePath = image.getAbsolutePath();


        Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show();

        try {
            outputStream = new FileOutputStream(storageDir);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(storageDir));
            context.sendBroadcast(intent);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Make bitmap from imageview
    private void makeBitmap() {
        View v = new View(getContext());
        Bitmap bitmap = Bitmap.createBitmap(500/*width*/, 500/*height*/, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        //ImageView iv = (ImageView) view.findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
    }
}




