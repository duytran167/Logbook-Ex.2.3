package com.example.logbook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;


import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    ImageView imageView2;
    Button button_next, button_prev;
    Button add_button ;
    Button launch_camera;
    EditText ImgUrl_input;
    int imageIndex;
    ArrayList<String> listImageUrl ;
    ActivityResultLauncher<Intent> activityResultLauncher;
    public static final int CAMERA_ACTION_CODE = 1;
    //get data
    MyDatabaseHelper myDB;
    ArrayList<String> img_id, image_Url;
    int i=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);

        button_prev = findViewById(R.id.button_prev);
        button_next = findViewById(R.id.button_next);


        //sample url

        listImageUrl = new ArrayList<>();
        /*listImageUrl.add("https://thuthuatphanmem.vn/uploads/2018/09/11/hinh-anh-dep-6_044127357.jpg");
        listImageUrl.add("https://allimages.sgp1.digitaloceanspaces.com/tipeduvn/2022/08/Anh-Dep-Lam-Hinh-Nen.jpg");
        listImageUrl.add("https://img.meta.com.vn/Data/image/2022/01/13/anh-dep-thien-nhien-3.jpg");*/

        //store img
        myDB = new MyDatabaseHelper(MainActivity.this);
        img_id = new ArrayList<>();
        image_Url = new ArrayList<>();

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA}, 101);
        }

//launch camera
        launch_camera =(Button) findViewById(R.id.launch_camera);
        launch_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 101);
                }
                catch(Exception e){
                    e.printStackTrace();
                }


            }
        });



        /*activityResultLauncher = registerForActivityResult(new ActivityResultContracts
                .StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    imageView2.setImageBitmap(bitmap);
                }
            }
        });*/



        button_next = findViewById(R.id.button_next);
        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int last = image_Url.size() -1;
                if(imageIndex == last)
                {
                    imageIndex = 0;
                }
                else{
                    imageIndex += 1;
                }
                loadAllImages();
            }
        });
        button_prev = findViewById(R.id.button_prev);
        button_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(imageIndex == 0)
                {
                    imageIndex = image_Url.size() - 1;
                }
                else{
                    imageIndex -= 1;
                }
                loadAllImages();
            }
        });
        storeDataInArrays();

        ImgUrl_input = findViewById(R.id.input_ImageUrl);
        add_button = findViewById(R.id.add_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(MainActivity.this);

                //validate url
                /*String WebUrl = "^((ftp|http|https):\\/\\/)?(www.)?(?!.*(ftp|http|https|www.))[a-zA-Z0-9_-]+(\\.[a-zA-Z]+)+((\\/)[\\w#]+)*(\\/\\w+\\?[a-zA-Z0-9_]+=\\w+(&[a-zA-Z0-9_]+=\\w+)*)?$";
                String website = ImgUrl_input.getText().toString().trim();
                if (website.trim().length() > 0) {
                    if (!website.matches(WebUrl)) {
                        //validation msg
                        Toast.makeText(getApplicationContext(), "Url is Invalid", Toast.LENGTH_SHORT).show();
                        return;

                    }
                }*/
                if (Patterns.WEB_URL.matcher(ImgUrl_input.getText().toString()).matches()) {
                    myDB.addBook(ImgUrl_input.getText().toString().trim());



                    ImgUrl_input.setText("");//CLEAR TEXT AFTER ENTER
                }
                else if
                ("".equals(ImgUrl_input.getText().toString().trim())) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "You did not enter a value!", Toast.LENGTH_LONG);
                    toast.getView().setBackgroundColor(Color.parseColor("#F6AE2D"));
                    toast.show();
                    return;
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Url is Invalid", Toast.LENGTH_SHORT);
                    toast.getView().setBackgroundColor(Color.RED);
                    toast.show();
                }
                getImages();
                imageIndex = image_Url.size() -1;
                loadAllImages();
            }
        });




    }




    /*void chkPosition(){
        int lastImagePosition = image_Url.size() -1 ;
        button_prev.setEnabled(imageIndex != 0);
        button_next.setEnabled(imageIndex != lastImagePosition);
    }*/





    void getImages(){
        myDB = new MyDatabaseHelper(getApplicationContext());
        Cursor cursor = myDB.readAllData();
        image_Url.clear();
        while(cursor.moveToNext())
        {
            image_Url.add(cursor.getString(1));
        }
    }

    void storeDataInArrays(){
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){
            myDB.getFromData();
            cursor = myDB.readAllData();
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }


        image_Url.clear();
        while (cursor.moveToNext()){
            image_Url.add(cursor.getString(1));
        }

    }
    void loadAllImages(){
        Toast.makeText(this, "Loading...", Toast.LENGTH_LONG).show();
        Picasso.get().load(image_Url.get(imageIndex))
                .resize(900, 700).into(imageView);

    }

    //display image form camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101)
        {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }
    }
}