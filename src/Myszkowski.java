import java.util.*;

public class Myszkowski
{
    String plainTextEdited;
    String key;
    char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j' , 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    char[] alphabetEdited;
    char[][] matrix;
    int [] numeracijaAlfabeta;
    char[] prviRed;
    public Myszkowski(String plainText, String key)
    {
        this.key=key.toLowerCase();
        this.alphabetEdited=formirajAlphabet(key);
        this.plainTextEdited=formirajPlainText(plainText);
        this.matrix=formirajMatricu();
        this.prviRed=formirajPrviRed();

    }
    public String formirajPlainText(String plainText)
    {
        String plainTextEdited = plainText.replace(" ", "");
        plainTextEdited=plainTextEdited.toLowerCase();
        return plainTextEdited;
    }
    public char [] formirajAlphabet(String key)
    {
        // konvertujemo key u keySet :
        Set<Character> keySet = new HashSet<>();
        for (char c : key.toCharArray()) {
            keySet.add(Character.toLowerCase(c));
        }
        // izdvajamo slova iz alfabeta ako se nalaze u key :
        List<Character> editedAlphabetList = new ArrayList<>();
        for (char c : alphabet) {
            if (keySet.contains(c)) {
                editedAlphabetList.add(c);
            }
        }
        // prebacujemo ih iz liste u char[] :
        char[] alphabetEdited = new char[editedAlphabetList.size()];
        for (int i = 0; i < editedAlphabetList.size(); i++)
        {
            alphabetEdited[i] = editedAlphabetList.get(i);
        }
        // ispis editovanog alfabeta :
      //  System.out.println(alphabetEdited);
        return alphabetEdited;
    }
    public int indeksDatogSlova(char c)
    {
        int indeks = 0;
        for(int i=0; i<alphabetEdited.length;i++)
        {
            if(c==alphabetEdited[i])
            {
               // System.out.println("Indeks slova " + c + " je "+ i);
                indeks=(i+1);
            }
        }
        return indeks;
    }
    public char[] formirajPrviRed()
    {
        StringBuilder prviRed = new StringBuilder();
        for (char c : key.toCharArray())
        {
            int pozicija = indeksDatogSlova(c);
            prviRed.append(pozicija);
        }
        // ispis niza, koji ce biti prvi red matrice :
      //  System.out.println(prviRed);

        return prviRed.toString().toCharArray();
    }
    public int brojRedova()
    {
        int keyLen=key.length();
        int brojRedova;

        if((plainTextEdited.length()) % keyLen == 0)
        {
            brojRedova = (plainTextEdited.length())/keyLen;
            brojRedova+=1;
        }
        else
        {
            brojRedova=(plainTextEdited.length())/keyLen+1;
            brojRedova+=1;
            // +1 dodatno zbog prvog reda sa brojevima kljuca
        }
        return brojRedova;
    }
    public char[][] formirajMatricu()
    {
        int keyLen=key.length();
        int brojKolona=keyLen;

        int brojRedova=brojRedova();

        char[][] matrix = new char[brojRedova][brojKolona];
        for (int j=0; j<brojKolona; j++)
        {
            matrix[0][j] = formirajPrviRed()[j];
        }
        for (int i=1; i<brojRedova; i++)
            for (int j=0; j<brojKolona; j++)
        {
            int index = (i - 1) * brojKolona + j;
            if (index >= plainTextEdited.length()) {
                matrix[i][j] = 0;
            }
                else
                {
                    matrix[i][j] = plainTextEdited.charAt(index);
                }
        }
        // ispis matrice :
//        for (char[] row : matrix) {
//            for (char c : row) {
//                System.out.print(c);
//            }
//            System.out.println();
//        }
        return matrix;
    }
    public int brojPonavljanjaSlovaIzKljuca(char c)
    {
        int brojPonavljanja=0;
        for (int i=0; i<key.length(); i++)
        {
            if(prviRed[i]==c)
            {
                brojPonavljanja++;
            }

        }
      //  System.out.println("Broj ponavljanja broja " + c + " je :" + brojPonavljanja);
        return brojPonavljanja;
    }
    public String enkriptujKolonu(int i)
    {
        StringBuilder cipherKolone=new StringBuilder();
        for (int j=1; j<brojRedova(); j++)
        {
            cipherKolone.append(matrix[j][i]);
        }

        return cipherKolone.toString();
    }
    public String enkriptujKoloneSaIstimBrojem(int brojIzKljuca, int brojPonavljanja)
    {
        char c = (char) ('0'+brojIzKljuca);
       // System.out.println("CHAR " + c);
        StringBuilder cipher = new StringBuilder();

      //  System.out.println("Broj iz kljuca : " + brojIzKljuca);


        ArrayList<Integer> indeksi = new ArrayList<Integer>(brojPonavljanja);
        for (int i=0; i<key.length(); i++)
        {
          //  System.out.println("Prvi red [i] " + prviRed[i]);
            if(prviRed[i]==c)
            {
              //  System.out.println("Nasao je");
                indeksi.add(i);
            }
        }

       // System.out.println("Indeksi broja : " + brojIzKljuca + " su : " + indeksi);

        for (int i=1; i<brojRedova(); i++)
            for (int j : indeksi)
            {
                cipher.append(matrix[i][j]);
            }


        return cipher.toString();
    }
    public String enkripcija()
    {
        ArrayList<Integer> dekriptovaniBrojevi = new ArrayList<Integer>();

        StringBuilder cipher = new StringBuilder();
        for (int i=1; i<key.length(); i++)
        {

            {
                for (int prviRed=0; prviRed<key.length();prviRed++)
                {
                    {
                        //   System.out.println("element matrice : "+matrix[0][prviRed]);

                        int br = Character.getNumericValue(matrix[0][prviRed]);

                        //  System.out.println("c : " + br);
                        if ((br==i)&&(!dekriptovaniBrojevi.contains(i)))
                        {
                          //  System.out.println("Usao u uslov");
                            int brojPonavljanja=brojPonavljanjaSlovaIzKljuca(matrix[0][prviRed]);
                            if(brojPonavljanja==1)
                            {
                                cipher.append(enkriptujKolonu(prviRed));
                                // System.out.println("en : " + cipher.toString());
                            }
                            else if(brojPonavljanja!=1)
                            {
                                // pozovi enkriptujKoloneSaIstimBrojem , proslijedi br = 4 i broj ponavljanja

                             //   System.out.println("CHAR BR treba biti 4 : " + br);
                                cipher.append(enkriptujKoloneSaIstimBrojem(br, brojPonavljanja));
                             //   System.out.println("Slovo iz kljuca se ponavlja");
                            }
                            dekriptovaniBrojevi.add(i);
                        }

                    }

                }


            }
          //  System.out.println(dekriptovaniBrojevi);
        }

        cipher= new StringBuilder(izbaciStoNijeAlfabet(cipher.toString()));

        return cipher.toString();
    }

    public String izbaciStoNijeAlfabet(String s)
    {
        StringBuilder rezultat = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLetter(c) && Arrays.binarySearch(alphabet, Character.toLowerCase(c)) >= 0) {
                rezultat.append(c);
            }
        }
        return rezultat.toString();
    }
}
