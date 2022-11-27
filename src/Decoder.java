import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Decoder {
    public static ArrayList<String> Tabela = null; /* Estrutura de dados para armazenar os códigos do dicionário */

    public static int comprimeiro_bits = 0; /* O comprimento dos bits permitidos no dicionário */
    public static int max_tamanho_tabela = 0; /* número máximo de códigos que podem ser salvos no dicionário */

    public static void main(String... args) {
        comprimeiro_bits = Integer.parseInt(args[1]);
        max_tamanho_tabela = (int) Math.pow((double) 2, (double) comprimeiro_bits);
        Tabela = new ArrayList<>(max_tamanho_tabela);

        BufferedReader input = null; /*  buffer de entrada */
        File ficheiro = null; /* para abrir o ficheiro */
        FileWriter fw = null; /* para escrever o ficheiro */
        BufferedWriter saida = null; /*  buffer de saída */

        String actualDirectorio = System.getProperty("user.dir") + "/"; /* caminho para o actual diretório*/
        String ficheiroEntrada = args[0]; /* nome do ficheiro de entrada */
        String caminhoEntrada = actualDirectorio + ficheiroEntrada; /* caminho do sistema do ficheiro de entrada */
        String ficheiroSaida = (ficheiroEntrada.split("\\."))[0] + "_descomprimido.txt"; /* nome do ficheiro de saída descomprimido '.txt' */

        String linha = ""; /* A linha que está sendo analisada*/
        String novaLinha = ""; /* a nova linha que será lida */
        String codigo = ""; /* o presente código sendo analisado */
        String string = ""; /* o presente string sendo analisado*/
        String novaString = ""; /* novo string sendo analisado*/

        char temp[] = {}; /* array temporário de códigos do ficheiro de entrada */

        try {
            /* abrir um ficheiro de saída e criar um ficheiro de leitura para o mesmo */
            ficheiro = new File(ficheiroSaida);
            fw = new FileWriter(ficheiro.getAbsoluteFile());

            /* abrir o buffer do ficheiro de entrada e saída*/
            input = new BufferedReader(new InputStreamReader(new FileInputStream(caminhoEntrada), Charset.forName("UTF-16BE")));
            saida = new BufferedWriter(fw);

            initTable(); /* inicializar o dicionário com 256 caracteres da tabela ASCII */

            boolean primeiraVez = true;
            System.out.println("\nDecompressão inicializada...\tTamanho da tabela: " + Tabela.size());
            linha = input.readLine(); /*ler a primeiro linha do ficheiro de entrada */
            while (linha != null) {
                if((novaLinha = input.readLine()) != null){ /*ler a próxima linha dedo ficheiro de entrada */
                    linha += "\n"; /* adicionar uma nova linha no fim da linha somente se esta não for a última linha*/
                }

                temp = linha.toCharArray(); /* linha para array de códigos  */

                for (int i = 0; i < temp.length; i++) {
                    codigo = String.valueOf(temp[i]); /* obter novo código para teste*/

                    if (primeiraVez) { /* avaliar esse bloco somente na primeira vez */
                        primeiraVez = false;

                        string = Tabela.get((int) codigo.charAt(0)); /* obter a string da tabela no índice do código */

                        saida.write(string); /* escrever a primeira string do primeiro código para o buffer de saída*/
                        System.out.print("Dados Decomprimidos:\t" + Tabela.indexOf(string) + "\t" + string);
                        System.out.println("\t\t\tCódigos:\t" + (int) codigo.charAt(0) + "\t" + codigo);
                        continue;
                    }

                    if (Tabela.size() <= (int) codigo.charAt(0)) {
                        novaString = string + string.toCharArray()[0]; /* se o código não é definido na tabela então deve gerar uma nova string da string */
                    } else {
                        novaString = Tabela.get((int) codigo.charAt(0)); /* caso contrário pega a nova string da tabela no índice do código */
                    }

                    saida.write(novaString); /* escreve a nova string pelo código existente para o buffer de saída */
                    System.out.print("Dados Decomprimidos:\t" + Tabela.indexOf(novaString) + "\t" + novaString);

                    if (Tabela.size() <= max_tamanho_tabela) {
                        Tabela.add(string + novaString.toCharArray()[0]); /* adiciona novo 'código' para a nova string se a capacidade máxima do dicionário é atingida*/
                        System.out.print("\t\t\tCódigos:\t" + (int) codigo.charAt(0) + "\t" + codigo);
                        System.out.println("\t\t\tTabela Actualizada:\t" + Tabela.indexOf(string + novaString.toCharArray()[0]) + "\t" + string + novaString.toCharArray()[0]);
                    }
                    string = novaString; /* faça de novo com a nova string */
                }
                linha = novaLinha; /* atrinuir a nova linha como a linha actual para continuar*/
            }
            System.out.println("Decompressão completa...\tTamanho da tabela: " + Tabela.size());
        } catch (IOException e) {
            e.printStackTrace();
        } finally { // O bloco é sempre executado
            if (input != null) {
                try {
                    input.close(); /* fechar o buffer de entrada  */
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (saida != null) {
                try {
                    saida.close(); /* fechar o buffer de saída*/
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* inicializar o dicionário com 256 caracteres da tabela ASCII */
    public static void initTable() {
        System.out.println("Inicializando a tabela...\tTamanho da tabela: " + Tabela.size());

        for (int i = 0; i < 256; i++) {
            Tabela.add(String.valueOf((char) i));
        }

        System.out.println("Inicialização completa..\tTamanho da tabela: " + Tabela.size());
    }

}