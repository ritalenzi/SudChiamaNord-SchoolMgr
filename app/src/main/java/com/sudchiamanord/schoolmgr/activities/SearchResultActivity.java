package com.sudchiamanord.schoolmgr.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.info.KidInfo;
import com.sudchiamanord.schoolmgr.info.SearchResult;
import com.sudchiamanord.schoolmgr.operations.SearchOps;
import com.sudchiamanord.schoolmgr.provider.query.Query;
import com.sudchiamanord.schoolmgr.provider.DBContract.KidEntry;
import com.sudchiamanord.schoolmgr.provider.DBContract.AdoptionEntry;
import com.sudchiamanord.schoolmgr.util.Utils;
import com.sudchiamanord.schoolmgr.util.scheleton.GenericActivity;
import com.sudchiamanord.schoolmgr.provider.query.QueryType;
import com.sudchiamanord.schoolmgr.util.RingProgressDialog;
import com.sudchiamanord.schoolmgr.util.adapters.SearchResultsArrayAdapter;
import com.sudchiamanord.schoolmgr.util.Tags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

/**
 * Created by rita on 12/21/15.
 */
public class SearchResultActivity extends GenericActivity<SearchOps>
{
    private RingProgressDialog mOpProgressDialog;
    private SearchResultsArrayAdapter mAdapter;
    private String mUser;
    private QueryType mQueryType;
    private boolean isSelected = false;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView (R.layout.activity_search_results_list);

        if (savedInstanceState != null) {
            mUser = savedInstanceState.getString (Tags.CURRENT_USER);
            mQueryType = QueryType.valueOf (savedInstanceState.getString (Tags.QUERY_TYPE));
        }
        else {
            mUser = getIntent().getStringExtra (Tags.CURRENT_USER);
            mQueryType = QueryType.valueOf (getIntent().getStringExtra (Tags.QUERY_TYPE));
        }

