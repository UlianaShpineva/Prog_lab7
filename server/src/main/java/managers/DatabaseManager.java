package managers;

import main.App;
import models.*;
import network.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class DatabaseManager {
    private Connection connection;
    private MessageDigest md;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrs" +
            "tuvwxyz0123456789<>?:@{!$%^&*()_+£$";
    private static final String PEPPER = "[g$J*(l;";
    private static final Logger databaseLogger = LogManager.getLogger(DatabaseManager.class);
    public DatabaseManager() {
        try {
            md = MessageDigest.getInstance(App.HASHING_ALGORITHM);
            this.connect();
            this.createMainBase();
        } catch (SQLException e) {
            databaseLogger.warn("Таблицы уже созданы");
        } catch (NoSuchAlgorithmException e) {
            databaseLogger.fatal("Такого алгоритма нет!");
        }
    }

    public void connect(){
        Properties info = null;
        try {
            info = new Properties();
            info.load(new FileInputStream(App.DATABASE_CONFIG_PATH));
            connection = DriverManager.getConnection(App.DATABASE_URL, info);
            databaseLogger.info("Успешно подключен к базе данных");
        } catch (SQLException | IOException e) {
            try{

                connection = DriverManager.getConnection(App.DATABASE_URL_HELIOS, info);
            } catch (SQLException ex) {
                databaseLogger.fatal("Невозможно подключиться к базе данных");
                databaseLogger.warn(e);
                databaseLogger.warn(ex);
                System.exit(1);
            }
        }
    }

    public void createMainBase() throws SQLException {
        connection
                .prepareStatement(DatabaseCommands.allTablesCreation)
                .execute();
        databaseLogger.info("Таблицы созданы");
    }

    public boolean checkExistUser(String login) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(DatabaseCommands.getUser);
        ps.setString(1, login);
        ResultSet resultSet = ps.executeQuery();
        return resultSet.next();
    }

    private String generateRandomString() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private String getSHA1Hash(String input){
        byte[] inputBytes = input.getBytes();
        md.update(inputBytes);
        byte[] hashBytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public void addUser(User user) throws SQLException {
        String login = user.name();
        String salt = this.generateRandomString();
        String pass = PEPPER + user.password() + salt;

        PreparedStatement ps = connection.prepareStatement(DatabaseCommands.addUser);
        if (this.checkExistUser(login)) throw new SQLException();
        ps.setString(1, login);
        ps.setString(2, this.getSHA1Hash(pass));
        ps.setString(3, salt);
        ps.execute();
        databaseLogger.info("Добавлен пользователь " + user);
    }

    public boolean confirmUser(User inputUser){
        try {
            String login = inputUser.name();
            PreparedStatement getUser = connection.prepareStatement(DatabaseCommands.getUser);
            getUser.setString(1, login);
            ResultSet resultSet = getUser.executeQuery();
            if(resultSet.next()) {
                String salt = resultSet.getString("salt");
                String toCheckPass = this.getSHA1Hash(PEPPER + inputUser.password() + salt);
                return toCheckPass.equals(resultSet.getString("password"));
            }
            else {
                return false;
            }
        } catch (SQLException e) {
            databaseLogger.fatal("Неверная команда sql");
            databaseLogger.debug(e);
            return false;
        }
    }

    public int addObject(StudyGroup studyGroup, User user){
        try {
            PreparedStatement ps = connection.prepareStatement(DatabaseCommands.addObject);
            ps.setString(1, studyGroup.getName());
            ps.setLong(2, studyGroup.getCoordinates().getX());
            ps.setInt(3, studyGroup.getCoordinates().getY());
            ps.setTimestamp(4, Timestamp.valueOf(studyGroup.getCreationDate().toLocalDateTime()));
            ps.setLong(5, studyGroup.getStudentsCount());
            ps.setInt(6, studyGroup.getExpelledStudents());
            ps.setInt(7, studyGroup.getTransferredStudents());
            ps.setObject(8, studyGroup.getFormOfEducation(), Types.OTHER);
            ps.setString(9, studyGroup.getGroupAdmin().getName());
            ps.setString(10, studyGroup.getGroupAdmin().getPassportID());
            ps.setObject(11, studyGroup.getGroupAdmin().getNationality(), Types.OTHER);
            ps.setLong(12, studyGroup.getGroupAdmin().getLocation().getX());
            ps.setInt(13, studyGroup.getGroupAdmin().getLocation().getY());
            ps.setDouble(14, studyGroup.getGroupAdmin().getLocation().getZ());
            ps.setString(15, studyGroup.getGroupAdmin().getLocation().getName());
            ps.setString(16, user.name());
            ResultSet resultSet = ps.executeQuery();

            if (!resultSet.next()) {
                databaseLogger.info("Объект не добавлен в таблицу");
                return -1;
            }
            databaseLogger.info("Объект добавлен в таблицу");
            return resultSet.getInt(1);
        } catch (SQLException e) {
            databaseLogger.info("Объект не добавлен в таблицу");
            databaseLogger.debug(e);
            return -1;
        }
    }

    public boolean updateObject(int id, StudyGroup studyGroup, User user){
        try {
            PreparedStatement ps = connection.prepareStatement(DatabaseCommands.updateUserObject);
            ps.setString(1, studyGroup.getName());
            ps.setLong(2, studyGroup.getCoordinates().getX());
            ps.setInt(3, studyGroup.getCoordinates().getY());
            ps.setTimestamp(4, Timestamp.valueOf(studyGroup.getCreationDate().toLocalDateTime()));
            ps.setLong(5, studyGroup.getStudentsCount());
            ps.setInt(6, studyGroup.getExpelledStudents());
            ps.setInt(7, studyGroup.getTransferredStudents());
            ps.setObject(8, studyGroup.getFormOfEducation(), Types.OTHER);
            ps.setString(9, studyGroup.getGroupAdmin().getName());
            ps.setString(10, studyGroup.getGroupAdmin().getPassportID());
            ps.setObject(11, studyGroup.getGroupAdmin().getNationality(), Types.OTHER);
            ps.setLong(12, studyGroup.getGroupAdmin().getLocation().getX());
            ps.setInt(13, studyGroup.getGroupAdmin().getLocation().getY());
            ps.setDouble(14, studyGroup.getGroupAdmin().getLocation().getZ());
            ps.setString(15, studyGroup.getGroupAdmin().getLocation().getName());

            ps.setInt(16, id);
            ps.setString(17, user.name());
            ResultSet resultSet = ps.executeQuery();
            System.out.println(resultSet);
            return resultSet.next();
        } catch (SQLException e) {
            databaseLogger.debug(e);
            return false;
        }
    }

    public boolean deleteObject(int id, User user){
        try{
            PreparedStatement ps = connection.prepareStatement(DatabaseCommands.deleteUserObject);
            ps.setString(1, user.name());
            ps.setInt(2, id);
            ResultSet resultSet = ps.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            databaseLogger.error("Не удалось удалить объект");
            databaseLogger.debug(e);
            return false;
        }
    }

    public boolean deleteAllObjects(User user, List<Integer> ids){
        try {
            for (Integer id : ids) {
                PreparedStatement ps = connection.prepareStatement(DatabaseCommands.deleteUserOwnedObjects);
                ps.setString(1, user.name());
                ps.setInt(2, id);
                ResultSet resultSet = ps.executeQuery();
            }
            databaseLogger.warn("Удалены все строки таблицы studyGroup, принадлежащие " + user.name());
            return true;
        } catch (SQLException e) {
            databaseLogger.error("Не удалось удалить строки таблицы studyGroup");
            databaseLogger.debug(e);
            return false;
        }
    }

    public ArrayDeque<StudyGroup> loadCollection(){
        try {
            PreparedStatement ps = connection.prepareStatement(DatabaseCommands.getAllObjects);
            ResultSet resultSet = ps.executeQuery();
            ArrayDeque<StudyGroup> collection = new ArrayDeque<>();
            while (resultSet.next()){
                collection.add(new StudyGroup(
                        resultSet.getInt("id"),
                        resultSet.getString("group_name"),
                        new Coordinates(
                                resultSet.getLong("cord_x"),
                                resultSet.getInt("cord_y")
                        ),
                        resultSet.getTimestamp("creation_date").toInstant().atZone(ZoneId.of("Europe/Moscow")),
                        resultSet.getLong("students_count"),
                        resultSet.getInt("expelled_students"),
                        resultSet.getInt("transferred_students"),
                        FormOfEducation.valueOf(resultSet.getString("form_of_education")),
                        new Person(
                                resultSet.getString("person_name"),
                                resultSet.getString("person_passport_ID"),
                                Country.valueOf(resultSet.getString("person_nationality")),
                                new Location(
                                        resultSet.getLong("person_location_x"),
                                        resultSet.getInt("person_location_y"),
                                        resultSet.getDouble("person_location_z"),
                                        resultSet.getString("person_location_name")
                                )
                        ),
                        resultSet.getString("owner_login")
                ));
            }
            databaseLogger.info("Коллекция успешно загружена из таблицы");
            return collection;
        } catch (SQLException e) {
            databaseLogger.warn("Коллекция пуста либо возникла ошибка при исполнении запроса");
            return new ArrayDeque<>();
        }
    }
}
