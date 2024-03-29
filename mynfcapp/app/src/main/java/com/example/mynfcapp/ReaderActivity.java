package com.example.mynfcapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.chaos.view.PinView;
import com.example.mynfcapp.AccountCreation.Database.SessionManager;
import com.example.mynfcapp.AccountCreation.LoginActivity;
import com.example.mynfcapp.AccountCreation.VerifyOTPSEC;
import com.example.mynfcapp.parser.NdefMessageParser;
import com.example.mynfcapp.record.ParsedNdefRecord;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

//extends AppCompatActivity
public class ReaderActivity extends Activity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private TextView text;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference reference;
    private boolean isSec;
    private RelativeLayout rl;
    private PinView pinView;

    private ImageView mainIcon, user_photo;
    private boolean verStat;
    String codeBySystem;



    public NdefMessage[] getMsgs() {
        return msgs;
    }

    public void setMsgs(NdefMessage[] msgs) {
        this.msgs = msgs;
    }

    private NdefMessage[] msgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reader);

        //User Session Details
        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();

        String fullName = userDetails.get(SessionManager.KEY_FULLNAME);
        String email = userDetails.get(SessionManager.KEY_EMAIL);
        String phoneNo = userDetails.get(SessionManager.KEY_PHONENUMNER);
        String password = userDetails.get(SessionManager.KEY_PASSWORD);
        String date = userDetails.get(SessionManager.KEY_DATE);
        String gender = userDetails.get(SessionManager.KEY_GENDER);

        //DESIGN
        rl = findViewById(R.id.reader_bg);
        mainIcon = findViewById(R.id.reader_logo);
        pinView = findViewById(R.id.pin_view_reader);

        //DATABASE
        mFirebaseAuth = FirebaseAuth.getInstance();
        isSec = false;
        verStat = false;
        user_photo = findViewById(R.id.user_photo_imageView);
        text = (TextView) findViewById(R.id.text);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC Available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        String _userNumber =  user.getPhoneNumber().trim();

        //Database Validation
        Query checkUser = FirebaseDatabase.getInstance().getReference("Security").orderByChild("phoneNo").equalTo(_userNumber);

        checkUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String systemPhoneNo = snapshot.child(_userNumber).child("phoneNo").getValue(String.class);
                    boolean approvedStatus = snapshot.child(_userNumber).child("approved").getValue(boolean.class);
                    System.out.println("Number in System is" + systemPhoneNo);
                    if (systemPhoneNo.equals(_userNumber) && (approvedStatus == true)) {
                        isSec = true;
                    } else isSec = false;

                } else {
                    isSec = false;
                }
                System.out.println("isSec is : " + isSec);

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                System.out.println("The read failed: " + error.getCode());
            }
        });




        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled())
                showWirelessSettings();

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void resolveIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;

            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];

                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
                msgs = new NdefMessage[] {msg};
            }
            setMsgs(msgs);
            displayMsgs(msgs, false);
        }
    }

    private void showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void displayMsgs(NdefMessage[] msgs, boolean verStat) {
        if (msgs == null || msgs.length == 0)
            return;

        StringBuilder builder = new StringBuilder();
        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
        final int size = records.size();

        for (int i = 0; i < size; i++) {
            ParsedNdefRecord record = records.get(i);
            String str = record.str();
            builder.append(str).append("\n");
        }


        //Check Date & Time
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy");
        Date currDate = new Date();
        System.out.println("Date is: " + formatter.format(currDate)); // CURRENTDATE

        Date currTime = new Date();
        DateFormat format = new SimpleDateFormat("HH:mm a");
        System.out.println(format.format(currTime)); //CURRENT TIME


        //Tag Content
        String tagContent = builder.toString();
        String tagID = tagContent.substring(13).trim();

        //VERIFY USER TO READ MESSAGE
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        if (user != null) {
            String userNumber = user.getPhoneNumber().trim();
            //String numberOnTag = tagContent.substring(tagContent.indexOf("+"), tagContent.indexOf("Location")).trim();


            //Database Validation
            Query checkTag = FirebaseDatabase.getInstance().getReference("Tags").orderByChild("id").equalTo(tagID);

            checkTag.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String fileName;
                        if (snapshot.child(tagID).child("fileName").exists()) {
                            fileName = snapshot.child(tagID).child("fileName").getValue(String.class);
                        } else fileName = null;
                        String dateOnTag = snapshot.child(tagID).child("date").getValue(String.class);
                        String endDateOnTag = snapshot.child(tagID).child("endDate").getValue(String.class);
                        String nameOnTag = snapshot.child(tagID).child("fullName").getValue(String.class);
                        String locationOnTag = snapshot.child(tagID).child("location").getValue(String.class);
                        String numOnTag = snapshot.child(tagID).child("phoneNo").getValue(String.class);
                        String timeOnTag = snapshot.child(tagID).child("time").getValue(String.class);
                        String endTimeOnTag = snapshot.child(tagID).child("endTime").getValue(String.class);
                        boolean voidTagStatus = snapshot.child(tagID).child("voidTag").getValue(boolean.class);

                        String finalMessage = ("Tag ID: " + tagID + "\n\nName: " + nameOnTag + "\nPhone Number: " + numOnTag + "\n\nDate: " + dateOnTag + " - " + endDateOnTag + "\nLocation: " + locationOnTag + "\nTime: " + timeOnTag + " - " + endTimeOnTag);

                        if (dateOnTag.equals(endDateOnTag)) {
                            finalMessage = ("Tag ID: " + tagID + "\n\nName: " + nameOnTag + "\nPhone Number: " + numOnTag + "\n\nDate: " + dateOnTag + "\nLocation: " + locationOnTag + "\nTime: " + timeOnTag + " - " + endTimeOnTag);
                        }

                        // CHECK USER VERIFICATION

                        if (userNumber.equals(numOnTag) && verStat == false) {
                            text.setText("");
                            sendVerificationCodeToUser(numOnTag);



                            } else if (verStat == true) {
                            //TO DO
                            pinView.setVisibility(View.INVISIBLE);
                            text.setText(finalMessage);
                            if (fileName != null) {
                                FirebaseStorage storage = FirebaseStorage.getInstance();


                            }


                            //TO DO- CHANGE TO JUST DATE
                            DateFormat format = new SimpleDateFormat("MMMM d, yyyy");
                            DateFormat format2 = new SimpleDateFormat("HH:mm aa");

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");

                            if (fileName != null) {
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReference().child("images/"+fileName);

                                try {
                                    final File localFile = File.createTempFile(fileName, "jpg");
                                    storageRef.getFile(localFile)
                                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                                    RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                                                    p.addRule(RelativeLayout.BELOW, R.id.user_photo_imageView);
                                                    text.setLayoutParams(p);
                                                    String pathToFile = localFile.getAbsolutePath();

                                                    user_photo.setVisibility(View.VISIBLE);

                                                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
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

                                                    user_photo.setImageBitmap(rotatedBitmap);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();;
                                }


                            }

                            try {
                                LocalDate eventDate = LocalDate.parse(dateOnTag, formatter);
                                LocalDate eventEndDate = LocalDate.parse(endDateOnTag, formatter);
                                LocalDate currentDate = LocalDate.now();
                                LocalTime eventTime = LocalTime.parse(timeOnTag);
                                LocalTime eventEndTime = LocalTime.parse(endTimeOnTag);
                                LocalTime currentTime = LocalTime.now();

                                //System.out.println("DEBUG123: " + eventDate + "\nTIME : " + eventTime);
                                //Date eventDate = format.parse(dateOnTag);
                                //Date eventEndDate = format.parse(endDateOnTag);
                                //Date eventTime = format2.parse(timeOnTag);
                                //Date eventEndTime = format2.parse(endTimeOnTag);

                                //(currDate.equals(eventDate) && currTime.before(eventTime))
                                //else if ((currDate.equals(eventDate) || (currDate.after(eventDate) && currDate.before(eventEndDate))) && (currTime.equals(eventEndTime) || (currTime.after(eventTime) && currTime.before(eventEndTime))))

                                if (currentDate.isBefore(eventDate) || currentDate.isAfter(eventEndDate) || voidTagStatus == true) {
                                    rl.setBackgroundColor(Color.RED);
                                    mainIcon.setImageResource(R.drawable.no_entry_icon_large); //512*512
                                    System.out.println("CURR DATE = " + currDate + "\nDATE ON TAG = " + eventDate);
                                } else {
                                    rl.setBackgroundColor(Color.GREEN);
                                    System.out.println("GREEEn CURR DATE = " + currDate + "\nDATE ON TAG = " + eventDate);
                                    mainIcon.setImageResource(R.drawable.check_mark_icon_large); //512*512

                                    //MARK TAG AS ACTIVATED
                                    FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
                                    DatabaseReference reference = rootNode.getReference("Tags");
                                    reference.child(tagID).child("activated").setValue(true);
                                    throw new Exception("This is an exception");                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (isSec == true) {

                            text.setText(finalMessage);

                            //TO DO- CHANGE TO JUST DATE
                            DateFormat format = new SimpleDateFormat("MMMM d, yyyy");
                            DateFormat format2 = new SimpleDateFormat("HH:mm aa");

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");

                            if (fileName != null) {
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReference().child("images/"+fileName);

                                try {
                                    final File localFile = File.createTempFile(fileName, "jpg");
                                    storageRef.getFile(localFile)
                                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                                    RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                                                    p.addRule(RelativeLayout.BELOW, R.id.user_photo_imageView);
                                                    text.setLayoutParams(p);
                                                    String pathToFile = localFile.getAbsolutePath();

                                                    user_photo.setVisibility(View.VISIBLE);

                                                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
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


                                                    user_photo.setImageBitmap(rotatedBitmap);
                                                    Toast.makeText(ReaderActivity.this, "Image Recieved", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();;
                                }


                            }

                            try {
                                LocalDate eventDate = LocalDate.parse(dateOnTag, formatter);
                                LocalDate eventEndDate = LocalDate.parse(endDateOnTag, formatter);
                                LocalDate currentDate = LocalDate.now();
                                LocalTime eventTime = LocalTime.parse(timeOnTag);
                                LocalTime eventEndTime = LocalTime.parse(endTimeOnTag);
                                LocalTime currentTime = LocalTime.now();

                                System.out.println("DEBUG123: " + eventDate + "\nTIME : " + eventTime);
                                //Date eventDate = format.parse(dateOnTag);
                                //Date eventEndDate = format.parse(endDateOnTag);
                                //Date eventTime = format2.parse(timeOnTag);
                                //Date eventEndTime = format2.parse(endTimeOnTag);

                                //(currDate.equals(eventDate) && currTime.before(eventTime))
                                //else if ((currDate.equals(eventDate) || (currDate.after(eventDate) && currDate.before(eventEndDate))) && (currTime.equals(eventEndTime) || (currTime.after(eventTime) && currTime.before(eventEndTime))))

                                if (currentDate.isBefore(eventDate) || currentDate.isAfter(eventEndDate) || voidTagStatus == true) {
                                    rl.setBackgroundColor(Color.RED);
                                    mainIcon.setImageResource(R.drawable.no_entry_icon_large); //512*512
                                    System.out.println("CURR DATE = " + currDate + "\nDATE ON TAG = " + eventDate);
                                } else {
                                    rl.setBackgroundColor(Color.GREEN);
                                    System.out.println("GREEEn CURR DATE = " + currDate + "\nDATE ON TAG = " + eventDate);
                                    mainIcon.setImageResource(R.drawable.check_mark_icon_large); //512*512

                                    //MARK TAG AS ACTIVATED
                                    FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
                                    DatabaseReference reference = rootNode.getReference("Tags");
                                    reference.child(tagID).child("activated").setValue(true);
                                    throw new Exception("This is an exception");                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            mainIcon.setImageResource(R.drawable.no_access_large);
                            rl.setBackgroundColor(Color.YELLOW);
                            text.setText("You do not have permission to access this!");
                        }
                    }
                    if (snapshot.exists() == false) {
                        mainIcon.setImageResource(R.drawable.no_data_large);
                        text.setText("This tag does not exist!");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("The read failed: " + error.getCode());
                }
            });



            /*System.out.println(userNumber);
            //System.out.println(numberOnTag);
            if (numberOnTag.equals(userNumber) || isSec == true) {
                text.setText(builder.toString());
            } else text.setText("This is not your Tag :(");
        } else Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();;*/

        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }



    //PHONE AUTHENTICATION
    private void sendVerificationCodeToUser(String phoneNo) {
        pinView.setVisibility(View.VISIBLE);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNo)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeBySystem = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if (code!=null) {
                        pinView.setText(code);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(ReaderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //TO DO
                            pinView.setVisibility(View.GONE);
                            text.setText("Authorised!");
                            displayMsgs(getMsgs(), true);

                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(ReaderActivity.this, "Verification Not Complete!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    //NFC METHODS
    private String dumpTagData(Tag tag) {
        StringBuilder sb = new StringBuilder();
        byte[] id = tag.getId();
        sb.append("ID (hex): ").append(toHex(id)).append('\n');
        sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n');
        sb.append("ID (dec): ").append(toDec(id)).append('\n');
        sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n');

        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());

        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                String type = "Unknown";

                try {
                    MifareClassic mifareTag = MifareClassic.get(tag);

                    switch (mifareTag.getType()) {
                        case MifareClassic.TYPE_CLASSIC:
                            type = "Classic";
                            break;
                        case MifareClassic.TYPE_PLUS:
                            type = "Plus";
                            break;
                        case MifareClassic.TYPE_PRO:
                            type = "Pro";
                            break;
                    }
                    sb.append("Mifare Classic type: ");
                    sb.append(type);
                    sb.append('\n');

                    sb.append("Mifare size: ");
                    sb.append(mifareTag.getSize() + " bytes");
                    sb.append('\n');

                    sb.append("Mifare sectors: ");
                    sb.append(mifareTag.getSectorCount());
                    sb.append('\n');

                    sb.append("Mifare blocks: ");
                    sb.append(mifareTag.getBlockCount());
                } catch (Exception e) {
                    sb.append("Mifare classic error: " + e.getMessage());
                }
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        return sb.toString();
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private String toReversedHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    private long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long toReversedDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

}
