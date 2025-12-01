#  Jakarta EE 11 - Servlets, Sessions et Filtres

## 📚 Objectifs Pédagogiques

Ce TP vous permet de comprendre :
- ✅ Les **Servlets** (gestion des requêtes HTTP GET/POST)
- ✅ Les **Sessions HTTP** (authentification et stockage de données utilisateur)
- ✅ Les **Filtres** (sécurisation et interception des requêtes)
- ✅ Le pattern **DAO** (Data Access Object)
- ✅ La **connexion JDBC** à MySQL

---

## 🏗️ Structure du Projet

```
Projet/
├── src/main/
│   ├── java/
│   │   ├── country.dao/
│   │   │   ├── DB.java              // Connexion à la BD
│   │   │   ├── Country.java         // DAO pour les pays
│   │   │   └── User.java            // DAO pour les utilisateurs
│   │   └── country.web/
│   │       ├── CountryServlet.java  // Affichage et ajout de pays
│   │       ├── LoginServlet.java    // Gestion de la connexion
│   │       ├── LogoutServlet.java   // Gestion de la déconnexion
│   │       └── AuthenticationFilter.java  // Filtre de sécurité
│   └── webapp/
│       ├── addCountry.html          // Formulaire d'ajout de pays
│       ├── login.html               // Page de connexion
│       └── WEB-INF/
│           └── lib/
│               └── mysql-connector-j-8.x.x.jar
```

---

## 🚀 Installation et Configuration

### 1. Prérequis

- ☕ **Java JDK 17+** (Jakarta EE 11 nécessite Java 17 minimum)
- 🐬 **MySQL Server** (version 8.0+)
- 🔧 **Eclipse IDE for Enterprise Java** (ou IntelliJ IDEA Ultimate)
- 🚢 **Apache Tomcat 10.1+** (compatible Jakarta EE 11)

### 2. Configuration de la Base de Données

#### Étape 1 : Installer la base Sakila

```bash
# Télécharger Sakila depuis le site officiel de MySQL
https://dev.mysql.com/doc/index-other.html

# Importer dans MySQL
mysql -u root -p < sakila-schema.sql
mysql -u root -p < sakila-data.sql
```

#### Étape 2 : Créer la table user

```sql
USE sakila;

CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL
);

INSERT INTO user (email, password) VALUES ('admin', '1234');
```

#### Étape 3 : Configurer DB.java

Modifier les informations de connexion dans `DB.java` :

```java
private static final String URL = "jdbc:mysql://localhost:3306/sakila";
private static final String USER = "root";         // Votre utilisateur MySQL
private static final String PASSWORD = "votre_mdp"; // Votre mot de passe MySQL
```

### 3. Configuration d'Eclipse

#### Étape 1 : Créer un Dynamic Web Project

1. File → New → Dynamic Web Project
2. Nom : `CountryManagement`
3. Target runtime : Apache Tomcat 10.1
4. Dynamic web module version : 6.0
5. Finish

#### Étape 2 : Ajouter le Driver MySQL

1. Télécharger **MySQL Connector/J** depuis :
   https://dev.mysql.com/downloads/connector/j/
2. Copier le fichier `mysql-connector-j-8.x.x.jar` dans :
   `WebContent/WEB-INF/lib/`

#### Étape 3 : Créer les packages et classes

Créer tous les fichiers fournis dans l'arborescence ci-dessus.

---

## 🔍 Concepts Clés Expliqués

### 1️⃣ Les Servlets

**Qu'est-ce qu'une Servlet ?**
- Une classe Java qui gère les requêtes et réponses HTTP
- Hérite de `HttpServlet`
- Utilise les annotations `@WebServlet` pour définir son URL

**Cycle de vie :**
```
init() → service() → destroy()
          ↓
    doGet() ou doPost()
```

**Exemple :**
```java
@WebServlet("/countries")  // URL qui déclenche cette servlet
public class CountryServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        // Traiter une requête GET (afficher des données)
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        // Traiter une requête POST (soumettre un formulaire)
    }
}
```

### 2️⃣ Les Sessions HTTP

**Pourquoi les sessions ?**
- HTTP est "sans état" (stateless) : chaque requête est indépendante
- Les sessions permettent de stocker des données entre plusieurs requêtes
- Utilisées pour l'authentification, les paniers d'achat, etc.

**Comment ça marche ?**
```java
// Créer ou récupérer une session
HttpSession session = request.getSession(true);

// Stocker des données
session.setAttribute("user", userObject);
session.setAttribute("email", "admin@example.com");

// Récupérer des données
User user = (User) session.getAttribute("user");

// Supprimer la session (déconnexion)
session.invalidate();
```

**Durée de vie :**
```java
// Par défaut : 30 minutes (1800 secondes)
session.setMaxInactiveInterval(1800);
```

### 3️⃣ Les Filtres

**Qu'est-ce qu'un Filtre ?**
- Intercepte les requêtes AVANT qu'elles n'atteignent les servlets
- Permet de vérifier l'authentification, logger les accès, modifier les requêtes

**Ordre d'exécution :**
```
Requête → Filtre1 → Filtre2 → Servlet → Réponse
```

