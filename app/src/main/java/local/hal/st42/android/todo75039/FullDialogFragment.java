package local.hal.st42.android.todo75039;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.app.AlertDialog;
import android.widget.Toast;

public class FullDialogFragment extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("確認！");
        builder.setMessage("このタスクを削除してもよろしいですか？");
        builder.setPositiveButton("削除", new DialogButtonClickListener());
        builder.setNegativeButton("キャンセル", new DialogButtonClickListener());
        return builder.create();
    }

    public class DialogButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Activity parent = getActivity();
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    ToDoEditActivity taskEditActivity = (ToDoEditActivity) getActivity();
                    taskEditActivity.listDelete();
                    Toast.makeText(parent, "タスクを削除しました。", Toast.LENGTH_SHORT).show();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    }
}