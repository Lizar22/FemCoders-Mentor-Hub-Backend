INSERT INTO users (id, username, email, password, role) VALUES
(1, 'Cris', 'cris.mouta@test.com', '$2a$12$78Gr4VVIp8QUeFoJOekOpezke7773Btetcf944r7tNXOAj.URoDDi', 'MENTOR'),
(2, 'Ana', 'ana.lopez@test.com', '$2a$12$78Gr4VVIp8QUeFoJOekOpezke7773Btetcf944r7tNXOAj.URoDDi', 'MENTOR'),
(3, 'Lucía', 'lucia.martinez@test.com', '$2a$12$78Gr4VVIp8QUeFoJOekOpezke7773Btetcf944r7tNXOAj.URoDDi', 'MENTOR'),
(4, 'Marta', 'marta.perez@test.com', '$2a$12$78Gr4VVIp8QUeFoJOekOpezke7773Btetcf944r7tNXOAj.URoDDi', 'MENTOR'),
(5, 'Elena', 'elena.garcia@test.com', '$2a$12$78Gr4VVIp8QUeFoJOekOpezke7773Btetcf944r7tNXOAj.URoDDi', 'MENTOR'),
(6, 'Sofia', 'sofia.gomez@test.com', '$2a$12$78Gr4VVIp8QUeFoJOekOpezke7773Btetcf944r7tNXOAj.URoDDi', 'MENTEE'),
(7, 'Laura', 'laura.rodriguez@test.com', '$2a$12$78Gr4VVIp8QUeFoJOekOpezke7773Btetcf944r7tNXOAj.URoDDi', 'MENTEE'),
(8, 'Paula', 'paula.martin@test.com', '$2a$12$78Gr4VVIp8QUeFoJOekOpezke7773Btetcf944r7tNXOAj.URoDDi', 'MENTEE'),
(9, 'Carla', 'carla.lopez@test.com', '$2a$12$78Gr4VVIp8QUeFoJOekOpezke7773Btetcf944r7tNXOAj.URoDDi', 'MENTEE'),
(10, 'Irene', 'irene.sanchez@test.com', '$2a$12$78Gr4VVIp8QUeFoJOekOpezke7773Btetcf944r7tNXOAj.URoDDi', 'MENTEE');

INSERT INTO mentor_profiles (id, full_name, mentor_level, bio, user_id) VALUES
(1, 'Cris Mouta', 'SENIOR', 'Full-stack developer with 10 years of experience and a passion for teaching', 1),
(2, 'Ana López', 'JUNIOR', 'Passionate about backend development and mentoring', 2),
(3, 'Lucía Martínez', 'MID', 'Expert in APIs and microservices', 3),
(4, 'Marta Pérez', 'SENIOR', 'Front-end specialist and mentor for beginners', 4),
(5, 'Elena García', 'MID', 'Experienced in backend and data-driven projects', 5);

INSERT INTO mentor_technologies (mentor_id, technologies) VALUES
(1, 'Java'),
(1, 'Spring Boot'),
(2, 'Python'),
(2, 'JavaScript'),
(3, 'Java'),
(3, 'Spring Boot'),
(4, 'JavaScript'),
(4, 'React'),
(5, 'Python'),
(5, 'Django');
