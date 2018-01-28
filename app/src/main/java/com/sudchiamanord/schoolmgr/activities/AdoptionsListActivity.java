package com.sudchiamanord.schoolmgr.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.info.ListaAdozioni;
import com.sudchiamanord.schoolmgr.util.adapters.AdoptionsListArrayAdapter;
import com.sudchiamanord.schoolmgr.util.Tags;

import java.util.List;

/**
 * Created by rita on 12/26/15.
 */
public class AdoptionsListActivity extends Activity
{
    private AdoptionsListArrayAdapter mAdapter;


    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView (R.layout.activity_adoptions_list);

        // TODO: add button in the action bar to add a new adoption

        ListView ListView = (ListView) findViewById (R.id.adoptionsList);
        mAdapter = new AdoptionsListArrayAdapter (this);
        ListView.setAdapter (mAdapter);
        ListView.setOnItemClickListener (new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id)
            {
                ListaAdozioni adoption = (ListaAdozioni) parent.getItemAtPosition (position);

                Intent intent = new Intent (getApplicationContext(), AdoptionInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable (Tags.ADOPTION_INFO, adoption);
                intent.putExtras (bundle);
                startActivity (intent);
//                startActivityForResult (intent, Tags.ADOPTION_INFO_REQUEST);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }

        List<ListaAdozioni> adoptionsList = bundle.getParcelableArrayList (Tags.ADOPTIONS_LIST_INFO);

        mAdapter.clear();
        if (adoptionsList != null) {
            mAdapter.addAll (adoptionsList);
        }
        mAdapter.notifyDataSetChanged();
    }

//    @Override
//    protected void onActivityResult (int requestCode, int resultCode, Intent data)
//    {
//
//        if (requestCode == Tags.ADOPTION_INFO_REQUEST) {
//            if (resultCode == Activity.RESULT_OK) {
//                // TODO: it means something has been changed so we either need to save a new object or update an old one
//            }
//            else {
//
//            }
//        }
//    }
}
