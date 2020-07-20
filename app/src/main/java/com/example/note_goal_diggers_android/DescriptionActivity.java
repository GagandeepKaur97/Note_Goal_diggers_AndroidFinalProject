package com.example.note_goal_diggers_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DescriptionActivity extends AppCompatActivity {
    ImageButton imageButton;
    //ImageView imageView;
    Toolbar toolbar;
    ImageView uploadingimage;
    public Uri imguri;
    private boolean imagepresent = false;
    ImageButton startRec;
    ImageButton stopRec;
    ImageButton playRec, replayRec;
    String mCurrentPhotoPath;
    Bitmap mImageBitmap;
    double latitude, longitude;

    private static final int REQUEST_CODE = 1;


    public static final int CAMERA_REQUEST = 1000;
    public static final int MY_CAMERA_PERMISSION_CODE = 1001;
    SimpleDatabase dataBaseHelper;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    Location noteLocation;
    String titleName;
    int nid;

    String audiofilepath;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    boolean selected;

    EditText editTextTitle;
    EditText editTextDesc;

    CategoryModel selectednote;

    final int REQUEST_PERMISSION_CODE = 1000;

    String RECORDED_FILE;


    @Override
    protected void onStart() {
        super.onStart();

        if (!checkPermission())
            requestPermission();
        else
            getLastLocation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        final EditText editTextTitle = findViewById(R.id.title_edit_text);
        final EditText editTextDesc = findViewById(R.id.description_edit_text);


//
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle("Detail");
        //image capture

        imageButton = findViewById(R.id.chooseimagebtn);
        toolbar = findViewById(R.id.toolbar_d);
        setSupportActionBar(toolbar);
//        toolbar.setTitle("Note Details");
        uploadingimage = findViewById(R.id.image_view);
        startRec = findViewById(R.id.btn_start_record);
        stopRec = findViewById(R.id.btn_stop_record);
        playRec = findViewById(R.id.btn_play_record);
        replayRec = findViewById(R.id.btn_replay);


        Button buttonSave = findViewById(R.id.btn_save_note);


        dataBaseHelper = new SimpleDatabase(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        buildLocationRequest();
        buildLocationCallBack();

        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        // set the volume of played media to maximum.
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        Intent intent = getIntent();
        selected = intent.getBooleanExtra("selected", false);

        if (selected) {

            selectednote = (CategoryModel) intent.getSerializableExtra("note");
            editTextTitle.setText(selectednote.getTitle());
            editTextDesc.setText(selectednote.getDescription());
            audiofilepath = selectednote.getAudio();
            mCurrentPhotoPath = selectednote.getImage();
            latitude = selectednote.getNoteLat();
            longitude = selectednote.getNoteLong();
            nid = selectednote.getId();
            startRec.setVisibility(View.GONE);
            playRec.setVisibility(View.VISIBLE);

            if (audiofilepath != null) {
                playRec.setVisibility(View.VISIBLE);
                startRec.setVisibility(View.GONE);
                stopRec.setVisibility(View.GONE);
                replayRec.setVisibility(View.GONE);
            } else {
                startRec.setVisibility(View.VISIBLE);
                stopRec.setVisibility(View.GONE);
                replayRec.setVisibility(View.GONE);
                playRec.setVisibility(View.GONE);

            }


            if (mCurrentPhotoPath != null) {
                try {
                    mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                    uploadingimage.setImageBitmap(mImageBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Filechooser();

                }
            });

//                titleName = editTextTitle.getText().toString();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                            == PackageManager.PERMISSION_DENIED) {
//                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                        requestPermissions(permission, CAMERA_REQUEST);
//
//                    } else {
//                        openCamera();
//                    }
//                } else {
//                    openCamera();
//




        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat();
                String joiningDate = sdf.format(calendar.getTime());

                String cname = MainActivity.categoryName.get(MainActivity.catPosition);
                String ntitle = editTextTitle.getText().toString().trim();
                String ndesc = editTextDesc.getText().toString().trim();

                titleName = ntitle;

                if (ntitle.isEmpty() && ndesc.isEmpty()) {
                    Toast.makeText(DescriptionActivity.this, "Fill the required feilds", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (selected) {
                    if (dataBaseHelper.updateNote(nid, ntitle, ndesc, audiofilepath, mCurrentPhotoPath)) {
                        Toast.makeText(DescriptionActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DescriptionActivity.this, "Not Updated", Toast.LENGTH_SHORT).show();
                    }
                }

                if (!selected) {
                    if (dataBaseHelper.addNote(cname, ntitle, ndesc, joiningDate, noteLocation.getLatitude(), noteLocation.getLongitude(), audiofilepath, mCurrentPhotoPath)) {
                        Toast.makeText(DescriptionActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DescriptionActivity.this, "Not saved", Toast.LENGTH_SHORT).show();
                    }
                }


                Intent intent = new Intent(DescriptionActivity.this, NotesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);


            }
        });

        startRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermissionDevice()) {
                    requestAudioPermission();
                    return;
                }

                if (checkPermissionDevice()) {

                    RECORDED_FILE = "/audio" + titleName + ".3gp";
                    audiofilepath = getExternalCacheDir().getAbsolutePath()
                            + RECORDED_FILE;
                    setUpMediaRecorder();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException ise) {
                        // make something ...
                        ise.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    startRec.setVisibility(View.GONE);
                    stopRec.setVisibility(View.VISIBLE);

                } else {
                    requestAudioPermission();
                }
            }
        });

        stopRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();
                stopRec.setVisibility(View.GONE);
                playRec.setVisibility(View.VISIBLE);
            }
        });

        playRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(audiofilepath);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playRec.setVisibility(View.GONE);
                        replayRec.setVisibility(View.VISIBLE);

                    }
                });

                mediaPlayer.start();
            }
        });

        replayRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(audiofilepath);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();

            }
        });


    }


    private void Filechooser () {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.category_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_location:
                System.out.println("//////////////////inside location/////////////////");
                Intent intent = new Intent(DescriptionActivity.this, MapsActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openCamera() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photofile = null;
            try {
                photofile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photofile != null) {

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, getPackageName() + ".provider", photofile));
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

            }
        }


    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = titleName + timeStamp + "_";

        File storageDir = Environment.getExternalStorageDirectory();
        File dir = new File(storageDir.getAbsolutePath() + "/notes/");
        if (!dir.exists()) {
            dir.mkdir();
        }

        File image = File.createTempFile(imageFileName, ".jpg", dir);


        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_CODE == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                openCamera();
                getLastLocation();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            } else {
                Toast.makeText(this, "denied", Toast.LENGTH_SHORT).show();
            }


//            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                openCamera();
//            }
//            else{
//                Toast.makeText(this, "denied camera", Toast.LENGTH_SHORT).show();
//            }
        }
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }

        if (CAMERA_REQUEST == requestCode) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            } else {
                Toast.makeText(this, " Camera denied", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==1 && resultCode ==RESULT_OK && data!= null && data.getData() != null){

            imguri = data.getData();
            uploadingimage.setImageURI(imguri);
            imagepresent = true;


        }
    }


    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);

    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {

                    noteLocation = location;


                }
            }
        };
    }

    private boolean checkPermission() {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private void requestAudioPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    noteLocation = task.getResult();
                    System.out.println(noteLocation.getLongitude());
                    System.out.println(noteLocation.getLatitude());

                }
            }
        });
    }

    private boolean checkPermissionDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    private void setUpMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(audiofilepath);


    }


}


