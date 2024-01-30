INSERT INTO user_details (id, full_name, age, discount_for_next_ticket) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'Mischie Andrei', 21, 0),
('550e8400-e29b-41d4-a716-446655440001', 'Vasile David', 21, 0),
('550e8400-e29b-41d4-a716-446655440002', 'Nichifor Andra', 21, 0);

INSERT INTO "user" (id, email, verified, password, promo_code, user_details_id) VALUES
(
    '5b8b414f-53e0-440c-ac22-85c11ba4d1af',
    'danielmamara71@gmail.com',
    true,
    '$2a$10$/JQAGOL4ifsFXfcUb2mV3ekUyliua2GtA2DlF.ZKEmLFA.S2m58Cy',
    NULL,
    NULL
),

(
    '373fe779-8e0b-47f3-abc9-d33c4612efd9',
    'gureanuraul24@gmail.com',
    true,
    '$2a$10$p9wiwOdK/.dfT8KbhkVwuO3MhOO/3zyKEBCGuWgz6mF0Qxr38gqfq',
    NULL,
    NULL
),

(
    '06d36936-d212-4bfa-b0f8-0ba1466ce65c',
    'mischie.andrey@gmail.com',
    true,
    '$2a$10$ZJ075ZNsBm/dxDisaYa/c.hI14vaFHi8c3uZLS8/s7nnTIhCvVT1W',
    NULL,
    '550e8400-e29b-41d4-a716-446655440000'
),

(
    'cec281ab-64e2-4cdd-bc40-b278a16e8e13',
    'vasiled027@gmail.com',
    true,
    '$2a$10$IvJOHsxgb3AGiUzhaLYbee2WuG2wZLH/PmVk.iyC9YVahjC470i4a',
    NULL,
    '550e8400-e29b-41d4-a716-446655440001'
),

(
    'e2d82bd4-77f3-4ac7-a341-ecd00f02cb4b',
    'andra.nichifor2@gmail.com',
    true,
    '$2a$10$ufw8uIKaav12YVD91ztsi.X7xuU3QbHc99g2r84CTUrbnERvjD7Ky',
    NULL,
    '550e8400-e29b-41d4-a716-446655440002'
),

(
    'd677f93b-5ede-4f70-92cc-533beadf15ca',
    'scanner1',
    true,
    '$2a$10$ufw8uIKaav12YVD91ztsi.X7xuU3QbHc99g2r84CTUrbnERvjD7Ky',
    NULL,
    NULL
),

(
    '0582feb5-0f1d-434f-95da-e3a6ad932bb2',
    'scanner2',
    true,
    '$2a$10$ufw8uIKaav12YVD91ztsi.X7xuU3QbHc99g2r84CTUrbnERvjD7Ky',
    NULL,
    NULL
),

(
    '39d6d309-494d-400c-91b6-b2e47afe7729',
    'scanner3',
    true,
    '$2a$10$ufw8uIKaav12YVD91ztsi.X7xuU3QbHc99g2r84CTUrbnERvjD7Ky',
    NULL,
    NULL
);

INSERT INTO user_roles (user_id, role_id) VALUES
(
    '5b8b414f-53e0-440c-ac22-85c11ba4d1af',
    'd7a1b382-8e75-4a45-9fe7-c8a8d43b4b9a'
),
(
    '373fe779-8e0b-47f3-abc9-d33c4612efd9',
    'd7a1b382-8e75-4a45-9fe7-c8a8d43b4b9a'
),
(
    '06d36936-d212-4bfa-b0f8-0ba1466ce65c',
    'b85f9a1e-8c7d-4d45-a9d1-3e4c6a7b8d2b'
),
(
    'cec281ab-64e2-4cdd-bc40-b278a16e8e13',
    'b85f9a1e-8c7d-4d45-a9d1-3e4c6a7b8d2b'
),
(
    'e2d82bd4-77f3-4ac7-a341-ecd00f02cb4b',
    'b85f9a1e-8c7d-4d45-a9d1-3e4c6a7b8d2b'
),
(
    'd677f93b-5ede-4f70-92cc-533beadf15ca',
    'ac3c2f3d-6e5e-4f3f-8f8e-b4c9e2a1b5c8'
),
(
    '0582feb5-0f1d-434f-95da-e3a6ad932bb2',
    'ac3c2f3d-6e5e-4f3f-8f8e-b4c9e2a1b5c8'
),
(
    '39d6d309-494d-400c-91b6-b2e47afe7729',
    'ac3c2f3d-6e5e-4f3f-8f8e-b4c9e2a1b5c8'
);


