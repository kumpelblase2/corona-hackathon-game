# Corona Zombie Game - Ein Hackathon Spiel

Dies ist ein Minecraft Minigame zu einem kleinen Hackathon mit den Themen "Infektion", "Reinheit" & "Lernen", wovon zwei Themen
behandelt werden sollten. Diese Spiel versucht sich an den ersten beiden.

Ziel des Spiels ist es, nicht durch die Zombies infiziert (Infektion) zu werden indem man sich hin und wieder wäscht (Reinheit).
Es ist eher sehr minimal geworden aufgrund von Zeiteinschränkungen.

Jede Runde werden exponential mehr Zombies auftauchen, ähnlich zu der Verbreitung von COVID-19, was der anlass des Hackathons war.
Das heißt, initial wird sich noch nicht so viel ändern, doch nach ein paar Runden geht es dann so richtig los. Die Spieler müssen
außerdem aufpassen, dass sie nicht durch Infektion über die Zeit zum Opfer fallen und sich dementsprechend häufig waschen (also
ins Wasser zu gehen)

Eine kleine Demo gibts hier:
![Demo Video](demo.webm)

## Spielen

Um das Spiel zu spielen, muss man als erstes das Plugin im Minecraft-Server installieren (Bukkit Server wird benötigt). Dazu findet
man unter [Releases](http://github.com/kumpelblase2/corona-hackathon-game/releases) eine Jar, welche man in de Plugins-Ordner
einfügen sollte. Danach Server starten und es kann los gehen!

Jeder Mitspieler kann durch das ausführen von "/join" dem nächsten Spiel betreten. Sobald alle Spieler das getan haben, kann durch
"/start" das Spiel gestartet werden.

## Konfiguration

Ein paar Einstellungen können, wenn gewünscht angepasst werden. Dazu im Server im Plugin-Ordner ein Ordner "CoronaVirus" erstellen und hier eine Datei namens "config.yml" anlegen. Diese kann dann wie folgt befüllt werden:

```yaml
zombie-increase: 1.1
infection-increase: 1.1
zombie-count: 3
cooldown: 5
```  

Die Werte können nach belieben angepasst werden.

- "zombie-increase" beschreibt den Zuwachs an Zombies pro Runde. Der Zuwachs ist exponential, d.h. in Runde 5 sind `zombie-increase ^ 5` vorhanden, beim Wert von `1.1` wären das `1.1 ^ 5 = 1.61` mal so viele, wie am Anfang.
- "infection-increase" gibt an, wie viel die Infektion über Zeit ansteigt. Auch dies ist exponential.
- "zombie-count" ist die Anzahl der Zombies pro Spieler, mit der angefangen wird.
- "cooldown" ist die Zeit zwischen zwei Runden in Sekunden.
