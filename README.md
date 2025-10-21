# Simulacija Kriptografskih Algoritama - Studentski projekat, predmet Kriptografija i računarska zaštita, Elektrotehnički fakultet

Ova aplikacija simulira rad jednostavnih kriptografskih algoritama. Trenutno su podržani sledeći algoritmi: **Rail Fence**, **Myszkowski** i **Playfair**. Aplikacija omogućava enkripciju proizvoljno unesenog teksta uz odabir ključa.

## Registracija i prijava

- Korisnik se prvo mora registrovati unosom:
  - korisničkog imena
  - lozinke
  - dodatnih podataka koji se koriste u sertifikatu
- Tokom registracije, aplikacija automatski generiše:
  - digitalni sertifikat povezan sa korisničkim podacima
  - par **RSA ključeva**
- Putanja do kreiranog sertifikata i ključa se prikazuje korisniku
- Privatni ključ i sertifikat su adekvatno zaštićeni
- Prijava se vrši u dva koraka:
  1. Unos digitalnog sertifikata
  2. Unos korisničkog imena i lozinke

## Funkcionalnosti

- Pregled dostupnih algoritama nakon prijave
- Enkripcija unesenog teksta uz odabrani ključ
- Prikaz rezultata enkripcije (šifrata)
- Čuvanje svake simulacije u zasebnoj tekstualnoj datoteci po korisniku, u formatu: TEKST | ALGORITAM | KLJUČ | ŠIFRAT
- Mogućnost pregleda istorije simulacija samo od strane prijavljenog korisnika
- Detekcija neovlašćenih izmena datoteka i obaveštavanje korisnika

## Sigurnost

- Aplikacija koristi infrastrukturu javnog ključa (PKI)
- Svi sertifikati su izdati od strane prethodno uspostavljenog CA tijela
- CRL lista i sertifikati korisnika se čuvaju na proizvoljnoj lokaciji
- Privatni ključ prijavljenog korisnika je zaštićen i dostupan samo unutar aplikacije
- Zaštita tajnosti i integriteta datoteka sa istorijom simulacija
