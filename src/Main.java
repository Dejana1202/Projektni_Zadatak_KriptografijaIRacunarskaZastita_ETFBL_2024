import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;


public class Main {

    int brojRegistrovanihKorisnika=0;
    public static String putanjaDoBrojaRegistrovanihKorisnika="brojRegistrovanihKorisnika.txt";
    public static File brojRegistrovanihKorisnikaFile = new File(putanjaDoBrojaRegistrovanihKorisnika);

    public static void main(String[] args)
    {
        Main m = new Main();
        String izbor = "";
        Scanner scan = new Scanner(System.in);
        try {
            while (!"3".equals(izbor)) {
                System.out.println();
                System.out.println("======================================================================");
                System.out.println("Izaberite opciju : ");
                System.out.println("[1] : Registracija\n[2] : Prijava\n[3] : Kraj");
                izbor = scan.nextLine();
                switch (izbor) {
                    case "1":
                    {
                        System.out.println("Registracija : ");
                        m.registracija();
                    }
                        break;
                    case "2":
                    {
                        System.out.println("Prijava");
                        m.prijava();
                    }
                        break;
                    case "3":
                        break;
                    default:
                        System.out.println("Nevažeća opcija. Izlaz iz programa.");
                        izbor = "3";
                        break;
                }
            }
            //=================================================================================================
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
            finally {
                scan.close();
            }
    }

    public void registracija() throws Exception
    {
        String korisnickoIme;
        String lozinka;

        BufferedReader bf = new BufferedReader(new FileReader(brojRegistrovanihKorisnikaFile));
        String brojRegistrovanihKorisnikaString = bf.readLine();
        int brojRegistrovanihKorisnika = Integer.parseInt(brojRegistrovanihKorisnikaString);
        bf.close();
        int br=brojRegistrovanihKorisnika+1;

        Scanner scan = new Scanner(System.in);

        System.out.println("Ime :");
        korisnickoIme = scan.nextLine();
        System.out.println("Lozinka :");
        lozinka = scan.nextLine();

        // pozvati fju generateHash(lozinka)
        String hesiranaLozinka = generisiHash(lozinka);

        //napraviti par RSA kljuceva za korisnika

        String putanjaDoKljuca=generisiParRSA(brojRegistrovanihKorisnika);
        System.out.println("Putanja do kljuca : " + putanjaDoKljuca);
        //********************************************************************************************************


        // zahtjev za kor. sertifikat
        // potpisivanje kor. sert. od strane CA


        // AKO se uspjesno kreira cert -------------> smjestiti korisnika + njegov hash u datoteku !
        //                  Kod ispod smjestiti u IF u tom slucaju.
        String imeDatoteke="Korisnici/korisnik"+(brojRegistrovanihKorisnika+1)+".txt";
        File korisnikFile=new File(imeDatoteke);
        FileWriter fileWriter=new FileWriter(korisnikFile);
        fileWriter.write(korisnickoIme+"\n");
        fileWriter.write(hesiranaLozinka+"\n");
        fileWriter.close();
    }

    String generisiHash(String lozinka) throws Exception
    {
        String hash="";
        String komanda = "openssl passwd -5 -salt 1122334455667788 " + lozinka;
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", komanda);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        hash=bufferedReader.readLine();
        bufferedReader.close();

        return hash;
    }
    String generisiParRSA(int brojRegistrovanihKorisnika) throws Exception
    {
        System.out.println("Unesi kljuc za enkripciju para RSA :");
        Scanner scanner=new Scanner(System.in);
        String kljuc=scanner.nextLine();

        String komanda = "openssl genrsa -out priprema/private/private" + (brojRegistrovanihKorisnika+1) + ".key -aes128 -passout pass:"+ kljuc +" 2048";

        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", komanda);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        process.waitFor();

        String putanja = "priprema/private/private" + (brojRegistrovanihKorisnika+1) + ".key";
        return putanja;
    }
    void generisiKorisnickiZahtjev(String korisnickoIme, int brojRegistrovanihKorisnika) {
        int br=brojRegistrovanihKorisnika+1;
        try {
            Scanner scan = new Scanner(System.in);
            System.out.println("Drzava(dvoslovna oznaka)[BA]:");
            String drzava = scan.nextLine();
            System.out.println("Savezna drzava, entitet, provincija[RS]:");
            String entitet = scan.nextLine();
            System.out.println("Grad[Banjaluka]:");
            String grad = scan.nextLine();
            System.out.println("Naziv organizacije[Elektrotehnicki fakultet]:");
            String nazivOrganizacije = scan.nextLine();
            System.out.println("Naziv organizacione jedinice[ETF]:");
            String nazivOrganizacioneJedinice = scan.nextLine();
            System.out.println("Email adresa[]:");
            String email = scan.nextLine();
            System.out.println("Unesi kljuc za RSA :");
            Scanner scanner=new Scanner(System.in);
            String kljucRSA=scanner.nextLine();
            System.out.println("Unesi kljuc za zahtjev :");
            String kljucZaZahtjev=scanner.nextLine();

            String opensslCommand = String.format(
                    "openssl req -new -out priprema/requests/req%d.csr -config priprema/openssl.cnf -key priprema/private/private%d.key -passin pass:"+kljucRSA+" -passout pass:"+kljucZaZahtjev+" -subj \"/C=%s/ST=%s/L=%s/O=%s/OU=%s/CN=%s/emailAddress=%s\"",
                    br, br, drzava, entitet, grad, nazivOrganizacije, nazivOrganizacioneJedinice, korisnickoIme, email);
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", opensslCommand);
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void potpisiSertifikat(int brojRegistrovanihKorisnika) throws Exception
    {
        int br=brojRegistrovanihKorisnika+1;

        System.out.println("Potpisivanje : ");
        System.out.println("Unesite kljuc za RSA : ");
        Scanner scanner = new Scanner(System.in);
        String kljuc = scanner.nextLine();

        String komanda = "openssl ca -in priprema/requests/req" + br + ".csr -out priprema/certs/cert" + br + ".crt " +
                "-config priprema/openssl.cnf -keyfile priprema/private/private4096.key -cert priprema/rootca.pem -passin pass:"+kljuc;
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", komanda);
        pb.redirectErrorStream(true);
        Process process=pb.start();

        try (OutputStream ops = process.getOutputStream()) {
            ops.write(("y\n").getBytes());
            ops.flush();
            ops.write(("y\n").getBytes());
            ops.flush();
        }

        int kraj=process.waitFor();
        if(kraj==0)
        {
            System.out.println("Sertifikat je potpisan.");
            try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(brojRegistrovanihKorisnikaFile)))) {
                pw.println(br);
            }
        }
    }
    public void prijava()
    {
        boolean verifikovanSertifikat=false;
        boolean validanNalog=false;
        String korisnickoIme;
        String lozinka;
        String hashLozinke;
        int redniBroj;
        String privatniKljucKorisnika="";
        String sert="";
        Scanner scanner=new Scanner(System.in);
        System.out.println("Unesi redni broj sertifikata : ");
        String unosBroja = scanner.nextLine();

        redniBroj=Integer.parseInt(unosBroja);
        sert="priprema/certs/cert"+redniBroj+".crt";
        try {
            verifikovanSertifikat=verifikacijaSertifikata(sert);
         //   System.out.println("Da li je sertifikat verifikovan : " + verifikovanSertifikat);
            if(verifikovanSertifikat==true)
            {
                System.out.println("Unesi korisnicko ime : ");
                korisnickoIme=scanner.nextLine();
                System.out.println("Unesi lozinku : ");
                lozinka=scanner.nextLine();
                hashLozinke=generisiHash(lozinka);
                validanNalog=validacijaNaloga(korisnickoIme, hashLozinke, redniBroj);
                if (validanNalog==true)
                {
                    privatniKljucKorisnika="private/private"+redniBroj+".key";
                    System.out.println("Dobro dosli u aplikaciju !");

                    // provjera neovlastene izmjene fajla :

                    neovlastenaIzmjena(redniBroj);


                    System.out.println("Zapocni simulaciju : ");

                    while (true) {
                        boolean odjava = simulacija(redniBroj);
                        if (odjava) break;
                    }


                }
                else if(validanNalog==false)
                {
                    System.out.println("Nalog nije validan. Neuspjesna prijava.");
                }
           //     System.out.println("Da li je nalog validan : " + validanNalog);
            }
            else if (verifikovanSertifikat==false)
            {
                System.out.println("Sertifikat nije verifikovan. Neuspjesna prijava.");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    boolean verifikacijaSertifikata(String sert) throws Exception
    {
        String komanda = "openssl verify -CAfile priprema/rootca.pem "+sert;
       // System.out.println("Komanda verify : "+komanda);
      //  System.out.println("Putanja do sertifikata : " + sert);
        ProcessBuilder pb= new ProcessBuilder("bash","-c", komanda);
        pb.redirectErrorStream(true);
        Process process=pb.start();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String poruka;
        while ((poruka = bufferedReader.readLine()) != null) {
            if (poruka.contains(": OK"))
            {
                return true;
            }
        }
        bufferedReader.close();
        return false;
    }
    boolean validacijaNaloga(String korisnickoIme, String hashLozinke, int redniBroj) throws Exception
    {
        String putanja = "Korisnici/korisnik" + redniBroj + ".txt";
        try (BufferedReader br = new BufferedReader(new FileReader(putanja))) {
            String imeIzDatoteke = br.readLine();
            String hashIzDatoteke = br.readLine();

            return (korisnickoIme.equals(imeIzDatoteke) && hashLozinke.equals(hashIzDatoteke));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    boolean simulacija(int redniBroj) throws Exception
    {
        File datoteka = new File("Simulacije/korisnik" + redniBroj + ".txt");
        if (!datoteka.exists()) {
            datoteka.createNewFile();
        }
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(datoteka, true)));


        System.out.println("Izaberi algoritam : ");
        System.out.println("[r] : Rail fence\n[m] : Myszkowski\n[p] : Playfair\nPregled istorije simulacija : [istorija]\nOdjava : [odjava]");
        Scanner scanner=new Scanner(System.in);
        String opcija = scanner.nextLine();

        if ("odjava".equalsIgnoreCase(opcija)) {
            System.out.println("Program je završen.");
            return true; // Vraća true ako je opcija "odjava"
        }
        else if("istorija".equals(opcija))
        {
            BufferedReader br = new BufferedReader(new FileReader(datoteka));
            String linija1, linija2;
            while ((linija1 = br.readLine()) != null && (linija2 = br.readLine()) != null)
            {
                String cipher = linija1 + "\n" + linija2;
              //  System.out.println("sadrzaj : " +cipher);

                System.out.println("Dekripcija : ");
                System.out.println();
                String decryptedText = dekripcijaSadrzaja(cipher);
             //   System.out.println(dekripcijaSadrzaja(cipher));

            }
            br.close();
            Files.write(Paths.get("tmpDecrypted.txt"), new byte[0]);

        }

        String plainText;

        if("r".equals(opcija))
        {
            while (true) {
                System.out.println("Unesite tekst do 100 karaktera : ");
                plainText = scanner.nextLine();
                if (plainText.length() <= 100) {
                    break;
                } else {
                    System.out.println("Tekst je predugačak. Molimo unesite tekst sa manje od 100 karaktera.");
                }
            }
            System.out.println("Unesite cjelobrojnu vrijednost kljuca : ");
            int key=scanner.nextInt();
            System.out.println("Rail fence simulacija : ");
            String cipherText=RailFence.encryptRailFence(plainText,key);
            System.out.println("Enkriptovan tekst : ");
            System.out.println(cipherText);
            String formatiranSadrzaj=plainText+"|Rail fence|"+key+"|"+cipherText;
            System.out.println();
            //System.out.println(formatiranSadrzaj);
            String enkriptovanSadrzaj=enkripcijaSadrzaja(formatiranSadrzaj);

            //upis enkriptovanSadrzaj u Simulacije/korisnikN.txt :


            pw.println(enkriptovanSadrzaj);
            pw.close();
            Files.write(Paths.get("tmp.txt"), new byte[0]);


        }
        else if("m".equals(opcija))
        {
            while (true) {
                System.out.println("Unesite tekst do 100 karaktera : ");
                plainText = scanner.nextLine();
                if (plainText.length() <= 100) {
                    break;
                } else {
                    System.out.println("Tekst je predugačak. Molimo unesite tekst sa manje od 100 karaktera.");
                }
            }
            System.out.println("Unesite kljuc (tekst) : ");
            String key2 = scanner.nextLine();
            System.out.println("Myszkowski simulacija : ");
            Myszkowski myszkowski = new Myszkowski(plainText,key2);
            myszkowski.formirajPrviRed();
            String cipherMyszkowski = myszkowski.enkripcija();
            System.out.println("Enkriptovan tekst : ");
            System.out.println(cipherMyszkowski);
            String formatiranSadrzaj=plainText+"|Myszkowski|"+key2+"|"+cipherMyszkowski;
            String enkriptovanSadrzaj=enkripcijaSadrzaja(formatiranSadrzaj);

            System.out.println("Formatiran sadrzaj :");
            System.out.println(formatiranSadrzaj);
            System.out.println("Enkriptovan sadrzaj, koji ide direktno u datoteku simulacije/korisnik1.txt : ");
            System.out.println(enkriptovanSadrzaj);

            pw.println(enkriptovanSadrzaj);
            pw.close();

            Files.write(Paths.get("tmp.txt"), new byte[0]);


        }
        else if("p".equals(opcija))
        {
            while (true) {
                System.out.println("Unesite tekst do 100 karaktera : ");
                plainText = scanner.nextLine();
                if (plainText.length() <= 100) {
                    break;
                } else {
                    System.out.println("Tekst je predugačak. Molimo unesite tekst sa manje od 100 karaktera.");
                }
            }
            System.out.println("Unesite kljuc (tekst) : ");
            String key3 = scanner.nextLine();
            System.out.println("Playfair simulacija : ");
            Playfair p = new Playfair(plainText, key3);
            String cipherPlayfair = p.enkripcija();

            System.out.println("Enkriptovan tekst : ");
            System.out.println(cipherPlayfair);
            String formatiranSadrzaj=plainText+"|Playfair|"+key3+"|"+cipherPlayfair;
            String enkriptovanSadrzaj=enkripcijaSadrzaja(formatiranSadrzaj);
            pw.println(enkriptovanSadrzaj);
            pw.close();
            Files.write(Paths.get("tmp.txt"), new byte[0]);

        }
        if ("r".equals(opcija) || "m".equals(opcija) || "p".equals(opcija)) {
            integritet(redniBroj);
        }
        return false;
    }
    String enkripcijaSadrzaja(String formatiranSadrzaj) throws Exception
    {
        StringBuilder enkriptovanSadrzaj=new StringBuilder();

        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter("tmpOpen.txt")))) {
            printWriter.print(formatiranSadrzaj);
        }
        String komanda = "openssl enc -aes-128-cbc -in tmpOpen.txt -out tmp.txt -k sigurnost -base64";
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", komanda);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        process.waitFor();

        List<String> linije = Files.readAllLines(Paths.get("tmp.txt"));
        for (String rijec : linije) {
            enkriptovanSadrzaj.append(rijec).append(System.lineSeparator());
        }
        Files.write(Paths.get("tmpOpen.txt"), new byte[0]);


        return enkriptovanSadrzaj.toString().trim();
    }
    String dekripcijaSadrzaja(String cipher) throws Exception
    {
       // System.out.println("pozvana je dekripcija sadrzaja");
       // System.out.println("cipher za dekripciju : "+ cipher);
        StringBuilder dekriptovanSadrzaj = new StringBuilder();
        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter("tmpOpen.txt")))) {
            printWriter.print(cipher);
        }

        String komanda = "openssl enc -aes-128-cbc -d -in tmpOpen.txt -out tmpDecrypted.txt -k sigurnost -base64";
      //  System.out.println("komanda za dekripciju : ");
      //  System.out.println(komanda);
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", komanda);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        process.waitFor();
      //  System.out.println("process.waitFor = " + process.waitFor());

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("tmpDecrypted.txt"))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                dekriptovanSadrzaj.append(line).append(System.lineSeparator());
            }
        }
        Files.write(Paths.get("tmpOpen.txt"), new byte[0]);


      //  System.out.println("Prije return iz dekripcije, sadrzaj je : ");
        System.out.println(dekriptovanSadrzaj.toString());
        return dekriptovanSadrzaj.toString().trim();
    }

    void integritet(int redniBroj) throws Exception
    {
        String komanda = "openssl dgst -sha1 -sign priprema/private/private" + redniBroj + ".key -out Potpisi/potpis" + redniBroj + ".sign" +
                " -passin pass:sigurnost Simulacije/korisnik" + redniBroj + ".txt";
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", komanda);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        process.waitFor();
    }
    void neovlastenaIzmjena(int redniBroj) throws Exception
    {
        File datoteka = new File("Simulacije/korisnik"+redniBroj+".txt");


        String komanda ="openssl dgst -sha1 -prverify priprema/private/private"+redniBroj+".key -passin pass:sigurnost -signature Potpisi/potpis"+redniBroj+".sign Simulacije/korisnik"+redniBroj+".txt";
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", komanda);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String poruka;

        while ((poruka=bufferedReader.readLine())!=null)
        {
            if (poruka.contains("Verified OK"))
            {
                System.out.println("Verified OK");

            }
            else System.out.println("Verification Failure - Detektovana je neovlastena izmjena fajla.");
        }
        process.waitFor();
    }


}