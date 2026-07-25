CREATE TABLE IF NOT EXISTS users (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
name varchar NOT NULL,
email varchar NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS item_requests (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
description varchar NOT NULL,
requestor_id BIGINT REFERENCES users(id) NOT NULL,
created timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
name varchar NOT NULL,
description varchar NOT NULL,
available boolean NOT NULL,
owner_id BIGINT REFERENCES users(id) NOT NULL,
item_request_id BIGINT REFERENCES item_requests(id),
CONSTRAINT unique_item UNIQUE (name, description, owner_id, item_request_id)
);


CREATE TABLE IF NOT EXISTS bookings (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
start_time timestamp NOT NULL,
end_time timestamp NOT NULL,
item_id BIGINT REFERENCES items(id) NOT NULL,
booker_id BIGINT REFERENCES users(id) NOT NULL,
booking_status varchar NOT NULL,
CONSTRAINT valid_endtime CHECK (end_time > start_time)
);

CREATE TABLE IF NOT EXISTS comments (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
text varchar NOT NULL,
author_name varchar NOT NULL,
item_id BIGINT REFERENCES items(id) NOT NULL,
created timestamp NOT NULL
);
