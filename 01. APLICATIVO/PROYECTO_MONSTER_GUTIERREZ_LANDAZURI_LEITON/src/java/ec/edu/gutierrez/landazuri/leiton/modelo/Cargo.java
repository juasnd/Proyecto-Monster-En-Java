package ec.edu.gutierrez.landazuri.leiton.modelo;

public class Cargo {
    private String pedepCodigo; // Código del departamento
    private String pecarCodigo; // Código del cargo
    private String pecarDescri; // Descripción del cargo
    
    // Campo auxiliar para mostrar en la tabla visual
    private String nombreDepartamento; 

    public Cargo() {
    }

    public Cargo(String pedepCodigo, String pecarCodigo, String pecarDescri) {
        this.pedepCodigo = pedepCodigo;
        this.pecarCodigo = pecarCodigo;
        this.pecarDescri = pecarDescri;
    }

    // Getters y Setters
    public String getPedepCodigo() { return pedepCodigo; }
    public void setPedepCodigo(String pedepCodigo) { this.pedepCodigo = pedepCodigo; }

    public String getPecarCodigo() { return pecarCodigo; }
    public void setPecarCodigo(String pecarCodigo) { this.pecarCodigo = pecarCodigo; }

    public String getPecarDescri() { return pecarDescri; }
    public void setPecarDescri(String pecarDescri) { this.pecarDescri = pecarDescri; }

    public String getNombreDepartamento() { return nombreDepartamento; }
    public void setNombreDepartamento(String nombreDepartamento) { this.nombreDepartamento = nombreDepartamento; }
}