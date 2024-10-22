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
            int op = scanner.nextInt();
            scanner.nextLine();

            if (semestres.isEmpty() && op > 2)
                op = -1;
            
            switch (op) {
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
        if (semestres.isEmpty()) {
            System.out.printf("%s\n%s\n%s\n",
            "Escolha uma opção:",
            "\t1. Adicionar semestre",
            "\t2. Sair");
        } else {
            System.out.printf("%s\n%s\n%s\n%s\n%s\n",
            "Escolha uma opção:",
            "\t1. Exibir semestres",
            "\t2. Adicionar semestre",
            "\t3. Calcular índices acadêmicos",
            "\t4. Sair");
        }
    }

    private static List<Semestre> lerArquivo() {
        List<String> linhas = new ArrayList<>();
        List<Semestre> semestres = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String linha;
            StringBuilder builder = new StringBuilder();
            while ((linha = reader.readLine()) != null) {
                for (char ch : linha.toCharArray()) {
                    if (ch == ';') {
                        linhas.add(builder.toString());
                        builder.setLength(0);
                    } else {
                        builder.append(ch);
                    }
                }
            }
            if (builder.length() > 0) {
                linhas.add(builder.toString());
            } else {
                return semestres;
            }

            for (String conteudo : linhas) {
                List<Materia> materias = new ArrayList<>();
                String[] partes = conteudo.split("\\.");
                int numeroSemestre = Integer.parseInt(partes[0]);
                int qtdMaterias = Integer.parseInt(partes[1]);
                int horasTotais = Integer.parseInt(partes[2]);
                String[] materiasInfo = partes[3].split(",");

                for (String texto : materiasInfo) {
                    String[] materiaInfo = texto.split("-");
                    String nome = materiaInfo[0];
                    int horas = Integer.parseInt(materiaInfo[1]);
                    String mencao = materiaInfo[2];
                    boolean eletiva = Boolean.parseBoolean(materiaInfo[3]);
                    Materia materia = new Materia(nome, horas, mencao, eletiva);
                    materias.add(materia);
                }

                Semestre semestre = new Semestre(numeroSemestre, qtdMaterias, horasTotais, materias);
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
                System.out.printf("%dº semestre - %d matérias (%d horas):\n", semestre.getNumero(), semestre.getQtdDeMaterias(), semestre.getHoras());
                System.out.println("-".repeat(85));
                System.out.printf("%-40s%s%s%s\n",
                    "Nome",
                    centralizar("Horas", 10),
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
        if (texto.length() >= larguraCampo)
            return texto.substring(0, larguraCampo);

        int espacosEsquerda = (larguraCampo - texto.length()) / 2;
        int espacosDireita = larguraCampo - texto.length() - espacosEsquerda;

        return " ".repeat(espacosEsquerda) + texto + " ".repeat(espacosDireita);
    }

    private static float calculaIRA(List<Semestre> semestres) {
        float divisor = 0, dividendo = 0;
        for (Semestre semestre : semestres) {
            int se = Math.min(semestre.getNumero(), 6);

            for (Materia materia : semestre.getMaterias()) {
                float cr = materia.getHoras() / 15;
                int eqMencao = converterMencao(materia.getMencao());
                dividendo += eqMencao * cr * se;
                divisor += cr * se;
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
                    dividendo += creditos * converterMencao(materia.getMencao());
                    divisor += creditos;
                }
            }
        }
        return dividendo/divisor;
        
    }

    private static List<Semestre> adicionarSemestre(List<Semestre> semestres, Scanner scanner) {
        List<Materia> materias = new ArrayList<>();
        int horasTotais = 0;
        System.out.println("Esse semestre conta como qual pelo SIGAA? (matérias de verão contam como do semestre anterior)");
        int numero = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Quantas matérias você fez nesse semestre?");
        int qtdMaterias = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < qtdMaterias; i++) {
            System.out.println("Qual o nome da matéria?");
            String nome = scanner.nextLine();

            System.out.println("Quantas horas a matéria " + nome + " possui?");
            int horas = scanner.nextInt();
            horasTotais += horas;
            scanner.nextLine();

            System.out.println("Qual a menção que você recebeu em " + nome + "?");
            String mencao = scanner.nextLine();

            System.out.println(nome + " é uma matéria eletiva? (true para eletiva, false para obrigatória)");
            boolean eletiva = scanner.nextBoolean();
            scanner.nextLine();

            Materia materia = new Materia(nome, horas, mencao, eletiva);
            materias.add(materia);
        }
        Semestre semestre = new Semestre(numero, qtdMaterias, horasTotais, materias);
        semestres.add(semestre);
        salvarSemestresNoArquivo(semestres);
        return semestres;
    }
    
    private static void salvarSemestresNoArquivo(List<Semestre> semestres) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Semestre semestre : semestres) {
                writer.write(semestre.getNumero() + '.' + semestre.getQtdDeMaterias() + '.' + semestre.getHoras() + ".\n");
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

    private static int converterMencao(String mencao) {
        Map<String, Integer> mencaoPesoMap = new HashMap<>();
        mencaoPesoMap.put("SS", 5);
        mencaoPesoMap.put("MS", 4);
        mencaoPesoMap.put("MM", 3);
        mencaoPesoMap.put("MI", 2);
        mencaoPesoMap.put("II", 1);
        mencaoPesoMap.put("SR", 0);
        return mencaoPesoMap.getOrDefault(mencao, 0);
    }

    /*private static List<Semestre> atualizarSemestres(List<Semestre> semestres, Scanner scanner) {
        System.out.printf("Qual semestre você quer atualizar?\n");
        int numeroSemestre = scanner.nextInt();
        scanner.nextLine();
        Semestre semestreAtualizado;

        for (Semestre semestre : semestres) {
            if (semestre.getNumero() == numeroSemestre) {
                semestreAtualizado = semestre;
            }
        }
        System.out.printf("Qual atributo do %dº semestre você quer atualizar?\n\t%s\n\t%s\n",
        numeroSemestre,
        "1. Quantidade de matérias",
        "2. Número do Semestre");
        int opcao = scanner.nextInt();
        scanner.nextLine();
        switch (opcao) {
            case 1:
                System.out.printf("Quantas matérias você fez no %dº semestre?\n", numeroSemestre);
                int qtdMaterias = scanner.nextInt();
                scanner.nextLine();
                
                semestreAtualizado.setQtdDeMaterias(qtdMaterias);
                break;
        
            default:
                break;
        }


        return semestres;
    }*/
}