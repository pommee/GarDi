package com.GarDi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.GarDi.Models.Product;
import com.GarDi.Models.RequestJSoup;
import com.GarDi.Models.Singleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class Camera extends AppCompatActivity {

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ToneGenerator toneGen1;
    private String barcodeData;
    private FloatingActionButton button;
    public String photoPath = null;
    private boolean barcodeScanned;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        surfaceView = findViewById(R.id.cameraView);
        button = findViewById(R.id.cameraButton);
        barcodeScanned = false; // Default
        Singleton.getInstance().setProduct(null);
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
                            } else {
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
                //Toast.makeText(getApplicationContext(), "Var vänlig och försök igen", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0 && !barcodeScanned) {

                    if (barcodes.valueAt(0).email != null) {
                        barcodeScanned = true;
                        barcodeData = barcodes.valueAt(0).email.toString();
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                        Singleton.getInstance().setScannedText(barcodeData);
                        Singleton.getInstance().setBarcode(generateBarcodeFromString(barcodeData));
                        retrieveMaterialFromBarcode(barcodeData);
                        try {
                            Singleton.getInstance().setItemName(RequestJSoup.getSearchResultFromGoogle(barcodeData));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(getApplicationContext(), BarcodeScanned.class);
                        startActivity(intent);

                    } else {
                        barcodeScanned = true;
                        barcodeData = barcodes.valueAt(0).displayValue;
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

                        Singleton.getInstance().setScannedText(barcodeData);
                        Singleton.getInstance().setBarcode(generateBarcodeFromString(barcodeData));
                        retrieveMaterialFromBarcode(barcodeData);
                        try {
                            Singleton.getInstance().setItemName(RequestJSoup.getSearchResultFromGoogle(barcodeData));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (Singleton.getInstance().getMaterialOfProduct() != null) {
                            Intent intent = new Intent(getApplicationContext(), BarcodeScanned.class);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }


    private void retrieveMaterialFromBarcode(String barcodeData) {    // Takes the barcode and returns the materials of the product :)
        Singleton.getInstance().setMaterialOfProduct("No materials found");
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
                        Singleton.getInstance().setMaterialOfProduct(product.get("packaging").toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Singleton.getInstance().getMaterialOfProduct().equals("No materials found")) {
            Log.d("MyTag", "No material found in openfoodfactsDB");
            fetchProductFromFirebase(barcodeData);
        }
    }

    private void fetchProductFromFirebase(String barcode) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Products").document(barcode);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            Product product = documentSnapshot.toObject(Product.class);
            if (product != null) {
                Singleton.getInstance().setProduct(product);
                Singleton.getInstance().setMaterialOfProduct(createMaterialString());
                //Singleton.getInstance().setItemName(product.getProductName());
                Log.d("MyTag", "DocumentSnapshot data: " + product.getBarcode() + "\n name: " + product.getProductName());
            }
        });
    }

    private String createMaterialString() {
        if (Singleton.getInstance().getProduct().getMaterialList().size() == 1) {
            return Singleton.getInstance().getProduct().getMaterialList().get(0);
        } else {
            StringBuilder material = new StringBuilder();
            for (int i = 0; i < Singleton.getInstance().getProduct().getMaterialList().size(); i++) {
                material.append(Singleton.getInstance().getProduct().getMaterialList().get(i));
                if (i < Singleton.getInstance().getProduct().getMaterialList().size() - 1) {
                    material.append(", ");
                }
            }
            return material.toString();
        }
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