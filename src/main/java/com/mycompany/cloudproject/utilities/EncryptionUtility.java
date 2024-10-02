package com.mycompany.cloudproject.utilities;

import org.mindrot.jbcrypt.BCrypt;

public class EncryptionUtility {

    public static  String getEncryptedPassword(String password){
        return BCrypt.hashpw(password,BCrypt.gensalt());
    }

    public static boolean getDecryptedPassword(String plainPassword, String hashedPassword){
        return BCrypt.checkpw(plainPassword,hashedPassword);
    }


}
