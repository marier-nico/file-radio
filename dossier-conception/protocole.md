# Protocole de transmission

La transmission est effectuée avec les informations suivantes :
- Taille du message
- Extension du fichier
- Contenu du fichier
- Indicateur de fin de transfert


### Taille du message

La taille du message est envoyée pour permettre au récepteur de connaître la progression de la réception du fichier, en comparant l'information reçue avec le nombre d'octets attendu.
Cette taille tiendra sur 24 bits, soit 3 octets **non signés**.
Le plus long message aura donc au plus 16 777 216 bits, donc un peu plus de 16MB.


### Extension du fichier

L'extension du fichier envoyé tiendra sur 10 caractères, soit 80 octets ou 640 bits.

### Contenu du fichier

Le contenu du fichier sera ensuite envoyé et aura au plus 2MB.

### Indication de fin de transfert

Les caractères "FINTSFT" seront envoyés à la fin du transfert du message.
Les octets représentant ces caractères sont les suivants : 0x46 0x49 0x4E 0x54 0x53 0x46 0x54.

## Exemple de transmission

`0x00 0x00 0x01 - 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x74 0x78 0x74 - 0x21 - 0x46 0x49 0x4E 0x54 0x53 0x46 0x54`

Ici, on voit que la première partie du message indique la transmission d'un seul octet. La deuxième partie indique l'extension du fichier, soit "txt" dans ce cas. La troisième est le message lui-même, soit "!" dans ce cas. La dernière partie du message représente l'indicateur de fin du message, soit "FINTEST" ici.