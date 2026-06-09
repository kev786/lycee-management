-- ============================================================
-- SCHEMA : Système de Gestion de Lycée
-- Base : MySQL 8.4.8
-- ============================================================
CREATE DATABASE IF NOT EXISTS lycee_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE lycee_db;

-- ============================================================
-- TABLE : classe
-- ============================================================
CREATE TABLE IF NOT EXISTS classe (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    niveau VARCHAR(20) NOT NULL,
    serie VARCHAR(20) NOT NULL,
    effectif_max INT DEFAULT 60,
    prof_principal VARCHAR(100),
    salle_principale VARCHAR(20),
    annee_scolaire VARCHAR(9) NOT NULL COMMENT 'ex: 2024-2025'
) ENGINE = InnoDB;

-- ============================================================
-- TABLE : Eleve
-- ============================================================
CREATE TABLE IF NOT EXISTS eleve (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    matricule VARCHAR(20) NOT NULL UNIQUE,
    nom VARCHAR(80) NOT NULL,
    prenom VARCHAR(80) NOT NULL,
    date_naissance DATE,
    classe_id BIGINT NOT NULL,
    nom_parent VARCHAR(120),
    tel_parent VARCHAR(20),
    email_parent VARCHAR(120),
    photo_filename VARCHAR(255),
    sexe CHAR(1) DEFAULT 'M' COMMENT 'M ou F',
    CONSTRAINT fk_eleve_classe FOREIGN KEY (classe_id) REFERENCES classe (id) ON DELETE RESTRICT
) ENGINE = InnoDB;

-- ============================================================
-- TABLE : note_eleve
-- ============================================================
CREATE TABLE IF NOT EXISTS note_eleve (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eleve_id BIGINT NOT NULL,
    matiere VARCHAR(60) NOT NULL,
    coefficient INT NOT NULL DEFAULT 1,
    notes_valeur DECIMAL(4, 2) NOT NULL,
    trimestre TINYINT NOT NULL CHECK (trimestre IN (1, 2, 3)),
    prof_saisie VARCHAR(100),
    CONSTRAINT fk_note_eleve FOREIGN KEY (eleve_id) REFERENCES eleve (id) ON DELETE CASCADE
) ENGINE = InnoDB;

-- ============================================================
-- TABLE : absence
-- ============================================================
CREATE TABLE IF NOT EXISTS absence (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eleve_id BIGINT NOT NULL,
    date_absence DATE NOT NULL,
    duree_heures INT DEFAULT 1,
    matiere VARCHAR(60),
    justifiee BOOLEAN DEFAULT FALSE,
    motif TEXT,
    CONSTRAINT fk_absence_eleve FOREIGN KEY (eleve_id) REFERENCES eleve (id) ON DELETE CASCADE
) ENGINE = InnoDB;

-- ============================================================
-- TABLE : utilisateurs
-- ============================================================
CREATE TABLE IF NOT EXISTS utilisateurs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(60) NOT NULL UNIQUE,
    password_hache VARCHAR(255) NOT NULL COMMENT 'BCrypt hash',
    role ENUM('Admin', 'Censeur', 'Surveillant') NOT NULL
) ENGINE = InnoDB;

-- ============================================================
-- DONNÉES DE DÉMONSTRATION : classe (20 lignes)
-- ============================================================
INSERT INTO
    classe (
        niveau,
        serie,
        effectif_max,
        prof_principal,
        salle_principale,
        annee_scolaire
    )
