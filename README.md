# TravelMate

Application mobile de planification de voyages et sorties en groupe pour Android et iOS.

## Description

TravelMate est une application complète permettant de créer et suivre des voyages, gérer les activités, le budget et les itinéraires, avec une messagerie interne et un chatbot IA intégré.

## Fonctionnalités principales

### ✅ Authentification
- Connexion et inscription sécurisées
- Gestion des rôles : **Participant** et **Organisateur**

### ✅ Gestion des voyages
- Création et édition de voyages
- Gestion des participants
- Itinéraires détaillés
- Suivi des budgets

### ✅ Gestion des activités
- Planification d'activités
- Assignation aux participants
- Catégorisation (Restaurant, Tourisme, Transport, etc.)

### ✅ Gestion du budget
- Suivi des dépenses
- Répartition par catégorie
- Répartition par participant
- Alertes de dépassement

### ✅ Messagerie
- Chat en temps réel par voyage
- Messages privés
- Chatbot IA pour suggestions de voyage

### ✅ Notifications
- Notifications automatiques
- Rappels d'activités
- Alertes de budget

### ✅ Profil utilisateur
- Gestion du profil
- Paramètres
- Historique des voyages

## Design UI/UX

- **Palette de couleurs** : Turquoise (#40E0D0), Orange (#FF8C42), Blanc (#FFFFFF)
- **Navigation** : 7 onglets principaux
  - Accueil
  - Voyages
  - Activités
  - Budget
  - Messagerie
  - Notifications
  - Profil

## Architecture technique

### Stack technologique
- **Langage** : Kotlin
- **UI** : Jetpack Compose
- **Navigation** : Navigation Compose
- **Architecture** : MVVM (Model-View-ViewModel)
- **Base de données** : Room Database
- **Networking** : Retrofit + Gson
- **Images** : Coil

### Structure du projet

```
app/src/main/java/com/example/travelmate/
├── data/
│   ├── models/          # Modèles de données (User, Travel, Activity, Budget, Message, Notification)
│   ├── repository/      # Repositories pour la gestion des données
│   └── room/            # Base de données Room (DAO, Database, Converters)
├── domain/
│   └── (à venir - cas d'usage métier)
├── ui/
│   ├── theme/           # Thème Material Design (couleurs, typographie)
│   ├── navigation/      # Navigation entre écrans
│   ├── screens/         # Écrans de l'application
│   │   ├── auth/        # Authentification (Login, Register)
│   │   ├── home/        # Accueil
│   │   ├── travels/     # Voyages
│   │   ├── activities/  # Activités
│   │   ├── budget/      # Budget
│   │   ├── messaging/   # Messagerie
│   │   ├── notifications/ # Notifications
│   │   └── profile/     # Profil
│   └── components/      # Composants réutilisables
└── util/                # Utilitaires et helpers
```

## Installation

1. Cloner le repository
2. Ouvrir le projet dans Android Studio
3. Synchroniser les dépendances Gradle
4. Exécuter l'application sur un émulateur ou un appareil physique

## Configuration requise

- Android Studio Hedgehog ou plus récent
- Min SDK : 24 (Android 7.0)
- Target SDK : 36
- Compile SDK : 36
- Kotlin 2.0.21+

## Prochaines étapes

- [ ] Implémenter l'authentification réelle (Firebase Auth ou API backend)
- [ ] Connexion à une API backend pour la synchronisation
- [ ] Implémenter le chatbot IA (OpenAI, Gemini, etc.)
- [ ] Ajouter les notifications push
- [ ] Implémenter la gestion des images/photos
- [ ] Tests unitaires et d'intégration
- [ ] Support iOS (Kotlin Multiplatform Mobile)

## Licence

Ce projet est développé dans le cadre d'un projet académique.

## Auteur

TravelMate Team

