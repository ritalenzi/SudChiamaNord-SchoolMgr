package com.sudchiamanord.schoolmgr.info;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rita on 12/10/15.
 */
public class ListaAdozioni implements Parcelable
{
    private Integer idpag;
    private String pclas;
    private String codic;
    private Integer idscu;
    private String scuno;
    private String scuci;
    private String scupr;
    private Integer addid;
    private String adnom;
    private String adcog;
    private String annos;

    private ListaAdozioni (Parcel in)
    {
        idpag = in.readInt();
        pclas = in.readString();
        codic = in.readString();
        idscu = in.readInt();
        scuno = in.readString();
        scuci = in.readString();
        scupr = in.readString();
        addid = in.readInt();
        adnom = in.readString();
        adcog = in.readString();
        annos = in.readString();
    }

    public ListaAdozioni()
    {
    }

    /**
     *
     * @return
     * The idpag
     */
    public Integer getIdpag() {
        return idpag;
    }

    /**
     *
     * @param idpag
     * The idpag
     */
    public void setIdpag(Integer idpag) {
        this.idpag = idpag;
    }

    /**
     *
     * @return
     * The pclas
     */
    public String getPclas() {
        return pclas;
    }

    /**
     *
     * @param pclas
     * The pclas
     */
    public void setPclas(String pclas) {
        this.pclas = pclas;
    }

    /**
     *
     * @return
     * The codic
     */
    public String getCodic() {
        return codic;
    }

    /**
     *
     * @param codic
     * The codic
     */
    public void setCodic(String codic) {
        this.codic = codic;
    }

    /**
     *
     * @return
     * The idscu
     */
    public Integer getIdscu() {
        return idscu;
    }

    /**
     *
     * @param idscu
     * The idscu
     */
    public void setIdscu(Integer idscu) {
        this.idscu = idscu;
    }

    /**
     *
     * @return
     * The scuno
     */
    public String getScuno() {
        return scuno;
    }

    /**
     *
     * @param scuno
     * The scuno
     */
    public void setScuno(String scuno) {
        this.scuno = scuno;
    }

    /**
     *
     * @return
     * The scuci
     */
    public String getScuci() {
        return scuci;
    }

    /**
     *
     * @param scuci
     * The scuci
     */
    public void setScuci(String scuci) {
        this.scuci = scuci;
    }

    /**
     *
     * @return
     * The scupr
     */
    public String getScupr() {
        return scupr;
    }

    /**
     *
     * @param scupr
     * The scupr
     */
    public void setScupr(String scupr) {
        this.scupr = scupr;
    }

    /**
     *
     * @return
     * The addid
     */
    public Integer getAddid() {
        return addid;
    }

    /**
     *
     * @param addid
     * The addid
     */
    public void setAddid(Integer addid) {
        this.addid = addid;
    }

    /**
     *
     * @return
     * The adnom
     */
    public String getAdnom() {
        return adnom;
    }

    /**
     *
     * @param adnom
     * The adnom
     */
    public void setAdnom(String adnom) {
        this.adnom = adnom;
    }

    /**
     *
     * @return
     * The adcog
     */
    public String getAdcog() {
        return adcog;
    }

    /**
     *
     * @param adcog
     * The adcog
     */
    public void setAdcog(String adcog) {
        this.adcog = adcog;
    }

    /**
     *
     * @return
     * The annos
     */
    public String getAnnos() {
        return annos;
    }

    /**
     *
     * @param annos
     * The annos
     */
    public void setAnnos(String annos) {
        this.annos = annos;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel (Parcel dest, int flags)
    {
        dest.writeInt (idpag);
        dest.writeString (pclas);
        dest.writeString (codic);
        dest.writeInt (idscu);
        dest.writeString (scuno);
        dest.writeString (scuci);
        dest.writeString (scupr);
        dest.writeInt (addid);
        dest.writeString (adnom);
        dest.writeString (adcog);
        dest.writeString (annos);
    }

    public static final Parcelable.Creator<ListaAdozioni> CREATOR = new Parcelable.Creator<ListaAdozioni>()
    {
        public ListaAdozioni createFromParcel (Parcel in)
        {
            return new ListaAdozioni (in);
        }

        public ListaAdozioni[] newArray (int size)
        {
            return new ListaAdozioni[size];
        }
    };
}