VALUES
    (
        '3iem',
        'A',
        50,
        'M. Nkomo Jean',
        'A01',
        '2024-2025'
    ),
    (
        '3iem',
        'C',
        50,
        'Mme Bella Marie',
        'A02',
        '2024-2025'
    ),
    (
        '3iem',
        'D',
        50,
        'M. Fouda Paul',
        'A03',
        '2024-2025'
    ),
    (
        '2nde',
        'A',
        55,
        'Mme Ngono Claire',
        'B01',
        '2024-2025'
    ),
    (
        '2nde',
        'C',
        55,
        'M. Mbarga Pierre',
        'B02',
        '2024-2025'
    ),
    (
        '2nde',
        'D',
        55,
        'Mme Ateba Rose',
        'B03',
        '2024-2025'
    ),
    (
        'Tle',
        'A',
        60,
        'M. Owona Serge',
        'C01',
        '2024-2025'
    ),
    (
        'Tle',
        'C',
        60,
        'Mme Essama Diane',
        'C02',
        '2024-2025'
    ),
    (
        'Tle',
        'D',
        60,
        'M. Biyong Jules',
        'C03',
        '2024-2025'
    ),
    (
        '3iem',
        'A',
        50,
        'M. Ondoa Alexis',
        'A04',
        '2024-2025'
    ),
    (
        '3iem',
        'C',
        50,
        'Mme Messi Hortense',
        'A05',
        '2024-2025'
    ),
    (
        '2nde',
        'A',
        55,
        'M. Abate François',
        'B04',
        '2024-2025'
    ),
    (
        '2nde',
        'C',
        55,
        'Mme Nguema Patricia',
        'B05',
        '2024-2025'
    ),
    (
        'Tle',
        'A',
        60,
        'M. Abena Gustave',
        'C04',
        '2024-2025'
    ),
    (
        'Tle',
        'C',
        60,
        'Mme Eboa Sandrine',
        'C05',
        '2024-2025'
    ),
    (
        '3iem',
        'D',
        50,
        'M. Mvondo Eric',
        'A06',
        '2024-2025'
    ),
    (
        '2nde',
        'D',
        55,
        'Mme Ayolo Brigitte',
        'B06',
        '2024-2025'
    ),
    (
        'Tle',
        'D',
        60,
        'M. Noa Clement',
        'C06',
        '2024-2025'
    ),
    (
        '3iem',
        'A',
        50,
        'Mme Bongo Irene',
        'A07',
        '2024-2025'
    ),
    (
        '2nde',
        'C',
        55,
        'M. Engonga Yves',
        'B07',
        '2024-2025'
    );

-- ============================================================
-- DONNÉES DE DÉMONSTRATION : eleve (20 lignes)
-- ============================================================
INSERT INTO
    eleve (
        matricule,
        nom,
        prenom,
        date_naissance,
        classe_id,
        nom_parent,
        tel_parent,
        email_parent,
        photo_filename,
        sexe
    )
VALUES
    (
        'LYC2024001',
        'Nkomo',
        'Alice',
        '2008-03-12',
        1,
        'Nkomo Bernard',
        '+237699001001',
        'nkomo.b@email.cm',
        NULL,
        'F'
    ),
    (
        'LYC2024002',
        'Bella',
        'Eric',
        '2007-07-22',
        1,
        'Bella Joseph',
        '+237699001002',
        'bella.j@email.cm',
        NULL,
        'M'
    ),
    (
        'LYC2024003',
        'Fouda',
        'Marie',
        '2008-01-05',
        2,
        'Fouda Célestine',
        '+237699001003',
        'fouda.c@email.cm',
        NULL,
        'F'
    ),
    (
        'LYC2024004',
        'Ngono',
        'Paul',
        '2007-11-30',
        2,
        'Ngono Théodore',
        '+237699001004',
        'ngono.t@email.cm',
        NULL,
        'M'
    ),
    (
        'LYC2024005',
        'Mbarga',
        'Sophie',
        '2006-05-14',
        4,
        'Mbarga Hélène',
        '+237699001005',
        'mbarga.h@email.cm',
        NULL,
        'F'
    ),
    (
        'LYC2024006',
        'Ateba',
        'Luc',
        '2006-08-18',
        4,
        'Ateba Gérard',
        '+237699001006',
        'ateba.g@email.cm',
        NULL,
        'M'
    ),
    (
        'LYC2024007',
        'Owona',
        'Julie',
        '2005-02-25',
        7,
        'Owona Marcel',
        '+237699001007',
        'owona.m@email.cm',
        NULL,
        'F'
    ),
    (
        'LYC2024008',
        'Essama',
        'Kevin',
        '2005-12-03',
        7,
        'Essama Victoire',
        '+237699001008',
        'essama.v@email.cm',
        NULL,
        'M'
    ),
    (
        'LYC2024009',
        'Biyong',
        'Laure',
        '2005-06-17',
        8,
        'Biyong Albert',
        '+237699001009',
        'biyong.a@email.cm',
        NULL,
        'F'
    ),
    (
        'LYC2024010',
        'Ondoa',
        'Max',
        '2008-09-09',
        3,
        'Ondoa Jeanne',
        '+237699001010',
        'ondoa.j@email.cm',
        NULL,
        'M'
    ),
    (
        'LYC2024011',
        'Messi',
        'Chloe',
        '2007-04-20',
        5,
        'Messi Robert',
        '+237699001011',
        'messi.r@email.cm',
        NULL,
        'F'
    ),
    (
        'LYC2024012',
        'Abate',
        'Nathan',
        '2006-10-11',
        5,
        'Abate Suzanne',
        '+237699001012',
        'abate.s@email.cm',
        NULL,
        'M'
    ),
    (
        'LYC2024013',
        'Nguema',
        'Iris',
        '2006-03-28',
        6,
        'Nguema Patrick',
        '+237699001013',
        'nguema.p@email.cm',
        NULL,
        'F'
    ),
    (
        'LYC2024014',
        'Abena',
        'Tom',
        '2005-01-14',
        9,
        'Abena Marguerite',
        '+237699001014',
        'abena.m@email.cm',
        NULL,
        'M'
    ),
    (
        'LYC2024015',
        'Eboa',
        'Diana',
        '2005-08-07',
        9,
        'Eboa Ferdinand',
        '+237699001015',
        'eboa.f@email.cm',
        NULL,
        'F'
    ),
    (
        'LYC2024016',
        'Mvondo',
        'Ryan',
        '2007-05-31',
        3,
        'Mvondo Clarisse',
        '+237699001016',
        'mvondo.c@email.cm',
        NULL,
        'M'
    ),
    (
        'LYC2024017',
        'Ayolo',
        'Nina',
        '2006-07-19',
        6,
        'Ayolo Thomas',
        '+237699001017',
        'ayolo.t@email.cm',
        NULL,
        'F'
    ),
    (
        'LYC2024018',
        'Noa',
        'Steve',
        '2005-11-23',
        8,
        'Noa Cécile',
        '+237699001018',
        'noa.c@email.cm',
        NULL,
        'M'
    ),
    (
        'LYC2024019',
        'Bongo',
        'Lea',
        '2008-02-16',
        1,
        'Bongo Michel',
        '+237699001019',
        'bongo.m@email.cm',
        NULL,
        'F'
    ),
    (
        'LYC2024020',
        'Engonga',
        'Joel',
        '2007-06-04',
        2,
        'Engonga Sylvie',
        '+237699001020',
        'engonga.s@email.cm',
        NULL,
        'M'
    );

