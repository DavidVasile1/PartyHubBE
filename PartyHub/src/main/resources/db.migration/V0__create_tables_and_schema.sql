
CREATE EXTENSION "uuid-ossp";

CREATE TABLE user_details (
    id UUID PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    age INT NOT NULL,
    discount_for_next_ticket INT
);

CREATE TABLE "user" (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    promo_code VARCHAR(255),
    verified BOOLEAN,
    verification_token UUID,
    user_details_id UUID,
    FOREIGN KEY (user_details_id) REFERENCES user_details(id)
);

CREATE TABLE role (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE user_roles (
    user_id UUID,
    role_id UUID,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);

CREATE TABLE event (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    main_banner VARCHAR(255),
    secondary_banner VARCHAR(255),
    location VARCHAR(255),
    date DATE,
    details TEXT,
    price FLOAT,
    discount FLOAT,
    tickets_number INT,
    tickets_left INT
);

CREATE TABLE ticket (
    id UUID PRIMARY KEY,
    validation_date DATE,
    price_paid FLOAT,
    type VARCHAR(255),
    event_id UUID,
    FOREIGN KEY (event_id) REFERENCES event(id)
);




