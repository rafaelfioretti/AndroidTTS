package br.com.rafaelfioretti.androidtts;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.text.TextDirectionHeuristicCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener{

    //@BindView(R.id.edtTexto) EditText edtTexto;
    //@BindView(R.id.tvExibir) TextView tvExibir;

    private EditText edtTexto;
    private TextView tvExibir;
    private TextToSpeech tts;
    private int REQUEST_TTS = 0;
    private int REQ_CODE_SPEECH_INPUT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtTexto = (EditText) findViewById(R.id.edtTexto);
        tvExibir = (TextView) findViewById(R.id.tvExibir);
        Button botao = (Button) findViewById(R.id.btnFalar);
        botao.setOnClickListener(this);
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, REQUEST_TTS);

        //ButterKnife.bind(this);
    }

    public void falar(String texto){

        tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void escutar(View v){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Prompt");//getString(R.string.speech_prompt));

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
            }
        catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "ERRO SPEECH", Toast.LENGTH_SHORT).show();
                    //getString(R.string.speech_not_supported),
        }

    }

    @Override
    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            if (tts.isLanguageAvailable(Locale.getDefault()) == TextToSpeech.LANG_AVAILABLE)
                tts.setLanguage(Locale.getDefault());
            else
                tts.setLanguage(Locale.US);

        }
        else{
             if(initStatus == TextToSpeech.ERROR){
                 Toast.makeText(this, "Erro TTS", Toast.LENGTH_LONG).show();
            }


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TTS){
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                tts = new TextToSpeech(this, this);
            }
            else{
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }else{
            if (requestCode == REQ_CODE_SPEECH_INPUT){
                if (resultCode == RESULT_OK && null !=data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvExibir.setText(result.get(0));

                    if (result.get(0).equalsIgnoreCase("teste"))
                        Toast.makeText(this, "Falou TESTE!", Toast.LENGTH_SHORT).show();
                }


            }

        }
    }

    @Override
    public void onClick(View view) {
        String texto = edtTexto.getText().toString();
        falar(texto);

    }
}
