package jagielski.kozak.atrakcjekrakowa;

/**
 * Created by Piotr on 2015-06-02.
 */
public class Miejsce {

    private int id;
    private String idMiejsca;
    private String nazwa;
    private String opis;
    private String zdjecie;
    private String gpsl;
    private String gpsh;

    public Miejsce() {
        this.id = 0;
        this.idMiejsca = null;
        this.nazwa = null;
        this.opis = null;
        this.zdjecie = null;
        this.gpsl = null;
        this.gpsh = null;
    }
    public Miejsce(int id, String idMiejsca, String nazwa, String opis, String zdjecie, String gpsl, String gpsh) {
        this.id = id;
        this.idMiejsca = idMiejsca;
        this.nazwa = nazwa;
        this.opis = opis;
        this.zdjecie = zdjecie;
        this.gpsl = gpsl;
        this.gpsh = gpsh;
    }
    public Miejsce(String idMiejsca, String nazwa, String opis, String zdjecie, String gpsl, String gpsh) {
        this.idMiejsca = idMiejsca;
        this.nazwa = nazwa;
        this.opis = opis;
        this.zdjecie = zdjecie;
        this.gpsl = gpsl;
        this.gpsh = gpsh;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdMiejsca() {
        return idMiejsca;
    }

    public void setIdMiejsca(String idMiejsca) {
        this.idMiejsca = idMiejsca;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getZdjecie() {
        return zdjecie;
    }

    public void setZdjecie(String zdjecie) {
        this.zdjecie = zdjecie;
    }

    public String getGpsl() {
        return gpsl;
    }

    public void setGpsl(String gpsl) {
        this.gpsl = gpsl;
    }

    public String getGpsh() {
        return gpsh;
    }

    public void setGpsh(String gpsh) {
        this.gpsh = gpsh;
    }

    @Override
    public String toString() {
        return nazwa;
    }
}