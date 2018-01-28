package com.sudchiamanord.schoolmgr.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.provider.query.QueryType;
import com.sudchiamanord.schoolmgr.util.fragments.SearchTabFragment;
import com.sudchiamanord.schoolmgr.util.Tags;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rita on 12/21/15.
 */
public class SearchActivity extends Activity implements ActionBar.TabListener
{
    private String mUser;
    private List<Fragment> fragList = new ArrayList<>();

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);
//        setContentView (R.layout.activity_search);

        mUser = getIntent().getStringExtra (Tags.CURRENT_USER);

        ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab tab = bar.newTab();
        tab.setTabListener (this);
        tab.setText(R.string.searchKidInfoTabTitle);
        bar.addTab(tab);

        tab = bar.newTab();
        tab.setTabListener (this);
        tab.setText(R.string.searchAdoptionCodeTabTitle);
        bar.addTab(tab);

        tab = bar.newTab();
        tab.setTabListener (this);
        tab.setText(R.string.searchAdopterTabTitle);
        bar.addTab(tab);

        tab = bar.newTab();
        tab.setTabListener (this);
        tab.setText (R.string.searchAllTabTitle);
        bar.addTab (tab);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        if (requestCode == Tags.SEARCH_REQUEST) {
            finish();
        }
    }

    public void searchAll (View view)
    {
        Intent intent = new Intent (this, SearchResultActivity.class);
        intent.putExtra (Tags.QUERY_TYPE, QueryType.all.name());
        intent.putExtra (Tags.CURRENT_USER, mUser);
        startActivityForResult (intent, Tags.SEARCH_REQUEST);
    }

    public void searchKidName (View view)
    {
        String kidFirstName = ((EditText) findViewById (R.id.searchKidNameEditText)).getText().toString();
        String kidLastName = ((EditText) findViewById (R.id.searchKidLastnameEditText)).getText().toString();
        String kidNickName = ((EditText) findViewById (R.id.searchKidNicknameEditText)).getText().toString();

        Intent intent = new Intent (this, SearchResultActivity.class);
        intent.putExtra (Tags.QUERY_TYPE, QueryType.kid_name.name());
        intent.putExtra (Tags.CURRENT_USER, mUser);
        intent.putExtra (Tags.SEARCH_KID_FIRST_NAME, kidFirstName);
        intent.putExtra (Tags.SEARCH_KID_LAST_NAME, kidLastName);
        intent.putExtra (Tags.SEARCH_KID_NICK_NAME, kidNickName);
        startActivityForResult (intent, Tags.SEARCH_REQUEST);
    }

    public void searchAdoptionId (View view)
    {
        String adoptionCode = ((EditText) findViewById (R.id.searchAdoptionCodeEditText)).getText().toString();

        Intent intent = new Intent (this, SearchResultActivity.class);
        intent.putExtra (Tags.QUERY_TYPE, QueryType.adoption_id.name());
        intent.putExtra (Tags.CURRENT_USER, mUser);
        intent.putExtra (Tags.SEARCH_ADOPTION_CODE, adoptionCode);
        startActivityForResult (intent, Tags.SEARCH_REQUEST);
    }

    public void searchAdopter (View view)
    {
        String adopterFirstName = ((EditText) findViewById (R.id.searchAdopterFirstNameEditText)).getText().toString();
        String adopterLastName = ((EditText) findViewById (R.id.searchAdopterLastnameEditText)).getText().toString();

        Intent intent = new Intent (this, SearchResultActivity.class);
        intent.putExtra (Tags.QUERY_TYPE, QueryType.adopter.name());
        intent.putExtra (Tags.CURRENT_USER, mUser);
        intent.putExtra (Tags.SEARCH_ADOPTER_FIRST_NAME, adopterFirstName);
        intent.putExtra (Tags.SEARCH_ADOPTER_LAST_NAME, adopterLastName);
        startActivityForResult(intent, Tags.SEARCH_REQUEST);
    }

    @Override
    public void onTabSelected (ActionBar.Tab tab, FragmentTransaction ft)
    {
        Fragment f = null;
        SearchTabFragment tf;

        if (fragList.size() > tab.getPosition())
            fragList.get(tab.getPosition());

        if (f == null) {
            tf = new SearchTabFragment();
            Bundle data = new Bundle();
            switch (tab.getPosition()) {
                case 0:
                    data.putInt (Tags.SEARCH_FRAGMENT_LAYOUT, R.layout.fragment_search_kid_name);
                    break;

                case 1:
                    data.putInt (Tags.SEARCH_FRAGMENT_LAYOUT, R.layout.fragment_search_adoption_id);
                    break;

                case 2:
                    data.putInt (Tags.SEARCH_FRAGMENT_LAYOUT, R.layout.fragment_search_adopter);
                    break;

                case 3:
                    data.putInt (Tags.SEARCH_FRAGMENT_LAYOUT, R.layout.fragment_search_all);
                    break;
            }

            tf.setArguments(data);
            fragList.add(tf);
        }
        else {
            tf = (SearchTabFragment) f;
        }

        ft.replace (android.R.id.content, tf);
    }

    @Override
    public void onTabUnselected (ActionBar.Tab tab, FragmentTransaction ft)
    {
        if (fragList.size() > tab.getPosition())
        {
            ft.remove (fragList.get (tab.getPosition()));
        }
    }

    @Override
    public void onTabReselected (ActionBar.Tab tab, FragmentTransaction ft)
    {
    }
}
