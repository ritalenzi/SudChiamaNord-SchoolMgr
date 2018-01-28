package com.sudchiamanord.schoolmgr.info;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by rita on 12/10/15.
 */
public class KidInfo implements Parcelable
{
    private Integer dbId;

    private Integer idado;
    private Integer stato;
    private String anome;
    private String acogn;
    private String anick;
    private Integer adsex;
    private String datan;
    private String indir;
    private String citta;
    private String adtel;
    private boolean dadAlive;
    private String pnome;
    private String pcogn;
    private String pnick;
    private String pajob;
    private boolean momAlive;
    private String mnome;
    private String mcogn;
    private String mnick;
    private String majob;
    private String afoto;
    private String ffoto;
    private String anote;
    private String creil;
    private String creda;
    private String scuol;
    private String annos;
    private Integer sclas;
    private String lastt;
    private String lastu;
    private List<ListaAdozioni> listaAdozioni = new ArrayList<>();

    private String localPhotoFile;
    private Bitmap photo;

    private KidInfo (Parcel in)
    {
        dbId = in.readInt();

        idado = in.readInt();
        stato = in.readInt();
        anome = in.readString();
        acogn = in.readString();
        anick = in.readString();
        adsex = in.readInt();
        datan = in.readString();
        indir = in.readString();
        citta = in.readString();
        adtel = in.readString();
        dadAlive = in.readByte() != 0;
        pnome = in.readString();
        pcogn = in.readString();
        pnick = in.readString();
        pajob = in.readString();
        momAlive = in.readByte() != 0;
        mnome = in.readString();
        mcogn = in.readString();
        mnick = in.readString();
        majob = in.readString();
        afoto = in.readString();
        ffoto = in.readString();
        anote = in.readString();
        creil = in.readString();
        creda = in.readString();
        scuol = in.readString();
        annos = in.readString();
        sclas = in.readInt();
        lastt = in.readString();
        lastu = in.readString();
        in.readList (listaAdozioni, getClass().getClassLoader());

        localPhotoFile = in.readString();
//        photo = in.readParcelable(getClass().getClassLoader());
    }

    public KidInfo()
    {
    }

    public Integer getDbId()
    {
        return dbId;
    }

    public void setDbId (Integer dbId)
    {
        this.dbId = dbId;
    }

    /**
     *
     * @return
     * The idado
     */
    public Integer getIdado() {
        return idado;
    }

    /**
     *
     * @param idado
     * The idado
     */
    public void setIdado(Integer idado) {
        this.idado = idado;
    }

    /**
     *
     * @return
     * The stato
     */
    public Integer getStato() {
        return stato;
    }

    /**
     *
     * @param stato
     * The stato
     */
    public void setStato(Integer stato) {
        this.stato = stato;
    }

    /**
     *
     * @return
     * The anome
     */
    public String getAnome() {
        return anome;
    }

    /**
     *
     * @param anome
     * The anome
     */
    public void setAnome(String anome) {
        this.anome = anome;
    }

    /**
     *
     * @return
     * The acogn
     */
    public String getAcogn() {
        return acogn;
    }

    /**
     *
     * @param acogn
     * The acogn
     */
    public void setAcogn(String acogn) {
        this.acogn = acogn;
    }

    /**
     *
     * @return
     * The anick
     */
    public String getAnick() {
        return anick;
    }

    /**
     *
     * @param anick
     * The anick
     */
    public void setAnick(String anick) {
        this.anick = anick;
    }

    /**
     *
     * @return
     * The adsex
     */
    public Integer getAdsex() {
        return adsex;
    }

    /**
     *
     * @param adsex
     * The adsex
     */
    public void setAdsex(Integer adsex) {
        this.adsex = adsex;
    }

    /**
     *
     * @return
     * The datan
     */
    public String getDatan() {
        return datan;
    }

    /**
     *
     * @param datan
     * The datan
     */
    public void setDatan(String datan) {
        this.datan = datan;
    }

    /**
     *
     * @return
     * The indir
     */
    public String getIndir() {
        return indir;
    }

    /**
     *
     * @param indir
     * The indir
     */
    public void setIndir(String indir) {
        this.indir = indir;
    }

    /**
     *
     * @return
     * The citta
     */
    public String getCitta() {
        return citta;
    }

    /**
     *
     * @param citta
     * The citta
     */
    public void setCitta(String citta) {
        this.citta = citta;
    }

