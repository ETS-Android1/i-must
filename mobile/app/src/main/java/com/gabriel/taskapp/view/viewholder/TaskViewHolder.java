package com.gabriel.taskapp.view.viewholder;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gabriel.taskapp.R;
import com.gabriel.taskapp.service.listener.TaskListener;
import com.gabriel.taskapp.service.model.local.TaskModel;
import com.gabriel.taskapp.view.FullscreenActivity;
import com.gabriel.taskapp.view.adapter.ImageAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import static com.gabriel.taskapp.service.constants.TaskConstants.TASK_IMAGE;
import static com.gabriel.taskapp.service.constants.TaskConstants.TASK_TAG;

public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TaskListener mListener;
    TaskModel mTask;
    ImageView mButtonEdit;
    ImageView mButtonComplete;
    ImageView mButtonRemove;

    public TaskViewHolder(@NonNull View itemView, TaskListener listener) {
        super(itemView);
        mListener = listener;
    }

    public void bind(TaskModel task) throws JSONException {
        mTask = task;
        mButtonEdit = itemView.findViewById(R.id.button_edit_task);
        mButtonComplete = itemView.findViewById(R.id.button_complete_task);
        mButtonRemove = itemView.findViewById(R.id.button_remove_task);
        ImageView buttonMenu = itemView.findViewById(R.id.button_task_menu);
        TextView textName = itemView.findViewById(R.id.text_description);

        textName.setText(task.getDescription());

        buttonMenu.setOnClickListener(this);
        mButtonEdit.setOnClickListener(this);
        mButtonComplete.setOnClickListener(this);
        mButtonRemove.setOnClickListener(this);

        if(task.getImagePaths() != null){
            JSONArray imagePaths = task.getImagePaths();
            ImageAdapter adapter = new ImageAdapter(itemView.getContext(), imagePaths);
            GridView grid = itemView.findViewById(R.id.grid_view_row_task_images);
            grid.setAdapter(adapter);
            grid.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(itemView.getContext(), FullscreenActivity.class);
                try {
                    intent.putExtra(TASK_IMAGE, imagePaths.getString(position));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                itemView.getContext().startActivity(intent);
            });
        }

        TextView taskDateText = itemView.findViewById(R.id.text_row_task_date);

        if(task.getDatetime() != null){
            taskDateText.setText(task.getDatetime());
        } else {
            taskDateText.setVisibility(View.INVISIBLE);
        }

        if (!task.getCompleted()) {
            mButtonComplete.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_baseline_crop_square_24));
        } else {
            textName.setPaintFlags(textName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_edit_task) {
            try {
                mListener.onEdit(mTask);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            changeTaskOptionsVisibility(false);
        } else if (id == R.id.button_complete_task) {
            mListener.onComplete(mTask);
        } else if (id == R.id.button_remove_task) {
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle(R.string.todo_removal)
                    .setMessage(R.string.want_to_remove)
                    .setPositiveButton(R.string.remove, (dialog, which) -> {
                        mListener.onDelete(mTask);
                        changeTaskOptionsVisibility(false);
                    })
                    .setNeutralButton(R.string.cancel, (dialog, which)-> {
                        changeTaskOptionsVisibility(false);
                    })
                    .show();
        } else if (id == R.id.button_task_menu) {
            if(mButtonEdit.getVisibility() == View.VISIBLE){
                changeTaskOptionsVisibility(false);
            } else {
                changeTaskOptionsVisibility(true);
            }

        }
    }

    private void changeTaskOptionsVisibility(Boolean isVisible){
        if(isVisible) {
            mButtonEdit.setVisibility(View.VISIBLE);
            mButtonRemove.setVisibility(View.VISIBLE);
        } else {
            mButtonEdit.setVisibility(View.INVISIBLE);
            mButtonRemove.setVisibility(View.INVISIBLE);
        }

    }
}
