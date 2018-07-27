package Utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.sendhan.mybusinessassistant.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by USER on 29-11-2017.
 */

public class RestoreFile {
    DriveFile file;
    Activity main;

    public RestoreFile(Activity main) {
        this.main = main;
    }
    public void appFolder(){
                final Task<DriveFolder> appFolderTask =getDriveResourceClient().getAppFolder();
        appFolderTask.addOnSuccessListener(main,new OnSuccessListener<DriveFolder>() {
            @Override
            public void onSuccess(DriveFolder driveFolder) {
                getFile(driveFolder);
            }
        });
    }
    private void getFile(DriveFolder parent) {
        Query query = new Query.Builder()
                .addFilter(Filters.contains(SearchableField.TITLE, ((MainActivity)main).mHelper.getDatabaseName()))
                .build();
        Task<MetadataBuffer> queryTask = getDriveResourceClient().queryChildren(parent, query);
        queryTask
                .addOnSuccessListener(main,
                        new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadataBuffer) {
                                file = metadataBuffer.get(0).getDriveId().asDriveFile();
                                downloadFile(file);
                            }
                        })
                .addOnFailureListener(main, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(main,"Unable to download file",
                                Toast.LENGTH_LONG).show();
                    }
                });


    }

    private void downloadFile(DriveFile file) {
        final Task<DriveContents> openFileTask =
                getDriveResourceClient().openFile(file, DriveFile.MODE_READ_ONLY);
        openFileTask.addOnCompleteListener(new OnCompleteListener<DriveContents>() {
            @Override
            public void onComplete(@NonNull Task<DriveContents> task) {
                openFileTask
                        .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                            @Override
                            public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                                DriveContents contents = task.getResult();
                                // Process contents...
                                // [START_EXCLUDE]
                                // [START read_as_string]
                                File NewDB = new File("/data/data/com.sendhan.mybusinessassistant/databases/MyBusinessAssistant.db");
                                OutputStream outStream = new FileOutputStream(NewDB);
                                try {

                                    InputStream is=contents.getInputStream();
                                    byte[] buffer = new byte[is.available()];
                                    is.read(buffer);
                                    outStream.write(buffer);
                                    outStream.close();
                                    outStream.flush();
                                    showMessage("Database Restored");

                                }catch (Exception ex){
                                    showMessage("Unable to Restore");
                                    outStream.close();
                                    outStream.flush();
                                }
                                // [END read_as_string]
                                // [END_EXCLUDE]
                                // [START discard_contents]
                                Task<Void> discardTask = getDriveResourceClient().discardContents(contents);
                                // [END discard_contents]
                                return discardTask;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure
                                // [START_EXCLUDE]

                                showMessage("Unable to restore database");

                                // [END_EXCLUDE]
                            }
                        });
            }
        });
    }
    public DriveResourceClient getDriveResourceClient() {
        return ((MainActivity)main).mDriveResourceClient;
    }
    protected void showMessage(String message) {
        Toast.makeText(main, message, Toast.LENGTH_LONG).show();
    }
}