-- ============================================================
-- DONNÉES DE DÉMONSTRATION : note_eleve (20 lignes)
-- ============================================================
INSERT INTO
    note_eleve (
        eleve_id,
        matiere,
        coefficient,
        notes_valeur,
        trimestre,
        prof_saisie
    )
VALUES
    (1, 'Mathématiques', 4, 14.50, 1, 'M. Fouda'),
    (1, 'Français', 4, 12.00, 1, 'Mme Bella'),
    (2, 'Mathématiques', 4, 16.25, 1, 'M. Fouda'),
    (2, 'Sciences', 3, 13.00, 1, 'M. Ondoa'),
    (3, 'Anglais', 3, 11.50, 1, 'Mme Ngono'),
    (3, 'Histoire-Géo', 2, 09.75, 1, 'M. Mbarga'),
    (4, 'Mathématiques', 4, 07.50, 1, 'M. Fouda'),
    (4, 'Physique-Chimie', 3, 08.00, 1, 'M. Biyong'),
    (5, 'Mathématiques', 4, 18.00, 2, 'M. Fouda'),
    (5, 'Français', 4, 15.50, 2, 'Mme Bella'),
    (6, 'Informatique', 2, 17.00, 2, 'M. Owona'),
    (6, 'Sciences', 3, 14.00, 2, 'M. Ondoa'),
    (7, 'Philosophie', 3, 11.00, 2, 'Mme Essama'),
    (7, 'Mathématiques', 4, 19.00, 2, 'M. Fouda'),
    (8, 'Histoire-Géo', 2, 13.50, 3, 'M. Mbarga'),
    (8, 'Anglais', 3, 16.00, 3, 'Mme Ngono'),
    (9, 'Physique-Chimie', 3, 10.25, 3, 'M. Biyong'),
    (10, 'Mathématiques', 4, 05.50, 3, 'M. Fouda'),
    (11, 'Français', 4, 14.75, 1, 'Mme Bella'),
    (12, 'Sciences', 3, 12.50, 1, 'M. Ondoa');

-- ============================================================
-- DONNÉES DE DÉMONSTRATION : absence (20 lignes)
-- ============================================================
INSERT INTO
    absence (
        eleve_id,
        date_absence,
        duree_heures,
        matiere,
        justifiee,
        motif
    )
