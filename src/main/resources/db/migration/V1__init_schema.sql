-- ============================================================
-- Blood Donation Portal — Database Schema
-- Version: V1
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ─────────────────────────────────────────────
-- 1. USERS
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users
(
    id         BIGINT          NOT NULL AUTO_INCREMENT,
    full_name  VARCHAR(100)    NOT NULL,
    email      VARCHAR(150)    NOT NULL UNIQUE,
    password   VARCHAR(255)    NOT NULL,
    phone      VARCHAR(20)     NOT NULL,
    role       ENUM('DONOR','RECEIVER','HOSPITAL','ADMIN') NOT NULL,
    is_active  TINYINT(1)      NOT NULL DEFAULT 1,
    created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_users_email   (email),
    INDEX idx_users_role    (role),
    INDEX idx_users_active  (is_active)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;


-- ─────────────────────────────────────────────
-- 2. DONOR PROFILES
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS donor_profiles
(
    id                   BIGINT          NOT NULL AUTO_INCREMENT,
    user_id              BIGINT          NOT NULL UNIQUE,
    blood_group          ENUM('A_POSITIVE','A_NEGATIVE',
    'B_POSITIVE','B_NEGATIVE',
    'AB_POSITIVE','AB_NEGATIVE',
    'O_POSITIVE','O_NEGATIVE')  NOT NULL,
    age                  INT             NOT NULL,
    weight_kg            DOUBLE          NOT NULL,
    city                 VARCHAR(100),
    state                VARCHAR(100),
    country              VARCHAR(100),
    latitude             DOUBLE          NOT NULL DEFAULT 0.0,
    longitude            DOUBLE          NOT NULL DEFAULT 0.0,
    last_donation_date   DATE,
    is_available         TINYINT(1)      NOT NULL DEFAULT 1,
    has_medical_condition TINYINT(1)     NOT NULL DEFAULT 0,
    medical_notes        TEXT,
    total_donations      INT             NOT NULL DEFAULT 0,
    updated_at           DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_donor_user
    FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE,
    INDEX idx_donor_blood_group   (blood_group),
    INDEX idx_donor_city          (city),
    INDEX idx_donor_available     (is_available),
    INDEX idx_donor_last_donation (last_donation_date),
    INDEX idx_donor_location      (latitude, longitude)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;


-- ─────────────────────────────────────────────
-- 3. HOSPITALS
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS hospitals
(
    id               BIGINT          NOT NULL AUTO_INCREMENT,
    user_id          BIGINT          NOT NULL UNIQUE,
    hospital_name    VARCHAR(200)    NOT NULL,
    license_number   VARCHAR(100)    NOT NULL UNIQUE,
    address          VARCHAR(300),
    city             VARCHAR(100),
    state            VARCHAR(100),
    latitude         DOUBLE          NOT NULL DEFAULT 0.0,
    longitude        DOUBLE          NOT NULL DEFAULT 0.0,
    contact_person   VARCHAR(100),
    emergency_phone  VARCHAR(20),
    is_verified      TINYINT(1)      NOT NULL DEFAULT 0,
    created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_hospital_user
    FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE,
    INDEX idx_hospital_city     (city),
    INDEX idx_hospital_verified (is_verified),
    INDEX idx_hospital_license  (license_number)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;


-- ─────────────────────────────────────────────
-- 4. BLOOD INVENTORY
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS blood_inventory
(
    id                  BIGINT  NOT NULL AUTO_INCREMENT,
    hospital_id         BIGINT  NOT NULL,
    blood_group         ENUM('A_POSITIVE','A_NEGATIVE',
    'B_POSITIVE','B_NEGATIVE',
    'AB_POSITIVE','AB_NEGATIVE',
    'O_POSITIVE','O_NEGATIVE') NOT NULL,
    units_available     INT     NOT NULL DEFAULT 0,
    minimum_threshold   INT     NOT NULL DEFAULT 5,
    last_updated        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_inventory_hospital
    FOREIGN KEY (hospital_id) REFERENCES hospitals (id)
    ON DELETE CASCADE,
    UNIQUE KEY uq_hospital_blood_group (hospital_id, blood_group),
    INDEX idx_inventory_blood_group (blood_group),
    INDEX idx_inventory_units       (units_available)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;


-- ─────────────────────────────────────────────
-- 5. RECEIVER REQUESTS
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS receiver_requests
(
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    receiver_user_id    BIGINT          NOT NULL,
    blood_group         ENUM('A_POSITIVE','A_NEGATIVE',
    'B_POSITIVE','B_NEGATIVE',
    'AB_POSITIVE','AB_NEGATIVE',
    'O_POSITIVE','O_NEGATIVE') NOT NULL,
    units_required      INT             NOT NULL,
    status              ENUM('PENDING','MATCHED',
                             'FULFILLED','CANCELLED','EXPIRED')
    NOT NULL DEFAULT 'PENDING',
    patient_name        VARCHAR(100),
    hospital            VARCHAR(200),
    city                VARCHAR(100),
    state               VARCHAR(100),
    latitude            DOUBLE          NOT NULL DEFAULT 0.0,
    longitude           DOUBLE          NOT NULL DEFAULT 0.0,
    notes               VARCHAR(1000),
    urgency_level       ENUM('LOW','MEDIUM','HIGH','CRITICAL')
    NOT NULL DEFAULT 'MEDIUM',
    matched_donor_id    BIGINT,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_request_receiver
    FOREIGN KEY (receiver_user_id) REFERENCES users (id)
    ON DELETE CASCADE,
    CONSTRAINT fk_request_matched_donor
    FOREIGN KEY (matched_donor_id) REFERENCES donor_profiles (id)
    ON DELETE SET NULL,
    INDEX idx_request_status       (status),
    INDEX idx_request_blood_group  (blood_group),
    INDEX idx_request_city         (city),
    INDEX idx_request_urgency      (urgency_level),
    INDEX idx_request_receiver     (receiver_user_id),
    INDEX idx_request_created      (created_at)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;


-- ─────────────────────────────────────────────
-- 6. DONOR REQUESTS (offers to donate)
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS donor_requests
(
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    donor_profile_id    BIGINT          NOT NULL,
    receiver_request_id BIGINT,
    blood_group         ENUM('A_POSITIVE','A_NEGATIVE',
    'B_POSITIVE','B_NEGATIVE',
    'AB_POSITIVE','AB_NEGATIVE',
    'O_POSITIVE','O_NEGATIVE') NOT NULL,
    units_offered       INT             NOT NULL,
    status              ENUM('PENDING','ACCEPTED',
                             'REJECTED','COMPLETED','WITHDRAWN')
    NOT NULL DEFAULT 'PENDING',
    preferred_date      DATE,
    city                VARCHAR(100),
    state               VARCHAR(100),
    message             VARCHAR(500),
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_donor_request_profile
    FOREIGN KEY (donor_profile_id) REFERENCES donor_profiles (id)
    ON DELETE CASCADE,
    CONSTRAINT fk_donor_request_receiver
    FOREIGN KEY (receiver_request_id) REFERENCES receiver_requests (id)
    ON DELETE SET NULL,
    UNIQUE KEY uq_donor_receiver_offer (donor_profile_id, receiver_request_id),
    INDEX idx_donor_req_status   (status),
    INDEX idx_donor_req_donor    (donor_profile_id),
    INDEX idx_donor_req_receiver (receiver_request_id)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;


-- ─────────────────────────────────────────────
-- 7. DONATION HISTORY
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS donation_history
(
    id                  BIGINT  NOT NULL AUTO_INCREMENT,
    donor_id            BIGINT  NOT NULL,
    receiver_request_id BIGINT,
    hospital_id         BIGINT,
    blood_group         ENUM('A_POSITIVE','A_NEGATIVE',
    'B_POSITIVE','B_NEGATIVE',
    'AB_POSITIVE','AB_NEGATIVE',
    'O_POSITIVE','O_NEGATIVE') NOT NULL,
    units_donated       INT     NOT NULL,
    donation_date       DATE    NOT NULL,
    notes               TEXT,
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_history_donor
    FOREIGN KEY (donor_id) REFERENCES donor_profiles (id)
    ON DELETE CASCADE,
    CONSTRAINT fk_history_request
    FOREIGN KEY (receiver_request_id) REFERENCES receiver_requests (id)
    ON DELETE SET NULL,
    CONSTRAINT fk_history_hospital
    FOREIGN KEY (hospital_id) REFERENCES hospitals (id)
    ON DELETE SET NULL,
    INDEX idx_history_donor        (donor_id),
    INDEX idx_history_date         (donation_date),
    INDEX idx_history_blood_group  (blood_group)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;