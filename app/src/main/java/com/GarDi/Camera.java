package com.GarDi;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.GarDi.Models.RequestJSoup;
import com.GarDi.Models.Singleton;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.protobuf.ByteString;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;


public class Camera extends AppCompatActivity {


    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ToneGenerator toneGen1;
    private String barcodeData;
    private FloatingActionButton button;
    public String photoPath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        surfaceView = findViewById(R.id.cameraView);
        button = findViewById(R.id.cameraButton);
        initialiseDetectorsAndSources();
    }

    private void initialiseDetectorsAndSources() {

        button.setOnClickListener(v -> {
            if (cameraSource != null) {
                cameraSource.takePicture(null, data -> {

                    File photoFile;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                    if (bitmap != null) {
                        Log.e("TAG", bitmap.toString() + "nr 2");
                        try {
                            File storageDir = getFilesDir();
                            photoFile = File.createTempFile("SNAPSHOT", ".jpg", storageDir);
                            photoPath = photoFile.getAbsolutePath();

                            FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                            fileOutputStream.flush();
                            fileOutputStream.close();

                            if (photoPath != null) {
                                Intent intent = new Intent(Camera.this, InfoActivity.class);
                                intent.putExtra("photoPath", photoPath);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "processing picture, please hold", Toast.LENGTH_LONG).show();
                                photoPath = null;
                            }else{
                                Toast.makeText(getApplicationContext(), "empty photoPath, try again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception exception) {
                            Toast.makeText(getApplicationContext(), "Error saving: " + exception.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Var vänlig och försök igen", Toast.LENGTH_SHORT).show();
            }
        });

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(getScreenHeight(), getScreenWidth())
                .setAutoFocusEnabled(true)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.e("TAG", "surface created");
                try {
                    if (ActivityCompat.checkSelfPermission(Camera.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && cameraSource != null) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(Camera.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.e("TAG", "surface changed");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.e("TAG", "surface destroyed");

                if (cameraSource != null) {
                    cameraSource.release();
                    cameraSource = null;
                }
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), "Var vänlig och försök igen", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {

                    if (barcodes.valueAt(0).email != null) {
                        barcodeData = barcodes.valueAt(0).email.toString();
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                        Singleton.getInstance().setScannedText(barcodeData);
                        Singleton.getInstance().setBarcode(generateBarcodeFromString(barcodeData));
                        Singleton.getInstance().setMaterialOfProduct(retrieveMaterialFromBarcode(barcodeData));
                        Intent intent = new Intent(getApplicationContext(), BarcodeScanned.class);
                        startActivity(intent);
                        try {
                            Singleton.getInstance().setItemName(RequestJSoup.getSearchResultFromGoogle(barcodeData));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        barcodeData = barcodes.valueAt(0).displayValue;
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                        Singleton.getInstance().setScannedText(barcodeData);
                        Singleton.getInstance().setBarcode(generateBarcodeFromString(barcodeData));
                        Singleton.getInstance().setMaterialOfProduct(retrieveMaterialFromBarcode(barcodeData));
                        Intent intent = new Intent(getApplicationContext(), BarcodeScanned.class);
                        startActivity(intent);
                        try {
                            Singleton.getInstance().setItemName(RequestJSoup.getSearchResultFromGoogle(barcodeData));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }


    private String retrieveMaterialFromBarcode(String barcodeData) {    // Takes the barcode and returns the materials of the product :)
        String material = "No materials found";
        String url = "https://world.openfoodfacts.org/api/v0/product/" + barcodeData + ".json";

        JSONParser parser = new JSONParser();

        try {
            URL oracle = new URL(url); // URL to Parse
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

            String inputLine;

            if ((inputLine = in.readLine()) != null) {
                JSONObject jsonObject = (JSONObject) parser.parse(inputLine);
                JSONObject product;

                if (jsonObject.get("product") != null) {
                    product = (JSONObject) jsonObject.get("product");
                    if (product.get("packaging") != null) { // If no packaging exists in DB
                        material = product.get("packaging").toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return material;
    }

    private static Bitmap generateBarcodeFromString(String barcodeText) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(barcodeText, BarcodeFormat.CODABAR, getScreenWidth(), 300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    @Override
    protected void onPause() {
        super.onPause();
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Objects.requireNonNull(getSupportActionBar()).hide();
    }
}