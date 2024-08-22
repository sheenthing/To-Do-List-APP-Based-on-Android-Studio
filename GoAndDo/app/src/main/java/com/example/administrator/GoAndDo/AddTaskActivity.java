package com.example.administrator.GoAndDo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;

public class AddTaskActivity extends AppCompatActivity {

    private EditText editTextTask, editTextDesc, editTextTime, editTextDate;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    Uri imageUri;
    ImageButton but;

    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        editTextTask = findViewById(R.id.editTextTask);
        editTextDesc = findViewById(R.id.editTextDesc);
        editTextTime = findViewById(R.id.editTextTime);
        editTextDate = findViewById(R.id.editTextDate);
        but = findViewById(R.id.cameraButton);
        iv = findViewById(R.id.imageView);

        findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTask();
            }
        });
    }

    private void saveTask() {
        final String sTask = editTextTask.getText().toString().trim();
        final String sDesc = editTextDesc.getText().toString().trim();
        final String sTime = editTextTime.getText().toString().trim();
        final String sDate = editTextDate.getText().toString().trim();
//        final String sImage = imageUri.toString();

        if (sTask.isEmpty()) {
            editTextTask.setError("Task required");
            editTextTask.requestFocus();
            return;
        }

        if (sDesc.isEmpty()) {
            editTextDesc.setError("Desc required");
            editTextDesc.requestFocus();
            return;
        }

        if (sTime.isEmpty()) {
            editTextTime.setError("Time required");
            editTextTime.requestFocus();
            return;
        }

        if (sDate.isEmpty()) {
            editTextDate.setError("Date required");
            editTextDate.requestFocus();
            return;
        }



        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a task
                Task task = new Task();
                task.setTask(sTask);
                task.setDesc(sDesc);
                task.setTime(sTime);
                task.setDate(sDate);

                if(imageUri!=null)
                {
                    final String sImage = imageUri.toString();
                    task.setImage(sImage);
                }
                task.setFinished(false);

                //adding to database
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .taskDao()
                        .insert(task);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }

    public void onDateClick(View view)
    {
        Log.d("ToDoApp","onDateClick");
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                EditText dateView = findViewById(R.id.editTextDate);

                dateView.setText(dayOfMonth + "/" + month + "/" + year);
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(this,listener,2020,1,1);
        dialog.show();
    }

    public void onTimeClick(View view)
    {
        Log.d("ToDoApp","onTimeClick");
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                EditText timeView = findViewById(R.id.editTextTime);

                timeView.setText(hourOfDay + ": " + minute);
            }
        };
        TimePickerDialog dialog = new TimePickerDialog(this,listener,0,0,true);
        dialog.show();
    }

    public void onCameraClick(View view)
    {
        Log.d("ToDoApp","onCameraClick");
        String imageFileName = "JPEG_" + System.currentTimeMillis() + ".jpg";

        //create a new file in the application file folder
        File imageFile = new File(getFilesDir(), imageFileName);

        //A unique code to identify results from the camera intent
        imageUri = FileProvider.getUriForFile(this,".fileprovider", imageFile);

        //Create an Intent and launch the Activity
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // check that the request was the Camera Intent request
        // and that the result was OK
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
        //Get a reference to the task ImageView from the Task creation layout by its ID
            ImageView taskImage = findViewById(R.id.imageView);
            //Set the task ImageView source to the image URL
            taskImage.setImageURI(imageUri);

        }
    }



}

