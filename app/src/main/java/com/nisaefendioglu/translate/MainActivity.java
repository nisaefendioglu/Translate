package com.nisaefendioglu.translate;

import static java.lang.Thread.sleep;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.HashMap;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    Spinner language1Spinner, language2Spinner;
    EditText input_editText;
    TextView result_TextView, progressBarTextView_main,selected_language_textView;
    Button translate_button;
    ImageButton shuffle_imgButton;
    ImageView copy_imageView;
    ProgressBar progressBar_main;
    static String LanguageOne = "English";
    static String LanguageTwo = "Turkish";
    static Boolean LanguageOneDownloaded = null;
    static Boolean LanguageTwoDownloaded = null;
    static String LanguageOneTAG = null;
    static String LanguageTwoTAG = null;
    static HashMap<String, String> Language_Tags;
    static HashMap<String, String> LanguageTagName;
    final static String TAG = "TAG";
    static RemoteModelManager modelManager;
    static Boolean SelectedLanguage;
    Toolbar toolbar_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LanguageTags();
        LanguageNames();

        input_editText = findViewById(R.id.userText_editText);
        result_TextView = findViewById(R.id.result_textView);
        result_TextView.setMovementMethod(new ScrollingMovementMethod());
        selected_language_textView=findViewById(R.id.detected_language_textView);
        translate_button = findViewById(R.id.translate_button);
        shuffle_imgButton = findViewById(R.id.swap_imageButton);
        language1Spinner = findViewById(R.id.language1_spinner);
        language2Spinner = findViewById(R.id.language2_spinner);
        modelManager = RemoteModelManager.getInstance();
        progressBar_main = findViewById(R.id.progressbar_main);
        progressBarTextView_main = findViewById(R.id.progressbarTextview_main);
        copy_imageView = findViewById(R.id.copy_imageview);



        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.languages_supported1, R.layout.color_spinner_layout);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language1Spinner.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.languages_supporteds,R.layout.color_spinner_layout);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language2Spinner.setAdapter(adapter2);



        translate_button.setOnClickListener(v -> {
            translate_text();
        });


        input_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (SelectedLanguage){
                    String user_text = input_editText.getText().toString();

                    LanguageIdentifier languageIdentifier = LanguageIdentification.getClient();
                    languageIdentifier.identifyLanguage(user_text)

                            .addOnSuccessListener(languageCode -> {

                                    if (LanguageTagName.containsKey(languageCode)){

                                        String languageName = LanguageTagName.get(languageCode);
                                        selected_language_textView.setText("Language Selected: " + languageName );

                                        LanguageOne = languageName;
                                        String lang1_tag = Language_Tags.get(LanguageOne);
                                        LanguageOneTAG = TranslateLanguage.fromLanguageTag(lang1_tag);


                                    }
                                    else {
                                        Log.i(TAG, "Can't identify language.");
                                        translate_button.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                                startActivity(intent);

                                            }
                                        });


                                    }

                            })

                            .addOnFailureListener(e -> {
                                Log.d(TAG, "afterTextChanged: " + e.getMessage());
                            });
                }
            }
        });


        AdapterView.OnItemSelectedListener spinner1_listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                LanguageOne = parent.getItemAtPosition(position).toString();

                if (LanguageOne.equals("Auto-Detect")) {

                    SelectedLanguage = true;
                    selected_language_textView.setText("First you have to choose the language");
                    selected_language_textView.setTextSize(18);
                    selected_language_textView.setTextColor(Color.RED);
                    return;


                } else {

                    SelectedLanguage = false;
                    selected_language_textView.setText(" ");

                    String lang1_tag = Language_Tags.get(LanguageOne);
                    LanguageOneTAG = TranslateLanguage.fromLanguageTag(lang1_tag);

                    TranslateRemoteModel model1 = new TranslateRemoteModel.Builder(LanguageOneTAG).build();
                    modelManager.isModelDownloaded(model1)
                            .addOnSuccessListener(aBoolean -> {
                                LanguageOneDownloaded = aBoolean;
                            })
                            .addOnFailureListener(e -> {
                            });
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        AdapterView.OnItemSelectedListener spinner2_listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                LanguageTwo = parent.getItemAtPosition(position).toString();

                String lang2_tag = Language_Tags.get(LanguageTwo);
                LanguageTwoTAG = TranslateLanguage.fromLanguageTag(lang2_tag);


                TranslateRemoteModel model2 = new TranslateRemoteModel.Builder(LanguageTwoTAG).build();
                modelManager.isModelDownloaded(model2)
                        .addOnSuccessListener(aBoolean -> {
                            LanguageTwoDownloaded = aBoolean;
                        })
                        .addOnFailureListener(e -> {
                        });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        language1Spinner.setOnItemSelectedListener(spinner1_listener);
        language2Spinner.setOnItemSelectedListener(spinner2_listener);


    }



    public void translate_text() {

        String user_text = input_editText.getText().toString();

        if (user_text.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Text", Toast.LENGTH_SHORT).show();
            return;
        }

        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(LanguageOneTAG)
                .setTargetLanguage(LanguageTwoTAG)
                .build();
        final Translator translator = Translation.getClient(options);


        if (LanguageOneDownloaded & LanguageTwoDownloaded) {
            translator.translate(user_text)
                    .addOnSuccessListener(s -> {
                        DownloadConditions conditions = new DownloadConditions.Builder().build();
                                translator.downloadModelIfNeeded(conditions)
                                        .addOnSuccessListener(unused -> {


                                            progressBar_main.setVisibility(View.INVISIBLE);

                                            String lang1_tag = Language_Tags.get(LanguageOne);
                                            LanguageOneTAG = TranslateLanguage.fromLanguageTag(lang1_tag);

                                            String lang2_tag = Language_Tags.get(LanguageTwo);
                                            LanguageTwoTAG = TranslateLanguage.fromLanguageTag(lang2_tag);

                                            LanguageOneDownloaded = true;
                                            LanguageTwoDownloaded = true;

                                            translate_text();

                                        })
                                        .addOnFailureListener(e -> {
                                        });

                        result_TextView.setText(s);
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "translate_string: " + e.getMessage());
                    });
        }

    }

    private void LanguageTags() {

        Language_Tags = new HashMap<>();
        Language_Tags.put("Afrikaans", "af");
        Language_Tags.put("Arabic", "ar");
        Language_Tags.put("Belarusian", "be");
        Language_Tags.put("Bulgarian", "bg");
        Language_Tags.put("Bengali", "bn");
        Language_Tags.put("Catalan", "ca");
        Language_Tags.put("Czech", "cs");
        Language_Tags.put("Welsh", "cy");
        Language_Tags.put("Danish", "da");
        Language_Tags.put("German", "de");
        Language_Tags.put("Greek", "el");
        Language_Tags.put("English", "en");
        Language_Tags.put("Esperanto", "eo");
        Language_Tags.put("Spanish", "es");
        Language_Tags.put("Estonian", "et");
        Language_Tags.put("Persian", "fa");
        Language_Tags.put("Finnish", "fi");
        Language_Tags.put("French", "fr");
        Language_Tags.put("Irish", "ga");
        Language_Tags.put("Galician", "gl");
        Language_Tags.put("Gujarati", "gu");
        Language_Tags.put("Hebrew", "he");
        Language_Tags.put("Croatian", "hr");
        Language_Tags.put("Haitian", "ht");
        Language_Tags.put("Hungarian", "hu");
        Language_Tags.put("Indonesian", "id");
        Language_Tags.put("Icelandic", "is");
        Language_Tags.put("Italian", "it");
        Language_Tags.put("Japanese", "ja");
        Language_Tags.put("Georgian", "ka");
        Language_Tags.put("Kannada", "kn");
        Language_Tags.put("Korean", "ko");
        Language_Tags.put("Lithuanian", "lt");
        Language_Tags.put("Hindi", "hi");
        Language_Tags.put("Latvian", "lv");
        Language_Tags.put("Macedonian", "mk");
        Language_Tags.put("Marathi", "mr");
        Language_Tags.put("Malay", "ms");
        Language_Tags.put("Maltese", "mt");
        Language_Tags.put("Dutch", "nl");
        Language_Tags.put("Norwegian", "no");
        Language_Tags.put("Polish", "pl");
        Language_Tags.put("Portuguese", "pt");
        Language_Tags.put("Romanian", "ro");
        Language_Tags.put("Russian", "ru");
        Language_Tags.put("Slovak", "sk");
        Language_Tags.put("Slovenian", "sl");
        Language_Tags.put("Albanian", "sq");
        Language_Tags.put("Swedish", "sv");
        Language_Tags.put("Swahili", "sw");
        Language_Tags.put("Tamil", "ta");
        Language_Tags.put("Telugu", "te");
        Language_Tags.put("Thai", "th");
        Language_Tags.put("Tagalog", "tl");
        Language_Tags.put("Turkish", "tr");
        Language_Tags.put("Ukrainian", "uk");
        Language_Tags.put("Urdu", "ur");
        Language_Tags.put("Vietnamese", "vi");
        Language_Tags.put("Chinese", "zh");

    }

    private void LanguageNames() {

        LanguageTagName = new HashMap<>();
        LanguageTagName.put("af", "Afrikaans");
        LanguageTagName.put("ar", "Arabic");
        LanguageTagName.put("be", "Belarusian");
        LanguageTagName.put("bg", "Bulgarian");
        LanguageTagName.put("bn", "Bengali");
        LanguageTagName.put("ca", "Catalan");
        LanguageTagName.put("cs", "Czech");
        LanguageTagName.put("cy", "Welsh");
        LanguageTagName.put("da", "Danish");
        LanguageTagName.put("de", "German");
        LanguageTagName.put("el", "Greek");
        LanguageTagName.put("en", "English");
        LanguageTagName.put("eo", "Esperanto");
        LanguageTagName.put("es", "Spanish");
        LanguageTagName.put("et", "Estonian");
        LanguageTagName.put("fa", "Persian");
        LanguageTagName.put("fi", "Finnish");
        LanguageTagName.put("fr", "French");
        LanguageTagName.put("ga", "Irish");
        LanguageTagName.put("gl", "Galician");
        LanguageTagName.put("gu", "Gujarati");
        LanguageTagName.put("he", "Hebrew");
        LanguageTagName.put("hi", "Hindi");
        LanguageTagName.put("hr", "Croatian");
        LanguageTagName.put("ht", "Haitian");
        LanguageTagName.put("hu", "Hungarian");
        LanguageTagName.put("id", "Indonesian");
        LanguageTagName.put("is", "Icelandic");
        LanguageTagName.put("it", "Italian");
        LanguageTagName.put("ja", "Japanese");
        LanguageTagName.put("ka", "Georgian");
        LanguageTagName.put("kn", "Kannada");
        LanguageTagName.put("ko", "Korean");
        LanguageTagName.put("lt", "Lithuanian");
        LanguageTagName.put("lv", "Latvian");
        LanguageTagName.put("mk", "Macedonian");
        LanguageTagName.put("mr", "Marathi");
        LanguageTagName.put("ms", "Malay");
        LanguageTagName.put("mt", "Maltese");
        LanguageTagName.put("nl", "Dutch");
        LanguageTagName.put("no", "Norwegian");
        LanguageTagName.put("pl", "Polish");
        LanguageTagName.put("pt", "Portuguese");
        LanguageTagName.put("ro", "Romanian");
        LanguageTagName.put("ru", "Russian");
        LanguageTagName.put("sk", "Slovak");
        LanguageTagName.put("sl", "Slovenian");
        LanguageTagName.put("sq", "Albanian");
        LanguageTagName.put("sv", "Swedish");
        LanguageTagName.put("sw", "Swahili");
        LanguageTagName.put("ta", "Tamil");
        LanguageTagName.put("te", "Telugu");
        LanguageTagName.put("th", "Thai");
        LanguageTagName.put("tl", "Tagalog");
        LanguageTagName.put("tr", "Turkish");
        LanguageTagName.put("uk", "Ukrainian");
        LanguageTagName.put("ur", "Urdu");
        LanguageTagName.put("vi", "Vietnamese");
        LanguageTagName.put("zh", "Chinese");

    }

    public void CopyResultClipboard(View view) {
        String text = result_TextView.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Result", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "Copied!", Toast.LENGTH_SHORT).show();
    }


}

