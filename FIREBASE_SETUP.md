# 🔥 Configuration Firebase pour TravelMate

## ⚠️ IMPORTANT : Configuration Requise

Pour que l'application fonctionne en temps réel, vous devez configurer Firebase dans votre projet.

## 📋 Étapes de Configuration

### 1. Créer un Projet Firebase

1. Allez sur [Firebase Console](https://console.firebase.google.com/)
2. Cliquez sur "Ajouter un projet"
3. Nommez votre projet : **TravelMate**
4. Suivez les étapes de configuration

### 2. Ajouter Android App à Firebase

1. Dans Firebase Console, cliquez sur l'icône Android
2. **Package name** : `com.example.travelmate`
3. **App nickname** : TravelMate (optionnel)
4. Téléchargez le fichier `google-services.json`
5. **Remplacez** le fichier `app/google-services.json` dans votre projet

### 3. Activer les Services Firebase

#### Firebase Authentication
1. Dans Firebase Console → Authentication
2. Cliquez sur "Get Started"
3. Activez **Email/Password** dans Sign-in method

#### Firebase Realtime Database
1. Dans Firebase Console → Realtime Database
2. Cliquez sur "Create Database"
3. Choisissez **Test mode** pour le développement (ou configurez les règles de sécurité)
4. Sélectionnez une région (ex: `us-central1`)

#### Firebase Cloud Messaging
1. Dans Firebase Console → Cloud Messaging
2. Le service est automatiquement activé

### 4. Règles de Sécurité Realtime Database

Dans Firebase Console → Realtime Database → Rules, ajoutez :

```json
{
  "rules": {
    "users": {
      "$userId": {
        ".read": "$userId === auth.uid",
        ".write": "$userId === auth.uid"
      }
    },
    "travels": {
      "$travelId": {
        ".read": "data.child('organiserId').val() === auth.uid || 
                  data.child('participantIdsJson').val().contains(auth.uid)",
        ".write": "data.child('organiserId').val() === auth.uid || 
                   newData.child('organiserId').val() === auth.uid"
      }
    },
    "activities": {
      "$activityId": {
        ".read": true,
        ".write": "auth != null"
      }
    },
    "messages": {
      "$messageId": {
        ".read": "auth != null",
        ".write": "auth != null && data.child('senderId').val() === auth.uid"
      }
    },
    "budgetItems": {
      "$itemId": {
        ".read": "auth != null",
        ".write": "auth != null"
      }
    },
    "notifications": {
      "$notificationId": {
        ".read": "data.child('userId').val() === auth.uid",
        ".write": "auth != null"
      }
    }
  }
}
```

### 5. Vérifier la Configuration

1. Assurez-vous que `app/google-services.json` est présent
2. Vérifiez que le plugin Google Services est appliqué dans `app/build.gradle.kts`
3. Sync le projet Gradle

## 🚀 Fonctionnalités Activées

Une fois configuré, vous aurez :

✅ **Authentification réseau** - Login/Register avec Firebase Auth
✅ **Synchronisation temps réel** - Données partagées entre utilisateurs
✅ **Messages en temps réel** - Chat instantané
✅ **Notifications push** - Notifications même quand l'app est fermée
✅ **Mode offline** - Firebase cache les données localement
✅ **Multi-appareils** - Synchronisation automatique

## 📝 Notes

- Le fichier `app/google-services.json` fourni est un **template**
- Vous devez le remplacer par votre fichier réel depuis Firebase Console
- Sans ce fichier, l'application ne pourra pas se connecter à Firebase

## 🔧 Dépannage

### Erreur : "FirebaseApp not initialized"
- Vérifiez que `google-services.json` est présent
- Vérifiez que le plugin Google Services est appliqué

### Erreur : "Permission denied"
- Vérifiez les règles de sécurité dans Firebase Console
- Assurez-vous que l'utilisateur est authentifié

### Pas de données en temps réel
- Vérifiez votre connexion internet
- Vérifiez que Firebase Realtime Database est activé
- Vérifiez les règles de sécurité

