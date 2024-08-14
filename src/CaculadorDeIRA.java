import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import models.Materia;
import models.Semestre;

public class CaculadorDeIRA {
    private static final String FILE_PATH = "semestres.txt";
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        List<Semestre> semestres = lerArquivo();
        boolean executando = true;
        while (executando) {
            exibirMenu();
            int acao = scanner.nextInt();
            switch (acao) {
                case 1:
                    exibirSemestre(semestres);
                    break;
                case 2:
                    adicionarSemestre(semestres, scanner);
                    break;
                case 3:
                    System.out.printf("O seu IRA é: %.3f\n", calculaIRA(semestres));
                    break;
                case 4:
                    executando = false;
                default:
                    break;
            }
        }
        scanner.close();
    }

    private static List<Semestre> lerArquivo() {
        List<Semestre> semestres = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                List<Materia> materias = new ArrayList<>();
                String[] partes = linha.split(";");
                int numeroDoSemestre = Integer.parseInt(partes[0]);
                int qtdDeMaterias = Integer.parseInt(partes[1]);
                String[] materiasString = partes[2].split(",");
                for (String texto : materiasString) {
                    String[] materiaString = texto.split("-");
                    String nome = materiaString[0];
                    int horas = Integer.parseInt(materiaString[1]);
                    String mencao = materiaString[2];
                    Materia materia = new Materia(nome, horas, mencao);
                    materias.add(materia);
                }
                Semestre semestre = new Semestre(numeroDoSemestre, qtdDeMaterias, materias);
                semestres.add(semestre);
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
        return semestres;
    }    

    private static void exibirSemestre(List<Semestre> semestres) {
        if (semestres.isEmpty())
            System.out.println("\nVocê não cadastrou nenhum semestre aqui!");
        else
            for (Semestre semestre : semestres) {
                System.out.println(semestre.getNumero() + "º semestre (" + semestre.getQtdDeMaterias() + " matérias):");
                for (Materia materia : semestre.getMaterias()) {
                    System.out.println("  " + materia.getNome() + ":");
                    System.out.println("      Horas: " + materia.getHoras());
                    System.out.println("      Menção: " + materia.getMencao());
                }
                System.out.println();
            }
    }

    private static float calculaIRA(List<Semestre> semestres) {
        if (semestres.isEmpty()) {
            System.out.println("\nNão tem como calcular seu IRA se você não adicionar matérias!");
        } else {
            float divisor = 0;
            float dividendo = 0;
            for (Semestre semestre : semestres) {
                float nDoSemestre = semestre.getNumero();
                for (Materia materia : semestre.getMaterias()) {
                    float creditos = materia.getHoras() / 15;
                    float pesoDaMencao = converteMencao(materia.getMencao());
                    dividendo += pesoDaMencao * creditos * nDoSemestre;
                    divisor += creditos * nDoSemestre;
                }
            }
            float ira = dividendo/divisor;
            return ira;
        }
        return 0;
    } 

    private static List<Semestre> adicionarSemestre(List<Semestre> semestres, Scanner scanner) {
        List<Materia> materias = new ArrayList<>();
        
        System.out.println("Esse semestre conta como qual pelo SIGAA? (matérias de verão contam como do semestre anterior)");
        int numero = scanner.nextInt();
        scanner.nextLine(); // Consome a nova linha deixada pelo nextInt()
        System.out.println("Quantas matérias você fez nesse semestre?");
        int qtdDeMaterias = scanner.nextInt();
        scanner.nextLine(); // Consome a nova linha deixada pelo nextInt()
        for (int i = 0; i < qtdDeMaterias; i++) {
            System.out.println("Qual a matéria?");
            String nome = scanner.nextLine();
            System.out.println("Quantas Horas " + nome + " tem?");
            int horas = scanner.nextInt();
            scanner.nextLine(); // Consome a nova linha deixada pelo nextInt()
            System.out.println("Qual foi a sua menção em " + nome + "?");
            String mencao = scanner.nextLine();
            Materia materia = new Materia(nome, horas, mencao);
            materias.add(materia);
        }
        Semestre semestre = new Semestre(numero, qtdDeMaterias, materias);
        semestres.add(semestre);
        salvarSemestresNoArquivo(semestres);
        return semestres;
    }
    
    private static void salvarSemestresNoArquivo(List<Semestre> semestres) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Semestre semestre : semestres) {
                writer.write(semestre.getNumero() + ";" + semestre.getQtdDeMaterias() + ";");
                for (int i = 0; i < semestre.getMaterias().size(); i++) {
                    Materia materia = semestre.getMaterias().get(i);
                    writer.write(materia.getNome() + "-" + materia.getHoras() + "-" + materia.getMencao());
                    if (i < semestre.getMaterias().size() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }
    
    private static void exibirMenu() {
        System.out.println("Escolha uma opção:");
        System.out.println("  1. Exibir semestres");
        System.out.println("  2. Adicionar semestre");
        System.out.println("  3. Calcular IRA");
        System.out.println("  4. Sair");
    }

    private static int converteMencao(String mencao) {
        int peso;
        if (mencao.intern() == "SS")
            peso = 5;
        else if (mencao.intern() == "MS")
            peso = 4;
        else if (mencao.intern() == "MM")
            peso = 3;
        else if (mencao.intern() == "MI")
            peso = 2;
        else if (mencao.intern() == "II")
            peso = 1;
        else
            peso = 0;
        return peso;
    }
}