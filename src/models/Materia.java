package models;

public class Materia {
    
    private String nome;
    private int horas;
    private String mencao;
    private boolean eletiva;
    
    public Materia(String nome, int horas, String mencao, boolean eletiva) {
        this.nome = nome;
        this.horas = horas;
        this.mencao = mencao;
        this.eletiva = eletiva;
    }
    
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public int getHoras() {
        return horas;
    }
    public void setHoras(int horas) {
        this.horas = horas;
    }
    public String getMencao() {
        return mencao;
    }
    public void setMencao(String mencao) {
        this.mencao = mencao;
    }
    public boolean isEletiva() {
        return eletiva;
    }
    public void setEletiva(boolean eletiva) {
        this.eletiva = eletiva;
    }
}
