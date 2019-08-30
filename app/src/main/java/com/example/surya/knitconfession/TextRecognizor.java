package com.example.surya.knitconfession;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

public class TextRecognizor extends AppCompatActivity {
    private final int TEXT_RECO_REQ_CODE=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognizor);

    }

    public void textReco(View view) {
        Intent intent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,TEXT_RECO_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==TEXT_RECO_REQ_CODE){
            if(requestCode==RESULT_OK){
                Bitmap bitmap=(Bitmap)data.getExtras().get("data");
                textRecognisation(bitmap);
            }
            else if(requestCode==RESULT_CANCELED){
                Toast.makeText(TextRecognizor.this,"Operation canceled by User",Toast.LENGTH_SHORT);
            }
            else{
                Toast.makeText(TextRecognizor.this,"Failde to capture Image",Toast.LENGTH_SHORT);
            }
        }
    }

    private void textRecognisation(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

    }
}
