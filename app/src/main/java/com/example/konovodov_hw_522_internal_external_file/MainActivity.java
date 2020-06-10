package com.example.konovodov_hw_522_internal_external_file;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private EditText mLoginEdTxt;
    private EditText mPassEdTxt;
    private CheckBox mCkBxSource;
    private FileInputStream fileInputStream;
    private StringBuffer loginFromFile;
    private StringBuffer passFromFile;
    private static String STATUS = "check_state";

    private static final String STORAGE_FILE = "login_file.txt";

    private SharedPreferences myStatusSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoginEdTxt = findViewById(R.id.editLogin);
        mPassEdTxt = findViewById(R.id.editPassword);
        Button mEnterBtn = findViewById(R.id.enterBtn);
        Button mRegBtn = findViewById(R.id.regBtn);
        mCkBxSource = findViewById(R.id.checkBox);
        myStatusSharedPref = getSharedPreferences("MyLoginData", MODE_PRIVATE);

        getPrefBtnCk();


        mCkBxSource.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (buttonView.isChecked()) {
                    setPrefBtnCk(getString(R.string.Btn_Check_is_Pushed));

                    setExternal(loginFromFile.toString(), passFromFile.toString(), getValuesFile());

                } else {
                    setPrefBtnCk(getString(R.string.Btn_Check_is_Not_Pushed));

                    setInternal(loginFromFile.toString(), passFromFile.toString());

                }
            }
        });

        mEnterBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if ((mLoginEdTxt.getText().length() == 0) || (mPassEdTxt.getText().length() == 0)) {
                    Toast.makeText(MainActivity.this, "Введите логин и пароль для входа", Toast.LENGTH_SHORT).show();
                } else {
                    if (mCkBxSource.isChecked()) {
                        getExternal(getValuesFile());
                    } else {
                        getInternal();
                    }
                }
            }
        });

        mRegBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if ((mLoginEdTxt.getText().length() == 0) || (mPassEdTxt.getText().length() == 0)) {
                    Toast.makeText(MainActivity.this, "Введите логин и пароль для регистрации", Toast.LENGTH_SHORT).show();
                } else {
                    //работаем...
                    if (mCkBxSource.isChecked()) {
                        setExternal(mLoginEdTxt.getText().toString(), mPassEdTxt.getText().toString(), getValuesFile());
                    } else {
                        setInternal(mLoginEdTxt.getText().toString(), mPassEdTxt.getText().toString());
                    }
                }
            }
        });
    }

    private void getInternal() {
        try {
            fileInputStream = openFileInput("file_name");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        try {
            loginFromFile = new StringBuffer(reader.readLine());
            passFromFile = new StringBuffer(reader.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (loginFromFile.toString().equals(mLoginEdTxt.getText().toString())) {
            if (passFromFile.toString().equals(mPassEdTxt.getText().toString())) {
                Toast.makeText(MainActivity.this, "УСПЕХ!!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Пароль введен не верно", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Логин введен не верно", Toast.LENGTH_SHORT).show();
        }
    }

    private void setInternal(String login, String pass) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = openFileOutput("file_name", MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        BufferedWriter bw = new BufferedWriter(outputStreamWriter);
        try {
            bw.write(login + "\n");
            bw.write(pass);
            loginFromFile = new StringBuffer(login);
            passFromFile = new StringBuffer(pass);
            Toast.makeText(MainActivity.this, "Данные сохранены", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setPrefBtnCk(String status) {

        SharedPreferences.Editor myEditor = myStatusSharedPref.edit();

        myEditor.putString(STATUS, status);
        myEditor.apply();
        Toast.makeText(MainActivity.this, "статус сохранен", Toast.LENGTH_LONG).show();
    }

    public String getPrefBtnCk() {
        if (!myStatusSharedPref.contains(STATUS)) {
            if (mCkBxSource.isChecked()) {
                setPrefBtnCk(getString(R.string.Btn_Check_is_Pushed));
                getExternal(getValuesFile());
                return getString(R.string.Btn_Check_is_Pushed);
            } else {
                setPrefBtnCk(getString(R.string.Btn_Check_is_Not_Pushed));
                getInternal();
                return getString(R.string.Btn_Check_is_Not_Pushed);
            }

        } else {
            String status = myStatusSharedPref.getString(STATUS, "");
            if (status.equals(getString(R.string.Btn_Check_is_Pushed))) {
                mCkBxSource.setChecked(true);
                //setPrefBtnCk(getString(R.string.Btn_Check_is_Pushed));
                getExternal(getValuesFile());
                return getString(R.string.Btn_Check_is_Pushed);
            } else {
                mCkBxSource.setChecked(false);
                //setPrefBtnCk(getString(R.string.Btn_Check_is_Not_Pushed));
                getInternal();
                return getString(R.string.Btn_Check_is_Not_Pushed);
            }
        }


    }

    private File getValuesFile() {

        return new File(getExternalFilesDir(null), STORAGE_FILE);
    }

    public void setExternal(String login, String pass, File file) {
        loginFromFile = new StringBuffer(login);
        passFromFile = new StringBuffer(pass);

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(login + "\n" + pass);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getExternal(File file) {

        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));
            int symbol;
            while ((symbol = reader.read()) != -1) {
                sb.append((char) symbol);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String fullFileContent = sb.toString();
        if (sb.length() != 0) {

            String[] data = fullFileContent.split("\n");
            loginFromFile = new StringBuffer(data[0]);
            passFromFile = new StringBuffer(data[1]);

            if (loginFromFile.toString().equals(mLoginEdTxt.getText().toString())) {
                if (passFromFile.toString().equals(mPassEdTxt.getText().toString())) {
                    Toast.makeText(this, "УСПЕХ!!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Пароль введен не верно", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Логин введен не верно", Toast.LENGTH_SHORT).show();
            }
        } else {
            loginFromFile = new StringBuffer(null);
            passFromFile = new StringBuffer(null);
        }
    }
}