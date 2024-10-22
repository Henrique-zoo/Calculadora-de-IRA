import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import models.Materia;
import models.Semestre;

public class CalculadorDeIRA {
    private static final String FILE_PATH = "semestres.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Semestre> semestres = lerArquivo();

        boolean executando = true;
        while (executando) {
            exibirMenu(semestres);
            int acao = scanner.nextInt();
            scanner.nextLine();
            if (semestres.isEmpty() && acao > 2)
                acao = -1;
            
            switch (acao) {
                case 1:
                    if (semestres.isEmpty())
                        adicionarSemestre(semestres, scanner);
                    else
                        exibirSemestre(semestres);
                    break;
                case 2:
                    if (semestres.isEmpty())
                        executando = false;
                    else
                        adicionarSemestre(semestres, scanner);
                    break;
                case 3:
                    float ira = calculaIRA(semestres);
                    float mp = calculaMP(semestres);     
                    System.out.printf("IRA: %.4f\nMP: %.4f\n", ira, mp);
                    break;
                case 4:
                    executando = false;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }
        }
        scanner.close();
    }

    private static void exibirMenu(List<Semestre> semestres) {
        if (semestres.isEmpty())
            System.out.printf("%s\n%s\n%s\n",
            "Escolha uma opção:",
            "\t1. Adicionar semestre",
            "\t2. Sair");
        else
            System.out.printf("%s\n%s\n%s\n%s\n%s\n",
            "Escolha uma opção:",
            "\t1. Exibir semestres",
            "\t2. Adicionar semestre",
            "\t3. Calcular índices acadêmicos",
            "\t4. Sair");
    }

    private static List<Semestre> lerArquivo() {
        ArrayList<String> linhas = new ArrayList<>();
        List<Semestre> semestres = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String linhaTemp;
            StringBuilder line = new StringBuilder();
            while ((linhaTemp = reader.readLine()) != null) {
                for (char ch : linhaTemp.toCharArray()) {
                    if (ch == ';') {
                        linhas.add(line.toString());
                        line.setLength(0);
                    } else {
                        line.append(ch);
                    }
                }
            }
            linhas.add(line.toString());

            for (String linha : linhas) {
                List<Materia> materias = new ArrayList<>();
                String[] partes = linha.split("\\.");
                int numeroDoSemestre = Integer.parseInt(partes[0]);
                int qtdDeMaterias = Integer.parseInt(partes[1]);
                String[] materiasString = partes[2].split(",");
                for (String texto : materiasString) {
                    String[] materiaString = texto.split("-");
                    String nome = materiaString[0];
                    int horas = Integer.parseInt(materiaString[1]);
                    String mencao = materiaString[2];
                    boolean eletiva = Boolean.parseBoolean(materiaString[3]);
                    Materia materia = new Materia(nome, horas, mencao, eletiva);
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
            System.out.printf("\nVocê não cadastrou nenhum semestre aqui!");
        else
            for (Semestre semestre : semestres) {
                System.out.println("\n" + semestre.getNumero() + "º semestre (" + semestre.getQtdDeMaterias() + " matérias):");
                System.out.println("-".repeat(85));
                System.out.printf("%-40s%s%s%s\n",
                    "Nome",
                    centralizar("Horas",10),
                    centralizar("Menção", 10),
                    centralizar("Obrigatória/Optativa", 25)
                );
                System.out.println("-".repeat(85));
                for (Materia materia : semestre.getMaterias()) {
                    System.out.printf("%-40s%s%s%s\n",
                        materia.getNome(),
                        centralizar(String.valueOf(materia.getHoras()), 10),
                        centralizar(materia.getMencao(), 10),
                        centralizar(materia.isEletiva() ? "Nao" : "Sim", 25)
                    );
                }
                System.out.println("-".repeat(85));
            }
    }

    private static String centralizar(String texto, int larguraCampo) {
        if (texto.length() >= larguraCampo) {
            return texto.substring(0, larguraCampo);
        }

        int espacosEsquerda = (larguraCampo - texto.length()) / 2;
        int espacosDireita = larguraCampo - texto.length() - espacosEsquerda;

        return " ".repeat(espacosEsquerda) + texto + " ".repeat(espacosDireita);
    }

    private static float calculaIRA(List<Semestre> semestres) {
        float divisor = 0, dividendo = 0;
        for (Semestre semestre : semestres) {
            int nDoSemestre = semestre.getNumero();
            if (nDoSemestre > 6)
                nDoSemestre = 6;

            for (Materia materia : semestre.getMaterias()) {
                float creditos = materia.getHoras() / 15;
                int pesoDaMencao = converteMencao(materia.getMencao());
                dividendo += pesoDaMencao * creditos * nDoSemestre;
                divisor += creditos * nDoSemestre;
            }
        }
        return dividendo/divisor;
    }

    private static float calculaMP(List<Semestre> semestres) {
        float divisor = 0, dividendo = 0;
        for (Semestre semestre : semestres) {
            for (Materia materia : semestre.getMaterias()) {
                if (!materia.isEletiva()) {
                    float creditos = materia.getHoras() / 15;
                    dividendo += creditos * converteMencao(materia.getMencao());
                    divisor += creditos;
                }
            }
        }
        return dividendo/divisor;
        
    }

    private static List<Semestre> adicionarSemestre(List<Semestre> semestres, Scanner scanner) {
        List<Materia> materias = new ArrayList<>();
        
        System.out.println("Esse semestre conta como qual pelo SIGAA? (matérias de verão contam como do semestre anterior)");
        int numero = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Quantas matérias você fez nesse semestre?");
        int qtdDeMaterias = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < qtdDeMaterias; i++) {
            System.out.println("Qual a matéria?");
            String nome = scanner.nextLine();
            System.out.println("Quantas Horas " + nome + " tem?");
            int horas = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Qual foi a sua menção em " + nome + "?");
            String mencao = scanner.nextLine();
            System.out.println(nome + "é uma matéria eletiva?");
            boolean obrigatoria = scanner.nextBoolean();
            Materia materia = new Materia(nome, horas, mencao, obrigatoria);
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
                writer.write(semestre.getNumero() + '.' + semestre.getQtdDeMaterias() + ".\n");
                int i = 0;
                for (Materia materia : semestre.getMaterias()) {
                    i++;
                    writer.write(materia.getNome() + '-' + materia.getHoras() + '-' + materia.getMencao() + '-' + materia.isEletiva());
                    if (i < semestre.getQtdDeMaterias())
                        writer.write(",\n");
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }

    private static int converteMencao(String mencao) {
        Map<String, Integer> mencaoPesoMap = new HashMap<>();
        mencaoPesoMap.put("SS", 5);
        mencaoPesoMap.put("MS", 4);
        mencaoPesoMap.put("MM", 3);
        mencaoPesoMap.put("MI", 2);
        mencaoPesoMap.put("II", 1);
        mencaoPesoMap.put("SR", 0);
        return mencaoPesoMap.getOrDefault(mencao, 0);
    }
}