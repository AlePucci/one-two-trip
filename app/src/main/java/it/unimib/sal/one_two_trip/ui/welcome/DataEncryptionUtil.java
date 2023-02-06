package it.unimib.sal.one_two_trip.ui.welcome;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

public class DataEncryptionUtil {
    private final Application application;
    public String readSecretDataWithEncryptedSharedPreferences(String sharedPreferencesFileName,
                                                               String key)
            throws GeneralSecurityException, IOException {

        MasterKey mainKey = new MasterKey.Builder(application)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                application,
                sharedPreferencesFileName,
                mainKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        return sharedPreferences.getString(key, null);
    }

    public void writeSecreteDataOnFile(String fileName, String data)
            throws GeneralSecurityException, IOException {

        MasterKey mainKey = new MasterKey.Builder(application)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();

        // Creates a file with this name, or replaces an existing file that has the same name.
        // Note that the file name cannot contain path separators.
        File fileToWrite = new File(application.getFilesDir(), fileName);
        EncryptedFile encryptedFile = new EncryptedFile.Builder(application,
                fileToWrite,
                mainKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build();

        // File cannot exist before using openFileOutput
        if (fileToWrite.exists()) {
            fileToWrite.delete();
        }

        byte[] fileContent = data.getBytes(StandardCharsets.UTF_8);
        OutputStream outputStream = encryptedFile.openFileOutput();
        outputStream.write(fileContent);
        outputStream.flush();
        outputStream.close();
    }
    public String readSecretDataOnFile(String fileName)
            throws GeneralSecurityException, IOException {

        // Although you can define your own key generation parameter specification, it's
        // recommended that you use the value specified here.
        MasterKey mainKey = new MasterKey.Builder(application)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();

        File file = new File(application.getFilesDir(), fileName);

        EncryptedFile encryptedFile = new EncryptedFile.Builder(application,
                file,
                mainKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build();

        if (file.exists()) {
            InputStream inputStream = encryptedFile.openFileInput();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int nextByte = inputStream.read();
            while (nextByte != -1) {
                byteArrayOutputStream.write(nextByte);
                nextByte = inputStream.read();
            }

            byte[] plaintext = byteArrayOutputStream.toByteArray();
            return new String(plaintext, StandardCharsets.UTF_8);
        }
        return null;
    }

    public DataEncryptionUtil(Application application) {
        this.application = application;
    }

    public void writeSecretDataWithEncryptedSharedPreferences(String sharedPreferencesFileName,
                                                                          String key, String value)
            throws GeneralSecurityException, IOException {

        MasterKey mainKey = new MasterKey.Builder(application)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        // Creates a file with this name, or replaces an existing file that has the same name.
        // Note that the file name cannot contain path separators.
        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                application,
                sharedPreferencesFileName,
                mainKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void deleteAll(String encryptedSharedPreferencesFileName, String encryptedFileDataFileName) {
        SharedPreferences sharedPref = application.getSharedPreferences(encryptedSharedPreferencesFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();

        new File(application.getFilesDir(), encryptedFileDataFileName).delete();
    }
}
