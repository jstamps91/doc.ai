CREATE TABLE diseases
(
    id INTEGER(8) NOT NULL,
    proper_name VARCHAR(50) NOT NULL,
    category VARCHAR(25) NOT NULL,
    "type" VARCHAR(15) NOT NULL,
    symptoms INTEGER[],
    severity VARCHAR(15) NOT NULL,
    vector VARCHAR(20),
    route_of_entry VARCHAR(20),
    region VARCHAR(10),
    incubation_min NUMBER(3),
    incubation_max NUMBER(4),
    infectious BOOLEAN NOT NULL,
    immunocomp BOOLEAN NOT NULL,
    precautions VARCHAR(75),
    comments VARCHAR(200)
);