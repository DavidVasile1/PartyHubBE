
CREATE EXTENSION "uuid-ossp";

CREATE TABLE user_details (
    id UUID PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    age INT NOT NULL,
    discount_for_next_ticket INT
);
CREATE TABLE statistics (
                            id UUID PRIMARY KEY,
                            tickets_sold INT,
                            money_earned NUMERIC,
                            generated_invites INT,
                            ticket_based_attendees INT,
                            invitation_based_attendees INT,
                        event_id UUID
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
    main_banner OID,
    secondary_banner OID,
    location VARCHAR(255),
    city VARCHAR(255),
    lng FLOAT,
    lat FLOAT,
    date DATE,
    details TEXT,
    price FLOAT,
    discount FLOAT,
    tickets_number INT,
    tickets_left INT,
    statistics_id UUID
);

CREATE TABLE ticket (
    id UUID PRIMARY KEY,
    validation_date TIMESTAMP,
    price_paid FLOAT,
    type VARCHAR(255),
    event_id UUID,
    FOREIGN KEY (event_id) REFERENCES event(id)
);

CREATE TABLE discount (
    id UUID PRIMARY KEY,
    event_id UUID,
    FOREIGN KEY (event_id) REFERENCES event(id),
    code VARCHAR(255),
    discount_value INT
);


