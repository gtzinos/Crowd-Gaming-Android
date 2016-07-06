package geotzinos.crowdgaming.Controller.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import geotzinos.crowdgaming.Model.Domain.Question;
import geotzinos.crowdgaming.R;

/**
 * Created by George on 2016-07-03.
 */
public class AnswerQuestionGroupAdapter extends BaseAdapter {
    private Context context;
    private Question question;
    private long selected_id;
    private static LayoutInflater inflater = null;

    public AnswerQuestionGroupAdapter(Context context, Question question) {
        this.context = context;
        this.question = question;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return question.getAnswersList().size();
    }

    @Override
    public Object getItem(int position) {
        return question.getAnswersList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return question.getAnswersList().get(position).getId();
    }

    public class Holder {
        RadioButton answerRadioButton;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.answer_question_group_adapter_view, null);
        holder.answerRadioButton = (RadioButton) rowView.findViewById(R.id.AnswerRadioButton);
        holder.answerRadioButton.setText(question.getAnswersList().get(position).getText());
        holder.answerRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_id = question.getAnswersList().get(position).getId();
            }
        });
        return rowView;
    }
}

