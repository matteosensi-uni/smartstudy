INSERT INTO library (name, opening_time, closing_time, street, number, city)
VALUES
    ('Biblioteca Centrale', '00:00', '00:00', 'Via Roma', '10', 'Milano'),
    ('Biblioteca Scientifica', '09:00', '20:00', 'Via Verdi', '15', 'Milano');


-- ===========================
-- TIME POLICIES
-- ===========================

INSERT INTO time_policy (max_temporary_leave_min, max_temporary_leave_times, name)
VALUES
    (1, 2, 'Standard'),
    (30, 3, 'Extended');


-- ===========================
-- STUDY AREAS
-- ===========================

INSERT INTO study_area (name, floor, type, id_library, id_policy)
VALUES
    ('Sala Studio A', 0, 'SILENT', 1, 1),
    ('Sala Studio B', 1, 'GROUP', 1, 2),
    ('Sala Informatica', 0, 'STANDARD', 2, 2),
    ('Sala Lettura', 1, 'SILENT', 2, 1);


-- ===========================
-- SEATS
-- ===========================

INSERT INTO seat (qr_code, type, status, id_area)
VALUES
    ('QR001','GROUP','UNAVAILABLE',1),
    ('QR002','GROUP','AVAILABLE',1),
    ('QR009','GROUP','BROKEN',1),

    ('QR003','INDIVIDUAL','AVAILABLE',2),
    ('QR004','INDIVIDUAL','AVAILABLE',2),

    ('QR005','GROUP','AVAILABLE',3),
    ('QR006','GROUP','AVAILABLE',3),

    ('QR007','INDIVIDUAL','AVAILABLE',4),
    ('QR008','INDIVIDUAL','AVAILABLE',4);


-- ===========================
-- USERS
-- ===========================

INSERT INTO app_user(name,password,surname,email)
VALUES
    ('Mario','password','Rossi','mario.rossi@test.it'),
    ('Luigi','password','Verdi','luigi.verdi@test.it'),
    ('Anna','password','Bianchi','anna.bianchi@test.it'),
    ('Marco','admin123','Neri','marco.neri@test.it'),
    ('Sara','admin123','Gialli','sara.gialli@test.it'),
    ('Luca','admin123','Esposito','luca.esposito@test.it');


-- ===========================
-- STUDENTS
-- ===========================

INSERT INTO student(user_id, card_active)
VALUES
    (1, TRUE),
    (2, TRUE),
    (3, FALSE);


-- ===========================
-- ADMINS
-- ===========================

INSERT INTO admin(user_id, is_present, id_library)
VALUES
    (4, TRUE, 1),
    (6, TRUE, 1),
    (5, FALSE, 2);


-- ===========================
-- ACCESS SESSIONS
-- ===========================

INSERT INTO access_session(entry_time, exit_time, id_library, student_id)
VALUES
    ('2026-07-24 09:00:00', NULL, 1, 1),

    ('2026-07-24 09:00:00', NULL, 1, 2),

    ('2026-07-24 10:00:00',
     '2026-07-24 12:30:00',
     2,
     2);


-- ===========================
-- RESERVATIONS
-- ===========================

INSERT INTO reservation(start_time,end_time,status,id_seat,access_id)
VALUES
    ('2026-07-24 10:10:00',
     '2026-07-24 12:00:00',
     'CLOSED',
     5,
     2),
    ('2026-07-24 10:10:00',
     null,
     'ACTIVE',
     1,
     1);


-- ===========================
-- TEMPORARY LEAVE
-- ===========================

INSERT INTO temporary_leave(start_time, expected_end_time, id_reservation)
VALUES
    ('2026-07-24 10:15:00',
     '2026-07-24 10:30:00',
     1);


-- ===========================
-- ABANDONMENT REPORT
-- ===========================

INSERT INTO abandonment_report(
    created_at,
    resolved_at,
    status,
    description,
    id_reservation,
    student_id,
    admin_id
)
VALUES(
          '2026-07-24 11:00:00',
          NULL,
          'CLOSED',
          'Lo studente ha lasciato gli effetti personali sul tavolo.',
          1,
          2,
          NULL
      );
