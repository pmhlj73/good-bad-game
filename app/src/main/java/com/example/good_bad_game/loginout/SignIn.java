package com.example.good_bad_game.loginout;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.good_bad_game.R;
import com.example.good_bad_game.home.Home;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignIn extends AppCompatActivity {

    private Boolean pass = false;
    private TextToSpeech tts;
    private String TAG = "SignIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


//      tts 객체 생성
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener(){

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = tts.setLanguage(Locale.KOREA);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.d(TAG,"tts error");
                        Toast.makeText(SignIn.this, "지원하지 않는 언어입니다.",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Button signUp = findViewById(R.id.btn_sign_up);
        EditText email = findViewById(R.id.email);
        EditText pw = findViewById(R.id.password);
        Button btn = findViewById(R.id.btn_sign_in);
        Boolean pass = false;

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts_speech("회원가입");
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });


        //----------------------------------------------22.05.10 (Django <-> Android 로그인)

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://54.180.121.58:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipt_email = email.getText().toString();
                String ipt_pw = passwordHash(pw.getText().toString());

                LoginService LoginService = retrofit.create(LoginService.class);

                Call<List<Login>> call = LoginService.getPosts();

                call.enqueue(new Callback<List<Login>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<Login>> call, Response<List<Login>> response) {
                        if (!response.isSuccessful())
                        {
                            Log.d("onResponse 발동","Connection은 성공하였으나 code 에러 발생");
                            AlertDialog.Builder ad = new AlertDialog.Builder(SignIn.this);
                            ad.setTitle("에러");
                            ad.setMessage("Code:" + response.code());
                            ad.show();
                            return;

                        }

                        List<Login> Login_infos = response.body();

                        for ( Login login_info : Login_infos)
                        {
                            Log.d("onResponse 발동","내부 데이터를 하나씩 가져와서 Login 정보와 비교 시작");
                            Log.d("이름 : ", ipt_email);
                            Log.d("데베이름 : ", login_info.get_mail());
                            Log.d("패스워드 : ", ipt_pw);
                            Log.d("데베패스워드 : ", login_info.get_password());
                            if(ipt_email.equals(login_info.get_mail()) && ipt_pw.equals(login_info.get_password())) {
                                Log.d("성공!","이름, 폰번호 일치");

//                                AlertDialog.Builder ad = new AlertDialog.Builder(SignIn.this);
//                                ad.setTitle("성공");
//                                ad.setMessage("ID : " + login_info.get_mail() + "    password : " + login_info.get_password());
//                                ad.show();
                                tts_speech("로그인");

                                boolean pass = true;
                                Intent intent = new Intent(getApplicationContext(), Home.class);
                                intent.putExtra("id", login_info.get_id());
                                intent.putExtra("nickname",login_info.get_nickname());
                                startActivity(intent);

                                break;
                            }
                        }

                        if(pass == false){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder ad = new AlertDialog.Builder(SignIn.this);
                                    tts_speech("이메일 혹은 패스워드가 잘못 되었습니다.");
                                    ad.setTitle("로그인 실패!");
                                    ad.setMessage("이메일 혹은 패스워드가 잘못 되었습니다.");
                                    ad.show();

                                }
                            }, 500);

                        }

                    }

                    @Override
                    public void onFailure(retrofit2.Call<List<Login>> call, Throwable t) {
                        Log.d("onFailure 발동","Connection Error");
                        AlertDialog.Builder ad = new AlertDialog.Builder(SignIn.this);
                        ad.setTitle("에러");
                        ad.setMessage(t.getMessage());
                        ad.show();

                    }
                });

            }
        });

    }

    public static String passwordHash(String password){
        return sha1("kD0a1"+md5("xA4"+password)+"f4A");
    }

    // SHA ( Secure Hash Algorithm )
    public static String sha1(String clearString) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(clearString.getBytes("UTF-8"));
            byte[] bytes = messageDigest.digest();
            StringBuilder buffer = new StringBuilder();
            for (byte b : bytes) {
                buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return buffer.toString();
        }
        catch (Exception ignored) {
            ignored.printStackTrace();
            return null;
        }
    }

    // MD5 ( Message Digest Algorithm : 무결성 검사에 사용하는 128비트 해쉬 함수 )
    // IETF의 RFC 1321로 지정되어 있으나 다수의 중요 결함이 발견되어 현재는 해쉬 용도로 SHA(해쉬함수 집합)와 같이 사용.
    public static String md5(String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    public void tts_speech(String text){
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}