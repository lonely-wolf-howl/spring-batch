INSERT INTO package (package_name, count, period, created_at)
VALUES ('Starter PT 10 Sessions', 10, 60, '2022-08-01 00:00:00'),
       ('Starter PT 20 Sessions', 20, 120, '2022-08-01 00:00:00'),
       ('Starter PT 30 Sessions', 30, 180, '2022-08-01 00:00:00'),
       ('Free Event Pilates 1 Session', 1, NULL, '2022-08-01 00:00:00'),
       ('Body Challenge PT 4 Weeks', NULL, 28, '2022-08-01 00:00:00'),
       ('Body Challenge PT 8 Weeks', NULL, 56, '2022-08-01 00:00:00'),
       ('InBody Consultation', NULL, NULL, '2022-08-01 00:00:00');

INSERT INTO `user` (user_id, user_name, status, phone, meta, created_at)
VALUES ('A1000000', 'Emily Johnson', 'ACTIVE', '010-2411-5823', NULL, '2022-08-01 00:00:00'),
       ('A1000001', 'Daniel Kim', 'ACTIVE', '010-3948-7762', NULL, '2022-08-01 00:00:00'),
       ('A1000002', 'Sophia Lee', 'INACTIVE', '010-5827-9134', NULL, '2022-08-01 00:00:00'),
       ('B1000000', 'Michael Brown', 'ACTIVE', '010-7365-2209', NULL, '2022-08-01 00:00:00'),
       ('B1000001', 'Olivia Park', 'INACTIVE', '010-8422-6173', NULL, '2022-08-01 00:00:00'),
       ('B2000000', 'James Kim', 'ACTIVE', '010-9531-7745', NULL, '2022-08-01 00:00:00'),
       ('B2000001', 'Grace Choi', 'ACTIVE', '010-1710-4896', NULL, '2022-08-01 00:00:00');

INSERT INTO user_group_mapping (user_group_id, user_id, user_group_name, description, created_at)
VALUES ('NVIDIA', 'A1000000', 'NVIDIA', 'Employee group of NVIDIA', '2022-08-01 00:00:00'),
       ('NVIDIA', 'A1000001', 'NVIDIA', 'Employee group of NVIDIA', '2022-08-01 00:00:00'),
       ('NVIDIA', 'A1000002', 'NVIDIA', 'Employee group of NVIDIA', '2022-08-01 00:00:00'),
       ('NVIDIA', 'B1000000', 'NVIDIA', 'Employee group of NVIDIA', '2022-08-01 00:00:00'),
       ('NVIDIA', 'B2000000', 'NVIDIA', 'Employee group of NVIDIA', '2022-08-01 00:00:00'),
       ('PALANTIR', 'B2000001', 'Palantir', 'Employee group of Palantir Technologies', '2022-08-01 00:00:00');