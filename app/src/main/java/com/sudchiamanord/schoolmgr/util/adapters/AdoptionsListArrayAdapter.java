package com.sudchiamanord.schoolmgr.util.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.info.ListaAdozioni;

import java.util.List;

/**
 * Created by rita on 12/26/15.
 */
public class AdoptionsListArrayAdapter extends ArrayAdapter<ListaAdozioni>
{
    private static final String TAG = SearchResultsArrayAdapter.class.getSimpleName();


    public AdoptionsListArrayAdapter (Context context)
    {
        super (context, R.layout.activity_adoption_info);
    }

    public AdoptionsListArrayAdapter (Context context, List<ListaAdozioni> objects)
    {
        super (context, R.layout.activity_adoption_info, objects);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent)
    {
        ListaAdozioni adoption = getItem (position);
        if (convertView == null) {
            convertView = LayoutInflater.from (getContext()).inflate (R.layout.adoption_row,
                    parent, false);
        }

        ((TextView) convertView.findViewById (R.id.adoptionIdValue)).setText (String.valueOf (adoption.getIdpag()));
        ((TextView) convertView.findViewById (R.id.adoptionYearValue)).setText (adoption.getAnnos());
        ((TextView) convertView.findViewById (R.id.adopterNameValue)).setText (adoption.getAdnom());
        ((TextView) convertView.findViewById (R.id.adopterLastnameValue)).setText (adoption.getAdcog());

        return convertView;
    }
}
