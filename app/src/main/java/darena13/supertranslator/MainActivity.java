package darena13.supertranslator;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

import java.io.EOFException;
import java.io.IOException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;//Adapter that will return fragments
    private ViewPager mViewPager;//ViewPager with the sections adapter

    //private DictAdapter dictAdapter;

    private RequestQueue mRequestQueue; //Volley's request queue
    public static final String YANDEX_TRNS_API_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?";
    public static final String YANDEX_DICT_API_URL = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?";
    public static final String TRNS_KEY = "trnsl.1.1.20170323T152914Z.07d329d9a6f367e9.7fc2032da9c680dd945a3f8ba5e65c2cacd82d4b";
    public static final String DICT_KEY = "dict.1.1.20170406T140716Z.aaf26a32ef51e3a4.7e8e7f021bbb2d111099eea26429f7b809d16406";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //creating a request queue
        mRequestQueue = Volley.newRequestQueue(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() { //All subclasses of Fragment must include a public no-argument constructor.
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle(); //A mapping from String keys to various Parcelable values.
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter { //Implementation of PagerAdapter that represents each page as a Fragment that is persistently kept in the fragment manager as long as the user can return to the page.

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position);

            switch (position) {
                case 0:
                    TranslationFormFragment tab1 = new TranslationFormFragment();
                    return tab1;
                case 1:
                    PlaceholderFragment tab2 = new PlaceholderFragment();
                    return tab2.newInstance(position);
                case 2:
                    PlaceholderFragment tab3 = new PlaceholderFragment();
                    return tab3.newInstance(position);
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

    public void translate(View view) {
        try {
            translate1();
        } catch (Exception e) {
            e.printStackTrace();
        }
        translate2();
    }


    public void translate1() {
        String textToTranslate;
        String dictionaryRequestUrl;

        EditText mEditText = (EditText)findViewById(R.id.textToTranslate);
        textToTranslate = mEditText.getText().toString();

        Uri builtDictUri = Uri.parse(YANDEX_DICT_API_URL)
                .buildUpon()
                .appendQueryParameter("key", DICT_KEY)
                .appendQueryParameter("lang", "en-ru")
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
                Toast.makeText(MainActivity.this, "HTTP ERROR", Toast.LENGTH_SHORT).show();
            }
        });

        //Add requests to RequestQueue to execute
        mRequestQueue.add(req);
    }

    public void translate2() {
        String textToTranslate;
        String translateRequestUrl;

        //Toast.makeText(MainActivity.this, "NOPE", Toast.LENGTH_SHORT).show();

        EditText mEditText = (EditText)findViewById(R.id.textToTranslate);
        textToTranslate = mEditText.getText().toString();

        Uri builtTrnsUri = Uri.parse(YANDEX_TRNS_API_URL)
                .buildUpon()
                .appendQueryParameter("key", TRNS_KEY)
                .appendQueryParameter("text", textToTranslate)
                .appendQueryParameter("lang", "en-ru")
                .build();
        translateRequestUrl = builtTrnsUri.toString();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, translateRequestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String result = response.getString("text");
                            result = result.substring(2, result.length()-2);
                            TextView resultsTextView = (TextView)findViewById(R.id.results);
                            resultsTextView.setText(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        //Add requests to RequestQueue to execute
        mRequestQueue.add(req);
    }
}
