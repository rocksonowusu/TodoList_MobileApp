package com.example.loginclass;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Dasboard extends AppCompatActivity {

    private TextView displayUsername;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter; // Ensure this is initialized properly
    private DBHelper dbHelper;
    private int userId;
    private ImageButton add_task;
    private EditText search_task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dasboard);

        dbHelper = new DBHelper(this);
        displayUsername = findViewById(R.id.display_username);
        recyclerView = findViewById(R.id.recyclerView);
        add_task = findViewById(R.id.fab_add_task);
        search_task = findViewById(R.id.search_task);

        userId = getIntent().getIntExtra("USER_ID", -1);


        taskAdapter = new TaskAdapter(this, null);


        String username = dbHelper.getUsername(userId);
        if (username != null) {
            displayUsername.setText(username);
        }

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);

        loadTasks();

        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add_task addTaskDialog = new Add_task(Dasboard.this, userId,Dasboard.this);
                addTaskDialog.show();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                taskAdapter.deleteTask(position);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                Paint p = new Paint();
                p.setColor(Color.RED);
                RectF background = new RectF(itemView.getRight() + dX, itemView.getTop(),
                        itemView.getRight(), itemView.getBottom());
                c.drawRect(background, p);



                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.7f; // Increase the threshold to slow down the swipe-to-delete action
            }

            @Override
            public float getSwipeEscapeVelocity(float defaultValue) {
                return 15 * defaultValue; // Increase the escape velocity to slow down the swipe
            }

            @Override
            public float getSwipeVelocityThreshold(float defaultValue) {
                return 15 * defaultValue; // Increase the velocity threshold to slow down the swipe
            }
        }).attachToRecyclerView(recyclerView);


        search_task.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterTasks(s.toString());
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isFirstLogin = sharedPreferences.getBoolean("isFirstLogin", true);

        if (isFirstLogin) {
            showFirstLoginMessage();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstLogin", false);
            editor.apply();
        }
    }

    private void showFirstLoginMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Welcome!")
                .setMessage("Begin with adding a task by clicking on the add button below.")
                .setPositiveButton("Got it!", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    void loadTasks() {
        Cursor cursor = dbHelper.getUserTasks(userId);
        taskAdapter.swapCursor(cursor);
    }

    private void filterTasks(String query) {
        Cursor cursor = dbHelper.searchTasks(userId, query);
        taskAdapter.swapCursor(cursor);
    }
}