VALUES
    (1, '2024-10-05', 2, 'Mathématiques', FALSE, NULL),
    (2, '2024-10-07', 1, 'Français', TRUE, 'Maladie'),
    (3, '2024-10-10', 3, 'Anglais', FALSE, NULL),
    (
        4,
        '2024-10-12',
        2,
        'Physique-Chimie',
        FALSE,
        NULL
    ),
    (
        5,
        '2024-11-03',
        1,
        'Français',
        TRUE,
        'Convocation'
    ),
    (6, '2024-11-05', 4, 'Informatique', FALSE, NULL),
    (7, '2024-11-08', 2, 'Philosophie', FALSE, NULL),
    (
        8,
        '2024-11-12',
        1,
        'Anglais',
        TRUE,
        'Décès familial'
    ),
    (
        9,
        '2024-11-15',
        3,
        'Physique-Chimie',
        FALSE,
        NULL
    ),
    (10, '2024-11-18', 2, 'Mathématiques', FALSE, NULL),
    (11, '2024-12-02', 1, 'Français', TRUE, 'Maladie'),
    (12, '2024-12-05', 2, 'Sciences', FALSE, NULL),
    (13, '2024-12-09', 3, 'Mathématiques', FALSE, NULL),
    (
        14,
        '2024-12-11',
        1,
        'Philosophie',
        TRUE,
        'Convocation admin'
    ),
    (15, '2024-12-14', 4, 'Histoire-Géo', FALSE, NULL),
    (1, '2025-01-08', 2, 'Français', FALSE, NULL),
    (3, '2025-01-10', 1, 'Mathématiques', FALSE, NULL),
    (7, '2025-01-14', 3, 'Mathématiques', FALSE, NULL),
    (
        10,
        '2025-01-16',
        2,
        'Physique-Chimie',
        FALSE,
        NULL
    ),
    (16, '2025-01-20', 1, 'Anglais', TRUE, 'Maladie');

-- ============================================================
-- DONNÉES DE DÉMONSTRATION : utilisateurs (20 lignes)
-- Mots de passe en clair : Admin123!, Censeur1!, Surv001!  etc.
-- (stocker en BCrypt à l'initialisation via l'application)
-- Pour les tests le hash ci-dessous correspond à "Password1!"
-- $2a$12$hashed... est un placeholder ; l'appli doit re-hacher
-- ============================================================
INSERT INTO
    utilisateurs (login, password_hache, role)
VALUES
    (
        'admin',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Admin'
    ),
    (
        'censeur1',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Censeur'
    ),
    (
        'surveillant1',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Surveillant'
    ),
    (
        'surveillant2',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Surveillant'
    ),
    (
        'censeur2',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Censeur'
    ),
    (
        'admin2',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Admin'
    ),
    (
        'surveillant3',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Surveillant'
    ),
    (
        'surveillant4',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Surveillant'
    ),
    (
        'censeur3',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Censeur'
    ),
    (
        'surveillant5',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Surveillant'
    ),
    (
        'surveillant6',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Surveillant'
    ),
    (
        'censeur4',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Censeur'
    ),
    (
        'surveillant7',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Surveillant'
    ),
    (
        'surveillant8',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Surveillant'
    ),
    (
        'admin3',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Admin'
    ),
    (
        'censeur5',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Censeur'
    ),
    (
        'surveillant9',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Surveillant'
    ),
    (
        'surveillant10',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Surveillant'
    ),
    (
        'censeur6',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Censeur'
    ),
    (
        'censeur7',
        '$2a$12$WBFGi3ZkHqb0TJqyMuYHzuGJY8C.b5nBJ9qKc0tPZlpHfME4VrJkO',
        'Censeur'
    );

-- NOTE: Remplacer les hashes par de vrais hashes BCrypt générés au démarrage.
-- Mot de passe par défaut pour les tests : Password1!

-- ============================================================
-- TABLE : parametre (clé/valeur — configuration établissement)
-- ============================================================
CREATE TABLE IF NOT EXISTS parametre (
    cle VARCHAR(60) PRIMARY KEY,
    valeur TEXT
) ENGINE = InnoDB;

INSERT INTO parametre (cle, valeur) VALUES
    ('nom_etablissement', 'Lycée de Démonstration'),
    ('annee_scolaire', '2024-2025'),
    ('entete_pdf', 'RÉPUBLIQUE DU CAMEROUN\nMINISTÈRE DES ENSEIGNEMENTS SECONDAIRES\nDÉLÉGATION RÉGIONALE DU CENTRE'),
    ('delegation', 'DÉLÉGATION RÉGIONALE DU CENTRE'),
    ('republique', 'RÉPUBLIQUE DU CAMEROUN'),
    ('ministere', 'MINISTÈRE DES ENSEIGNEMENTS SECONDAIRES'),
    ('ville', 'Yaoundé'),
    ('logo_filename', ''),
    ('filigrane_logo', 'false')
ON DUPLICATE KEY UPDATE cle = cle;

-- ============================================================
-- TABLE : notification
-- ============================================================
CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_cible VARCHAR(30) COMMENT 'NULL = tous les rôles',
    message VARCHAR(500) NOT NULL,
    lien VARCHAR(255),
    type VARCHAR(30) DEFAULT 'info',
    lue BOOLEAN DEFAULT FALSE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- Migration base existante (si eleve créée sans sexe) :
-- ALTER TABLE eleve ADD COLUMN sexe CHAR(1) DEFAULT 'M' COMMENT 'M ou F' AFTER photo_filename;