package jagielski.kozak.atrakcjekrakowa;

/**
 * Created by Piotr on 2015-06-03.
 */
public class Ulubione {
    private int id;
    private String idMiejsca;


    public Ulubione() {
        this.id = 0;
        this.idMiejsca = null;
    }
    public Ulubione(int id, String idMiejsca) {
        this.id = id;
        this.idMiejsca = idMiejsca;

    }
    public Ulubione(String idMiejsca) {
        this.idMiejsca = idMiejsca;
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

    public void setIdMiejsca(String nazwa) {
        this.idMiejsca = idMiejsca;
    }


    @Override
    public String toString() {
        return idMiejsca;
    }
}