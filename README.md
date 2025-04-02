# Health Tracker

Health Tracker on Vaadin-sovelluskehyksellä toteutettu terveystietojen seurantasovellus. Sovelluksen avulla käyttäjät voivat tallentaa ja seurata terveystietojaan, kuten verenpainetta, sykettä ja verensokeri.

## Ominaisuudet

- Käyttäjien hallinta ja käyttöoikeustasot (admin/käyttäjä)
- Henkilöiden tietojen hallinta
- Mittaustietojen tallennus ja hallinta (verenpaine, syke, paino, pituus, lämpötila, verensokeri)
- Mittaustulosten visualisointi ja suodatus
- Reaaliaikaiset päivitykset Server Push -teknologialla
- Responsiivinen käyttöliittymä

## Teknologiat

- Java 17
- Spring Boot 3.x
- Vaadin 24
- Spring Security
- H2-tietokanta (kehitysympäristössä)
- Maven

## Asennus ja käyttöönotto

### Vaatimukset

- Java JDK 17 tai uudempi
- Maven 3.6 tai uudempi

### Ohjeet

1. Kloonaa repositorio omalle koneellesi:
   ```
   git clone https://github.com/RampeK/vaadin-harjoitus.git
   cd vaadin-harjoitus
   ```

2. Käynnistä sovellus Maven-työkalulla:
   ```
   mvn spring-boot:run
   ```

3. Avaa selain osoitteessa [http://localhost:8080](http://localhost:8080)

4. Kirjaudu sisään seuraavilla tunnuksilla:
   - Tavallinen käyttäjä: käyttäjätunnus `user`, salasana `user`
   - Ylläpitäjä: käyttäjätunnus `admin`, salasana `admin`

## Sovelluksen rakenne

- **Model**: Tietomallit ja entiteetit (`Person`, `Measurement`, jne.)
- **Repository**: Tietokantakäsittely
- **Service**: Sovelluslogiikka
- **UI**: Vaadin-käyttöliittymäkomponentit
- **Security**: Käyttäjien autentikointi ja käyttöoikeudet

## Käyttöliittymän näkymät

- **Dashboard**: Yleiskatsaus mittaustiedoista
- **Persons**: Henkilötietojen hallinta
- **Measurements**: Mittaustulosten tallennus ja tarkastelu
- **Medical Notes**: Lääketieteellisten muistiinpanojen hallinta

## Reaaliaikaiset päivitykset

Sovellus hyödyntää Vaadin Server Push -teknologiaa, mikä mahdollistaa reaaliaikaiset päivitykset käyttöliittymään. Tämä toteutetaan keskitetysti AppShellConfig-luokassa, joka välittää päivitykset WebSocket-yhteyden kautta.

## Lisensointi

Tämä projekti on lisensoitu MIT-lisenssillä. Katso lisätietoja [LICENSE](LICENSE) tiedostosta. 