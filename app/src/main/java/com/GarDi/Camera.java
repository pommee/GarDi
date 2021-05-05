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
import android.os.Build;
import android.os.Bundle;
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

import com.GarDi.Models.RequestJSoup;
import com.GarDi.Models.Singleton;
import com.clarifai.channel.ClarifaiChannel;
import com.clarifai.credentials.ClarifaiCallCredentials;
import com.clarifai.grpc.api.Concept;
import com.clarifai.grpc.api.Data;
import com.clarifai.grpc.api.Image;
import com.clarifai.grpc.api.Input;
import com.clarifai.grpc.api.MultiOutputResponse;
import com.clarifai.grpc.api.PostModelOutputsRequest;
import com.clarifai.grpc.api.V2Grpc;
import com.clarifai.grpc.api.status.StatusCode;
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

import org.apache.commons.math3.analysis.function.Sin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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


public class Camera extends AppCompatActivity {


    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ToneGenerator toneGen1;
    private String barcodeData;
    private FloatingActionButton button;
    private V2Grpc.V2BlockingStub stub;


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
                    Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    picture.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(getContentResolver(), picture, "Title", null);
                    Uri imageUri = Uri.parse(path);
                    String image = imageUri.toString();

                    getDetails(image);
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
                try {
                    if (ActivityCompat.checkSelfPermission(Camera.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
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
                        Intent intent = new Intent(getApplicationContext(), BarcodeScanned.class);
                        startActivity(intent);
                        try {
                            Singleton.getInstance().setItemName(RequestJSoup.getSearchResultFromGoogle(barcodeData));
                            Singleton.getInstance().setBarcode(generateBarcodeFromString(barcodeData));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        barcodeData = barcodes.valueAt(0).displayValue;
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                        Singleton.getInstance().setScannedText(barcodeData);
                        Intent intent = new Intent(getApplicationContext(), BarcodeScanned.class);
                        startActivity(intent);
                        try {
                            Singleton.getInstance().setItemName(RequestJSoup.getSearchResultFromGoogle(barcodeData));
                            Singleton.getInstance().setBarcode(generateBarcodeFromString(barcodeData));
                            Singleton.getInstance().setMaterialOfProduct(retrieveMaterialFromBarcode(barcodeData));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }


    private String retrieveMaterialFromBarcode(String barcodeData) {    // Takes the barcode and returns the materials of the product :)
        String material = null;
        String url = "https://world.openfoodfacts.org/api/v0/product/" + barcodeData + ".json";

        JSONParser parser = new JSONParser();

        try {
            URL oracle = new URL(url); // URL to Parse
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

            String inputLine;

            if((inputLine = in.readLine()) != null) {
                JSONObject jsonObject = (JSONObject) parser.parse(inputLine);
                JSONObject product;

                if(jsonObject.get("product") != null){
                    product = (JSONObject) jsonObject.get("product");
                    if (product.get("packaging") != null) { // If no packaging exists in DB
                        material = product.get("packaging").toString();
                    } else {
                        material = "No materials found";
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
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Objects.requireNonNull(getSupportActionBar()).hide();
        initialiseDetectorsAndSources();
    }

    public void getDetails(String image) {
        Path pathNew = null;
        try {
            Uri photo = Uri.parse(image);
            Log.i("URI", image);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                pathNew = Paths.get(getRealPathFromUri(getApplicationContext(), photo));
            }
            if (stub == null) {
                stub = V2Grpc.newBlockingStub(ClarifaiChannel.INSTANCE.getInsecureGrpcChannel())
                        .withCallCredentials(new ClarifaiCallCredentials("28f7631e75464771b2f266ff6e0053e0"));
            }

            MultiOutputResponse response = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                response = stub.postModelOutputs(
                        PostModelOutputsRequest.newBuilder()
                                .setModelId("aaa03c23b3724a16a56b629203edc62c")
                                .addInputs(
                                        Input.newBuilder().setData(
                                                Data.newBuilder().setImage(Image.newBuilder().setBase64(ByteString.copyFrom(Files.readAllBytes(pathNew)))
                                                )
                                        )
                                )
                                .build()
                );
            }

            if (response.getStatus().getCode() != StatusCode.SUCCESS) {
                throw new RuntimeException("Request failed, status: " + response.getStatus());
            }

            List<String> list = new ArrayList<>();

            for (Concept c : response.getOutputs(0).getData().getConceptsList()) {
                list.add(String.format("%12s: %,.2f", c.getName(), c.getValue()));
            }
            Intent intent = new Intent(Camera.this, InfoActivity.class);
            intent.putExtra("LIST", (Serializable) list);
            startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}