        ListView listView = (ListView) findViewById (R.id.searchResultsList);
        mAdapter = new SearchResultsArrayAdapter (this);
        listView.setAdapter (mAdapter);
        listView.setOnItemClickListener (new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id)
            {
                if (isSelected) {
                    return;
                }

                SearchResult searchResult = (SearchResult) parent.getItemAtPosition (position);
                KidInfo kidInfo = searchResult.getKidInfo();

                Intent intent = new Intent (getApplicationContext(), KidInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable (Tags.KID_INFO, kidInfo);
                bundle.putString (Tags.CURRENT_USER, mUser);
                intent.putExtras (bundle);
                startActivity (intent);
            }
        });

        listView.setOnItemLongClickListener (new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick (AdapterView<?> parent, View view, int position, long id)
            {
                isSelected = true;

                SearchResult searchResult = (SearchResult) parent.getItemAtPosition (position);
                final KidInfo kidInfo = searchResult.getKidInfo();

                new AlertDialog.Builder (SearchResultActivity.this)
                        .setTitle (R.string.generalInfo)
                        .setMessage (R.string.confirmDeleteKidInfo)
                        .setPositiveButton (android.R.string.ok, new DialogInterface.OnClickListener()
                        {
                            public void onClick (DialogInterface dialog, int which)
                            {
                                isSelected = false;

                                Intent intent = new Intent (getApplicationContext(), DeleteKidInfoActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt (Tags.KID_INFO_DB_ID, kidInfo.getDbId());
                                intent.putExtras (bundle);
                                startActivity (intent);
                            }
                        })
                        .setNegativeButton (android.R.string.cancel, new DialogInterface.OnClickListener()
                        {
                            public void onClick (DialogInterface dialog, int which)
                            {
                                isSelected = false;
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return false;
            }
        });

        super.onCreate (savedInstanceState, SearchOps.class);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        mOpProgressDialog = new RingProgressDialog (SearchResultActivity.this);
        Query kidInfoQuery = null;
        Query adoptionsQuery = null;
        switch (mQueryType) {
            case all:
                kidInfoQuery = null;
                adoptionsQuery = null;
                break;

            case kid_name:
                kidInfoQuery = createKidInfoQueryForNameSearch();
                adoptionsQuery = null;
                break;

            case adoption_id:
                adoptionsQuery = createAdoptionQueryForAdoptionIdSearch();
                kidInfoQuery = createKidInfoQueryForIdSearch();
                break;

            case adopter:
                adoptionsQuery = createAdoptionQueryForAdopterSearch();
                kidInfoQuery = createKidInfoQueryForIdSearch();
                break;
        }
        getOps().query(mQueryType, kidInfoQuery, adoptionsQuery);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        outState.putString (Tags.CURRENT_USER, mUser);
        outState.putString(Tags.QUERY_TYPE, mQueryType.name());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed()
    {
        setResult (RESULT_OK, new Intent());
        finish();
    }

    private Query createKidInfoQueryForNameSearch()
    {
        Uri uri = KidEntry.KID_CONTENT_URI;
        String[] projection = null;
        StringBuilder sbWhereClause = new StringBuilder();
        List<String> whereArgsList = new ArrayList<>();
        String kidFirstName = getIntent().getStringExtra (Tags.SEARCH_KID_FIRST_NAME);
        String kidLastName = getIntent().getStringExtra (Tags.SEARCH_KID_LAST_NAME);
        String kidNickName = getIntent().getStringExtra (Tags.SEARCH_KID_NICK_NAME);
        if ((kidFirstName != null) && (!kidFirstName.isEmpty())) {
            sbWhereClause.append ("lower(");
            sbWhereClause.append (KidEntry.COLUMN_KID_NAME);
            sbWhereClause.append (")");
//            sbWhereClause.append (" = ?");
            sbWhereClause.append (" LIKE ?");
            whereArgsList.add ("%" + kidFirstName.toLowerCase() + "%");
        }
        if ((kidLastName != null) && (!kidLastName.isEmpty())) {
            if (sbWhereClause.toString().endsWith ("?")) {
                sbWhereClause.append (" AND ");
            }
            sbWhereClause.append ("lower(");
            sbWhereClause.append (KidEntry.COLUMN_KID_LASTNAME);
            sbWhereClause.append (")");
//            sbWhereClause.append (" = ?");
            sbWhereClause.append (" LIKE ?");
            whereArgsList.add ("%" + kidLastName.toLowerCase() + "%");
        }
        if ((kidNickName != null) && (!kidNickName.isEmpty())) {
            if (sbWhereClause.toString().endsWith ("?")) {
                sbWhereClause.append (" AND ");
            }
            sbWhereClause.append ("lower(");
            sbWhereClause.append (KidEntry.COLUMN_KID_NICKNAME);
            sbWhereClause.append (")");
//            sbWhereClause.append (" = ?");
            sbWhereClause.append (" LIKE ?");
            whereArgsList.add ("%" + kidNickName.toLowerCase() + "%");
        }

        String whereClause = sbWhereClause.toString();
        String[] whereArgs = new String[whereArgsList.size()];
        whereArgsList.toArray (whereArgs);
        String sortOrder = null;

        return new Query (uri, projection, whereClause, whereArgs, sortOrder);
    }

    private Query createAdoptionQueryForAdoptionIdSearch()
    {
        Uri uri = AdoptionEntry.ADOPTION_CONTENT_URI;
        String[] projection = {AdoptionEntry.COLUMN_KID_ID};

        StringBuilder sbWhereClause = new StringBuilder();
        List<String> whereArgsList = new ArrayList<>();
        String adoptionCode = getIntent().getStringExtra (Tags.SEARCH_ADOPTION_CODE);
        if ((adoptionCode != null) && (!adoptionCode.isEmpty())) {
            sbWhereClause.append ("lower(");
            sbWhereClause.append (AdoptionEntry.COLUMN_ADOPTION_CODE);
            sbWhereClause.append (")");
//            sbWhereClause.append (" = ?");
            sbWhereClause.append (" LIKE ?");
            whereArgsList.add (adoptionCode.toLowerCase());
        }

        String whereClause = sbWhereClause.toString();
        String[] whereArgs = new String[whereArgsList.size()];
        whereArgsList.toArray (whereArgs);
        String sortOrder = null;

        return new Query (uri, projection, whereClause, whereArgs, sortOrder);
    }

    private Query createAdoptionQueryForAdopterSearch()
    {
        Uri uri = AdoptionEntry.ADOPTION_CONTENT_URI;
        String[] projection = null;
        StringBuilder sbWhereClause = new StringBuilder();
        List<String> whereArgsList = new ArrayList<>();

        String adopterFirstName = getIntent().getStringExtra (Tags.SEARCH_ADOPTER_FIRST_NAME);
        String adopterLastName = getIntent().getStringExtra (Tags.SEARCH_ADOPTER_LAST_NAME);
        if ((adopterFirstName != null) && (!adopterFirstName.isEmpty())) {
            sbWhereClause.append ("lower(");
            sbWhereClause.append (AdoptionEntry.COLUMN_ADOPTER_NAME);
            sbWhereClause.append (")");
//            sbWhereClause.append (" = ?");
            sbWhereClause.append (" LIKE ?");
            whereArgsList.add (adopterFirstName.toLowerCase());
        }
        if ((adopterLastName != null) && (!adopterLastName.isEmpty())) {
            if (sbWhereClause.toString().endsWith ("?")) {
                sbWhereClause.append (" AND ");
            }
            sbWhereClause.append ("lower(");
            sbWhereClause.append (AdoptionEntry.COLUMN_ADOPTER_LASTNAME);
            sbWhereClause.append (")");
//            sbWhereClause.append (" = ?");
            sbWhereClause.append (" LIKE ?");
            whereArgsList.add (adopterLastName.toLowerCase());
        }

        String whereClause = sbWhereClause.toString();
        String[] whereArgs = new String[whereArgsList.size()];
        whereArgsList.toArray (whereArgs);
        String sortOrder = null;

        return new Query (uri, projection, whereClause, whereArgs, sortOrder);
    }

    private Query createKidInfoQueryForIdSearch()
    {
        Uri uri = KidEntry.KID_CONTENT_URI;
        String[] projection = null;
        String whereClause = KidEntry._ID + " = ?";
        String[] whereArgs = null;
        String sortOrder = null;

        return new Query (uri, projection, whereClause, whereArgs, sortOrder);
    }

    public void notifyProgressUpdate (int progress, int dialogTitle, int dialogExpl)
    {
        mOpProgressDialog.updateProgressDialog (progress, dialogTitle, dialogExpl);
    }

    public void displayResults (List<KidInfo> kidsInfo, String errorMessage) {
        mAdapter.clear();

        if (errorMessage != null) {
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
            return;
        }

        Collections.sort (kidsInfo, new LastNamesComparator());

        Properties properties = Utils.getProperties (getFileStreamPath (ConfigActivity.CONFIG_FILE));
        String schoolYear = properties.getProperty (Tags.SCHOOL_YEAR);
        List<SearchResult> searchResults;
        searchResults = new ArrayList<>();
        for (KidInfo kidInfo : kidsInfo) {
            searchResults.add (new SearchResult (kidInfo, schoolYear));
        }

        mAdapter.addAll (searchResults);
        mAdapter.notifyDataSetChanged();
    }

    class LastNamesComparator implements Comparator<KidInfo>
    {
        @Override
        public int compare (KidInfo lhs, KidInfo rhs)
        {
            if ((lhs.getAcogn() == null) && (rhs.getAcogn() == null)) {
                return 0;
            }

            if (lhs.getAcogn() == null) {
                return 1;
            }

            if (rhs.getAcogn() == null) {
                return -1;
            }

            return lhs.getAcogn().compareToIgnoreCase (rhs.getAcogn());
        }
    }
}
