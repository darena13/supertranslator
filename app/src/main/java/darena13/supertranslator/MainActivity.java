package darena13.supertranslator;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private HistoryDataSource datasource;

    private RequestQueue mRequestQueue; //Volley's очередь на исполнение запросов
    public static final String YANDEX_TRNS_API_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?";
    public static final String YANDEX_DICT_API_URL = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?";
    public static final String TRNS_KEY = "trnsl.1.1.20170323T152914Z.07d329d9a6f367e9.7fc2032da9c680dd945a3f8ba5e65c2cacd82d4b";
    public static final String DICT_KEY = "dict.1.1.20170406T140716Z.aaf26a32ef51e3a4.7e8e7f021bbb2d111099eea26429f7b809d16406";

    private HistoryFragment historyFragment;
    private HistoryFragment favoritesFragment;

    //создаем листенер на изменения в данных для Истории и Избранного
    private OnChangeHistoryListener onChangeHistoryListener = new OnChangeHistoryListener() {
        @Override
        public void onChange() {
            historyFragment.updateList();
            favoritesFragment.updateList();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //закладки
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //фрагменты для закладок История и Избранное
        historyFragment = new HistoryFragment();
        favoritesFragment = new HistoryFragment();
        favoritesFragment.setShowFavoritesOnly(true);

        //
        historyFragment.setOnChangeHistoryListener(onChangeHistoryListener);
        favoritesFragment.setOnChangeHistoryListener(onChangeHistoryListener);

        //создаем ДАО
        datasource = new HistoryDataSource(this);
        //создаем/открываем БД
        datasource.open();

        //creating a request queue
        mRequestQueue = Volley.newRequestQueue(this);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //создаем фрагменты для каждой закладки
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TranslationFormFragment();
                case 1:
                    return historyFragment;
                case 2:
                    return favoritesFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.section1);
                case 1:
                    return getResources().getString(R.string.section2);
                case 2:
                    return getResources().getString(R.string.section3);
            }
            return null;
        }
    }

    //кнопка Translate
    public void translate(View view) {
        try {
            translateDict();
        } catch (Exception e) {
            e.printStackTrace();
        }
        translateTrns();
    }

    //поиск словарной статьи
    public void translateDict() {
        String dictionaryRequestUrl;

        EditText mEditText = (EditText) findViewById(R.id.textToTranslate);
        String textToTranslate = mEditText.getText().toString();

        String lang = getLanguageCode();

        Uri builtDictUri = Uri.parse(YANDEX_DICT_API_URL)
                .buildUpon()
                .appendQueryParameter("key", DICT_KEY)
                .appendQueryParameter("lang", lang)
                .appendQueryParameter("text", textToTranslate)
                .build();
        dictionaryRequestUrl = builtDictUri.toString();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, dictionaryRequestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray dictRows = response.getJSONArray("def");
                            ListView list = (ListView) findViewById(R.id.dict_list);
                            DictAdapter adapter = (DictAdapter) list.getAdapter();
                            adapter.update(dictRows);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "NOPE", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(MainActivity.this, "Dictionary ERROR", Toast.LENGTH_SHORT).show();

            }
        });

        //добавляем запрос в очередь
        mRequestQueue.add(req);
    }

    //перевод предложений
    public void translateTrns() {
        String translateRequestUrl;

        EditText mEditText = (EditText) findViewById(R.id.textToTranslate);
        final String textToTranslate = mEditText.getText().toString();

        final String lang = getLanguageCode();

        //собираем URI для запроса
        Uri builtTrnsUri = Uri.parse(YANDEX_TRNS_API_URL)
                .buildUpon()
                .appendQueryParameter("key", TRNS_KEY)
                .appendQueryParameter("text", textToTranslate)
                .appendQueryParameter("lang", lang)
                .build();
        translateRequestUrl = builtTrnsUri.toString();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, translateRequestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String result = response.getString("text");
                            result = result.substring(2, result.length() - 2);
                            TextView resultsTextView = (TextView) findViewById(R.id.results);
                            resultsTextView.setText(result);
                            //добавляем перевод в БД
                            datasource.insertOrUpdateHistoryItem(textToTranslate, result, lang);
                            //сообщаем листенеру, что ситуация изменилась
                            onChangeHistoryListener.onChange();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(MainActivity.this, "HTTP ERROR", Toast.LENGTH_SHORT).show();
            }
        });

        //Add requests to RequestQueue to execute
        mRequestQueue.add(req);
    }

    //поменять направление перевода
    public void langSwitch(View view) {
        Spinner spinnerFrom = (Spinner) findViewById(R.id.language_from_spinner);
        Spinner spinnerTo = (Spinner) findViewById(R.id.language_to_spinner);
        int a = (int) spinnerFrom.getSelectedItemId();
        int b = (int) spinnerTo.getSelectedItemId();
        spinnerFrom.setSelection(b);
        spinnerTo.setSelection(a);
    }

    //lang для URI
    private String getLanguageCode() {
        Spinner spinnerLangFrom = (Spinner) findViewById(R.id.language_from_spinner);
        Spinner spinnerLangTo = (Spinner) findViewById(R.id.language_to_spinner);
        int langFromId = (int) spinnerLangFrom.getSelectedItemId();
        int langToId = (int) spinnerLangTo.getSelectedItemId();

        String langFrom;
        String langTo;
        switch (langFromId) {
            case 0:
                langFrom = "en";
                break;
            case 1:
                langFrom = "ru";
                break;
            case 2:
                langFrom = "az";
                break;
            case 3:
                langFrom = "sq";
                break;
            case 4:
                langFrom = "am";
                break;
            case 5:
                langFrom = "ar";
                break;
            case 6:
                langFrom = "hy";
                break;
            case 7:
                langFrom = "af";
                break;
            case 8:
                langFrom = "eu";
                break;
            case 9:
                langFrom = "ba";
                break;
            case 10:
                langFrom = "be";
                break;
            case 11:
                langFrom = "bn";
                break;
            case 12:
                langFrom = "bg";
                break;
            case 13:
                langFrom = "bs";
                break;
            case 14:
                langFrom = "cy";
                break;
            case 15:
                langFrom = "hu";
                break;
            case 16:
                langFrom = "vi";
                break;
            case 17:
                langFrom = "ht";
                break;
            case 18:
                langFrom = "gl";
                break;
            case 19:
                langFrom = "nl";
                break;
            case 20:
                langFrom = "mrj";
                break;
            case 21:
                langFrom = "el";
                break;
            case 22:
                langFrom = "ka";
                break;
            case 23:
                langFrom = "gu";
                break;
            case 24:
                langFrom = "da";
                break;
            case 25:
                langFrom = "he";
                break;
            case 26:
                langFrom = "yi";
                break;
            case 27:
                langFrom = "id";
                break;
            case 28:
                langFrom = "ga";
                break;
            case 29:
                langFrom = "it";
                break;
            case 30:
                langFrom = "is";
                break;
            case 31:
                langFrom = "es";
                break;
            case 32:
                langFrom = "kk";
                break;
            case 33:
                langFrom = "kn";
                break;
            case 34:
                langFrom = "ca";
                break;
            case 35:
                langFrom = "ky";
                break;
            case 36:
                langFrom = "zh";
                break;
            case 37:
                langFrom = "ko";
                break;
            case 38:
                langFrom = "xh";
                break;
            case 39:
                langFrom = "la";
                break;
            case 40:
                langFrom = "lv";
                break;
            case 41:
                langFrom = "lt";
                break;
            case 42:
                langFrom = "lb";
                break;
            case 43:
                langFrom = "mg";
                break;
            case 44:
                langFrom = "ms";
                break;
            case 45:
                langFrom = "ml";
                break;
            case 46:
                langFrom = "mt";
                break;
            case 47:
                langFrom = "mk";
                break;
            case 48:
                langFrom = "mi";
                break;
            case 49:
                langFrom = "mr";
                break;
            case 50:
                langFrom = "mhr";
                break;
            case 51:
                langFrom = "mn";
                break;
            case 52:
                langFrom = "de";
                break;
            case 53:
                langFrom = "ne";
                break;
            case 54:
                langFrom = "no";
                break;
            case 55:
                langFrom = "pa";
                break;
            case 56:
                langFrom = "pap";
                break;
            case 57:
                langFrom = "fa";
                break;
            case 58:
                langFrom = "pl";
                break;
            case 59:
                langFrom = "pt";
                break;
            case 60:
                langFrom = "ro";
                break;
            case 61:
                langFrom = "ceb";
                break;
            case 62:
                langFrom = "sr";
                break;
            case 63:
                langFrom = "si";
                break;
            case 64:
                langFrom = "sk";
                break;
            case 65:
                langFrom = "sl";
                break;
            case 66:
                langFrom = "sw";
                break;
            case 67:
                langFrom = "su";
                break;
            case 68:
                langFrom = "tg";
                break;
            case 69:
                langFrom = "th";
                break;
            case 70:
                langFrom = "tl";
                break;
            case 71:
                langFrom = "ta";
                break;
            case 72:
                langFrom = "tt";
                break;
            case 73:
                langFrom = "te";
                break;
            case 74:
                langFrom = "tr";
                break;
            case 75:
                langFrom = "udm";
                break;
            case 76:
                langFrom = "uz";
                break;
            case 77:
                langFrom = "uk";
                break;
            case 78:
                langFrom = "ur";
                break;
            case 79:
                langFrom = "fi";
                break;
            case 80:
                langFrom = "fr";
                break;
            case 81:
                langFrom = "hi";
                break;
            case 82:
                langFrom = "hr";
                break;
            case 83:
                langFrom = "cs";
                break;
            case 84:
                langFrom = "sv";
                break;
            case 85:
                langFrom = "gd";
                break;
            case 86:
                langFrom = "et";
                break;
            case 87:
                langFrom = "eo";
                break;
            case 88:
                langFrom = "jv";
                break;
            case 89:
                langFrom = "ja";
                break;
            default:
                langFrom = "";
                break;
        }

        switch (langToId) {
            case 0:
                langTo = "en";
                break;
            case 1:
                langTo = "ru";
                break;
            case 2:
                langTo = "az";
                break;
            case 3:
                langTo = "sq";
                break;
            case 4:
                langTo = "am";
                break;
            case 5:
                langTo = "ar";
                break;
            case 6:
                langTo = "hy";
                break;
            case 7:
                langTo = "af";
                break;
            case 8:
                langTo = "eu";
                break;
            case 9:
                langTo = "ba";
                break;
            case 10:
                langTo = "be";
                break;
            case 11:
                langTo = "bn";
                break;
            case 12:
                langTo = "bg";
                break;
            case 13:
                langTo = "bs";
                break;
            case 14:
                langTo = "cy";
                break;
            case 15:
                langTo = "hu";
                break;
            case 16:
                langTo = "vi";
                break;
            case 17:
                langTo = "ht";
                break;
            case 18:
                langTo = "gl";
                break;
            case 19:
                langTo = "nl";
                break;
            case 20:
                langTo = "mrj";
                break;
            case 21:
                langTo = "el";
                break;
            case 22:
                langTo = "ka";
                break;
            case 23:
                langTo = "gu";
                break;
            case 24:
                langTo = "da";
                break;
            case 25:
                langTo = "he";
                break;
            case 26:
                langTo = "yi";
                break;
            case 27:
                langTo = "id";
                break;
            case 28:
                langTo = "ga";
                break;
            case 29:
                langTo = "it";
                break;
            case 30:
                langTo = "is";
                break;
            case 31:
                langTo = "es";
                break;
            case 32:
                langTo = "kk";
                break;
            case 33:
                langTo = "kn";
                break;
            case 34:
                langTo = "ca";
                break;
            case 35:
                langTo = "ky";
                break;
            case 36:
                langTo = "zh";
                break;
            case 37:
                langTo = "ko";
                break;
            case 38:
                langTo = "xh";
                break;
            case 39:
                langTo = "la";
                break;
            case 40:
                langTo = "lv";
                break;
            case 41:
                langTo = "lt";
                break;
            case 42:
                langTo = "lb";
                break;
            case 43:
                langTo = "mg";
                break;
            case 44:
                langTo = "ms";
                break;
            case 45:
                langTo = "ml";
                break;
            case 46:
                langTo = "mt";
                break;
            case 47:
                langTo = "mk";
                break;
            case 48:
                langTo = "mi";
                break;
            case 49:
                langTo = "mr";
                break;
            case 50:
                langTo = "mhr";
                break;
            case 51:
                langTo = "mn";
                break;
            case 52:
                langTo = "de";
                break;
            case 53:
                langTo = "ne";
                break;
            case 54:
                langTo = "no";
                break;
            case 55:
                langTo = "pa";
                break;
            case 56:
                langTo = "pap";
                break;
            case 57:
                langTo = "fa";
                break;
            case 58:
                langTo = "pl";
                break;
            case 59:
                langTo = "pt";
                break;
            case 60:
                langTo = "ro";
                break;
            case 61:
                langTo = "ceb";
                break;
            case 62:
                langTo = "sr";
                break;
            case 63:
                langTo = "si";
                break;
            case 64:
                langTo = "sk";
                break;
            case 65:
                langTo = "sl";
                break;
            case 66:
                langTo = "sw";
                break;
            case 67:
                langTo = "su";
                break;
            case 68:
                langTo = "tg";
                break;
            case 69:
                langTo = "th";
                break;
            case 70:
                langTo = "tl";
                break;
            case 71:
                langTo = "ta";
                break;
            case 72:
                langTo = "tt";
                break;
            case 73:
                langTo = "te";
                break;
            case 74:
                langTo = "tr";
                break;
            case 75:
                langTo = "udm";
                break;
            case 76:
                langTo = "uz";
                break;
            case 77:
                langTo = "uk";
                break;
            case 78:
                langTo = "ur";
                break;
            case 79:
                langTo = "fi";
                break;
            case 80:
                langTo = "fr";
                break;
            case 81:
                langTo = "hi";
                break;
            case 82:
                langTo = "hr";
                break;
            case 83:
                langTo = "cs";
                break;
            case 84:
                langTo = "sv";
                break;
            case 85:
                langTo = "gd";
                break;
            case 86:
                langTo = "et";
                break;
            case 87:
                langTo = "eo";
                break;
            case 88:
                langTo = "jv";
                break;
            case 89:
                langTo = "ja";
                break;
            default:
                langTo = "";
                break;
        }

        return langFrom + "-" + langTo;
    }

}