**Exemple :**
```java
@WebFilter("/addCountry.html")  // URLs à intercepter
public class AuthenticationFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        // Vérifier si l'utilisateur est connecté
        HttpSession session = ((HttpServletRequest) request).getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);
        
        if (isLoggedIn) {
            chain.doFilter(request, response);  // Laisser passer
        } else {
            ((HttpServletResponse) response).sendRedirect("login.html");  // Bloquer
        }
    }
}
```

### 4️⃣ Le Pattern DAO

**Data Access Object (DAO)**
- Sépare la logique métier de l'accès aux données
- Une classe par table de la base de données
- Encapsule toutes les opérations CRUD (Create, Read, Update, Delete)

**Avantages :**
- ✅ Code plus organisé et maintenable
- ✅ Réutilisable dans tout le projet
- ✅ Facilite les tests unitaires
- ✅ Changement de BD plus facile

**Exemple :**
```java
public class Country {
    // Attributs correspondant aux colonnes de la table
    private int countryId;
    private String country;
    
    // Méthode pour récupérer tous les pays
    public static List<Country> get() { /* ... */ }
    
    // Méthode pour ajouter un pays
    public static boolean add(String name) { /* ... */ }
}
```

---

## 🔄 Flux de l'Application

### Partie 1 : Affichage et Ajout de Pays

```
1. User accède à : http://localhost:8080/countries
   ↓
2. CountryServlet.doGet() est appelé
   ↓
3. Country.get() récupère les pays de la BD
   ↓
4. La servlet génère du HTML dynamique
   ↓
5. Le navigateur affiche la liste

Pour ajouter un pays :
1. User clique sur "Ajouter un nouveau pays"
   ↓
2. Le navigateur charge addCountry.html
   ↓
3. User remplit le formulaire et soumet
   ↓
4. Le formulaire envoie une requête POST à /addCountry
   ↓
5. CountryServlet.doPost() est appelé
   ↓
6. Country.add() insère le pays dans la BD
   ↓
7. La servlet affiche la liste mise à jour
```

### Partie 2 : Authentification

```
1. User accède à : http://localhost:8080/addCountry.html
   ↓
2. AuthenticationFilter intercepte la requête
   ↓
3. Le filtre vérifie la session
   ↓
4a. Session existe → laisser passer
4b. Pas de session → rediriger vers login.html
   ↓
5. User entre ses identifiants
   ↓
6. LoginServlet.doPost() est appelé
   ↓
7. User.authenticate() vérifie les identifiants
   ↓
8a. OK → créer une session et rediriger vers /countries
8b. Erreur → retour à login.html avec message d'erreur
```

---

## 🧪 Tests et Utilisation

### Comptes de test

| Email | Mot de passe |
|-------|--------------|
| admin | 1234 |

### Scénarios de test

#### Test 1 : Afficher les pays
1. Accéder à `http://localhost:8080/countries`
2. Vérifier que la liste des pays s'affiche

#### Test 2 : Ajouter un pays (sans authentification)
1. Accéder à `http://localhost:8080/addCountry.html`
2. Vérifier la redirection vers `login.html`

#### Test 3 : Se connecter
1. Sur `login.html`, entrer : admin / 1234
2. Vérifier la redirection vers la liste des pays
3. Vérifier l'affichage de l'email en haut de la page

#### Test 4 : Ajouter un pays (authentifié)
1. Cliquer sur "Ajouter un nouveau pays"
2. Entrer "Maroc" et soumettre
3. Vérifier que "Maroc" apparaît dans la liste

#### Test 5 : Se déconnecter
1. Cliquer sur "Se déconnecter"
2. Vérifier la redirection vers `login.html`
3. Essayer d'accéder à `addCountry.html`
4. Vérifier la redirection vers `login.html`

---

## 🐛 Résolution de Problèmes

### Erreur : ClassNotFoundException: com.mysql.cj.jdbc.Driver
**Solution :** Vérifier que le fichier `mysql-connector-j-x.x.x.jar` est bien dans `WEB-INF/lib/`

### Erreur : 404 Not Found
**Solution :** 
- Vérifier que le serveur Tomcat est démarré
- Vérifier l'URL (sensible à la casse)
- Vérifier les annotations `@WebServlet`

### Erreur : SQLException: Access denied
**Solution :** Vérifier les credentials MySQL dans `DB.java`

### La session ne fonctionne pas
**Solution :**
- Vérifier que vous utilisez `request.getSession(true)` pour créer une session
- Vérifier que vous appelez `session.setAttribute()` après authentification

### Le filtre ne bloque pas l'accès
**Solution :**
- Vérifier l'annotation `@WebFilter(urlPatterns = {"/addCountry.html", "/addCountry"})`
- Vérifier que vous ne faites pas de `chain.doFilter()` pour les utilisateurs non authentifiés

---

## 📖 Ressources Supplémentaires

- [Documentation Jakarta Servlet](https://jakarta.ee/specifications/servlet/)
- [Tutoriel MySQL](https://dev.mysql.com/doc/)
- [Guide Tomcat](https://tomcat.apache.org/tomcat-10.1-doc/)

---