    /**
     *
     * @return
     * The adtel
     */
    public String getAdtel() {
        return adtel;
    }

    /**
     *
     * @param adtel
     * The adtel
     */
    public void setAdtel(String adtel) {
        this.adtel = adtel;
    }

    public boolean isDadAlive()
    {
        return dadAlive;
    }

    public int getIsDadAliveAsInt()
    {
        if (dadAlive) {
            return 1;
        }

        return 0;
    }

    public void setDadAlive (boolean b)
    {
        dadAlive = b;
    }

    /**
     *
     * @return
     * The pnome
     */
    public String getPnome() {
        return pnome;
    }

    /**
     *
     * @param pnome
     * The pnome
     */
    public void setPnome(String pnome) {
        this.pnome = pnome;
    }

    /**
     *
     * @return
     * The pcogn
     */
    public String getPcogn() {
        return pcogn;
    }

    /**
     *
     * @param pcogn
     * The pcogn
     */
    public void setPcogn(String pcogn) {
        this.pcogn = pcogn;
    }

    /**
     *
     * @return
     * The pnick
     */
    public String getPnick() {
        return pnick;
    }

    /**
     *
     * @param pnick
     * The pnick
     */
    public void setPnick(String pnick) {
        this.pnick = pnick;
    }

    /**
     *
     * @return
     * The pajob
     */
    public String getPajob() {
        return pajob;
    }

    public String getDBPajob()
    {
        if ((pajob != null) && (pajob.equalsIgnoreCase ("dead"))) {
            return null;
        }

        return pajob;
    }

    /**
     *
     * @param pajob
     * The pajob
     */
    public void setPajob(String pajob) {
        this.pajob = pajob;
    }

    public boolean isMomAlive()
    {
        return momAlive;
    }

    public int getIsMomAliveAsInt()
    {
        if (dadAlive) {
            return 1;
        }

        return 0;
    }

    public void setMomAlive (boolean b)
    {
        momAlive = b;
    }

    /**
     *
     * @return
     * The mnome
     */
    public String getMnome() {
        return mnome;
    }

    /**
     *
     * @param mnome
     * The mnome
     */
    public void setMnome(String mnome) {
        this.mnome = mnome;
    }

    /**
     *
     * @return
     * The mcogn
     */
    public String getMcogn() {
        return mcogn;
    }

    /**
     *
     * @param mcogn
     * The mcogn
     */
    public void setMcogn(String mcogn) {
        this.mcogn = mcogn;
    }

    /**
     *
     * @return
     * The mnick
     */
    public String getMnick() {
        return mnick;
    }

    /**
     *
     * @param mnick
     * The mnick
     */
    public void setMnick(String mnick) {
        this.mnick = mnick;
    }

    /**
     *
     * @return
     * The majob
     */
    public String getMajob() {
        return majob;
    }

    public String getDBMajob()
    {
        if ((majob != null) && (majob.equalsIgnoreCase ("dead"))) {
            return null;
        }

        return majob;
    }

    /**
     *
     * @param majob
     * The majob
     */
    public void setMajob(String majob) {
        this.majob = majob;
    }

    /**
     *
     * @return
     * The afoto
     */
    public String getAfoto() {
        return afoto;
    }

    /**
     *
     * @param afoto
     * The afoto
     */
    public void setAfoto(String afoto) {
        this.afoto = afoto;
    }

    /**
     *
     * @return
     * The ffoto
     */
    public String getFfoto() {
        return ffoto;
    }

    /**
     *
     * @param ffoto
     * The ffoto
     */
    public void setFfoto(String ffoto) {
        this.ffoto = ffoto;
    }

    /**
     *
     * @return
     * The anote
     */
    public String getAnote() {
        return anote;
    }

    /**
     *
     * @param anote
     * The anote
     */
    public void setAnote(String anote) {
        this.anote = anote;
    }

    /**
     *
     * @return
     * The creil
     */
    public String getCreil() {
        return creil;
    }

    /**
     *
     * @param creil
     * The creil
     */
    public void setCreil(String creil) {
        this.creil = creil;
    }

    /**
     *
     * @return
     * The creda
     */
    public String getCreda() {
        return creda;
    }

    /**
     *
     * @param creda
     * The creda
     */
    public void setCreda(String creda) {
        this.creda = creda;
    }

    /**
     *
     * @return
     * The scuol
     */
    public String getScuol() {
        return scuol;
    }

