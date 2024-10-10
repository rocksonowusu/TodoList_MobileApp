package com.example.loginclass;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private Cursor cursor;
    private DBHelper dbHelper;

    public TaskAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        this.dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        String task = cursor.getString(cursor.getColumnIndexOrThrow("task"));
        int taskId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        int isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("completed"));  // Updated column name

        holder.taskText.setText(task);
        holder.checkBox.setChecked(isCompleted == 1);

        updateTaskAppearance(holder.taskText, isCompleted);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int newStatus = isChecked ? 1 : 0;
            dbHelper.updateTaskStatus(taskId, newStatus);
            updateTaskAppearance(holder.taskText, newStatus);
            Toast.makeText(context, isChecked ? "Task marked as completed" : "Task marked as uncompleted", Toast.LENGTH_SHORT).show();
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (isCompleted == 1) {
                Toast.makeText(context, "Completed tasks cannot be edited", Toast.LENGTH_SHORT).show();
            } else {
                showEditDialog(taskId, task);
            }
            return true;
        });


    }

    private void showEditDialog(int taskId, String currentTask) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_ediit_task, null);
        builder.setView(dialogView);

        EditText editTaskText = dialogView.findViewById(R.id.edit_task_text);
        editTaskText.setText(currentTask);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTask = editTaskText.getText().toString().trim();
            if (!newTask.isEmpty()) {
                dbHelper.updateTask(taskId, newTask);
                swapCursor(dbHelper.getUserTasks(cursor.getInt(cursor.getColumnIndexOrThrow("userId"))));
                Toast.makeText(context, "Task updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Task cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateTaskAppearance(TextView taskText, int isCompleted) {
        if (isCompleted == 1) {
            taskText.setPaintFlags(taskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            taskText.setTextColor(context.getResources().getColor(R.color.grey));
        } else {
            taskText.setPaintFlags(taskText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            taskText.setTextColor(context.getResources().getColor(R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    @SuppressLint("Range")
    public void deleteTask(int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        dbHelper.deleteTask(id);
        swapCursor(dbHelper.getUserTasks(cursor.getInt(cursor.getColumnIndex("userId"))));
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView taskText;
        CheckBox checkBox;


        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskText = itemView.findViewById(R.id.task_text);
            checkBox = itemView.findViewById(R.id.task_checkbox);

        }
    }
}
