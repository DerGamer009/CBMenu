# CBMenu

Dieses Maven-Projekt enthält ein Bukkit/Spigot-Plugin für Minecraft **1.21**, das ein grafisches Menü über den Befehl `/cb` bereitstellt. Spieler können damit bequem zwischen verschiedenen CityBuild- und Farm-Servern wechseln.

## Features

- Hauptmenü mit den Einträgen **CB01** und **CB02**
- Untermenüs für die jeweiligen CityBuild- und Farm-Server
- Glasrahmen, Schließen-Button und Item-Lore
- Soundeffekte und kleine Öffnungsanimation
- Verbindung zu den Servern `cb01`, `Farm01`, `cb02` und `Farm02` über Plugin-Message

## Build

```bash
mvn package
```

Die erzeugte JAR-Datei `cbmenu-1.0.0.jar` kommt in den `plugins`-Ordner eines kompatiblen Servers. Anschließend steht der Befehl `/cb` zur Verfügung.
