package com.example.administrator.GoAndDo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;

public class UpdateTaskActivity extends AppCompatActivity {

    private EditText editTextTask, editTextDesc, editTextTime, editTextDate;
    private CheckBox checkBoxFinished;

    private ImageView imageView;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);


        editTextTask = findViewById(R.id.editTextTask);
        editTextDesc = findViewById(R.id.editTextDesc);
        editTextTime = findViewById(R.id.editTextTime);
        editTextDate = findViewById(R.id.editTextDate);
        imageView = findViewById(R.id.imageView);

        checkBoxFinished = findViewById(R.id.checkBoxFinished);


        final Task task = (Task) getIntent().getSerializableExtra("task");

        loadTask(task);

        findViewById(R.id.button_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTask(task);
            }
        });

        findViewById(R.id.button_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateTaskActivity.this);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteTask(task);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog ad = builder.create();
                ad.show();
            }
        });
    }

    private void loadTask(Task task) {
        editTextTask.setText(task.getTask());
        editTextDesc.setText(task.getDesc());
        editTextTime.setText(task.getTime());
        editTextDate.setText(task.getDate());

        if(task.getImage()!=null)
        {
            imageView.setImageURI(Uri.parse(task.getImage()));
        }



        checkBoxFinished.setChecked(task.isFinished());
    }

    private void updateTask(final Task task) {
        final String sTask = editTextTask.getText().toString().trim();
        final String sDesc = editTextDesc.getText().toString().trim();
        final String sTime = editTextTime.getText().toString().trim();
        final String sDate = editTextDate.getText().toString().trim();



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
            editTextDate.setError("Time required");
            editTextDate.requestFocus();
            return;
        }





        class UpdateTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                task.setTask(sTask);
                task.setDesc(sDesc);
                task.setTime(sTime);
                task.setDate(sDate);
                if(imageUri!=null)
                {
                    final String sImage = imageUri.toString();
                    task.setImage(sImage);
                }
                //
                task.setFinished(checkBoxFinished.isChecked());
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .taskDao()
                        .update(task);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(UpdateTaskActivity.this, MainActivity.class));
            }
        }

        UpdateTask ut = new UpdateTask();
        ut.execute();
    }


    private void deleteTask(final Task task) {
        class DeleteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .taskDao()
                        .delete(task);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(UpdateTaskActivity.this, MainActivity.class));
            }
        }

        DeleteTask dt = new DeleteTask();
        dt.execute();

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

