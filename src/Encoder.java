import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Encoder {
    public static ArrayList<String> Table = null;

    public static int bit_length = 0;
    public static int max_table_size = 0;

    public static void main(String... args) {
        bit_length = Integer.parseInt(args[1]);
        max_table_size = (int) Math.pow((double) 2, (double) bit_length);
        Table = new ArrayList<>(max_table_size);

        BufferedReader input = null; /* buffer de entrada */
        BufferedWriter output = null; /* buffer de saída */

        String currentDir = System.getProperty("user.dir") + "/"; /* caminho para o diretório de trabalho atual */
        String inputFile = args[0]; /* nome do arquivo de entrada */
        String inputPath = currentDir + inputFile; /* caminho do sistema do arquivo de entrada */
        String outputFile = (inputFile.split("\\."))[0] + ".lzw"; /* caminho do sistema do arquivo de saída com extensão '.lzw'*/

        String line = ""; /* linha atual sendo lida */
        String symbol = ""; /* csímbolo atual sendo verificado */
        String str = ""; /* símbolo atual sendo verificado */

        char temp[] = {}; /* matriz temporária de símbolos do arquivo de entrada */

        try {
            /* abre os arquivos de entrada e saída como buffers*/
            input = new BufferedReader(new FileReader(inputPath));
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-16BE")); /* 16-bit Unicode Transformation Format with Big Endianness */

            initTable(); /* inicializa o dicionário com os 256 códigos de caracteres ASCII existentes */

            System.out.println("\nCompressão inicializada...\tTamanho da tabela: " + Table.size());
            while ((line = input.readLine()) != null) { /* lê a entrada linha por linha */
                line += "\n"; /* adiciona um caractere de nova linha no final de 'line' */
                temp = line.toCharArray(); /* linha para array de 'símbolos' */

                for (int i = 0; i < temp.length; i++) {
                    symbol = String.valueOf(temp[i]); /* obtem novo 'símbolo' para teste */

                    if (Table.contains(str + symbol)) {
                        str += symbol; /* concatenar se 'código' para 'STRING + SÍMBOLO' existir no dicionário (algoritmo ganancioso) */
                    } else {
                        output.write(Table.indexOf(str)); /* senão escreva o 'código' para a 'string' existente no buffer de saída */
                        System.out.print("Dados comprimidos:\t" + Table.indexOf(str) + "\t" + str);
                        if (Table.size() <= max_table_size) {
                            Table.add(str + symbol); /* adicionar novo 'código' para a nova string ('STRING + SYMBOL') se a capacidade máxima do dicionário não for atingida */
                            System.out.println("\t\t\tactualizacao da tabela:\t" + Table.indexOf(str+symbol) + "\t" + str+symbol);
                        }
                        str = symbol; /* recomeçar com o novo 'símbolo' */
                    }
                }
            }
            if(str.length() > 1){
                str = str.substring(0, str.length()-1); /* remova o caractere 'line feed' da última linha (já que um extra '\n' foi adicionado anteriormente), se o comprimento de 'string' for maior que 1 */
                output.write(Table.indexOf(str)); /* escreva o 'código' para a última 'string' para o buffer de saídar */
                System.out.println("Ultimo dado comprimido:\t" + Table.indexOf(str) + "\t" + str);
            }

            System.out.println("Compressão completa...\tTamanho da tabela: " + Table.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally { // o bloco finalmente é sempre executado; o código de limpeza é incluído na maioria dos casos.
            if (input != null) {
                try {
                    input.close(); /* fechar o buffer de entrada */
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (output != null) {
                try {
                    output.close(); /* fechar o buffer de saída */
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* inicializar o dicionário com os 256 códigos de caracteres ASCII existentes */
    public static void initTable() {
        System.out.println("Inicializado a Tabela...\tTamanho da tabela: " + Table.size());

        for (int i = 0; i < 256; i++) {
            Table.add(String.valueOf((char) i));

        }

        System.out.println("Inicialização completa...\tTamanho da tabela: " + Table.size());
    }

}