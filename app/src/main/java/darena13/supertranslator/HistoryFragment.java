package darena13.supertranslator;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

/**
 * Created by darena13 on 18.04.2017.
 */

public class HistoryFragment extends Fragment implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {
    //URI к БД в качестве провайдера данных для лоадера
    private static final Uri HISTORY_URI = RequestProvider.urlForItems(0);
    //запрос в БД на поиск строчек с совпадением по полям text и trns (перевод)
    private static final String SELECTION =
            HistoryOpenHelper.COLUMN_TEXT + " LIKE ? OR "
                    + HistoryOpenHelper.COLUMN_TRNS + " LIKE ?";
    //такой же запрос, но для записей, добавленных в избранное
    private static final String FAVORITES_ONLY_SELECTION =
            "(" + SELECTION + ") AND " + HistoryOpenHelper.COLUMN_FAV + " = 1";

    private HistoryDataSource dataSource; //ДАО
    private ListView historyList;
    private CustomAdapter adapter;
    private SearchView searchView;
    private String filter = ""; //фильтр для сортировки списка по тексту в SearchView
    private boolean showFavoritesOnly; //История или Избранное
    private OnChangeHistoryListener onChangeHistoryListener; //

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);

        //Лоадер для загрузки данных из БД в ListView
        getLoaderManager().initLoader(0, null, this);

        //создаем ДАО
        dataSource = new HistoryDataSource(getContext());
        //создаем/открываем БД
        dataSource.open();
        historyList = (ListView) view.findViewById(R.id.history_list);
        searchView = (SearchView) view.findViewById(R.id.history_search);

        //задаем листенер для поля Поиск
        searchView.setOnQueryTextListener(this);

        return view;
    }

    //История или Избранное
    public void setShowFavoritesOnly(boolean showFavoritesOnly) {
        this.showFavoritesOnly = showFavoritesOnly;
    }

    //задать листенер
    public void setOnChangeHistoryListener(OnChangeHistoryListener listener) {
        this.onChangeHistoryListener = listener;
    }

    //если данные изменились, список нужно обновить
    public void updateList() {
        getLoaderManager().getLoader(0).onContentChanged();
    }

    @Override
    public void onResume() {
        dataSource.open();
        super.onResume();
    }

    @Override
    public void onPause() {
        dataSource.close();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        dataSource.close();
        super.onDestroy();
    }

    //при создании лоадера
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
        //в качестве аргумента для LIKE в запросе текст от пользователя +% (любые символы)
        String likeArg = filter + "%";
        //История или Избранное
        String selection = showFavoritesOnly ? FAVORITES_ONLY_SELECTION : SELECTION;
        //создаем новый КурсорЛоадер с фильтром и сортировкой по дате
        return new CursorLoader(getContext(), HISTORY_URI, null, selection,
                new String[]{likeArg, likeArg}, HistoryOpenHelper.COLUMN_DATE + " DESC");
    }

    //лоадер закончил загрузку данных
    @Override
    public void onLoadFinished(Loader arg0, Cursor cursor) {
        //перемещаем курсор на первую строчку результатов запроса
        cursor.moveToFirst();
        //создаем адаптер
        adapter = new CustomAdapter(getContext(), dataSource, cursor, onChangeHistoryListener);
        historyList.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader arg0) {
        //ничего не делаем
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //передаем в фильтр текст, который пользователь набирает в поле SearchView
        filter = newText;
        //перезапускаем лоадер, чтобы сделать новый запрос - с новым фильтром
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }
}
