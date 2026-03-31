-- ============================================================
-- Seed Data — development only
-- ============================================================

-- Password for all seed users = "Password@123"
-- BCrypt hash of "Password@123"
SET @pwd = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lHuu';

-- ─────────────────────────────────────────────
-- USERS
-- ─────────────────────────────────────────────
INSERT INTO users (full_name, email, password, phone, role, is_active) VALUES
                                                                           ('Admin User',        'admin@blood.com',     @pwd, '9000000001', 'ADMIN',    1),
                                                                           ('Rahul Sharma',      'rahul@donor.com',     @pwd, '9000000002', 'DONOR',    1),
                                                                           ('Priya Singh',       'priya@donor.com',     @pwd, '9000000003', 'DONOR',    1),
                                                                           ('Amit Verma',        'amit@donor.com',      @pwd, '9000000004', 'DONOR',    1),
                                                                           ('Neha Gupta',        'neha@receiver.com',   @pwd, '9000000005', 'RECEIVER', 1),
                                                                           ('Vikram Patel',      'vikram@receiver.com', @pwd, '9000000006', 'RECEIVER', 1),
                                                                           ('AIIMS Delhi',       'aiims@hospital.com',  @pwd, '9000000007', 'HOSPITAL', 1),
                                                                           ('Apollo Hospital',   'apollo@hospital.com', @pwd, '9000000008', 'HOSPITAL', 1);

-- ─────────────────────────────────────────────
-- DONOR PROFILES
-- ─────────────────────────────────────────────
INSERT INTO donor_profiles
(user_id, blood_group, age, weight_kg, city, state, country,
 latitude, longitude, last_donation_date, is_available,
 has_medical_condition, total_donations)
VALUES
    (2, 'O_NEGATIVE',  28, 72.0, 'Delhi',   'Delhi',       'India', 28.6139, 77.2090, '2024-06-01', 1, 0, 5),
    (3, 'A_POSITIVE',  25, 58.0, 'Mumbai',  'Maharashtra', 'India', 19.0760, 72.8777, '2024-03-15', 1, 0, 3),
    (4, 'B_POSITIVE',  32, 80.0, 'Delhi',   'Delhi',       'India', 28.7041, 77.1025, NULL,          1, 0, 0);

-- ─────────────────────────────────────────────
-- HOSPITALS
-- ─────────────────────────────────────────────
INSERT INTO hospitals
(user_id, hospital_name, license_number, address, city, state,
 latitude, longitude, contact_person, emergency_phone, is_verified)
VALUES
    (7, 'AIIMS Delhi',     'AIIMS-DL-001', 'Ansari Nagar, New Delhi', 'Delhi',  'Delhi',       28.5672, 77.2100, 'Dr. Ramesh Kumar', '011-26588500', 1),
    (8, 'Apollo Hospital', 'APOL-MH-002', 'Sarita Vihar, New Delhi',  'Delhi',  'Delhi',       28.5355, 77.2910, 'Dr. Sunita Rao',   '011-71791090', 1);

-- ─────────────────────────────────────────────
-- BLOOD INVENTORY
-- ─────────────────────────────────────────────
INSERT INTO blood_inventory (hospital_id, blood_group, units_available, minimum_threshold)
VALUES
    (1, 'A_POSITIVE',  15, 5),
    (1, 'A_NEGATIVE',   4, 3),
    (1, 'B_POSITIVE',  12, 5),
    (1, 'B_NEGATIVE',   2, 3),
    (1, 'AB_POSITIVE',  8, 3),
    (1, 'AB_NEGATIVE',  1, 2),
    (1, 'O_POSITIVE',  20, 8),
    (1, 'O_NEGATIVE',   3, 5),
    (2, 'A_POSITIVE',  10, 5),
    (2, 'B_POSITIVE',   9, 5),
    (2, 'O_POSITIVE',  14, 8),
    (2, 'O_NEGATIVE',   2, 5);

-- ─────────────────────────────────────────────
-- RECEIVER REQUESTS
-- ─────────────────────────────────────────────
INSERT INTO receiver_requests
(receiver_user_id, blood_group, units_required, status,
 patient_name, hospital, city, state,
 latitude, longitude, urgency_level, notes)
VALUES
    (5, 'O_NEGATIVE', 2, 'PENDING',   'Neha Gupta',   'AIIMS Delhi',     'Delhi',  'Delhi', 28.5672, 77.2100, 'HIGH',     'Post surgery requirement'),
    (6, 'A_POSITIVE', 1, 'PENDING',   'Vikram Patel', 'Apollo Hospital', 'Delhi',  'Delhi', 28.5355, 77.2910, 'MEDIUM',   'Scheduled transfusion');