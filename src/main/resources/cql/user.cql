CREATE KEYSPACE IF NOT EXISTS testKeySpace WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};
CREATE TABLE IF NOT EXISTS testKeySpace.user(user_id text, firstname text, lastname text, city text, PRIMARY KEY (user_id, firstname));
