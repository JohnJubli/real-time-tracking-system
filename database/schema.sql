-- Users table
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50),
  password VARCHAR(255),
  role VARCHAR(20)
);

-- Location table
CREATE TABLE locations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT,
  latitude DOUBLE,
  longitude DOUBLE,
  timestamp DATETIME
);
