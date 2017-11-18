package com.example.android.testapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.vansuita.gaussianblur.GaussianBlur;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jesus on 16/11/2017.
 */

public class LockActivity extends AppCompatActivity {
     @BindViews({R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6,
             R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn_clear})
    List<View> btnNumPads;

     @BindViews({R.id.dot_1, R.id.dot_2, R.id.dot_3, R.id.dot_4})
    List<ImageView> dots;

     private final static String TRUE_CODE = "1920";
     private static final int MAX_LENGHT = 4;
     private String codeString = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_lock);

        ButterKnife.bind(this);

        // Difuminador del fondo.
        ImageView imageView = (ImageView) findViewById(R.id.fondo);
        GaussianBlur.with(this).size(619).radius(16).put(R.drawable.fondo_1, imageView);
    }





    // Define la acción del botón de eliminar número.
    @OnClick (R.id.btn_clear)
    public void onClear(){
        if (codeString.length() > 0){
            // Elimina el último caracter introducido en la contraseña.
            codeString = removeLastChar(codeString);

            // Actualiza los círculos
            setDotImagesState();
        }
    }

    @OnClick ({R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6,
            R.id.btn7, R.id.btn8, R.id.btn9})
    public void OnClick(Button button){
        getStringCode(button.getId());
        if (codeString.length() == MAX_LENGHT){
            if (codeString.equals(TRUE_CODE)){
                Toast.makeText(this, "Contraseña correcta", Toast.LENGTH_SHORT).show();
                setIsPass();
                finish();
            } else {
                Log.v("LockActvity: ", "Contraseña incorrecta");
                Toast.makeText(this, "Contraseña Incorrecta", Toast.LENGTH_SHORT).show();
                // Vibra la imagen.
                shakeAnimation();
            }
        } else if (codeString.length() > MAX_LENGHT){
            // Resetea la contraseña introducida.
            codeString = "";
            getStringCode(button.getId());
        }
        setDotImagesState();
    }

    // Animación al fallar la contraseña.
    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.vibrate_anim);
        findViewById(R.id.dot_layout).startAnimation(shake);
    }

    // Obtiene y actualiza el valor de la contraseña.
    private void getStringCode( int buttonId){
        switch (buttonId){
            case R.id.btn0:
                codeString += 0;
                break;
            case R.id.btn1:
                codeString += 1;
                break;
            case R.id.btn2:
                codeString += 2;
                break;
            case R.id.btn3:
                codeString += 3;
                break;
            case R.id.btn4:
                codeString += 4;
                break;
            case R.id.btn5:
                codeString += 5;
                break;
            case R.id.btn6:
                codeString += 6;
                break;
            case R.id.btn7:
                codeString += 7;
                break;
            case R.id.btn8:
                codeString += 8;
                break;
            case R.id.btn9:
                codeString += 9;
                break;
            default:
                break;
        }
    }

    // Actualiza el color de los círculos.
    private void setDotImagesState(){
        for (int i = 0; i < codeString.length(); i++){
            dots.get(i).setImageResource(R.drawable.dot_enabled);
        }
        if (codeString.length() < 4){
            for (int j = codeString.length(); j <4; j++){
                dots.get(j).setImageResource(R.drawable.dot_disabled);
            }
        }
        }

    // Elimina un caracter de la contraseña al pulsar el boton de eliminar.
    private String removeLastChar(String s){
        if (s == null || s.length() == 0){
            return s;
        }
        return s.substring(0, s.length() -1);
    }


    private void setIsPass(){
        SharedPreferences.Editor editor = getSharedPreferences("PASS_CODE", MODE_PRIVATE).edit();
        editor.putBoolean("is_pass", true);
        editor.apply();
    }
}
