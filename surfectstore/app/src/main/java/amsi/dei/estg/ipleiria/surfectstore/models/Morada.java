package amsi.dei.estg.ipleiria.surfectstore.models;

public class Morada {
    private int moradaId;
    private String nomeMorada;
    private String codigoPostalMorada;
    private String paisMorada;
    private String distritoMorada;
    private String email;

    public Morada(int moradaId, String nomeMorada, String codigoPostalMorada, String paisMorada, String distritoMorada, String email) {
        this.moradaId = moradaId;
        this.nomeMorada = nomeMorada;
        this.codigoPostalMorada = codigoPostalMorada;
        this.paisMorada = paisMorada;
        this.distritoMorada = distritoMorada;
        this.email = email;
    }
    public Morada() {
        this.moradaId = moradaId;
        this.nomeMorada = nomeMorada;
        this.codigoPostalMorada = codigoPostalMorada;
        this.paisMorada = paisMorada;
        this.distritoMorada = distritoMorada;
        this.email = email;
    }
    public int getMoradaId() {
        return moradaId;
    }

    public void setMoradaId(int moradaId) {
        this.moradaId = moradaId;
    }

    public String getNomeMorada() {
        return nomeMorada;
    }

    public void setNomeMorada(String nomeMorada) {
        this.nomeMorada = nomeMorada;
    }

    public String getCodigoPostalMorada() {
        return codigoPostalMorada;
    }

    public void setCodigoPostalMorada(String codigoPostalMorada) {
        this.codigoPostalMorada = codigoPostalMorada;
    }

    public String getPaisMorada() {
        return paisMorada;
    }

    public void setPaisMorada(String paisMorada) {
        this.paisMorada = paisMorada;
    }

    public String getDistritoMorada() {
        return distritoMorada;
    }

    public void setDistritoMorada(String distritoMorada) {
        this.distritoMorada = distritoMorada;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

