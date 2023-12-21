package jdbc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private final CustomDataSource customDataSource = CustomDataSource.getInstance();

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = """
        INSERT INTO myusers(
        firstname, lastname, age)
        VALUES (?, ?, ?);
        """;
    private static final String updateUserSQL = """
            UPDATE myusers
            SET firstname=?, lastname=?, age=?
            WHERE id = ?
            """;
    private static final String deleteUser = """
            DELETE FROM public.myusers
            WHERE id = ?
            """;
    private static final String findUserByIdSQL = """
            SELECT id, firstname, lastname, age FROM myusers
            WHERE id = ?
            """;
    private static final String findUserByNameSQL = """
            SELECT id, firstname, lastname, age FROM myusers
            WHERE firstname LIKE CONCAT('%', ?, '%')
            """;
    private static final String findAllUserSQL = """
            SELECT id, firstname, lastname, age FROM myusers
            """;

    public Long createUser(User user) {

        Long id = null;

        try (var conn = customDataSource.getConnection();
             var statement = conn.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setObject(1, user.getFirstName());
            statement.setObject(2, user.getLastName());
            statement.setObject(3, user.getAge());
            statement.execute();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public User findUserById(Long userId) {

        User user = null;

        try (var conn = customDataSource.getConnection();
             var statement = conn.prepareStatement(findUserByIdSQL)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = map(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public User findUserByName(String userName) {

        User user = null;

        try (var conn = customDataSource.getConnection();
             var statement = conn.prepareStatement(findUserByNameSQL)) {
            statement.setString(1, userName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = map(resultSet);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    public List<User> findAllUser() {

        List<User> users = new ArrayList<>();

        try (var conn = customDataSource.getConnection();
             var statement = conn.prepareStatement(findAllUserSQL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(map(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public User updateUser(User user) {
        try (var conn = customDataSource.getConnection();
             var statement = conn.prepareStatement(updateUserSQL)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setInt(3, user.getAge());
            statement.setLong(4, user.getId());
            if (statement.executeUpdate() != 0) {
                return findUserById(user.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void deleteUser(Long userId) {

        try (var conn = customDataSource.getConnection();
             var statement = conn.prepareStatement(deleteUser)) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User map(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .firstName(rs.getString("firstName"))
                .lastName(rs.getString("lastName"))
                .age(rs.getInt("age"))
                .build();
    }
}