package managers;

public class DatabaseCommands {
    public static final String allTablesCreation = """
            CREATE TYPE FORM_OF_EDUCATION AS ENUM(
                'DISTANCE_EDUCATION',
                'FULL_TIME_EDUCATION',
                'EVENING_CLASSES'
            );
            CREATE TYPE COUNTRY AS ENUM(
                'UNITED_KINGDOM',
                'USA',
                'GERMANY'
            );
            CREATE TABLE IF NOT EXISTS studyGroup (
                id SERIAL PRIMARY KEY,
                group_name TEXT NOT NULL,
                cord_x NUMERIC NOT NULL,
                cord_y NUMERIC NOT NULL,
                creation_date TIMESTAMP NOT NULL,
                students_count BIGINT NOT NULL,
                expelled_students BIGINT NOT NULL,
                transferred_students BIGINT NOT NULL,
                form_of_education FORM_OF_EDUCATION,
                person_name TEXT NOT NULL,
                person_passport_ID TEXT NOT NULL,
                person_nationality COUNTRY,
                person_location_x BIGINT NOT NULL,
                person_location_y BIGINT NOT NULL,
                person_location_z BIGINT NOT NULL,
                person_location_name TEXT NOT NULL,
                owner_login TEXT NOT NULL
            );
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                login TEXT,
                password TEXT,
                salt TEXT
            ); 
            """;

    public static final String addUser = """
            INSERT INTO users(login, password, salt) VALUES (?, ?, ?);""";

    public static final String getUser = """
            SELECT * FROM users WHERE (login = ?);""";

    public static final String addObject = """
            INSERT INTO studyGroup(group_name, cord_x, cord_y, creation_date, students_count, expelled_students, transferred_students, form_of_education, person_name, person_passport_ID, person_nationality, person_location_x, person_location_y, person_location_z, person_location_name, owner_login)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id;
            """;

    public static final String getAllObjects = """
            SELECT * FROM studyGroup;
            """;

    public static final String deleteUserOwnedObjects = """
            DELETE FROM studyGroup WHERE (owner_login = ?) AND (id = ?) RETURNING id;
            """;

    public static final String deleteUserObject = """
            DELETE FROM studyGroup WHERE (owner_login = ?) AND (id = ?) RETURNING id;
            """;

    public static final String updateUserObject = """
            UPDATE studyGroup
            SET (group_name, cord_x, cord_y, creation_date, students_count, expelled_students, transferred_students, form_of_education, person_name, person_passport_ID, person_nationality, person_location_x, person_location_y, person_location_z, person_location_name, owner_login)
             = (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            WHERE (id = ?) AND (owner_login = ?)
            RETURNING id;
            """;
}
