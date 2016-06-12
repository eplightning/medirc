package org.eplight.medirc.server.user.auth;

import org.eplight.medirc.protocol.Basic;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;

/**
 * Created by EpLightning on 27.03.2016.
 */
public class JDBCAuthentication implements Authentication {

    @Inject
    private Connection connection;

    @Override
    public int authenticate(Basic.Handshake msg) {
        if (msg.getCredentialsCase() != Basic.Handshake.CredentialsCase.SIMPLE || msg.getSimple() == null)
            return 0;

        String username = msg.getSimple().getUsername();
        String password = msg.getSimple().getPassword();

        if (username.isEmpty() || password.isEmpty())
            return 0;

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE name = ?");

            stmt.setString(1, username);

            ResultSet set = stmt.executeQuery();

            if (set.next()) {
                if (set.getString("hash").equals(hashPassword(password, set.getString("salt")))) {
                    return set.getInt("id");
                } else {
                    return 0;
                }
            } else {
                return autoCreateUser(username, password);
            }
        } catch (SQLException e) {
            // logger
        }

        return 0;
    }

    private int autoCreateUser(String username, String password) {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);

        String salt = DatatypeConverter.printHexBinary(bytes);
        String hash = hashPassword(password, salt);

        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO users (name, hash, salt) VALUES (" +
                    "?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, username);
            stmt.setString(2, hash);
            stmt.setString(3, salt);

            stmt.execute();

            ResultSet id = stmt.getGeneratedKeys();

            if (id.next()) {
                return id.getInt(1);
            } else {
                throw new RuntimeException("Generated ID not returned ?!");
            }
        } catch (SQLException e) {
            // logger
        }

        return 0;
    }

    private String hashPassword(String password, String salt) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), DatatypeConverter.parseHexBinary(salt), 100, 256);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();

            return DatatypeConverter.printHexBinary(res);
        } catch( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException(e);
        }
    }
}
