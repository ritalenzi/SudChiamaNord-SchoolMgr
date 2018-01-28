package com.sudchiamanord.schoolmgr.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.info.ListaAdozioni;
import com.sudchiamanord.schoolmgr.util.Tags;

/**
 * Created by rita on 12/27/15.
 */
public class AdoptionInfoActivity extends Activity
{
    // TODO: 1) se si e' arrivati qui dal bottone NEW su un'activity precedente i campi sono modificabili altrimenti no
    // TODO: 2) sulla action bar c'e' un bottone EDIT e quando si clicca i campi diventano editabili
    // TODO: 3) quando si ritorna si manda RESULT_CANCEL se nessu campo e' stato modificato; se NEW o EDIT sono stati usati, si assume che qualcosa sia stato modificato e si chiede si salvare, poi si manda RESULT_OK con il nuovo oggetto ListaAdozioni

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView (R.layout.activity_adoption_info);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }

        ListaAdozioni adoption = bundle.getParcelable (Tags.ADOPTION_INFO);
        if (adoption == null) {
            return;
        }

        ((TextView) findViewById (R.id.adPaymentIdValue)).setText (String.valueOf (adoption.getIdpag()));
        String adClass = getString (R.string.unknown);
        if ((adoption.getPclas() != null) && (!adoption.getPclas().equals ("?"))) {
            adClass = adoption.getPclas();
        }
        ((TextView) findViewById (R.id.adClassValue)).setText (adClass);
        ((TextView) findViewById (R.id.adCodeValue)).setText (adoption.getCodic());
        ((TextView) findViewById (R.id.adSchoolIdValue)).setText (String.valueOf(adoption.getIdscu()));
        ((TextView) findViewById (R.id.adSchoolNameValue)).setText (adoption.getScuno());
        ((TextView) findViewById (R.id.adSchoolCityValue)).setText (adoption.getScuci());
        ((TextView) findViewById (R.id.adPrefixValue)).setText (adoption.getScupr());
        ((TextView) findViewById (R.id.adopterIdValue)).setText (String.valueOf (adoption.getAddid()));
        String fullName = (adoption.getAdnom() != null ? adoption.getAdnom() + " " : "") +
                (adoption.getAdcog() != null ? adoption.getAdcog() : "");
        ((TextView) findViewById (R.id.adopterFullNameValue)).setText (fullName);
        ((TextView) findViewById (R.id.adYearValue)).setText (adoption.getAnnos());
    }
}
