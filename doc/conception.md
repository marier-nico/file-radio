# A. Description du projet:

- Objectifs

    Le besoin auquel notre projet vise à répondre est la transmission de données sans fil.
    Pour répondre au besoin, il faut que la transmission soit sécuritaire et privée.
    De plus, il faut être en mesure de détecter et corriger les erreurs de transmission d'information, afin d'éviter la corruption de l'information.
    Notre projet répond également au besoin grandissant de comprendre en détail le fonctionnement d'Internet et les principes fondamentaux physiques et mathématiques qui permettent l'utilisation d'un tel réseau.

- Description détaillée

    Au plus haut niveau, l'application sera utilisée de la façon suivante : l'utilisateur commence par sélectionner un fichier de son choix, de n'importe quel type et de n'importe quel format, qu'il veut envoyer à un autre ordinateur. Ensuite, le logiciel se charge de convertir le fichier de l'utilisateur dans un format binaire sérialisé. Le fichier, sous forme binaire, est converti à nouveau sous un format analogue qui sera un son pour émettre du courant alternatif dans notre circuit. Le son sera sélectionné très précisément, car il est responsable de la modulation de la fréquence de notre once ainsi que de l'amplitude de celle-ci. L'utilisation du son à cet effet permettra à l'utilisateur de choisir lui-même la fréquence sur laquelle il désire transmettre l'information. Le son sera transformé en onde électromagnétique par notre circuit RLC et reçu par notre autre circuit de réception. Une fois reçu par le récepteur, connecté à un second ordinateur, les ondes électromagnétiques seront reconverties en binaire, pour ultimement recréer l'information d'origine. S'il advenait qu'une erreur de transmission se produise, elle serait corrigée, si notre système de correction d'erreurs le permet. Sinon, l'ordinateur receveur avertira l'utilisateur de la corruption de son information et l'invitera à réinitialiser le transfert.

    - Concepts
        - Circuits électriques
            - Calculs pour déterminer les composantes
            - Circuits RLC
            - Courant alternatif
        - Ondes électromagnétiques
            - Fréquence
            - Amplitude
            - Ondes AM et FM
        - Correction et détection d'erreurs
        - Sérialisation d'objets en blob (**b**inary **l**arge **ob**ject)
        - Conversion byte -> représentation binaire
        - Modèle MVC
        - Lecture de fichiers
        - Conversion digital to analog et analog to digital
        - Interprétation du flux binaire reçu pour reconstruire l'informaiton
    - Nouveau concepts

        Tout ce qui a trait aux ondes est nouveau pour nous étant donné que nous sommes en train de compléter le cours d'ondes et physique moderne.
        Malgré le fait que nous avons fait des circuits dans le cours de physique précédent, ils étaient théoriques.
        Nous n'avons que très rarement assemblé des circuits, et nous ne les avons jamais élaborés nous-mêmes.
        La conversion digital to analog nécessaire à l'envoi d'ondes est quelque chose que nous n'avons jamais fait.
        Aussi, nous n'avons jamais transmis d'ondes entre deux circuits.
        Il nous faudra également apprendre les principes de correction d'erreurs pour détecter et corriger les erreurs.

    - Complexité

        La complexité provient surtout du fait que nous avons beaucoup de choses à apprendre.
        Il est difficile d'évaluer correctement la complexité du projet étant donné notre inexpérience dans les nouveaux concepts.
        Cependant, nous pouvons dire avec certitude que la tâche ne sera pas si facile, car la contrainte de temps est concidérable.
        De plus, les nombreuses tâches du projet vont nécessiter une attention particulière, étant donné que le succès de chaque étape est obligatoire pour l'élaboration de l'étape suivante.
        Nous sommes convaincus de la possibilité de réaliser le projet, car il est possible de retirer certains facteurs de complexité s'il s'avère que notre entreprise était initialement trop ambitieuse. 
        Notre professeur de physique nous a confirmé qu'il allait pouvoir nous aider en cas de besoin, et qu'il est certain que la réalisation du projet est possible. 

- Concepteurs, rôles et justifications
    - Nicolas Marier, chef et développeur
    - Thomas Garneau-Hamel, électronique et physique
    - Guillaume Rousseau, documentation
    - Charles-Antoine Demetriade, graphiques / interface

    Nous avons choisi ces rôles en fonction de nos forces et nos goûts ainsi que nos faiblesses.

- Séparation des tâches
    - Nicolas Marier : élaboration de la programmation des classes métier ainsi que des diagrammes UML.
    - Thomas Garneau-Hamel : conception du circuit électrique et calculs.
    - Guillaume Rousseau : documentation, recherche, contrôle de qualité.
    - Charles-Antoine Demetriade : création de la vue et de ce qui se rapporte à l'environnement graphique.

- Type d'application

    Windows, Mac, Linux (exécutable par la JVM)

- Langages utilisés, patrons de conception
    - Langage : Java
    - Patrons de conception : MVC

- Technologies impliquées
    - IDE: Eclipse, IntelliJ, SceneBuilder, PlantUML
    - Hardware : un ordinateur et un raspberry pi, des composantes élctroniques (résistances, condensateurs, fils)

- Références et documentation : TODO

# B. Prototypes de l'application

- Prototype
    Placer l'image ici.
- Principaux objets