    /**
     *
     * @param scuol
     * The scuol
     */
    public void setScuol(String scuol) {
        this.scuol = scuol;
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

    /**
     *
     * @return
     * The sclas
     */
    public Integer getSclas() {
        return sclas;
    }

    /**
     *
     * @param sclas
     * The sclas
     */
    public void setSclas(Integer sclas) {
        this.sclas = sclas;
    }

    /**
     *
     * @return
     * The lastt
     */
    public String getLastt() {
        return lastt;
    }

    /**
     *
     * @param lastt
     * The lastt
     */
    public void setLastt(String lastt) {
        this.lastt = lastt;
    }

    /**
     *
     * @return
     * The lastu
     */
    public String getLastu() {
        return lastu;
    }

    /**
     *
     * @param lastu
     * The lastu
     */
    public void setLastu(String lastu) {
        this.lastu = lastu;
    }

    /**
     *
     * @return
     * The listaAdozioni
     */
    public List<ListaAdozioni> getListaAdozioni() {
        return listaAdozioni;
    }

    /**
     *
     * @param listaAdozioni
     * The lista_adozioni
     */
    public void setListaAdozioni(List<ListaAdozioni> listaAdozioni) {
        this.listaAdozioni = listaAdozioni;
        Collections.sort (this.listaAdozioni, new YearsComparator());
    }

    public void addAdoption (ListaAdozioni adoption) {
        if (adoption == null) {
            return;
        }
        if (listaAdozioni == null) {
            listaAdozioni = new ArrayList<>();
        }
        listaAdozioni.add (adoption);
        Collections.sort (listaAdozioni, new YearsComparator());
    }

    public ListaAdozioni getAdoption (String year)
    {
        if (listaAdozioni == null) {
            return null;
        }

        if (year == null) {
            return null;
        }

        for (ListaAdozioni adoption : listaAdozioni) {
            if (year.equals (adoption.getAnnos())) {
                return adoption;
            }
        }

        return null;
    }

    public String getLocalPhotoFile()
    {
        return localPhotoFile;
    }

    public void setLocalPhotoFile (String filename)
    {
        localPhotoFile = filename;
    }

    public Bitmap getPhoto()
    {
        return photo;
    }

    public void setPhoto (Bitmap photo)
    {
        this.photo = photo;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel (Parcel dest, int flags)
    {
        dest.writeInt (dbId);

        dest.writeInt (idado);
        dest.writeInt(stato);
        dest.writeString (anome);
        dest.writeString (acogn);
        dest.writeString(anick);
        dest.writeInt(adsex);
        dest.writeString (datan);
        dest.writeString (indir);
        dest.writeString (citta);
        dest.writeString(adtel);
        dest.writeByte ((byte) (dadAlive ? 1 : 0));
        dest.writeString (pnome);
        dest.writeString (pcogn);
        dest.writeString (pnick);
        dest.writeString(pajob);
        dest.writeByte ((byte) (momAlive ? 1 : 0));
        dest.writeString (mnome);
        dest.writeString (mcogn);
        dest.writeString (mnick);
        dest.writeString (majob);
        dest.writeString (afoto);
        dest.writeString (ffoto);
        dest.writeString (anote);
        dest.writeString (creil);
        dest.writeString (creda);
        dest.writeString (scuol);
        dest.writeString(annos);
        dest.writeInt(sclas);
        dest.writeString (lastt);
        dest.writeString(lastu);
        dest.writeList (listaAdozioni);

        dest.writeString (localPhotoFile);
//        dest.writeParcelable (photo, flags);
    }

    public static final Parcelable.Creator<KidInfo> CREATOR = new Parcelable.Creator<KidInfo>()
    {
        public KidInfo createFromParcel (Parcel in)
        {
            return new KidInfo (in);
        }

        public KidInfo[] newArray (int size)
        {
            return new KidInfo[size];
        }
    };

    class YearsComparator implements Comparator<ListaAdozioni>
    {
        @Override
        public int compare (ListaAdozioni lhs, ListaAdozioni rhs)
        {
            if ((lhs.getAnnos() == null) && (rhs.getAnnos() == null)) {
                return 0;
            }

            if (lhs.getAnnos() == null) {
                return 1;
            }

            if (rhs.getAnnos() == null) {
                return -1;
            }

            return (lhs.getAnnos().compareToIgnoreCase (rhs.getAnnos())) * (-1);
        }
    }
}
