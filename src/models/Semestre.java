package models;

import java.util.List;

public class Semestre {

    private int numero;
    private int qtdDeMaterias;
    private List<Materia> materias;

    public Semestre(int numero, int qtdDeMaterias, List<Materia> materias) {
        this.numero = numero;
        this.qtdDeMaterias = qtdDeMaterias;
        this.materias = materias;

    }

    public int getNumero() {
        return numero;
    }
    public void setNumero(int numero) {
        this.numero = numero;
    }
    public int getQtdDeMaterias() {
        return qtdDeMaterias;
    }
    public void setQtdDeMaterias(int qtdDeMaterias) {
        this.qtdDeMaterias = qtdDeMaterias;
    }
    public List<Materia> getMaterias() {
        return materias;
    }
    public void setMaterias(List<Materia> materias) {
        this.materias = materias;
    }
    public void addMateria(Materia materia) {
        this.materias.add(materia);
    }
    public void removeMateria(Materia materia) {
        this.materias.remove(materia);
    }
}
