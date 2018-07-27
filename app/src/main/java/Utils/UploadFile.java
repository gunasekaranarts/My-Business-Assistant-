package Utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.sendhan.mybusinessassistant.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by USER on 29-11-2017.
 */

public class UploadFile {
    Activity main;

    public UploadFile(Activity main) {
        this.main = main;
    }

    public void UploadFile()
    {
            final File currentDB = new File("/data/data/com.sendhan.mybusinessassistant/databases/MyBusinessAssistant.db");
            if (currentDB.exists()) {
                deleteFileFromDrive();

                final Task<DriveFolder> appFolderTask = getDriveResourceClient().getAppFolder();
                final Task<DriveContents> createContentsTask = getDriveResourceClient().createContents();
                Tasks.whenAll(appFolderTask, createContentsTask)
                        .continueWithTask(new Continuation<Void, Task<DriveFile>>() {
                            @Override
                            public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
                                DriveFolder parent = appFolderTask.getResult();
                                DriveContents contents = createContentsTask.getResult();

                                OutputStream outputStream = contents.getOutputStream();

                                file2Os(outputStream, currentDB);
                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setTitle(((MainActivity) main).mHelper.getDatabaseName())
                                        .setMimeType("application/x-sqlite3")
                                        .setStarred(true)
                                        .build();
                                //Task<Void> commitTask = getDriveResourceClient().commitContents(contents,changeSet);
                                // [END discard_contents]

                                return getDriveResourceClient().createFile(parent, changeSet, contents);
                            }
                        })
                        .addOnSuccessListener(main,
                                new OnSuccessListener<DriveFile>() {
                                    @Override
                                    public void onSuccess(DriveFile driveFile) {
                                        AppPreferences.getInstance(main).setDriveId(driveFile.getDriveId().encodeToString());
                                        showMessage("Data uploaded to google drive " +
                                                driveFile.getDriveId().encodeToString());
                                    }
                                })
                        .addOnFailureListener(main, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                showMessage("Unable to create file");

                            }
                        });
            }

    }
    public void deleteFileFromDrive()
    {
        final String driveIdStr = AppPreferences.getInstance(main).getDriveId();
        if(!driveIdStr.equals(""))
        {
            DriveId fileId = DriveId.decodeFromString(driveIdStr);
            DriveFile oldFile = fileId.asDriveFile();
            getDriveResourceClient().delete(oldFile)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            AppPreferences.getInstance(main).setDriveId("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }
    public DriveResourceClient getDriveResourceClient() {
        return ((MainActivity)main).mDriveResourceClient;
    }
    static boolean file2Os(OutputStream os, File file) {
        boolean bOK = false;
        InputStream is = null;
        if (file != null && os != null) try {
            byte[] buf = new byte[4096];
            is = new FileInputStream(file);
            int c;
            while ((c = is.read(buf, 0, buf.length)) > 0)
                os.write(buf, 0, c);
            bOK = true;
        } catch (Exception e) {e.printStackTrace();}
        finally {
            try {
                os.flush(); os.close();
                if (is != null )is.close();
            } catch (Exception e) {e.printStackTrace();}
        }
        return  bOK;
    }
    protected void showMessage(String message) {
        Toast.makeText(main, message, Toast.LENGTH_LONG).show();
    }
}
