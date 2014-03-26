/*
 * This file is part of BBCT for Android.
 *
 * Copyright 2012-14 codeguru <codeguru@users.sourceforge.net>
 *
 * BBCT for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BBCT for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package bbct.android.common.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import bbct.android.common.R;
import java.util.ArrayList;

public class FilterCards extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.filter_cards);
        this.buttonOk = (Button) this.findViewById(R.id.ok_button);
        this.mainLayout = (LinearLayout) this.findViewById(R.id.mainLayout);

        // set title
        String format = this.getString(R.string.bbct_title);
        String filterCardsTitle = this.getString(R.string.filter_cards_title);
        String title = String.format(format, filterCardsTitle);
        this.setTitle(title);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // restore input fields state
        if (savedInstanceState != null) {
            ArrayList<Integer> enabledFields = savedInstanceState
                    .getIntegerArrayList(this.getString(R.string.input_extra));
            for (int i : enabledFields) {
                EditText et = (EditText) this.findViewById(TEXT_FIELDS[i]);
                et.setEnabled(true);
            }
        }

        // restore "Ok" button state
        if (this.numberChecked() > 0) {
            this.buttonOk.setEnabled(true);
        }
    }

    /**
     * Save the state of all {@link EditText} elements.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<Integer> enabledFields = new ArrayList<Integer>();
        for (int i = 0; i < TEXT_FIELDS.length; i++) {
            EditText et = (EditText) this.findViewById(TEXT_FIELDS[i]);
            if (et.isEnabled()) {
                enabledFields.add(i);
            }
        }

        outState.putIntegerArrayList(this.getString(R.string.input_extra),
                enabledFields);
    }

    /**
     * Finds the corresponding {@link EditText} element
     * given a {@link CheckBox} that was clicked upon.
     * @param v - the checkbox that was clicked
     */
    public void onCheckBoxClick(View v) {
        EditText input = null;

        for (int i = 0; i < CHECKBOXES.length; i++) {
            if (v.getId() == CHECKBOXES[i]) {
                input = (EditText) this.findViewById(TEXT_FIELDS[i]);
            }
        }

        // toggle ok button
        if (this.numberChecked() > 0) {
            this.buttonOk.setEnabled(true);
        } else {
            this.buttonOk.setEnabled(false);
        }

        this.toggleTextField(input);
    }

    /**
     * Toggles the state of {@link EditText}.
     * @param et - the {@link EditText} to toggle
     */
    private void toggleTextField(EditText et) {
        if (et.isEnabled()) {
            et.setEnabled(false);
            this.mainLayout.requestFocus();
        } else {
            et.setEnabled(true);
            et.requestFocus();
        }
    }

    /**
     * Counts the number of {@link CheckBox} elements
     * that are checked.
     * @return the number of checked elements
     */
    private int numberChecked() {
        int count = 0;
        for (int i = 0; i < CHECKBOXES.length; i++) {
            CheckBox cb = (CheckBox) this.findViewById(CHECKBOXES[i]);
            if (cb.isChecked()) {
                count++;
            }
        }

        return count;
    }

    /**
     * Sets the combination of filter parameters as a result
     * of {@link FilterCards} activity and exits.
     * @param v - "Ok" button that was clicked
     */
    public void onConfirm(View v) {
        Intent intent = new Intent();
        for (int i = 0; i < TEXT_FIELDS.length; i++) {
            EditText input = (EditText) this.findViewById(TEXT_FIELDS[i]);
            if (input.isEnabled() && input.getText().toString().length() > 0) {
                String key = this.getString(EXTRAS[i]);
                intent.putExtra(key, input.getText().toString());
            }
        }

        this.setResult(RESULT_OK, intent);
        this.finish();
    }

    /**
     * Finishes {@link FilterCards} activity without
     * returning a result.
     * @param v - "Cancel" button that was clicked
     */
    public void onCancel(View v) {
        this.setResult(RESULT_CANCELED);
        this.finish();
    }

    private static final int[] CHECKBOXES = { R.id.brand_check,
            R.id.year_check, R.id.number_check, R.id.player_name_check,
            R.id.team_check };

    private static final int[] TEXT_FIELDS = { R.id.brand_input,
            R.id.year_input, R.id.number_input, R.id.player_name_input,
            R.id.team_input };

    private static final int[] EXTRAS = { R.string.brand_extra,
            R.string.year_extra, R.string.number_extra,
            R.string.player_name_extra, R.string.team_extra };

    private Button buttonOk;
    private LinearLayout mainLayout;
}
