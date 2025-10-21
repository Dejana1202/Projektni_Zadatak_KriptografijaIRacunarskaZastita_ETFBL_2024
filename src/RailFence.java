public class RailFence
{
    public static String encryptRailFence(String plainTextOriginal, int key)
    {
        StringBuilder plainText = new StringBuilder(plainTextOriginal.replace(" ", ""));
        StringBuilder cipherText = new StringBuilder();

        //   System.out.println("Plaintext bez razmaka : ");
        //   System.out.println(plainText);

        //inicijalizacija kolosjeka :

        StringBuilder rails[] = new StringBuilder[key];
        for(int i=0; i<key; i++)
        {
            rails[i] = new StringBuilder();
        }

        int currentRail=0;
        boolean down = false;

        // enkripcija :
        // upis slova u odgovarajuce kolosjeke

        for(int i=0; i<plainText.length(); i++)
        {
            rails[currentRail].append(plainText.charAt(i));
            if (currentRail == 0 || currentRail==key-1)
            {
                down= !down;
            }
            if (down){
                currentRail++;
            }
            else {
                currentRail--;
            }
        }

        // spajanje kolosjeka od 0. do (key-1). u jedan String

        for (int i=0; i<key; i++)
        {
            cipherText.append(rails[i]);
        }

        return cipherText.toString();
    }
}
