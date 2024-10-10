package com.example.loginclass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class Add_task extends BottomSheetDialog {

    private DBHelper dbHelper;
    private int userId;
    private EditText taskEditText;
    private Button addButton;
    private Dasboard dasboard;

    public Add_task(@NonNull Context context, int userId, Dasboard dasboard) {
        super(context);
        this.userId = userId;
        this.dasboard = dasboard;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        dbHelper = new DBHelper(getContext());

        taskEditText = findViewById(R.id.task_edit_text);
        addButton = findViewById(R.id.add_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = taskEditText.getText().toString().trim();
                if (!task.isEmpty()) {
                    boolean isInserted = dbHelper.insertTask(userId, task);
                    if (isInserted) {
                        dasboard.loadTasks();
                        dismiss(); // Close the dialog if the task is added successfully
                    } else {
                        Toast.makeText(getContext(), "Failed to add task", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    taskEditText.setError("Task cannot be empty");
                }
            }
        });
    }
}
