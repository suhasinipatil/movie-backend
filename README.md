# movie-backend
 ## Setup for PostgresSQL
 ```
 C:\Users\suhasini>psql -U postgres
Password for user postgres:
psql (14.6)
WARNING: Console code page (437) differs from Windows code page (1252)
         8-bit characters might not work correctly. See psql reference
         page "Notes for Windows users" for details.
Type "help" for help.

postgres=# CREATE DATABASE movie;
CREATE DATABASE
postgres=# CREATE USER movie_user WITH ENCRYPTED PASSWORD '<>';
CREATE ROLE
postgres=# GRANT ALL PRIVILEGES ON DATABASE todo TO movie_user;
GRANT
postgres=# \q
```
