package com.example.mynfcapp;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.mynfcapp.AccountCreation.NFCHelperClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Nullable;

import static android.nfc.NfcAdapter.getDefaultAdapter;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class PassCreation extends Activity {

    NfcAdapter nfcAdapter;
    TextView txtTagContent;
    ToggleButton tglReadWrite, tglLockTag;
    TextInputLayout phoneNo, name, location;
    CountryCodePicker countryCodePicker;

    Button dateButton, timeButton, endDateButton, endTimeButton, cameraButton;
    TextView dateTextView, timeTextView, endDateTextView, endTimeTextView;
    ImageView cameraImage;

    StorageReference storageReference;
    boolean imageTaken, tagLock;
    String pathToFile, fileName;
    File image;
    Uri photoUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        nfcAdapter = getDefaultAdapter(this);
        txtTagContent = (TextView) findViewById(R.id.txtTagContent);
        tglReadWrite = (ToggleButton) findViewById(R.id.tglReadWrite);
        tglLockTag = (ToggleButton) findViewById(R.id.tglLockTag);

        //SEC Input
        phoneNo = findViewById(R.id.book_phoneNo);
        name = findViewById(R.id.book_fullname);
        location = findViewById(R.id.book_location);
        countryCodePicker = findViewById(R.id.book_country_code_picker);

        dateButton = findViewById(R.id.dateButton);
        endDateButton = findViewById(R.id.endDateButton);
        timeButton = findViewById(R.id.timeButton);
        endTimeButton = findViewById(R.id.endTimeButton);
        dateTextView = findViewById(R.id.dateTextView);
        endDateTextView = findViewById(R.id.endDateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        endTimeTextView = findViewById(R.id.endTimeTextView);

        cameraButton = findViewById(R.id.cameraButton);
        cameraImage = findViewById(R.id.camera_image);
        imageTaken = false;

        //FIREBASE STORAGE REF
        storageReference = FirebaseStorage.getInstance().getReference();

        //REQUEST CAMERA PERMISSION
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open Camera
                dispatchPictureTakerAction();
            }
        });


        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC AVAILABLE!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "NFC NOT AVAILABLE", Toast.LENGTH_LONG).show();
        }

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDateButton();
            }
        });
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleTimeButton();
            }
        });
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleEndDateButton();
            }
        });
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleEndTimeButton();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
                ExifInterface ei = null;
                try {
                    ei = new ExifInterface(pathToFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap rotatedBitmap = null;
                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(bitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(bitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(bitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = bitmap;
                }

                cameraImage.setImageBitmap(rotatedBitmap);

                imageTaken = true;
            }
        }

    }

    private void dispatchPictureTakerAction() {
        Intent takePic = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePic.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createPhotoFile();

            if (photoFile != null) {
                pathToFile = photoFile.getAbsolutePath();
                photoUri = FileProvider.getUriForFile(PassCreation.this, "com.example.mynfcapp.fileprovider", photoFile);
                setUri(photoUri);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePic, 1);
            }
        }
    }

    private File createPhotoFile() {
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        image = null;
        try {
            image = File.createTempFile(fileName, ".jpg", storageDir);
        } catch (IOException e) {
            Log.d("mylog", "Exception: " + e.toString());
        }
        setImage(image);
        setFileName(fileName);
        return image;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void setImage (File image) {
        this.image =  image;
    }

    private File getImage () {
        return image;
    }

    private void setUri (Uri imageUri) {
        this.photoUri = imageUri;
    }

    private Uri getUri () {
        return photoUri;
    }

    private void setFileName (String fileName) {
        this.fileName =  fileName;
    }

    private String getFileName () {
        return fileName;
    }

    private void handleTimeButton() {
        Calendar calendar = Calendar.getInstance();
        int HOUR = calendar.get(Calendar.HOUR_OF_DAY);
        int MINUTE = calendar.get(Calendar.MINUTE);

        boolean is24HourFormat = DateFormat.is24HourFormat(this);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR_OF_DAY, hour);
                calendar1.set(Calendar.MINUTE, minute);

                CharSequence charSequence = DateFormat.format("HH:mm", calendar1);
                timeTextView.setText(charSequence);
            }
        }, HOUR, MINUTE, is24HourFormat);

        timePickerDialog.show();
    }

    private void handleDateButton() {
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE =  calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int date) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, date);

                CharSequence dateCharSequence = DateFormat.format("MMM d, yyyy", calendar1);
                dateTextView.setText(dateCharSequence);
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();
    }

    private void handleEndTimeButton() {
        Calendar calendar = Calendar.getInstance();
        int HOUR = calendar.get(Calendar.HOUR_OF_DAY);
        int MINUTE = calendar.get(Calendar.MINUTE);

        boolean is24HourFormat = DateFormat.is24HourFormat(this);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                Calendar calendar2 = Calendar.getInstance();
                calendar2.set(Calendar.HOUR_OF_DAY, hour);
                calendar2.set(Calendar.MINUTE, minute);

                CharSequence charSequence = DateFormat.format("HH:mm", calendar2);
                endTimeTextView.setText(charSequence);
            }
        }, HOUR, MINUTE, is24HourFormat);

        timePickerDialog.show();
    }

    private void handleEndDateButton() {
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE =  calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int date) {

                Calendar calendar2 = Calendar.getInstance();
                calendar2.set(Calendar.YEAR, year);
                calendar2.set(Calendar.MONTH, month);
                calendar2.set(Calendar.DATE, date);

                CharSequence dateCharSequence = DateFormat.format("MMM d, yyyy", calendar2);
                endDateTextView.setText(dateCharSequence);
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();
    }




    @Override
    protected void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();

        disableForegroundDispatchSystem();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Toast.makeText(this, "NfcIntent!", Toast.LENGTH_SHORT).show();

            if (tglReadWrite.isChecked()) {
                Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                if (parcelables != null && parcelables.length > 0) {
                    readTextFromMessage((NdefMessage)parcelables[0]);
                } else {
                    Toast.makeText(this, "No NDEF Message", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (!validatePhoneNumber() | !validateFullName() | !validateLocation()) return;
                if (dateTextView.getText() == null || timeTextView == null) return;

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                //PUT INFO ON THE TAG
                String _phoneNumber = phoneNo.getEditText().getText().toString().trim();
                String _fullName = name.getEditText().getText().toString().trim();
                String _location = location.getEditText().getText().toString().trim();
                String _date = dateTextView.getText().toString().trim();
                String _endDate = endDateTextView.getText().toString().trim();
                String _time = timeTextView.getText().toString().trim();
                String _endTime = endTimeTextView.getText().toString().trim();

                //Use Case handle of user input 0
                if (_phoneNumber.charAt(0) == '0') {
                    _phoneNumber = _phoneNumber.substring(1);
                }
                String _completePhoneNumber = "+" + countryCodePicker.getFullNumber() + _phoneNumber;
                String uniqueID = UUID.randomUUID().toString();

                String fullContent = ("Your Tag ID: " + uniqueID);

                //NdefMessage ndefMessage = createNdefMessage(txtTagContent.getText()+"");
                NdefMessage ndefMessage = createNdefMessage(fullContent);
                FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
                DatabaseReference reference = rootNode.getReference("Tags");

                //IF IMAGE IS ATTATCHED
                if (pathToFile != null && tglLockTag.isChecked()) {
                    NFCHelperClass addNewTag = new NFCHelperClass(uniqueID, _fullName, _completePhoneNumber, _location, _date, _endDate, _time, _endTime, false, false, getFileName(), tagLock = true);
                    reference.child(uniqueID).setValue(addNewTag);

                    writeNdefMessage(tag, ndefMessage);

                    //UPLOAD IMAGE TO FIREBASE
                    StorageReference imageRef = storageReference.child("images/"+getFileName());

                    UploadTask uploadTask = imageRef.putFile(getUri());

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(PassCreation.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PassCreation.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                        }
                    });

                } else if (tglLockTag.isChecked()) {
                    writeNdefMessage(tag, ndefMessage);
                    makeTagReadOnly(tag);
                    Toast.makeText(this, "TAG LOCKED!", Toast.LENGTH_SHORT).show();
                    rootNode = FirebaseDatabase.getInstance();
                    reference = rootNode.getReference("Tags");

                    NFCHelperClass addNewTag = new NFCHelperClass(uniqueID, _fullName, _completePhoneNumber, _location, _date, _endDate, _time, _endTime, false, false,  tagLock = true);
                    reference.child(uniqueID).setValue(addNewTag);

                    //NFCHelperClass addNewTag = new NFCHelperClass(uniqueID, _fullName, _completePhoneNumber, _location, _date, _endDate, _time, _endTime, false, false, tagLock = false);
                    //reference.child(uniqueID).setValue(addNewTag); //COMMENT OUT


                }


                if (!tglLockTag.isChecked()) {

                    Toast.makeText(this, "TAG NEEDS TO BE LOCKED!", Toast.LENGTH_SHORT).show();

                }
            }

        }
    }

    private void readTextFromMessage (NdefMessage ndefMessage) {

        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if (ndefRecords != null && ndefRecords.length > 0) {

            NdefRecord ndefRecord = ndefRecords[0];

            String tagContent = getTextFromNdefRecord(ndefRecord);

            txtTagContent.setText(tagContent);

        } else {
            Toast.makeText(this, "No NDEF RECORDS", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {

        try {
            if (tag == null) {
                Toast.makeText(this, "Tag object cannot be null", Toast.LENGTH_SHORT).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);

            if(ndef == null) {
                //format the tag
                formatTag(tag, ndefMessage);
            } else {
                ndef.connect();

                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Tag not writable", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                Toast.makeText(this, "Tag written", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("writeNdefMessage", e.getMessage());
        }
    }

    private NdefRecord createTextRecord(String content) {
        try {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());
        } catch (UnsupportedEncodingException e) {
            Log.e("createTextRecord", e.getMessage());
        }
        return null;

    }



    private NdefMessage createNdefMessage (String content) {

        NdefRecord ndefRecord = createTextRecord(content);

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[] {ndefRecord});

        return ndefMessage;
    }

    public void tglReadWriteOnClick(View view) {
        txtTagContent.setText("");
        tglLockTag.setChecked(false);

        if (!tglReadWrite.isChecked()) {
            tglLockTag.setVisibility(View.VISIBLE);
        } else {
            tglLockTag.setVisibility(View.GONE);
        }
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0 ) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if  (ndefFormatable == null) {
                Toast.makeText(this, "Tag is NOT Ndef Formatable!", Toast.LENGTH_SHORT).show();
            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();
        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }
    }


    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(this, PassCreation.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[] {};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    public void makeTagReadOnly(Tag tag) {
        if (tag == null) {
            return;
        }
        try {

            Ndef ndef = Ndef.get(tag);

            if (ndef != null) {
                ndef.connect();

                if (ndef.canMakeReadOnly()) {

                    ndef.makeReadOnly();
                    Toast.makeText(this, "READ ONLY", Toast.LENGTH_SHORT).show();
                }

                ndef.close();

            }
        } catch (IOException e) {
            e.printStackTrace();;
        }

    }


    /**
     * Validation Method
     */
    private boolean validatePhoneNumber() {
        String val = phoneNo.getEditText().getText().toString().trim();
        //String checkspaces = "Aw{1,20}z";
        String checkspaces = "^[^\\s].+[^\\s]$";
        if (val.isEmpty()) {
            phoneNo.setError("Enter valid phone number");
            return false;
        } else if (!val.matches(checkspaces)) {
            phoneNo.setError("No White spaces are allowed! " + val );
            return false;
        } else {
            phoneNo.setError(null);
            phoneNo.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateFullName() {
        String val = name.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            name.setError("Field cannot be empty!");
            return false;
        } else {
            name.setError(null);
            name.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateLocation() {
        String val = location.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            location.setError("Field cannot be empty!");
            return false;
        } else {
            location.setError(null);
            location.setErrorEnabled(false);
            return true;
        }
    }

}