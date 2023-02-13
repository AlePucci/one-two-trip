package it.unimib.sal.one_two_trip.util;

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

/**
 * Utility class to read and write encrypted data using SharedPreferences API.
 * Doc can be read <a href="https://developer.android.com/training/data-storage/shared-preferences">here.</a>
 */
public class DataEncryptionUtil {

    private final Application application;

    public DataEncryptionUtil(Application application) {
        this.application = application;
    }

    public String readSecretDataWithEncryptedSharedPreferences(String sharedPreferencesFileName,
                                                               String key)
            throws GeneralSecurityException, IOException {

        MasterKey mainKey = new MasterKey.Builder(this.application)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                this.application,
                sharedPreferencesFileName,
                mainKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        return sharedPreferences.getString(key, null);
    }

    public void writeSecreteDataOnFile(String fileName, String data)
            throws GeneralSecurityException, IOException {

        MasterKey mainKey = new MasterKey.Builder(this.application)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();

        File fileToWrite = new File(this.application.getFilesDir(), fileName);
        EncryptedFile encryptedFile = new EncryptedFile.Builder(application,
                fileToWrite,
                mainKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build();

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
        MasterKey mainKey = new MasterKey.Builder(this.application)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();

        File file = new File(this.application.getFilesDir(), fileName);

        EncryptedFile encryptedFile = new EncryptedFile.Builder(this.application,
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

    public void writeSecretDataWithEncryptedSharedPreferences(String sharedPreferencesFileName,
                                                              String key, String value)
            throws GeneralSecurityException, IOException {

        MasterKey mainKey = new MasterKey.Builder(this.application)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                this.application,
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
        SharedPreferences sharedPref = this.application.getSharedPreferences(encryptedSharedPreferencesFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();

        new File(this.application.getFilesDir(), encryptedFileDataFileName).delete();
    }
}
