import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Playfair {
    String plainTextEdited;
    String keyEdited;
    char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    char[] alphabetEdited;
    char[][] matrix;
    char[][] digrami;

    public Playfair(String plainText, String key) {
        this.keyEdited = formirajKey(key);
        this.alphabetEdited = formirajAlphabet(alphabet, keyEdited);
        this.plainTextEdited = formirajPlainText(plainText);
        this.matrix = formirajMatricu();
        this.digrami = formirajDigrame();

    }

    public char[] formirajAlphabet(char[] alphabet, String key) {
        Set<Character> keySet = new HashSet<>();
        for (char c : key.toCharArray()) {
            keySet.add(Character.toLowerCase(c));
        }
        StringBuilder alphabetEdited = new StringBuilder();
        for (char c : alphabet) {
            // ako se karakter iz alfabeta ne nalazi u Stringu key, dodajemo ga u alphabetEdited :
            if (!keySet.contains(Character.toLowerCase(c))) {
                alphabetEdited.append(c);
            }
        }

        return alphabetEdited.toString().toCharArray();
    }

    public String formirajPlainText(String plainText) {
        String plainTextEdited = plainText.replace(" ", "");
        plainTextEdited = plainTextEdited.replace("j", "i");

        // postoje li 2 uzastopna ista karaktera :
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < plainTextEdited.length(); i++) {
            char currentChar = plainTextEdited.charAt(i);
            result.append(currentChar);

            // ako smo na zadnjem karakteru, preskoci :
            if (i == plainTextEdited.length() - 1) {
                continue;
            }

            // ako je (i+1). karakter jednak i. karakteru, ubaci X :
            char nextChar = plainTextEdited.charAt(i + 1);
            if (currentChar == nextChar) {
                result.append('x');
            }
        }
        plainTextEdited = result.toString();

        // ako je broj slova neparan - dodaj na kraju X :
        if (plainTextEdited.length() % 2 != 0) {
            plainTextEdited += 'x';
        }

        plainTextEdited = plainTextEdited.toLowerCase();

        return plainTextEdited;
    }

    public String formirajKey(String key) {
        String keyEdited = key.replace("j", "i");
        keyEdited = keyEdited.replace(" ", "");

        // izbaci slova, koja se ponavljaju : LinkedHashSet cuva redoslijed karaktera ali istovremeno osigurava da svaki element unutar njega bude jedinstven
        // Kada se karakteri dodaju u LinkedHashSet, bilo koji duplikat će biti ignorisan :

        Set<Character> uniqueChars = new LinkedHashSet<>();
        for (char c : keyEdited.toCharArray()) {
            uniqueChars.add(c);
        }
        StringBuilder result = new StringBuilder();
        for (char c : uniqueChars) {
            result.append(c);
        }
        keyEdited = result.toString();

        keyEdited = keyEdited.toLowerCase();

        return keyEdited;
    }

    public char[][] formirajMatricu() {
        char[][] matrix = new char[5][5];
        int index = 0;
        for (int i = 0; i < keyEdited.length(); i++) {
            char c = keyEdited.charAt(i);
            matrix[i / 5][i % 5] = c;
            index++;
        }
        for (int i = index, j = 0; i < 25; i++, j++) {
            char c = alphabetEdited[j];
            matrix[i / 5][i % 5] = c;
        }

        // ispis matrice :
//        for (char[] row : matrix) {
//            for (char c : row) {
//                System.out.print(c + " ");
//            }
//            System.out.println();
//        }

        return matrix;
    }

    public char[][] formirajDigrame() {
        int row = (plainTextEdited.length()) / 2;

        char[][] digrami = new char[row][2];
        int i = 0;

        for (int j = 0; j < row; j++) {
            digrami[j][0] = plainTextEdited.charAt(i++);
            digrami[j][1] = plainTextEdited.charAt(i++);

        }

//        // ispis digrama za enkripciju :
//        System.out.println("Digrami : ");
//        for (char[] c : digrami) {
//            System.out.println(c);
//        }

        return digrami;
    }

    public int[] pozicijaSlovaUMatrici(char slovo) {
        int[] position = new int[2];
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) {
                if (matrix[i][j] == slovo) {
                    position[0] = i;
                    position[1] = j;
                }
            }

        // ispis pozicije slova u matrici :
        //System.out.println("Pozicija slova : " + slovo + " : " + "[" + position[0] + "][" + position[1] + "]");
        return position;
    }

    public String enkripcija() {
        StringBuilder cipher = new StringBuilder();
        int row = (plainTextEdited.length()) / 2;
        for (int i = 0; i < row; i++) {
            char slovo1 = digrami[i][0];
            char slovo2 = digrami[i][1];
            int[] p1 = pozicijaSlovaUMatrici(slovo1);
            int[] p2 = pozicijaSlovaUMatrici(slovo2);
            String noviDigram = enkripcijaDatogDigrama(p1, p2);
            cipher.append(noviDigram);
        }
     //   System.out.println("Cipher : ");
     //   System.out.println(cipher.toString());
        return cipher.toString();
    }

    public String enkripcijaDatogDigrama(int[] p1, int[] p2) {
        String noviDigram = "";
        int i = p1[0];
        int j = p1[1];
        int k = p2[0];
        int s = p2[1];
        if (i == k) {
            // isti red
            noviDigram = istiRed(i, j, k, s);
        } else if (j == s) {
            // ista kolona
            noviDigram = istaKolona(i, j, k, s);
        } else if (i != k) {
            // razliciti redovi / kolone
            noviDigram = razlicitiRedoviKolone(i, j, k, s);
        }

        return noviDigram;
    }

    public String istiRed(int i, int j, int k, int s) {
        String noviDigram = "";

        if (j == 4) {
            noviDigram += matrix[i][0];
        } else if (j != 4) {
            noviDigram += matrix[i][j + 1];
        }
        if (s == 4) {
            noviDigram += matrix[k][0];
        } else if (s != 4) {
            noviDigram += matrix[k][s + 1];
        }
        return noviDigram;
    }

    public String istaKolona(int i, int j, int k, int s) {
        String noviDigram = "";

        if (i == 4) {
            noviDigram += matrix[0][j];
        } else if (i != 4) {
            noviDigram += matrix[i + 1][j];
        }
        if (k == 4) {
            noviDigram += matrix[0][s];
        } else if (k != 4) {
            noviDigram += matrix[k + 1][s];
        }
        return noviDigram;
    }

    public String razlicitiRedoviKolone(int i, int j, int k, int s) {
        String noviDigram = "";

        noviDigram += matrix[i][s];
        noviDigram += matrix[k][j];

        return noviDigram;
    }

}
